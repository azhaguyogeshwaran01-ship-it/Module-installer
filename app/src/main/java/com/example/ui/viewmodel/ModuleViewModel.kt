package com.example.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BackupSnapshotEntity
import com.example.data.local.ExecutionLogEntity
import com.example.data.local.ModuleEntity
import com.example.data.repository.ModuleRepository
import com.example.model.DeviceSystemInfo
import com.example.model.ModuleCompatibilityReport
import com.example.model.RootStatus
import com.example.model.SecurityPatchAudit
import com.example.model.ThemeOverrideConfig
import com.example.service.CompatibilityEngine
import com.example.service.DeviceDiagnostics
import com.example.service.ShellExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

data class InserterUiState(
    val isBusy: Boolean = false,
    val busyMessage: String = "",
    val selectedCategory: String = "ALL",
    val searchQuery: String = "",
    val forcedMode: RootStatus? = null,
    val customCommandInput: String = "",
    val lastGeneratedRescueZip: File? = null,
    val showNewModuleDialog: Boolean = false,
    val showRescueDialog: Boolean = false,
    val inspectModule: ModuleEntity? = null,
    val promptBackupForModule: ModuleEntity? = null,
    val promptBackupForTheme: Boolean = false,
    val selectedBackupForDetail: BackupSnapshotEntity? = null,
    val selectedModuleForCompatibility: ModuleCompatibilityReport? = null,
    val showSystemPatchAuditDialog: Boolean = false
)

class ModuleViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = ModuleRepository(database)
    private val shellExecutor = ShellExecutor(application, database.logDao())

    private val _deviceInfo = MutableStateFlow(DeviceDiagnostics.inspectDevice())
    val deviceInfo: StateFlow<DeviceSystemInfo> = _deviceInfo.asStateFlow()

    val modules: StateFlow<List<ModuleEntity>> = repository.allModules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val snapshots: StateFlow<List<BackupSnapshotEntity>> = repository.allSnapshots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logs: StateFlow<List<ExecutionLogEntity>> = repository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _themeConfig = MutableStateFlow(ThemeOverrideConfig())
    val themeConfig: StateFlow<ThemeOverrideConfig> = _themeConfig.asStateFlow()

    private val _uiState = MutableStateFlow(InserterUiState())
    val uiState: StateFlow<InserterUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultModulesIfNeeded()
            refreshDeviceInfo()
        }
    }

    fun refreshDeviceInfo() {
        _deviceInfo.value = DeviceDiagnostics.inspectDevice()
    }

    val activeOperatingMode: RootStatus
        get() = _uiState.value.forcedMode ?: _deviceInfo.value.rootStatus

    fun setForcedMode(mode: RootStatus?) {
        _uiState.value = _uiState.value.copy(forcedMode = mode)
    }

    fun setCategoryFilter(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setCustomCommandInput(cmd: String) {
        _uiState.value = _uiState.value.copy(customCommandInput = cmd)
    }

    fun setShowNewModuleDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showNewModuleDialog = show)
    }

    fun setShowRescueDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showRescueDialog = show)
    }

    fun setInspectModule(module: ModuleEntity?) {
        _uiState.value = _uiState.value.copy(inspectModule = module)
    }

    fun getModuleCompatibility(module: ModuleEntity): ModuleCompatibilityReport {
        return CompatibilityEngine.evaluateModule(module, _deviceInfo.value)
    }

    fun getSecurityPatchAudit(): SecurityPatchAudit {
        return CompatibilityEngine.auditDeviceSecurityPatch(_deviceInfo.value)
    }

    fun openCompatibilityDialogForModule(module: ModuleEntity) {
        val report = getModuleCompatibility(module)
        _uiState.value = _uiState.value.copy(selectedModuleForCompatibility = report)
    }

    fun openSystemPatchAuditDialog() {
        _uiState.value = _uiState.value.copy(
            selectedModuleForCompatibility = null,
            showSystemPatchAuditDialog = true
        )
    }

    fun dismissCompatibilityDialog() {
        _uiState.value = _uiState.value.copy(
            selectedModuleForCompatibility = null,
            showSystemPatchAuditDialog = false
        )
    }

    fun openBackupDetail(snapshot: BackupSnapshotEntity) {
        _uiState.value = _uiState.value.copy(selectedBackupForDetail = snapshot)
    }

    fun dismissBackupDetail() {
        _uiState.value = _uiState.value.copy(selectedBackupForDetail = null)
    }

    fun dismissBackupPrompt() {
        _uiState.value = _uiState.value.copy(
            promptBackupForModule = null,
            promptBackupForTheme = false
        )
    }

    /**
     * Entry point for toggling a module:
     * If user wants to ENABLE a module, we automatically prompt them to create a full system backup!
     */
    fun toggleModule(module: ModuleEntity) {
        if (!module.isEnabled) {
            // Check root requirement
            if (module.isRootRequired && !activeOperatingMode.isElevated) {
                Toast.makeText(
                    getApplication(),
                    "Module '${module.title}' requires elevated Root privileges. Please switch mode or grant root.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            // Prompt pre-modification backup dialog
            _uiState.value = _uiState.value.copy(promptBackupForModule = module)
        } else {
            // Disabling module does not introduce new modifications; unmount cleanly
            executeModuleUnmount(module)
        }
    }

    fun confirmModuleInjectionWithBackup(module: ModuleEntity, backupTitle: String, backupDesc: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                promptBackupForModule = null,
                isBusy = true,
                busyMessage = "Capturing System Backup..."
            )

            val activeList = modules.value.filter { it.isEnabled }.map { it.id }
            repository.createSnapshot(
                title = backupTitle.ifBlank { "Pre-Modification: ${module.title}" },
                description = backupDesc.ifBlank { "Stability snapshot before enabling ${module.title}." },
                moduleCount = activeList.size,
                activeModulesJson = activeList.toString()
            )

            executeModuleInjection(module)
        }
    }

    fun confirmModuleInjectionWithoutBackup(module: ModuleEntity) {
        _uiState.value = _uiState.value.copy(promptBackupForModule = null)
        executeModuleInjection(module)
    }

    private fun executeModuleInjection(module: ModuleEntity) {
        viewModelScope.launch {
            val report = getModuleCompatibility(module)
            _uiState.value = _uiState.value.copy(
                isBusy = true,
                busyMessage = "Adapting & Injecting ${module.title}..."
            )

            shellExecutor.injectModule(
                moduleId = module.id,
                moduleTitle = module.title,
                mountTarget = module.patchMountTarget,
                rootStatus = activeOperatingMode,
                appliedShim = report.appliedShim
            )

            repository.setModuleEnabled(module.id, true)
            _uiState.value = _uiState.value.copy(isBusy = false, busyMessage = "")
            Toast.makeText(getApplication(), "${module.title} injected successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun executeModuleUnmount(module: ModuleEntity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isBusy = true,
                busyMessage = "Unmounting ${module.title}..."
            )

            shellExecutor.removeModule(
                moduleId = module.id,
                rootStatus = activeOperatingMode
            )

            repository.setModuleEnabled(module.id, false)
            _uiState.value = _uiState.value.copy(isBusy = false, busyMessage = "")
            Toast.makeText(getApplication(), "${module.title} unmounted cleanly.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Theme Overrides with Pre-Modification Backup Prompt
     */
    fun requestApplyThemeOverrides() {
        _uiState.value = _uiState.value.copy(promptBackupForTheme = true)
    }

    fun confirmThemeOverridesWithBackup(backupTitle: String, backupDesc: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                promptBackupForTheme = null as? Boolean ?: false,
                isBusy = true,
                busyMessage = "Saving Pre-Theme Backup..."
            )

            val activeList = modules.value.filter { it.isEnabled }.map { it.id }
            repository.createSnapshot(
                title = backupTitle.ifBlank { "Pre-Theme Overrides Backup" },
                description = backupDesc.ifBlank { "State before updating Material You overlays." },
                moduleCount = activeList.size,
                activeModulesJson = activeList.toString()
            )

            executeApplyThemeOverrides()
        }
    }

    fun confirmThemeOverridesWithoutBackup() {
        _uiState.value = _uiState.value.copy(promptBackupForTheme = false)
        executeApplyThemeOverrides()
    }

    private fun executeApplyThemeOverrides() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isBusy = true,
                busyMessage = "Applying theme & overlay overrides..."
            )
            shellExecutor.applyThemeOverrides(_themeConfig.value, activeOperatingMode)
            _uiState.value = _uiState.value.copy(isBusy = false, busyMessage = "")
            Toast.makeText(getApplication(), "Theme overrides and Fabricated Overlays applied!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Reliable System Restore:
     * Reverts active modules to match the snapshot's state, resets overlays, and synchronizes the system.
     */
    fun restoreSnapshot(snapshot: BackupSnapshotEntity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isBusy = true,
                busyMessage = "Reverting to Snapshot: ${snapshot.title}..."
            )

            // Parse saved active module IDs
            val rawJson = snapshot.activeModulesJson.replace("[", "").replace("]", "").replace("\"", "")
            val targetActiveModuleIds = rawJson.split(",").map { it.trim() }.filter { it.isNotBlank() }

            val allCurrentMods = modules.value
            for (mod in allCurrentMods) {
                val shouldBeActive = targetActiveModuleIds.contains(mod.id)
                if (mod.isEnabled && !shouldBeActive) {
                    shellExecutor.removeModule(mod.id, activeOperatingMode)
                    repository.setModuleEnabled(mod.id, false)
                } else if (!mod.isEnabled && shouldBeActive) {
                    shellExecutor.injectModule(
                        moduleId = mod.id,
                        moduleTitle = mod.title,
                        mountTarget = mod.patchMountTarget,
                        rootStatus = activeOperatingMode
                    )
                    repository.setModuleEnabled(mod.id, true)
                }
            }

            shellExecutor.restoreSystemState(targetActiveModuleIds, activeOperatingMode)
            _uiState.value = _uiState.value.copy(isBusy = false, busyMessage = "")
            Toast.makeText(
                getApplication(),
                "Device reverted to '${snapshot.title}' successfully! (${targetActiveModuleIds.size} modules active)",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun renameSnapshot(snapshotId: Long, newTitle: String) {
        viewModelScope.launch {
            repository.renameSnapshot(snapshotId, newTitle)
            Toast.makeText(getApplication(), "Backup renamed to '$newTitle'", Toast.LENGTH_SHORT).show()
        }
    }

    fun createManualSnapshot(title: String, description: String) {
        viewModelScope.launch {
            val activeList = modules.value.filter { it.isEnabled }.map { it.id }
            repository.createSnapshot(
                title = title.ifBlank { "Manual Stability Snapshot" },
                description = description.ifBlank { "User-initiated backup snapshot." },
                moduleCount = activeList.size,
                activeModulesJson = activeList.toString(),
                isEmergencyPoint = false
            )
            Toast.makeText(getApplication(), "Backup snapshot created successfully.", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteSnapshot(snapshot: BackupSnapshotEntity) {
        viewModelScope.launch {
            repository.deleteSnapshot(snapshot.id)
            if (_uiState.value.selectedBackupForDetail?.id == snapshot.id) {
                _uiState.value = _uiState.value.copy(selectedBackupForDetail = null)
            }
            Toast.makeText(getApplication(), "Snapshot removed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun createCustomModule(
        title: String,
        author: String,
        version: String,
        category: String,
        description: String,
        mountTarget: String,
        isPartitionPatch: Boolean,
        isRootOnly: Boolean,
        scriptHook: String
    ) {
        viewModelScope.launch {
            val id = "custom_" + title.lowercase().replace(Regex("[^a-z0-9]"), "_") + "_" + (System.currentTimeMillis() % 10000)
            val newMod = ModuleEntity(
                id = id,
                title = title,
                author = author.ifBlank { "User" },
                version = version.ifBlank { "1.0.0" },
                versionCode = 1,
                description = description.ifBlank { "User-defined module package." },
                category = category,
                isEnabled = false,
                isRootRequired = isRootOnly,
                supportsNonRoot = !isRootOnly,
                isPartitionPatch = isPartitionPatch,
                patchMountTarget = mountTarget.ifBlank { "/system/etc" },
                scriptHook = scriptHook,
                configParams = "{}"
            )
            repository.insertOrUpdateModule(newMod)
            _uiState.value = _uiState.value.copy(showNewModuleDialog = false)
            Toast.makeText(getApplication(), "Custom module '$title' created!", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteModule(module: ModuleEntity) {
        viewModelScope.launch {
            if (module.isEnabled) {
                shellExecutor.removeModule(module.id, activeOperatingMode)
            }
            repository.deleteModule(module.id)
            if (_uiState.value.inspectModule?.id == module.id) {
                _uiState.value = _uiState.value.copy(inspectModule = null)
            }
            Toast.makeText(getApplication(), "Module '${module.title}' uninstalled.", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateThemeConfig(update: (ThemeOverrideConfig) -> ThemeOverrideConfig) {
        _themeConfig.value = update(_themeConfig.value)
    }

    fun generateRescueZip() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBusy = true, busyMessage = "Compiling Emergency Rescue Package...")
            val file = shellExecutor.generateEmergencyRescueZip()
            _uiState.value = _uiState.value.copy(
                isBusy = false,
                busyMessage = "",
                lastGeneratedRescueZip = file,
                showRescueDialog = true
            )
        }
    }

    fun triggerEmergencyRollback() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBusy = true, busyMessage = "Executing 1-Tap Emergency Rollback...")
            shellExecutor.emergencyRollback(activeOperatingMode)
            repository.disableAllModules()
            _uiState.value = _uiState.value.copy(isBusy = false, busyMessage = "")
            Toast.makeText(getApplication(), "Emergency Rollback complete! All modules neutralized.", Toast.LENGTH_LONG).show()
        }
    }

    fun executeTerminalCommand() {
        val cmd = _uiState.value.customCommandInput.trim()
        if (cmd.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(customCommandInput = "")
            shellExecutor.executeCommand(
                command = cmd,
                tag = "USER_SHELL",
                requiresRoot = activeOperatingMode.isElevated,
                rootStatus = activeOperatingMode
            )
        }
    }

    fun clearTerminalLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }
}

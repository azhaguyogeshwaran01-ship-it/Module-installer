package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ModuleEntity
import com.example.ui.components.BackupDetailDialog
import com.example.ui.components.CompatibilityCheckDialog
import com.example.ui.components.ModuleInspectDialog
import com.example.ui.components.NewModuleDialog
import com.example.ui.components.PreModificationBackupDialog
import com.example.ui.components.RescueZipDialog
import com.example.ui.components.StatusBadge
import com.example.ui.screens.BackupRescueScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ModulesScreen
import com.example.ui.screens.PartitionsScreen
import com.example.ui.screens.TerminalScreen
import com.example.ui.screens.ThemeOverridesScreen
import com.example.ui.theme.CyberCyanBright
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.viewmodel.ModuleViewModel

enum class AppNavTab(val title: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, "tab_dashboard"),
    MODULES("Modules", Icons.Default.ViewModule, "tab_modules"),
    THEMES("Themes", Icons.Default.Palette, "tab_themes"),
    PARTITIONS("Partitions", Icons.Default.Layers, "tab_partitions"),
    STABILITY("Stability", Icons.Default.Shield, "tab_stability"),
    TERMINAL("Terminal", Icons.Default.Terminal, "tab_terminal")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(viewModel: ModuleViewModel) {
    var currentTab by rememberSaveable { mutableStateOf(AppNavTab.DASHBOARD) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeMode = viewModel.activeOperatingMode

    Scaffold(
        containerColor = CyberDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MODULE INSERTER",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(
                            text = if (activeMode.isElevated) "SU ROOT" else "SHIZUKU/SANDBOX",
                            color = if (activeMode.isElevated) CyberEmerald else CyberCyanBright
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { currentTab = AppNavTab.TERMINAL },
                        modifier = Modifier.testTag("topbar_terminal_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "Console",
                            tint = if (currentTab == AppNavTab.TERMINAL) CyberCyanBright else Color(0xFF94A3B8)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberSurface)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CyberSurface,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.border(1.dp, CyberSurfaceBorder.copy(alpha = 0.5f))
            ) {
                listOf(
                    AppNavTab.DASHBOARD,
                    AppNavTab.MODULES,
                    AppNavTab.THEMES,
                    AppNavTab.PARTITIONS,
                    AppNavTab.STABILITY
                ).forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = CyberCyanBright,
                            indicatorColor = CyberCyanBright,
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppNavTab.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToModules = { currentTab = AppNavTab.MODULES },
                    onNavigateToTheme = { currentTab = AppNavTab.THEMES },
                    onNavigateToTerminal = { currentTab = AppNavTab.TERMINAL },
                    onNavigateToBackup = { currentTab = AppNavTab.STABILITY }
                )
                AppNavTab.MODULES -> ModulesScreen(viewModel = viewModel)
                AppNavTab.THEMES -> ThemeOverridesScreen(viewModel = viewModel)
                AppNavTab.PARTITIONS -> PartitionsScreen(viewModel = viewModel)
                AppNavTab.STABILITY -> BackupRescueScreen(viewModel = viewModel)
                AppNavTab.TERMINAL -> TerminalScreen(viewModel = viewModel)
            }

            // Global Busy/Executing Loader Overlay
            AnimatedVisibility(
                visible = uiState.isBusy,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.75f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .background(CyberSurface, RoundedCornerShape(16.dp))
                            .border(1.dp, CyberCyanBright, RoundedCornerShape(16.dp))
                            .padding(24.dp)
                    ) {
                        CircularProgressIndicator(
                            color = CyberCyanBright,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.busyMessage,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }

    // Pre-Modification Backup Prompt Dialog (Module)
    uiState.promptBackupForModule?.let { mod ->
        val compatReport = viewModel.getModuleCompatibility(mod)
        PreModificationBackupDialog(
            module = mod,
            compatibilityReport = compatReport,
            onConfirmWithBackup = { title, desc ->
                viewModel.confirmModuleInjectionWithBackup(mod, title, desc)
            },
            onSkipBackup = {
                viewModel.confirmModuleInjectionWithoutBackup(mod)
            },
            onDismiss = {
                viewModel.dismissBackupPrompt()
            }
        )
    }

    // Pre-Modification Backup Prompt Dialog (Theme)
    if (uiState.promptBackupForTheme) {
        val dummyMod = ModuleEntity(
            id = "theme_matrix_mod",
            title = "System Theme & Overlay Customization",
            author = "SystemUI",
            version = "1.0",
            versionCode = 1,
            description = "Material You dynamic accent, notification blur, and animation scale overrides.",
            category = "THEME_OVERRIDE",
            isEnabled = false,
            isRootRequired = false,
            supportsNonRoot = true,
            isPartitionPatch = false,
            patchMountTarget = "/system/overlay",
            scriptHook = "service",
            configParams = "{}"
        )
        val compatReport = viewModel.getModuleCompatibility(dummyMod)
        PreModificationBackupDialog(
            module = dummyMod,
            compatibilityReport = compatReport,
            onConfirmWithBackup = { title, desc ->
                viewModel.confirmThemeOverridesWithBackup(title, desc)
            },
            onSkipBackup = {
                viewModel.confirmThemeOverridesWithoutBackup()
            },
            onDismiss = {
                viewModel.dismissBackupPrompt()
            }
        )
    }

    // Compatibility Check & Security Patch Scanner Dialog
    if (uiState.selectedModuleForCompatibility != null || uiState.showSystemPatchAuditDialog) {
        val patchAudit = viewModel.getSecurityPatchAudit()
        val deviceInfo = viewModel.deviceInfo.value
        CompatibilityCheckDialog(
            report = uiState.selectedModuleForCompatibility,
            patchAudit = patchAudit,
            deviceInfo = deviceInfo,
            onDismiss = { viewModel.dismissCompatibilityDialog() }
        )
    }

    // Backup Details & Restore Management Dialog
    uiState.selectedBackupForDetail?.let { snapshot ->
        BackupDetailDialog(
            snapshot = snapshot,
            onRename = { newTitle -> viewModel.renameSnapshot(snapshot.id, newTitle) },
            onRestore = { viewModel.restoreSnapshot(snapshot) },
            onExportRescueZip = { viewModel.generateRescueZip() },
            onDelete = { viewModel.deleteSnapshot(snapshot) },
            onDismiss = { viewModel.dismissBackupDetail() }
        )
    }

    // Rescue Dialog
    if (uiState.showRescueDialog) {
        RescueZipDialog(
            file = uiState.lastGeneratedRescueZip,
            onDismiss = { viewModel.setShowRescueDialog(false) }
        )
    }

    // Inspect Module Dialog
    uiState.inspectModule?.let { mod ->
        ModuleInspectDialog(
            module = mod,
            onDismiss = { viewModel.setInspectModule(null) },
            onDelete = { viewModel.deleteModule(mod) },
            onCheckCompatibility = { viewModel.openCompatibilityDialogForModule(mod) }
        )
    }

    // New Module Dialog
    if (uiState.showNewModuleDialog) {
        NewModuleDialog(
            onDismiss = { viewModel.setShowNewModuleDialog(false) },
            onCreate = { title, author, version, category, description, mountTarget, isPartitionPatch, isRootOnly, scriptHook ->
                viewModel.createCustomModule(
                    title, author, version, category, description, mountTarget, isPartitionPatch, isRootOnly, scriptHook
                )
            }
        )
    }
}

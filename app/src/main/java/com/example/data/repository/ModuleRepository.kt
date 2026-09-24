package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BackupSnapshotEntity
import com.example.data.local.ExecutionLogEntity
import com.example.data.local.ModuleEntity
import kotlinx.coroutines.flow.Flow

class ModuleRepository(private val database: AppDatabase) {

    val allModules: Flow<List<ModuleEntity>> = database.moduleDao().getAllModules()
    val activeModules: Flow<List<ModuleEntity>> = database.moduleDao().getActiveModules()
    val allSnapshots: Flow<List<BackupSnapshotEntity>> = database.backupDao().getAllSnapshots()
    val recentLogs: Flow<List<ExecutionLogEntity>> = database.logDao().getRecentLogs()

    suspend fun initializeDefaultModulesIfNeeded() {
        val initialModules = listOf(
            ModuleEntity(
                id = "axial_thermal",
                title = "Axial Thermal Engine 5.0",
                author = "KernelMod Team",
                version = "5.0.2-Ultra",
                versionCode = 502,
                description = "Dynamically mitigates aggressive CPU throttling and optimizes schedutil governor rate limits for sustained peak frame rates.",
                category = "KERNEL_PERF",
                isEnabled = false,
                isRootRequired = false,
                supportsNonRoot = true,
                isPartitionPatch = true,
                patchMountTarget = "/vendor/etc/thermal-engine.conf",
                scriptHook = "post-fs-data",
                configParams = "{\"targetTemp\": 44, \"governorProfile\": \"performance\", \"boostFrequency\": true}"
            ),
            ModuleEntity(
                id = "monet_matrix",
                title = "Monet Matrix Theme Override",
                author = "MaterialX Labs",
                version = "3.4.1",
                versionCode = 341,
                description = "Injects custom dynamic Material You palette tokens, glassmorphic QS notification blur, and fine-tuned system accent overrides.",
                category = "THEME_OVERRIDE",
                isEnabled = true,
                isRootRequired = false,
                supportsNonRoot = true,
                isPartitionPatch = false,
                patchMountTarget = "/system/overlay",
                scriptHook = "service",
                configParams = "{\"accent\": \"#06B6D4\", \"blurStrength\": 75, \"qsTransparency\": 80}"
            ),
            ModuleEntity(
                id = "dynamic_status_bar",
                title = "Dynamic Island & SystemUI Mod",
                author = "SysMod OpenSource",
                version = "2.1.0",
                versionCode = 210,
                description = "Overrides status bar layout, centers digital clock with seconds, displays realtime network traffic speed, and enables floating pill alerts.",
                category = "SYSTEM_UI",
                isEnabled = false,
                isRootRequired = false,
                supportsNonRoot = true,
                isPartitionPatch = true,
                patchMountTarget = "/system_ext/priv-app/SystemUI",
                scriptHook = "post-fs-data",
                configParams = "{\"clockPos\": \"center\", \"showSeconds\": true, \"trafficRate\": true}"
            ),
            ModuleEntity(
                id = "audio_hifi_studio",
                title = "Audio HAL Hi-Fi Studio Pro",
                author = "Audiophile Devs",
                version = "4.2.0",
                versionCode = 420,
                description = "Patches audio_policy_configuration.xml to unlock direct 384kHz/32-bit DAC output, bypasses resampling, and injects Dolby Atmos profiles.",
                category = "AUDIO_HAL",
                isEnabled = false,
                isRootRequired = true,
                supportsNonRoot = false,
                isPartitionPatch = true,
                patchMountTarget = "/vendor/etc/audio_policy_configuration.xml",
                scriptHook = "post-fs-data",
                configParams = "{\"sampleRate\": 384000, \"bitDepth\": 32, \"directPcm\": true}"
            ),
            ModuleEntity(
                id = "security_patch_armor",
                title = "Security Patch Armor 2026",
                author = "CoreSec Project",
                version = "1.8.0",
                versionCode = 180,
                description = "Dynamic compatibility shim preventing bootloops on newer Android security patches. Patches sepolicy.rule and verifies dm-verity integrity.",
                category = "STABILITY_PATCH",
                isEnabled = true,
                isRootRequired = false,
                supportsNonRoot = true,
                isPartitionPatch = false,
                patchMountTarget = "/system/etc/security",
                scriptHook = "post-fs-data",
                configParams = "{\"avbBypass\": true, \"autoRescueMode\": true, \"selinuxAuditFilter\": true}"
            ),
            ModuleEntity(
                id = "zram_compactor",
                title = "ZRAM & Memory Compactor 4.0",
                author = "Performance Team",
                version = "4.0.0",
                versionCode = 400,
                description = "Sets ZRAM compression algorithm to zstd, scales swap pool to 50% physical RAM, and compacts background app memory pages proactively.",
                category = "MEMORY_RAM",
                isEnabled = false,
                isRootRequired = false,
                supportsNonRoot = true,
                isPartitionPatch = false,
                patchMountTarget = "/sys/block/zram0",
                scriptHook = "service",
                configParams = "{\"algorithm\": \"zstd\", \"swappiness\": 160, \"zramSizeGb\": 4}"
            ),
            ModuleEntity(
                id = "touch_turbo_latency",
                title = "Ultra Touch Latency & Game Turbo",
                author = "GamingKernel Group",
                version = "3.1.2",
                versionCode = 312,
                description = "Overrides vendor touch sampling limits to 480Hz, disables surfaceflinger frame pacing wait, and locks dynamic refresh rate to maximum.",
                category = "KERNEL_PERF",
                isEnabled = false,
                isRootRequired = false,
                supportsNonRoot = true,
                isPartitionPatch = false,
                patchMountTarget = "/system/build.prop",
                scriptHook = "service",
                configParams = "{\"touchSampleRate\": 480, \"forceRefreshHz\": 120, \"sfThrottleDisabled\": true}"
            ),
            ModuleEntity(
                id = "font_google_sans",
                title = "Google Sans Flex & Unicode 16 Emoji",
                author = "Typography Forge",
                version = "2.0.0",
                versionCode = 200,
                description = "Systemless font package replacing Roboto and default NotoColorEmoji with high-contrast variable Google Sans and the newest emoji glyphs.",
                category = "THEME_OVERRIDE",
                isEnabled = false,
                isRootRequired = false,
                supportsNonRoot = true,
                isPartitionPatch = true,
                patchMountTarget = "/system/fonts",
                scriptHook = "post-fs-data",
                configParams = "{\"weight\": \"regular\", \"includeMonospace\": true}"
            )
        )

        for (module in initialModules) {
            val existing = database.moduleDao().getModuleById(module.id)
            if (existing == null) {
                database.moduleDao().insertOrUpdate(module)
            }
        }

        // Initialize a baseline backup snapshot if none exists
        if (database.backupDao().getCount() == 0) {
            database.backupDao().insertSnapshot(
                BackupSnapshotEntity(
                    title = "Factory Baseline Snapshot",
                    timestamp = System.currentTimeMillis() - 86400000L,
                    moduleCount = 1,
                    description = "Stock system configuration baseline captured before initial module insertion.",
                    activeModulesJson = "[\"monet_matrix\"]",
                    systemPropsJson = "{\"ro.build.version.release\":\"15\",\"ro.boot.verifiedbootstate\":\"green\"}",
                    themeConfigJson = "{\"accentColorHex\":\"#0EA5E9\",\"iconShape\":\"Squircle\"}",
                    isEmergencyRescuePoint = true,
                    checksum = "SHA256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069"
                )
            )
        }
    }

    suspend fun setModuleEnabled(id: String, enabled: Boolean) {
        database.moduleDao().setEnabled(id, enabled)
    }

    suspend fun insertOrUpdateModule(module: ModuleEntity) {
        database.moduleDao().insertOrUpdate(module)
    }

    suspend fun deleteModule(id: String) {
        database.moduleDao().deleteById(id)
    }

    suspend fun createSnapshot(
        title: String,
        description: String,
        moduleCount: Int,
        activeModulesJson: String,
        isEmergencyPoint: Boolean = false
    ): Long {
        val now = System.currentTimeMillis()
        val rawData = "$title-$now-$activeModulesJson"
        val hash = java.security.MessageDigest.getInstance("SHA-256")
            .digest(rawData.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }

        val snapshot = BackupSnapshotEntity(
            title = title,
            timestamp = now,
            moduleCount = moduleCount,
            description = description,
            activeModulesJson = activeModulesJson,
            systemPropsJson = "{\"ro.build.version.sdk\": \"35\", \"persist.sys.rescue\": \"0\"}",
            themeConfigJson = "{\"accent\": \"#0EA5E9\", \"overlay\": \"active\"}",
            isEmergencyRescuePoint = isEmergencyPoint,
            checksum = "SHA256:${hash.take(32)}"
        )
        return database.backupDao().insertSnapshot(snapshot)
    }

    suspend fun renameSnapshot(id: Long, newTitle: String) {
        database.backupDao().updateTitle(id, newTitle)
    }

    suspend fun deleteSnapshot(id: Long) {
        database.backupDao().deleteSnapshot(id)
    }

    suspend fun disableAllModules() {
        database.moduleDao().disableAllModules()
    }

    suspend fun clearLogs() {
        database.logDao().clearLogs()
    }
}

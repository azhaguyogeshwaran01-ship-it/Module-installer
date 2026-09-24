package com.example.model

enum class RootStatus(val label: String, val isElevated: Boolean) {
    ROOTED_MAGISK("Magisk (SU Granted)", true),
    ROOTED_KERNEL_SU("KernelSU (GKI Driver)", true),
    ROOTED_APATCH("APatch (Kernel Patch)", true),
    ROOTED_GENERIC_SU("Standard Superuser (SU)", true),
    NON_ROOTED_SHIZUKU("Non-Rooted (Shizuku Bridge Connected)", false),
    NON_ROOTED_USERLAND("Non-Rooted (Safe Overlay Sandbox)", false)
}

enum class SELinuxMode(val label: String) {
    ENFORCING("Enforcing (Secure)"),
    PERMISSIVE("Permissive (Audit Only)"),
    UNKNOWN("Unknown / Virtualized")
}

data class SystemPartitionInfo(
    val name: String,
    val mountPoint: String,
    val fsType: String,
    val totalBytes: Long,
    val freeBytes: Long,
    val isReadOnly: Boolean,
    val hasOverlay: Boolean
) {
    val usedPercentage: Int
        get() = if (totalBytes > 0) {
            (((totalBytes - freeBytes).toDouble() / totalBytes.toDouble()) * 100).toInt().coerceIn(0, 100)
        } else 0
}

data class DeviceSystemInfo(
    val deviceModel: String,
    val manufacturer: String,
    val androidVersion: String,
    val apiLevel: Int,
    val securityPatch: String,
    val kernelRelease: String,
    val rootStatus: RootStatus,
    val selinuxMode: SELinuxMode,
    val dmVerityStatus: String,
    val activeMountMethod: String,
    val partitions: List<SystemPartitionInfo>
)

data class ThemeOverrideConfig(
    val accentColorHex: String = "#0EA5E9",
    val iconShape: String = "Squircle",
    val clockPosition: String = "Center",
    val showSeconds: Boolean = true,
    val qsColumns: Int = 2,
    val blurRadius: Int = 65,
    val refreshRateHz: Int = 120,
    val animationScale: Float = 0.5f,
    val dynamicMonetEnabled: Boolean = true,
    val transparentNavigationBar: Boolean = true
)

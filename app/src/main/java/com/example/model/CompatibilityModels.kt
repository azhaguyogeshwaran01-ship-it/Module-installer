package com.example.model

enum class CompatibilityStatus(val label: String, val badgeText: String) {
    FULLY_COMPATIBLE("Fully Compatible", "100% PASS"),
    COMPATIBLE_WITH_SHIM("Adapted via Dynamic Shim", "AUTO-SHIMMED"),
    SECURITY_PATCH_WARNING("Patch Conflict Warning", "PATCH CAUTION"),
    INCOMPATIBLE("System Incompatible", "INCOMPATIBLE")
}

data class SecurityPatchAudit(
    val patchDate: String,
    val apiLevel: Int,
    val patchTier: String,
    val supports16KbPages: Boolean,
    val enforcesStrictDmVerity: Boolean,
    val requiresSyntheticSepolicy: Boolean,
    val activeShims: List<String>,
    val advisories: List<String>
)

data class ModuleCompatibilityReport(
    val moduleId: String,
    val moduleTitle: String,
    val status: CompatibilityStatus,
    val headline: String,
    val appliedShim: String?,
    val advisories: List<String>,
    val isSafeToApply: Boolean
)

package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modules")
data class ModuleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val version: String,
    val versionCode: Int,
    val description: String,
    val category: String, // KERNEL_PERF, THEME_OVERRIDE, SYSTEM_UI, AUDIO_HAL, STABILITY_PATCH, MEMORY_RAM, CUSTOM
    val isEnabled: Boolean = false,
    val isRootRequired: Boolean = false,
    val supportsNonRoot: Boolean = true,
    val isPartitionPatch: Boolean = false,
    val patchMountTarget: String = "/system/etc",
    val scriptHook: String = "post-fs-data",
    val configParams: String = "{}",
    val installedAt: Long = System.currentTimeMillis()
)

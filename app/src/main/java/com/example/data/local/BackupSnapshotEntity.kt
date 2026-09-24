package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backup_snapshots")
data class BackupSnapshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val moduleCount: Int,
    val description: String,
    val activeModulesJson: String,
    val systemPropsJson: String,
    val themeConfigJson: String,
    val isEmergencyRescuePoint: Boolean = false,
    val checksum: String = ""
)

@Entity(tableName = "execution_logs")
data class ExecutionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String,
    val command: String,
    val output: String,
    val isSuccess: Boolean
)

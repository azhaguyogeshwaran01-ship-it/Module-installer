package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleDao {
    @Query("SELECT * FROM modules ORDER BY title ASC")
    fun getAllModules(): Flow<List<ModuleEntity>>

    @Query("SELECT * FROM modules WHERE isEnabled = 1")
    fun getActiveModules(): Flow<List<ModuleEntity>>

    @Query("SELECT * FROM modules WHERE id = :id LIMIT 1")
    suspend fun getModuleById(id: String): ModuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(module: ModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(modules: List<ModuleEntity>)

    @Update
    suspend fun update(module: ModuleEntity)

    @Query("UPDATE modules SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setEnabled(id: String, isEnabled: Boolean)

    @Query("DELETE FROM modules WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE modules SET isEnabled = 0")
    suspend fun disableAllModules()
}

@Dao
interface BackupDao {
    @Query("SELECT * FROM backup_snapshots ORDER BY timestamp DESC")
    fun getAllSnapshots(): Flow<List<BackupSnapshotEntity>>

    @Query("SELECT * FROM backup_snapshots WHERE id = :id LIMIT 1")
    suspend fun getSnapshotById(id: Long): BackupSnapshotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: BackupSnapshotEntity): Long

    @Query("DELETE FROM backup_snapshots WHERE id = :id")
    suspend fun deleteSnapshot(id: Long)

    @Query("UPDATE backup_snapshots SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String)

    @Query("SELECT COUNT(*) FROM backup_snapshots")
    suspend fun getCount(): Int
}

@Dao
interface LogDao {
    @Query("SELECT * FROM execution_logs ORDER BY timestamp DESC LIMIT 200")
    fun getRecentLogs(): Flow<List<ExecutionLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ExecutionLogEntity)

    @Query("DELETE FROM execution_logs")
    suspend fun clearLogs()
}

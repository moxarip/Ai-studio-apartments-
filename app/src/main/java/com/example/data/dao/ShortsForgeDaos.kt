package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun observeProjectById(id: String): Flow<ProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)
}

@Dao
interface ClipDao {
    @Query("SELECT * FROM clips WHERE projectId = :projectId ORDER BY confidence DESC")
    fun getClipsForProject(projectId: String): Flow<List<ClipEntity>>

    @Query("SELECT * FROM clips WHERE id = :id LIMIT 1")
    suspend fun getClipById(id: String): ClipEntity?

    @Query("SELECT * FROM clips WHERE id = :id LIMIT 1")
    fun observeClipById(id: String): Flow<ClipEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClip(clip: ClipEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClips(clips: List<ClipEntity>)

    @Update
    suspend fun updateClip(clip: ClipEntity)

    @Query("DELETE FROM clips WHERE projectId = :projectId")
    suspend fun deleteClipsByProject(projectId: String)

    @Query("SELECT COUNT(*) FROM clips WHERE exportStatus = 'exported'")
    fun getExportedClipCount(): Flow<Int>
}

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY startedAt DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE projectId = :projectId ORDER BY startedAt DESC")
    fun getJobsForProject(projectId: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE stage NOT IN ('completed', 'failed') ORDER BY startedAt DESC")
    fun getActiveJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    suspend fun getJobById(id: String): JobEntity?

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    fun observeJobById(id: String): Flow<JobEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity)

    @Update
    suspend fun updateJob(job: JobEntity)

    @Query("DELETE FROM jobs WHERE id = :id")
    suspend fun deleteJobById(id: String)
}

@Dao
interface ExportDao {
    @Query("SELECT * FROM exports ORDER BY exportedAt DESC")
    fun getAllExports(): Flow<List<ExportEntity>>

    @Query("SELECT * FROM exports WHERE projectId = :projectId ORDER BY exportedAt DESC")
    fun getExportsForProject(projectId: String): Flow<List<ExportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExport(export: ExportEntity)

    @Query("DELETE FROM exports WHERE id = :id")
    suspend fun deleteExportById(id: String)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE projectId = :projectId ORDER BY timestamp ASC")
    fun getMessages(projectId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE projectId = :projectId")
    suspend fun clearChat(projectId: String)
}

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: UserSettingsEntity)
}

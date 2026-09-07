package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import com.example.domain.models.CanvasElement
import com.example.domain.models.EditableField
import com.example.domain.models.PosterBackground
import com.example.domain.models.TemplateStatus
import com.example.domain.models.UserRole
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow

// Entities
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val projectId: String,
    val userId: String,
    val templateId: String,
    val templateName: String,
    val projectName: String,
    val canvasWidth: Int,
    val canvasHeight: Int,
    val backgroundJson: String,
    val elementsJson: String,
    val fieldValuesJson: String,
    val previewThumbnail: String?,
    val isDraft: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val templateId: String,
    val userId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey val templateId: String,
    val creatorId: String,
    val creatorName: String,
    val name: String,
    val description: String,
    val categoryId: String,
    val categoryName: String,
    val thumbnailUrl: String,
    val canvasWidth: Int,
    val canvasHeight: Int,
    val backgroundJson: String,
    val elementsJson: String,
    val editableFieldsJson: String,
    val status: String,
    val rejectionReason: String?,
    val isFeatured: Boolean,
    val isPro: Boolean,
    val usageCount: Int,
    val favoriteCount: Int,
    val rating: Float,
    val tagsJson: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: Long,
    val isRead: Boolean
)

@Entity(tableName = "app_users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val role: String,
    val avatarUrl: String?,
    val isCreatorVerified: Boolean,
    val isSuspended: Boolean,
    val bio: String,
    val totalTemplatesCreated: Int,
    val totalDownloads: Int
)

// DAOs
@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getProjectsByUser(userId: String): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE projectId = :projectId")
    suspend fun getProjectById(projectId: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE projectId = :projectId")
    suspend fun deleteProjectById(projectId: String)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getFavoritesByUser(userId: String): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE templateId = :templateId AND userId = :userId)")
    fun isFavorite(templateId: String, userId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE templateId = :templateId AND userId = :userId")
    suspend fun removeFavorite(templateId: String, userId: String)
}

@Dao
interface TemplateDao {
    @Query("SELECT * FROM templates ORDER BY createdAt DESC")
    fun getAllTemplates(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE status = 'APPROVED' ORDER BY usageCount DESC")
    fun getApprovedTemplates(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE creatorId = :creatorId ORDER BY updatedAt DESC")
    fun getTemplatesByCreator(creatorId: String): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE templateId = :templateId")
    suspend fun getTemplateById(templateId: String): TemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<TemplateEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: TemplateEntity)

    @Update
    suspend fun updateTemplate(template: TemplateEntity)

    @Query("DELETE FROM templates WHERE templateId = :templateId")
    suspend fun deleteTemplateById(templateId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsByUser(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM app_users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM app_users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
}

@Entity(tableName = "template_versions")
data class TemplateVersionEntity(
    @PrimaryKey val versionId: String,
    val templateId: String,
    val versionNumber: Int,
    val elementsJson: String,
    val backgroundJson: String,
    val editableFieldsJson: String,
    val changedByUserId: String,
    val changedByUserName: String,
    val changeNote: String,
    val createdAt: Long
)

@Dao
interface TemplateVersionDao {
    @Query("SELECT * FROM template_versions WHERE templateId = :templateId ORDER BY versionNumber DESC")
    fun getVersionsByTemplate(templateId: String): Flow<List<TemplateVersionEntity>>

    @Query("SELECT * FROM template_versions WHERE templateId = :templateId ORDER BY versionNumber DESC LIMIT 1")
    suspend fun getLatestVersion(templateId: String): TemplateVersionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: TemplateVersionEntity)
}

// Database
@Database(
    entities = [
        ProjectEntity::class,
        FavoriteEntity::class,
        TemplateEntity::class,
        NotificationEntity::class,
        UserEntity::class,
        TemplateVersionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun templateDao(): TemplateDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userDao(): UserDao
    abstract fun templateVersionDao(): TemplateVersionDao
}

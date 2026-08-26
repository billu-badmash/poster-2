package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.FavoriteEntity
import com.example.data.local.toDomain
import com.example.data.local.toEntity
import com.example.data.sample.SampleData
import com.example.domain.models.AppNotification
import com.example.domain.models.Category
import com.example.domain.models.Project
import com.example.domain.models.Template
import com.example.domain.models.TemplateReport
import com.example.domain.models.TemplateStatus
import com.example.domain.models.UserProfile
import com.example.domain.models.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class PosterRepository(context: Context) {

    private val db: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "poster_maker.db"
    ).fallbackToDestructiveMigration().build()

    private val scope = CoroutineScope(Dispatchers.IO)

    // Current Active User State
    private val _currentUser = MutableStateFlow(SampleData.getInitialUsers().first())
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // In-memory reports state for Admin moderation
    private val _reports = MutableStateFlow<List<TemplateReport>>(
        listOf(
            TemplateReport(
                reportId = "rep_101",
                templateId = "tmpl_summer_fest",
                templateName = "Summer Music Festival",
                reportedByUserId = "user_demo",
                reportedByUserName = "Alex Rivera",
                reason = "Copyright concern",
                details = "Check background illustration attribution.",
                timestamp = System.currentTimeMillis() - 86400000L,
                isResolved = false
            )
        )
    )
    val reports: StateFlow<List<TemplateReport>> = _reports.asStateFlow()

    private val _categories = MutableStateFlow(SampleData.categories)
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    init {
        // Seed initial data asynchronously on first run
        scope.launch {
            val existing = db.templateDao().getTemplateById("tmpl_summer_fest")
            if (existing == null) {
                db.templateDao().insertTemplates(SampleData.getInitialTemplates().map { it.toEntity() })
                db.userDao().insertUsers(SampleData.getInitialUsers().map { it.toEntity() })
                
                // Add initial demo notification
                db.notificationDao().insertNotification(
                    AppNotification(
                        id = UUID.randomUUID().toString(),
                        userId = "user_demo",
                        title = "Welcome to Poster Maker!",
                        message = "Explore 100+ customizable poster templates and create your first flyer in seconds.",
                        type = "SYSTEM"
                    ).toEntity()
                )
            }
        }
    }

    // USER & AUTH METHODS
    fun setCurrentUser(user: UserProfile) {
        _currentUser.value = user
        scope.launch {
            db.userDao().insertUser(user.toEntity())
        }
    }

    fun switchRole(newRole: UserRole) {
        val current = _currentUser.value
        val updated = current.copy(role = newRole)
        _currentUser.value = updated
        scope.launch {
            db.userDao().insertUser(updated.toEntity())
        }
    }

    fun getAllUsers(): Flow<List<UserProfile>> {
        return db.userDao().getAllUsers().map { list -> list.map { it.toDomain() } }
    }

    suspend fun toggleUserSuspension(userId: String, isSuspended: Boolean) {
        val userEntity = db.userDao().getUserById(userId) ?: return
        db.userDao().insertUser(userEntity.copy(isSuspended = isSuspended))
    }

    // TEMPLATE METHODS
    fun getAllTemplates(): Flow<List<Template>> {
        return db.templateDao().getAllTemplates().map { list -> list.map { it.toDomain() } }
    }

    fun getApprovedTemplates(): Flow<List<Template>> {
        return db.templateDao().getApprovedTemplates().map { list -> list.map { it.toDomain() } }
    }

    fun getPendingTemplates(): Flow<List<Template>> {
        return db.templateDao().getAllTemplates().map { list ->
            list.map { it.toDomain() }.filter { it.status == TemplateStatus.PENDING }
        }
    }

    fun getTemplatesByCreator(creatorId: String): Flow<List<Template>> {
        return db.templateDao().getTemplatesByCreator(creatorId).map { list -> list.map { it.toDomain() } }
    }

    suspend fun getTemplateById(templateId: String): Template? {
        return db.templateDao().getTemplateById(templateId)?.toDomain()
    }

    suspend fun saveTemplate(template: Template) {
        db.templateDao().insertTemplate(template.toEntity())
    }

    suspend fun deleteTemplate(templateId: String) {
        db.templateDao().deleteTemplateById(templateId)
    }

    suspend fun updateTemplateStatus(templateId: String, status: TemplateStatus, rejectionReason: String? = null) {
        val tmpl = db.templateDao().getTemplateById(templateId)?.toDomain() ?: return
        val updated = tmpl.copy(status = status, rejectionReason = rejectionReason, updatedAt = System.currentTimeMillis())
        db.templateDao().updateTemplate(updated.toEntity())

        // Send notification to creator
        val title = when (status) {
            TemplateStatus.APPROVED -> "Template Approved 🎉"
            TemplateStatus.REJECTED -> "Template Rejected ⚠️"
            TemplateStatus.PENDING -> "Template Under Review"
            TemplateStatus.DRAFT -> "Template Saved as Draft"
        }
        val message = when (status) {
            TemplateStatus.APPROVED -> "'${tmpl.name}' is now live on the Poster Marketplace!"
            TemplateStatus.REJECTED -> "'${tmpl.name}' was rejected. Reason: ${rejectionReason ?: "Quality guidelines"}"
            TemplateStatus.PENDING -> "'${tmpl.name}' has been submitted for admin approval."
            TemplateStatus.DRAFT -> "Draft saved successfully."
        }

        db.notificationDao().insertNotification(
            AppNotification(
                id = UUID.randomUUID().toString(),
                userId = tmpl.creatorId,
                title = title,
                message = message,
                type = status.name
            ).toEntity()
        )
    }

    suspend fun toggleTemplateFeatured(templateId: String, isFeatured: Boolean) {
        val tmpl = db.templateDao().getTemplateById(templateId)?.toDomain() ?: return
        val updated = tmpl.copy(isFeatured = isFeatured, updatedAt = System.currentTimeMillis())
        db.templateDao().updateTemplate(updated.toEntity())
    }

    suspend fun duplicateTemplate(templateId: String, newCreatorId: String, newCreatorName: String): Template? {
        val original = db.templateDao().getTemplateById(templateId)?.toDomain() ?: return null
        val copy = original.copy(
            templateId = "tmpl_" + UUID.randomUUID().toString().take(8),
            creatorId = newCreatorId,
            creatorName = newCreatorName,
            name = "${original.name} (Copy)",
            status = TemplateStatus.DRAFT,
            usageCount = 0,
            favoriteCount = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        db.templateDao().insertTemplate(copy.toEntity())
        return copy
    }

    suspend fun incrementTemplateUsage(templateId: String) {
        val tmpl = db.templateDao().getTemplateById(templateId)?.toDomain() ?: return
        val updated = tmpl.copy(usageCount = tmpl.usageCount + 1)
        db.templateDao().updateTemplate(updated.toEntity())
    }

    // FAVORITES
    fun getFavoriteTemplates(userId: String): Flow<List<Template>> {
        return combine(
            db.favoriteDao().getFavoritesByUser(userId),
            db.templateDao().getAllTemplates()
        ) { favs, tmpls ->
            val favIds = favs.map { it.templateId }.toSet()
            tmpls.map { it.toDomain() }.filter { it.templateId in favIds }
        }.flowOn(Dispatchers.IO)
    }

    fun isFavorite(templateId: String, userId: String): Flow<Boolean> {
        return db.favoriteDao().isFavorite(templateId, userId)
    }

    suspend fun toggleFavorite(templateId: String, userId: String) {
        val tmpl = db.templateDao().getTemplateById(templateId)?.toDomain() ?: return
        try {
            db.favoriteDao().addFavorite(FavoriteEntity(templateId, userId))
            val updated = tmpl.copy(favoriteCount = tmpl.favoriteCount + 1)
            db.templateDao().updateTemplate(updated.toEntity())
        } catch (e: Exception) {
            db.favoriteDao().removeFavorite(templateId, userId)
            val updated = tmpl.copy(favoriteCount = maxOf(0, tmpl.favoriteCount - 1))
            db.templateDao().updateTemplate(updated.toEntity())
        }
    }

    // PROJECTS
    fun getProjectsByUser(userId: String): Flow<List<Project>> {
        return db.projectDao().getProjectsByUser(userId).map { list -> list.map { it.toDomain() } }
    }

    suspend fun getProjectById(projectId: String): Project? {
        return db.projectDao().getProjectById(projectId)?.toDomain()
    }

    suspend fun saveProject(project: Project) {
        db.projectDao().insertProject(project.toEntity())
    }

    suspend fun deleteProject(projectId: String) {
        db.projectDao().deleteProjectById(projectId)
    }

    // NOTIFICATIONS
    fun getNotifications(userId: String): Flow<List<AppNotification>> {
        return db.notificationDao().getNotificationsByUser(userId).map { list -> list.map { it.toDomain() } }
    }

    fun markAllNotificationsAsRead(userId: String) {
        scope.launch {
            db.notificationDao().markAllAsRead(userId)
        }
    }

    suspend fun markNotificationAsRead(id: String) {
        db.notificationDao().markAsRead(id)
    }

    // REPORTS
    fun reportTemplate(templateId: String, templateName: String, reason: String, details: String) {
        val user = _currentUser.value
        val report = TemplateReport(
            reportId = "rep_" + UUID.randomUUID().toString().take(8),
            templateId = templateId,
            templateName = templateName,
            reportedByUserId = user.id,
            reportedByUserName = user.name,
            reason = reason,
            details = details,
            timestamp = System.currentTimeMillis(),
            isResolved = false
        )
        _reports.value = listOf(report) + _reports.value
    }

    fun resolveReport(reportId: String) {
        _reports.value = _reports.value.map {
            if (it.reportId == reportId) it.copy(isResolved = true) else it
        }
    }
}

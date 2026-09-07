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
import com.example.domain.models.TemplateVersion
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

    // Authentication & Current Active User State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow(SampleData.getInitialUsers().first())
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Dark Mode State
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

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

    fun getCachedApprovedTemplates(): List<Template> {
        return SampleData.getInitialTemplates().filter { it.status == TemplateStatus.APPROVED }
    }

    private val _approvedTemplatesCache = MutableStateFlow(getCachedApprovedTemplates())

    init {
        // Seed initial data asynchronously on first run & observe database
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

            // Sync cache with database updates in real time
            db.templateDao().getApprovedTemplates().collect { list ->
                if (list.isNotEmpty()) {
                    _approvedTemplatesCache.value = list.map { it.toDomain() }
                }
            }
        }
    }

    // USER & AUTH METHODS
    fun getPresetUsers(): List<UserProfile> = SampleData.getInitialUsers()

    fun login(user: UserProfile) {
        _currentUser.value = user
        _isLoggedIn.value = true
        scope.launch {
            db.userDao().insertUser(user.toEntity())
        }
    }

    fun loginWithCredentials(email: String, role: UserRole, customName: String? = null): UserProfile {
        val existingPreset = SampleData.getInitialUsers().find { it.email.equals(email.trim(), ignoreCase = true) }
        val user = existingPreset?.copy(role = role) ?: UserProfile(
            id = "user_" + UUID.randomUUID().toString().take(8),
            name = customName?.takeIf { it.isNotBlank() } ?: email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() },
            email = email.trim(),
            role = role,
            isCreatorVerified = role == UserRole.CREATOR,
            bio = if (role == UserRole.CREATOR) "Creator & Template Designer" else "Poster Maker Member"
        )
        login(user)
        return user
    }

    fun registerUser(name: String, email: String, role: UserRole, bio: String = ""): UserProfile {
        val newUser = UserProfile(
            id = "user_" + UUID.randomUUID().toString().take(8),
            name = name.trim(),
            email = email.trim(),
            role = role,
            isCreatorVerified = role == UserRole.CREATOR,
            bio = bio.trim().ifEmpty {
                when (role) {
                    UserRole.CREATOR -> "Visual Designer & Template Artist"
                    UserRole.ADMIN -> "Platform Operations Specialist"
                    UserRole.USER -> "Poster Maker Designer"
                }
            }
        )
        login(newUser)
        return newUser
    }

    fun logout() {
        _isLoggedIn.value = false
    }

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

    fun updateUserProfile(name: String, bio: String, avatarUrl: String? = null) {
        val current = _currentUser.value
        val updated = current.copy(name = name.trim(), bio = bio.trim(), avatarUrl = avatarUrl)
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

    fun getApprovedTemplates(): StateFlow<List<Template>> {
        return _approvedTemplatesCache.asStateFlow()
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
        // Auto-save version before updating existing template
        val existing = db.templateDao().getTemplateById(template.templateId)
        if (existing != null) {
            val latestVersion = db.templateVersionDao().getLatestVersion(template.templateId)
            val nextVersion = (latestVersion?.versionNumber ?: 0) + 1
            val version = TemplateVersion(
                versionId = "ver_${UUID.randomUUID().toString().take(8)}",
                templateId = template.templateId,
                versionNumber = nextVersion,
                elements = existing.toDomain().elements,
                background = existing.toDomain().background,
                editableFields = existing.toDomain().editableFields,
                changedByUserId = _currentUser.value.id,
                changedByUserName = _currentUser.value.name,
                changeNote = "Auto-saved version $nextVersion"
            )
            db.templateVersionDao().insertVersion(version.toEntity())
        }
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
            TemplateStatus.APPROVED, TemplateStatus.PUBLISHED -> "Template Approved 🎉"
            TemplateStatus.REJECTED -> "Template Rejected ⚠️"
            TemplateStatus.PENDING -> "Template Under Review"
            TemplateStatus.DRAFT -> "Template Saved as Draft"
            TemplateStatus.ARCHIVED -> "Template Archived"
        }
        val message = when (status) {
            TemplateStatus.APPROVED, TemplateStatus.PUBLISHED -> "'${tmpl.name}' is now live on the Poster Marketplace!"
            TemplateStatus.REJECTED -> "'${tmpl.name}' was rejected. Reason: ${rejectionReason ?: "Quality guidelines"}"
            TemplateStatus.PENDING -> "'${tmpl.name}' has been submitted for admin approval."
            TemplateStatus.DRAFT -> "Draft saved successfully."
            TemplateStatus.ARCHIVED -> "'${tmpl.name}' has been archived."
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

    suspend fun renameProject(projectId: String, newName: String) {
        val proj = db.projectDao().getProjectById(projectId)?.toDomain() ?: return
        val updated = proj.copy(projectName = newName.trim(), updatedAt = System.currentTimeMillis())
        db.projectDao().insertProject(updated.toEntity())
    }

    suspend fun duplicateProject(projectId: String): Project? {
        val original = db.projectDao().getProjectById(projectId)?.toDomain() ?: return null
        val copy = original.copy(
            projectId = "proj_" + UUID.randomUUID().toString().take(8),
            projectName = "${original.projectName} (Copy)",
            isDraft = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        db.projectDao().insertProject(copy.toEntity())
        return copy
    }

    suspend fun updateProjectStatus(projectId: String, isDraft: Boolean) {
        val proj = db.projectDao().getProjectById(projectId)?.toDomain() ?: return
        val updated = proj.copy(isDraft = isDraft, updatedAt = System.currentTimeMillis())
        db.projectDao().insertProject(updated.toEntity())
    }

    suspend fun deleteProject(projectId: String) {
        db.projectDao().deleteProjectById(projectId)
    }

    fun getPopularCreators(): List<UserProfile> = listOf(
        UserProfile(
            id = "creator_studio_x",
            name = "Studio X Designs",
            email = "creator@studiox.io",
            role = UserRole.CREATOR,
            isCreatorVerified = true,
            bio = "Minimalist event & branding posters",
            totalTemplatesCreated = 18,
            totalDownloads = 4850
        ),
        UserProfile(
            id = "creator_brand_pro",
            name = "BrandPro Agency",
            email = "contact@brandpro.co",
            role = UserRole.CREATOR,
            isCreatorVerified = true,
            bio = "Corporate keynotes & business flyers",
            totalTemplatesCreated = 12,
            totalDownloads = 3200
        ),
        UserProfile(
            id = "creator_design_lab",
            name = "DesignLab Co",
            email = "lab@designlab.dev",
            role = UserRole.CREATOR,
            isCreatorVerified = true,
            bio = "Food, cafe & festival promo graphics",
            totalTemplatesCreated = 15,
            totalDownloads = 3910
        )
    )

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

    // TEMPLATE VERSIONING
    fun getTemplateVersions(templateId: String): Flow<List<TemplateVersion>> {
        return db.templateVersionDao().getVersionsByTemplate(templateId).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getLatestTemplateVersion(templateId: String): TemplateVersion? {
        return db.templateVersionDao().getLatestVersion(templateId)?.toDomain()
    }

    suspend fun restoreTemplateVersion(versionId: String): Template? {
        val versionEntity = db.templateVersionDao().getLatestVersion("") ?: return null
        // Find the specific version
        val allVersions = db.templateVersionDao().getVersionsByTemplate(versionEntity.templateId)
        // This is a simplified restore - in production you'd query by versionId directly
        return null
    }

    suspend fun restoreTemplateToVersion(templateId: String, version: TemplateVersion): Template? {
        val existing = db.templateDao().getTemplateById(templateId)?.toDomain() ?: return null
        // Save current state as a new version before restoring
        val latestVersion = db.templateVersionDao().getLatestVersion(templateId)
        val nextVersion = (latestVersion?.versionNumber ?: 0) + 1
        val currentAsVersion = TemplateVersion(
            versionId = "ver_${UUID.randomUUID().toString().take(8)}",
            templateId = templateId,
            versionNumber = nextVersion,
            elements = existing.elements,
            background = existing.background,
            editableFields = existing.editableFields,
            changedByUserId = _currentUser.value.id,
            changedByUserName = _currentUser.value.name,
            changeNote = "Auto-saved before restore to v${version.versionNumber}"
        )
        db.templateVersionDao().insertVersion(currentAsVersion.toEntity())

        // Restore the template to the selected version
        val restored = existing.copy(
            elements = version.elements,
            background = version.background,
            editableFields = version.editableFields,
            updatedAt = System.currentTimeMillis()
        )
        db.templateDao().updateTemplate(restored.toEntity())
        return restored
    }
}

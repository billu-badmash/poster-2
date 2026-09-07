package com.example.data.local

import com.example.domain.models.AppNotification
import com.example.domain.models.CanvasElement
import com.example.domain.models.EditableField
import com.example.domain.models.PosterBackground
import com.example.domain.models.Project
import com.example.domain.models.Template
import com.example.domain.models.TemplateStatus
import com.example.domain.models.TemplateVersion
import com.example.domain.models.UserProfile
import com.example.domain.models.UserRole
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiHelper {
    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val elementListType = Types.newParameterizedType(List::class.java, CanvasElement::class.java)
    val elementListAdapter = moshi.adapter<List<CanvasElement>>(elementListType)

    val fieldListType = Types.newParameterizedType(List::class.java, EditableField::class.java)
    val fieldListAdapter = moshi.adapter<List<EditableField>>(fieldListType)

    val backgroundAdapter = moshi.adapter(PosterBackground::class.java)

    val stringMapType = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
    val stringMapAdapter = moshi.adapter<Map<String, String>>(stringMapType)

    val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
    val stringListAdapter = moshi.adapter<List<String>>(stringListType)
}

fun Template.toEntity(): TemplateEntity {
    return TemplateEntity(
        templateId = templateId,
        creatorId = creatorId,
        creatorName = creatorName,
        name = name,
        description = description,
        categoryId = categoryId,
        categoryName = categoryName,
        thumbnailUrl = thumbnailUrl,
        canvasWidth = canvasWidth,
        canvasHeight = canvasHeight,
        backgroundJson = MoshiHelper.backgroundAdapter.toJson(background),
        elementsJson = MoshiHelper.elementListAdapter.toJson(elements),
        editableFieldsJson = MoshiHelper.fieldListAdapter.toJson(editableFields),
        status = status.name,
        rejectionReason = rejectionReason,
        isFeatured = isFeatured,
        isPro = isPro,
        usageCount = usageCount,
        favoriteCount = favoriteCount,
        rating = rating,
        tagsJson = MoshiHelper.stringListAdapter.toJson(tags),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun TemplateEntity.toDomain(): Template {
    val bg = try {
        MoshiHelper.backgroundAdapter.fromJson(backgroundJson) ?: PosterBackground()
    } catch (e: Exception) {
        PosterBackground()
    }
    val elemList = try {
        MoshiHelper.elementListAdapter.fromJson(elementsJson) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
    val fields = try {
        MoshiHelper.fieldListAdapter.fromJson(editableFieldsJson) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
    val tagList = try {
        MoshiHelper.stringListAdapter.fromJson(tagsJson) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
    val tmplStatus = try {
        TemplateStatus.valueOf(status)
    } catch (e: Exception) {
        TemplateStatus.APPROVED
    }

    return Template(
        templateId = templateId,
        creatorId = creatorId,
        creatorName = creatorName,
        name = name,
        description = description,
        categoryId = categoryId,
        categoryName = categoryName,
        thumbnailUrl = thumbnailUrl,
        canvasWidth = canvasWidth,
        canvasHeight = canvasHeight,
        background = bg,
        elements = elemList,
        editableFields = fields,
        status = tmplStatus,
        rejectionReason = rejectionReason,
        isFeatured = isFeatured,
        isPro = isPro,
        usageCount = usageCount,
        favoriteCount = favoriteCount,
        rating = rating,
        tags = tagList,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Project.toEntity(): ProjectEntity {
    return ProjectEntity(
        projectId = projectId,
        userId = userId,
        templateId = templateId,
        templateName = templateName,
        projectName = projectName,
        canvasWidth = canvasWidth,
        canvasHeight = canvasHeight,
        backgroundJson = MoshiHelper.backgroundAdapter.toJson(background),
        elementsJson = MoshiHelper.elementListAdapter.toJson(elements),
        fieldValuesJson = MoshiHelper.stringMapAdapter.toJson(fieldValues),
        previewThumbnail = previewThumbnail,
        isDraft = isDraft,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ProjectEntity.toDomain(): Project {
    val bg = try {
        MoshiHelper.backgroundAdapter.fromJson(backgroundJson) ?: PosterBackground()
    } catch (e: Exception) {
        PosterBackground()
    }
    val elemList = try {
        MoshiHelper.elementListAdapter.fromJson(elementsJson) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
    val fields = try {
        MoshiHelper.stringMapAdapter.fromJson(fieldValuesJson) ?: emptyMap()
    } catch (e: Exception) {
        emptyMap()
    }

    return Project(
        projectId = projectId,
        userId = userId,
        templateId = templateId,
        templateName = templateName,
        projectName = projectName,
        canvasWidth = canvasWidth,
        canvasHeight = canvasHeight,
        background = bg,
        elements = elemList,
        fieldValues = fields,
        previewThumbnail = previewThumbnail,
        isDraft = isDraft,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun UserProfile.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        role = role.name,
        avatarUrl = avatarUrl,
        isCreatorVerified = isCreatorVerified,
        isSuspended = isSuspended,
        bio = bio,
        totalTemplatesCreated = totalTemplatesCreated,
        totalDownloads = totalDownloads
    )
}

fun UserEntity.toDomain(): UserProfile {
    val userRole = try {
        UserRole.valueOf(role)
    } catch (e: Exception) {
        UserRole.USER
    }
    return UserProfile(
        id = id,
        name = name,
        email = email,
        role = userRole,
        avatarUrl = avatarUrl,
        isCreatorVerified = isCreatorVerified,
        isSuspended = isSuspended,
        bio = bio,
        totalTemplatesCreated = totalTemplatesCreated,
        totalDownloads = totalDownloads
    )
}

fun AppNotification.toEntity(): NotificationEntity {
    return NotificationEntity(
        id = id,
        userId = userId,
        title = title,
        message = message,
        type = type,
        timestamp = timestamp,
        isRead = isRead
    )
}

fun NotificationEntity.toDomain(): AppNotification {
    return AppNotification(
        id = id,
        userId = userId,
        title = title,
        message = message,
        type = type,
        timestamp = timestamp,
        isRead = isRead
    )
}

fun TemplateVersion.toEntity(): TemplateVersionEntity {
    return TemplateVersionEntity(
        versionId = versionId,
        templateId = templateId,
        versionNumber = versionNumber,
        elementsJson = MoshiHelper.elementListAdapter.toJson(elements),
        backgroundJson = MoshiHelper.backgroundAdapter.toJson(background),
        editableFieldsJson = MoshiHelper.fieldListAdapter.toJson(editableFields),
        changedByUserId = changedByUserId,
        changedByUserName = changedByUserName,
        changeNote = changeNote,
        createdAt = createdAt
    )
}

fun TemplateVersionEntity.toDomain(): TemplateVersion {
    val elemList = try {
        MoshiHelper.elementListAdapter.fromJson(elementsJson) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
    val bg = try {
        MoshiHelper.backgroundAdapter.fromJson(backgroundJson) ?: PosterBackground()
    } catch (e: Exception) {
        PosterBackground()
    }
    val fields = try {
        MoshiHelper.fieldListAdapter.fromJson(editableFieldsJson) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
    return TemplateVersion(
        versionId = versionId,
        templateId = templateId,
        versionNumber = versionNumber,
        elements = elemList,
        background = bg,
        editableFields = fields,
        changedByUserId = changedByUserId,
        changedByUserName = changedByUserName,
        changeNote = changeNote,
        createdAt = createdAt
    )
}

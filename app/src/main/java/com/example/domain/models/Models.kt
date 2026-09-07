package com.example.domain.models

enum class UserRole {
    USER,
    CREATOR,
    ADMIN
}

enum class ExportQuality(val label: String, val description: String, val scaleFactor: Float) {
    STANDARD("Standard (540p)", "Fast export, smaller file", 0.5f),
    HIGH("High (1080p)", "Best balance of quality & size", 1.0f),
    MAX_4K("Max (4K)", "Highest quality, large file", 2.0f)
}

enum class TemplateStatus {
    DRAFT,
    PENDING,
    APPROVED,
    PUBLISHED,
    REJECTED,
    ARCHIVED
}

enum class TemplatePermissionLevel(val label: String, val badgeColorHex: String, val description: String) {
    BASIC("Basic Editing", "#059669", "Customize dynamic form text fields and replace photos."),
    FLEXIBLE("Flexible Editing", "#2563EB", "Edit text, change fonts & colors, move select items, and customize background."),
    FULLY_EDITABLE("Fully Editable", "#7C3AED", "Full creative freedom to add, move, reorder, and redesign all elements.")
}

data class ElementPermissions(
    val editableText: Boolean = true,
    val changeFont: Boolean = true,
    val changeColor: Boolean = true,
    val move: Boolean = true,
    val resize: Boolean = true,
    val rotate: Boolean = true,
    val delete: Boolean = true
)

enum class ProjectStatus(val label: String) {
    DRAFT("Draft"),
    COMPLETED("Completed")
}

enum class ElementType {
    TEXT,
    IMAGE,
    SHAPE
}

enum class ShapeType {
    RECTANGLE,
    ROUNDED_RECT,
    CIRCLE,
    LINE,
    BADGE
}

enum class FieldType {
    TEXT,
    PHONE,
    EMAIL,
    DATE,
    TIME,
    PRICE,
    WEBSITE,
    ADDRESS,
    IMAGE,
    LOGO
}

enum class BackgroundType {
    SOLID,
    GRADIENT_LINEAR,
    GRADIENT_RADIAL,
    PATTERN,
    IMAGE
}

enum class CanvasAspectRatio(val label: String, val width: Int, val height: Int, val ratioStr: String) {
    SQUARE("Instagram Post / Square", 1080, 1080, "1:1"),
    STORY("Instagram Story / WhatsApp", 1080, 1920, "9:16"),
    FLYER("Flyer / A4 Document", 1240, 1754, "1:1.41"),
    LANDSCAPE_BANNER("Facebook / Banner", 1200, 630, "1.9:1"),
    PORTRAIT_POSTER("Poster / Vertical", 1080, 1350, "4:5")
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String? = null,
    val isCreatorVerified: Boolean = false,
    val isSuspended: Boolean = false,
    val bio: String = "",
    val totalTemplatesCreated: Int = 0,
    val totalDownloads: Int = 0
)

data class EditableField(
    val fieldId: String,
    val elementId: String,
    val type: FieldType = FieldType.TEXT,
    val label: String,
    val placeholder: String = "",
    val required: Boolean = true,
    val defaultValue: String = "",
    val maxLength: Int = 100,
    val aspectRatio: Float? = null // For images/logos
)

data class CanvasElement(
    val id: String,
    val type: ElementType,
    // Relative position (0.0 to 1.0) for responsive rendering across screen sizes
    val xRatio: Float = 0.1f,
    val yRatio: Float = 0.1f,
    val widthRatio: Float = 0.8f,
    val heightRatio: Float = 0.15f,
    val rotation: Float = 0f,
    val opacity: Float = 1f,
    val layerOrder: Int = 0,
    val isLocked: Boolean = false,
    val isVisible: Boolean = true,
    val permissions: ElementPermissions = ElementPermissions(),
    
    // Editable field link
    val isEditableField: Boolean = false,
    val editableFieldId: String? = null,

    // Text specific properties
    val text: String = "Your Text Here",
    val fontSizeSp: Float = 22f,
    val fontFamilyName: String = "Default",
    val fontColorHex: String = "#111827",
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val textAlign: String = "CENTER", // LEFT, CENTER, RIGHT
    val letterSpacingSp: Float = 0f,
    val hasShadow: Boolean = false,
    val shadowColorHex: String = "#80000000",
    val hasStroke: Boolean = false,
    val strokeColorHex: String = "#000000",

    // Image specific properties
    val imageUrl: String? = null,
    val localUri: String? = null,
    val cornerRadiusDp: Float = 0f,
    val borderWidthDp: Float = 0f,
    val borderColorHex: String = "#000000",

    // Shape specific properties
    val shapeType: ShapeType = ShapeType.RECTANGLE,
    val fillColorHex: String = "#3B82F6",
    val strokeWidthDp: Float = 0f,
    val shapeCornerRadiusDp: Float = 12f
)

data class PosterBackground(
    val type: BackgroundType = BackgroundType.SOLID,
    val color1Hex: String = "#FFFFFF",
    val color2Hex: String = "#F3F4F6",
    val angleDegrees: Float = 135f,
    val imageUrl: String? = null,
    val patternType: String? = null
)

data class Template(
    val templateId: String,
    val creatorId: String,
    val creatorName: String,
    val name: String,
    val description: String,
    val categoryId: String,
    val categoryName: String,
    val thumbnailUrl: String = "",
    val canvasWidth: Int = 1080,
    val canvasHeight: Int = 1080,
    val background: PosterBackground = PosterBackground(),
    val elements: List<CanvasElement> = emptyList(),
    val editableFields: List<EditableField> = emptyList(),
    val permissionLevel: TemplatePermissionLevel = TemplatePermissionLevel.FLEXIBLE,
    val status: TemplateStatus = TemplateStatus.APPROVED,
    val rejectionReason: String? = null,
    val isFeatured: Boolean = false,
    val isPro: Boolean = false,
    val usageCount: Int = 0,
    val favoriteCount: Int = 0,
    val rating: Float = 4.8f,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Project(
    val projectId: String,
    val userId: String,
    val templateId: String,
    val templateName: String,
    val projectName: String,
    val canvasWidth: Int = 1080,
    val canvasHeight: Int = 1080,
    val background: PosterBackground = PosterBackground(),
    val elements: List<CanvasElement> = emptyList(),
    val fieldValues: Map<String, String> = emptyMap(),
    val previewThumbnail: String? = null,
    val isDraft: Boolean = false,
    val projectStatus: ProjectStatus = if (isDraft) ProjectStatus.DRAFT else ProjectStatus.COMPLETED,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Category(
    val id: String,
    val name: String,
    val iconName: String,
    val description: String,
    val templateCount: Int = 0,
    val colorHex: String = "#111827"
)

data class TemplateReport(
    val reportId: String,
    val templateId: String,
    val templateName: String,
    val reportedByUserId: String,
    val reportedByUserName: String,
    val reason: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false
)

data class AppNotification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // APPROVAL, REJECTION, FEATURED, SYSTEM
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class TemplateVersion(
    val versionId: String,
    val templateId: String,
    val versionNumber: Int,
    val elements: List<CanvasElement> = emptyList(),
    val background: PosterBackground = PosterBackground(),
    val editableFields: List<EditableField> = emptyList(),
    val changedByUserId: String,
    val changedByUserName: String,
    val changeNote: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

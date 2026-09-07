package com.example.ui.editor

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DynamicForm
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.AlignHorizontalCenter
import androidx.compose.material.icons.filled.AlignHorizontalLeft
import androidx.compose.material.icons.filled.AlignHorizontalRight
import androidx.compose.material.icons.filled.AlignVerticalCenter
import androidx.compose.material.icons.filled.AlignVerticalTop
import androidx.compose.material.icons.filled.AlignVerticalBottom
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.WidthNormal
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.repository.PosterRepository
import com.example.domain.models.BackgroundType
import com.example.domain.models.CanvasElement
import com.example.domain.models.EditableField
import com.example.domain.models.ElementType
import com.example.domain.models.FieldType
import com.example.domain.models.PosterBackground
import com.example.domain.models.Project
import com.example.domain.models.CanvasAspectRatio
import com.example.domain.models.ShapeType
import com.example.domain.models.Template
import com.example.domain.models.TemplateStatus
import com.example.domain.models.UserRole
import com.example.ui.components.PosterCanvasView
import com.example.ui.theme.*
import com.example.utils.BackgroundPresets
import com.example.utils.CanvasUtils
import com.example.utils.MarketingCopyInspiration
import com.example.utils.SmartAlignmentHelper
import com.example.utils.StickerAssetHelper
import com.example.utils.StickerCategory
import com.example.utils.StickerPreset
import com.example.utils.TypographyHelper
import kotlinx.coroutines.launch
import java.util.UUID

enum class EditorTab(val label: String) {
    ELEMENTS("Add"),
    STYLE("Style"),
    BACKGROUND("Background"),
    LAYERS("Layers"),
    DYNAMIC_FIELDS("Field Config")
}

enum class ColorPickerTarget {
    TEXT_COLOR,
    TEXT_STROKE_COLOR,
    TEXT_SHADOW_COLOR,
    IMAGE_BORDER_COLOR,
    SHAPE_FILL,
    BACKGROUND_COLOR1,
    BACKGROUND_COLOR2
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosterEditorScreen(
    templateId: String?,
    projectId: String?,
    repository: PosterRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState()

    var posterTitle by remember { mutableStateOf("Untitled Poster") }
    var canvasWidth by remember { mutableStateOf(1080) }
    var canvasHeight by remember { mutableStateOf(1080) }
    var selectedAspectRatio by remember { mutableStateOf(CanvasAspectRatio.SQUARE) }
    var showAspectRatioMenu by remember { mutableStateOf(false) }
    var background by remember { mutableStateOf(PosterBackground()) }
    val elements = remember { mutableStateListOf<CanvasElement>() }
    val editableFields = remember { mutableStateListOf<EditableField>() }
    val extractedPalette = remember { mutableStateListOf<String>() }

    var selectedElementId by remember { mutableStateOf<String?>(null) }
    var activeTab by remember { mutableStateOf(EditorTab.ELEMENTS) }
    var smartGuidesEnabled by remember { mutableStateOf(true) }

    var showColorPickerDialog by remember { mutableStateOf(false) }
    var colorPickerTarget by remember { mutableStateOf<ColorPickerTarget?>(null) }
    var initialPickerColor by remember { mutableStateOf("#FFFFFF") }
    var showExportDialog by remember { mutableStateOf(false) }

    // Undo / Redo history stacks
    val undoStack = remember { mutableStateListOf<List<CanvasElement>>() }
    val redoStack = remember { mutableStateListOf<List<CanvasElement>>() }
    // Track which element drag just started (to avoid pushing history on every drag frame)
    var dragHistoryPushedForId by remember { mutableStateOf<String?>(null) }

    fun pushHistory() {
        undoStack.add(elements.map { it.copy() })
        redoStack.clear()
        if (undoStack.size > 20) undoStack.removeAt(0)
    }

    LaunchedEffect(templateId, projectId) {
        if (projectId != null) {
            val proj = repository.getProjectById(projectId)
            if (proj != null) {
                posterTitle = proj.projectName
                canvasWidth = proj.canvasWidth
                canvasHeight = proj.canvasHeight
                background = proj.background
                elements.clear()
                elements.addAll(proj.elements)
            }
        } else if (templateId != null) {
            val tmpl = repository.getTemplateById(templateId)
            if (tmpl != null) {
                posterTitle = "${tmpl.name} (Custom)"
                canvasWidth = tmpl.canvasWidth
                canvasHeight = tmpl.canvasHeight
                background = tmpl.background
                elements.clear()
                elements.addAll(tmpl.elements)
                editableFields.clear()
                editableFields.addAll(tmpl.editableFields)
            }
        } else {
            // New blank canvas
            posterTitle = "My New Poster"
            elements.clear()
            // Add initial title element
            elements.add(
                CanvasElement(
                    id = "el_" + UUID.randomUUID().toString().take(6),
                    type = ElementType.TEXT,
                    text = "HEADING TITLE",
                    fontSizeSp = 28f,
                    fontColorHex = "#111827",
                    isBold = true,
                    xRatio = 0.1f,
                    yRatio = 0.15f,
                    widthRatio = 0.8f,
                    heightRatio = 0.1f,
                    layerOrder = 1
                )
            )
        }
    }

    val selectedElement = elements.find { it.id == selectedElementId }
    var showRenamePosterDialog by remember { mutableStateOf(false) }
    var editingTitleInput by remember { mutableStateOf("") }

    // Image picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { pickedUri ->
            pushHistory()
            val newId = "el_" + UUID.randomUUID().toString().take(6)
            elements.add(
                CanvasElement(
                    id = newId,
                    type = ElementType.IMAGE,
                    localUri = pickedUri.toString(),
                    xRatio = 0.15f,
                    yRatio = 0.25f,
                    widthRatio = 0.7f,
                    heightRatio = 0.35f,
                    cornerRadiusDp = 16f,
                    layerOrder = elements.size + 1
                )
            )
            selectedElementId = newId
            activeTab = EditorTab.STYLE

            scope.launch {
                val colors = CanvasUtils.extractColorPalette(context, pickedUri)
                if (colors.isNotEmpty()) {
                    extractedPalette.clear()
                    extractedPalette.addAll(colors)
                }
            }
        }
    }

    if (showRenamePosterDialog) {
        Dialog(onDismissRequest = { showRenamePosterDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Rename Poster", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editingTitleInput,
                        onValueChange = { editingTitleInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimaryLight,
                            unfocusedTextColor = TextPrimaryLight,
                            focusedContainerColor = SurfaceLight,
                            unfocusedContainerColor = SurfaceLight,
                            focusedBorderColor = PrimaryBlack,
                            unfocusedBorderColor = BorderLight,
                            cursorColor = PrimaryBlack
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showRenamePosterDialog = false }) {
                            Text("Cancel", color = TextSecondaryLight)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (editingTitleInput.isNotBlank()) {
                                    posterTitle = editingTitleInput.trim()
                                }
                                showRenamePosterDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                        ) {
                            Text("Update Name")
                        }
                    }
                }
            }
        }
    }

    if (showColorPickerDialog) {
        ColorPickerDialog(
            initialColorHex = initialPickerColor,
            onColorSelected = { hex ->
                when (colorPickerTarget) {
                    ColorPickerTarget.TEXT_COLOR -> {
                        selectedElement?.let { el ->
                            val updated = el.copy(fontColorHex = hex)
                            val idx = elements.indexOfFirst { it.id == el.id }
                            if (idx != -1) {
                                pushHistory()
                                elements[idx] = updated
                            }
                        }
                    }
                    ColorPickerTarget.TEXT_STROKE_COLOR -> {
                        selectedElement?.let { el ->
                            val updated = el.copy(strokeColorHex = hex, hasStroke = true)
                            val idx = elements.indexOfFirst { it.id == el.id }
                            if (idx != -1) {
                                pushHistory()
                                elements[idx] = updated
                            }
                        }
                    }
                    ColorPickerTarget.TEXT_SHADOW_COLOR -> {
                        selectedElement?.let { el ->
                            val updated = el.copy(shadowColorHex = hex, hasShadow = true)
                            val idx = elements.indexOfFirst { it.id == el.id }
                            if (idx != -1) {
                                pushHistory()
                                elements[idx] = updated
                            }
                        }
                    }
                    ColorPickerTarget.IMAGE_BORDER_COLOR -> {
                        selectedElement?.let { el ->
                            val updated = el.copy(borderColorHex = hex)
                            val idx = elements.indexOfFirst { it.id == el.id }
                            if (idx != -1) {
                                pushHistory()
                                elements[idx] = updated
                            }
                        }
                    }
                    ColorPickerTarget.SHAPE_FILL -> {
                        selectedElement?.let { el ->
                            val updated = el.copy(fillColorHex = hex)
                            val idx = elements.indexOfFirst { it.id == el.id }
                            if (idx != -1) {
                                pushHistory()
                                elements[idx] = updated
                            }
                        }
                    }
                    ColorPickerTarget.BACKGROUND_COLOR1 -> {
                        background = background.copy(color1Hex = hex)
                    }
                    ColorPickerTarget.BACKGROUND_COLOR2 -> {
                        background = background.copy(color2Hex = hex)
                    }
                    null -> {}
                }
            },
            onDismiss = { showColorPickerDialog = false }
        )
    }

    if (showExportDialog) {
        ExportDialog(
            posterTitle = posterTitle,
            canvasWidth = canvasWidth,
            canvasHeight = canvasHeight,
            background = background,
            elements = elements.toList(),
            onDismiss = { showExportDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.clickable {
                            editingTitleInput = posterTitle
                            showRenamePosterDialog = true
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = posterTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Rename",
                                tint = AccentBlue,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "${canvasWidth}x${canvasHeight} • ${elements.size} layers (Tap to rename)",
                            fontSize = 10.sp,
                            color = TextTertiaryLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("editor_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Undo
                    IconButton(
                        onClick = {
                            if (undoStack.isNotEmpty()) {
                                redoStack.add(elements.map { it.copy() })
                                val prev = undoStack.removeAt(undoStack.lastIndex)
                                elements.clear()
                                elements.addAll(prev)
                            }
                        },
                        enabled = undoStack.isNotEmpty()
                    ) {
                        Icon(
                            Icons.Default.Undo,
                            contentDescription = "Undo",
                            tint = if (undoStack.isNotEmpty()) TextPrimaryLight else Color(0xFFD1D5DB)
                        )
                    }

                    // Redo
                    IconButton(
                        onClick = {
                            if (redoStack.isNotEmpty()) {
                                undoStack.add(elements.map { it.copy() })
                                val next = redoStack.removeAt(redoStack.lastIndex)
                                elements.clear()
                                elements.addAll(next)
                            }
                        },
                        enabled = redoStack.isNotEmpty()
                    ) {
                        Icon(
                            Icons.Default.Redo,
                            contentDescription = "Redo",
                            tint = if (redoStack.isNotEmpty()) TextPrimaryLight else Color(0xFFD1D5DB)
                        )
                    }

                    // Export / Share Studio
                    IconButton(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.padding(end = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Export & Share",
                            tint = AccentBlue
                        )
                    }

                    // Save / Publish
                    Button(
                        onClick = {
                            scope.launch {
                                val projId = projectId ?: ("proj_" + UUID.randomUUID().toString().take(8))
                                val project = Project(
                                    projectId = projId,
                                    userId = currentUser.id,
                                    templateId = templateId ?: "custom",
                                    templateName = posterTitle,
                                    projectName = posterTitle,
                                    canvasWidth = canvasWidth,
                                    canvasHeight = canvasHeight,
                                    background = background,
                                    elements = elements.toList(),
                                    isDraft = false
                                )
                                repository.saveProject(project)

                                // If creator wants to save as a public/draft template:
                                if (currentUser.role == UserRole.CREATOR || currentUser.role == UserRole.ADMIN) {
                                    val tmplId = templateId ?: ("tmpl_" + UUID.randomUUID().toString().take(8))
                                    val tmpl = Template(
                                        templateId = tmplId,
                                        creatorId = currentUser.id,
                                        creatorName = currentUser.name,
                                        name = posterTitle,
                                        description = "Custom designed template by ${currentUser.name}",
                                        categoryId = "cat_business",
                                        categoryName = "Business",
                                        canvasWidth = canvasWidth,
                                        canvasHeight = canvasHeight,
                                        background = background,
                                        elements = elements.toList(),
                                        editableFields = editableFields.toList(),
                                        status = if (currentUser.role == UserRole.ADMIN) TemplateStatus.APPROVED else TemplateStatus.PENDING
                                    )
                                    repository.saveTemplate(tmpl)
                                }

                                Toast.makeText(context, "Saved Successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlack),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. Canvas Work Area with Smart Guides & Formation Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE5E7EB))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Floating smart guides & dimension resizer & quick formation header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Smart Guides toggle pill
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (smartGuidesEnabled) Color(0xFF0F172A) else Color(0xFFFFFFFF))
                                    .border(
                                        1.dp,
                                        if (smartGuidesEnabled) Color(0xFF334155) else Color(0xFFCBD5E1),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { smartGuidesEnabled = !smartGuidesEnabled }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (smartGuidesEnabled) Color(0xFF00E5FF) else Color(0xFF94A3B8))
                                )
                                Text(
                                    text = if (smartGuidesEnabled) "🧲 Guides: ON" else "🧲 Guides: OFF",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (smartGuidesEnabled) Color.White else Color(0xFF475569)
                                )
                            }

                            // Dimension Presets Resizer Pill
                            Box {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(20.dp))
                                        .clickable { showAspectRatioMenu = true }
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "📐 ${selectedAspectRatio.ratioStr}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showAspectRatioMenu,
                                    onDismissRequest = { showAspectRatioMenu = false }
                                ) {
                                    CanvasAspectRatio.values().forEach { aspect ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(
                                                        "${aspect.ratioStr} • ${aspect.label}",
                                                        fontSize = 12.sp,
                                                        fontWeight = if (selectedAspectRatio == aspect) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                    Text(
                                                        "${aspect.width} x ${aspect.height} px",
                                                        fontSize = 10.sp,
                                                        color = Color.Gray
                                                    )
                                                }
                                            },
                                            onClick = {
                                                selectedAspectRatio = aspect
                                                canvasWidth = aspect.width
                                                canvasHeight = aspect.height
                                                showAspectRatioMenu = false
                                                Toast.makeText(context, "Resized to ${aspect.label}", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Quick formation pills for selected element
                        if (selectedElement != null) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CanvasQuickActionButton(
                                    label = "Duplicate",
                                    icon = Icons.Default.ContentCopy,
                                    onClick = {
                                        pushHistory()
                                        val cloned = selectedElement.copy(
                                            id = "el_" + UUID.randomUUID().toString().take(6),
                                            xRatio = (selectedElement.xRatio + 0.04f).coerceAtMost(0.85f),
                                            yRatio = (selectedElement.yRatio + 0.04f).coerceAtMost(0.85f),
                                            layerOrder = elements.size + 1
                                        )
                                        elements.add(cloned)
                                        selectedElementId = cloned.id
                                    }
                                )
                                CanvasQuickActionButton(
                                    label = "Center",
                                    icon = Icons.Default.CenterFocusStrong,
                                    onClick = {
                                        pushHistory()
                                        val idx = elements.indexOfFirst { it.id == selectedElement.id }
                                        if (idx != -1) {
                                            elements[idx] = SmartAlignmentHelper.centerCanvas(selectedElement)
                                        }
                                    }
                                )
                                CanvasQuickActionButton(
                                    label = "Fit Width",
                                    icon = Icons.Default.WidthNormal,
                                    onClick = {
                                        pushHistory()
                                        val idx = elements.indexOfFirst { it.id == selectedElement.id }
                                        if (idx != -1) {
                                            elements[idx] = SmartAlignmentHelper.matchCanvasWidth(selectedElement)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Canvas View
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        PosterCanvasView(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            canvasWidth = canvasWidth,
                            canvasHeight = canvasHeight,
                            background = background,
                            elements = elements,
                            selectedElementId = selectedElementId,
                            isInteractive = true,
                            smartGuidesEnabled = smartGuidesEnabled,
                            onElementSelected = { id ->
                                selectedElementId = id
                                if (id != null) {
                                    activeTab = EditorTab.STYLE
                                }
                                // Reset drag history tracker on new selection
                                dragHistoryPushedForId = null
                            },
                            onElementMoved = { elId, newX, newY ->
                                // Push history only ONCE per drag gesture (first move event)
                                if (dragHistoryPushedForId != elId) {
                                    pushHistory()
                                    dragHistoryPushedForId = elId
                                }
                                val idx = elements.indexOfFirst { it.id == elId }
                                if (idx != -1) {
                                    elements[idx] = elements[idx].copy(xRatio = newX, yRatio = newY)
                                }
                            },
                            onElementResized = { elId, newW, newH ->
                                // Push history only ONCE per drag gesture
                                if (dragHistoryPushedForId != elId) {
                                    pushHistory()
                                    dragHistoryPushedForId = elId
                                }
                                val idx = elements.indexOfFirst { it.id == elId }
                                if (idx != -1) {
                                    elements[idx] = elements[idx].copy(widthRatio = newW, heightRatio = newH)
                                }
                            },
                            onBackgroundClick = {
                                selectedElementId = null
                                activeTab = EditorTab.BACKGROUND
                            }
                        )
                    }
                }
            }

            // 2. Tab Navigation Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceLight,
                shadowElevation = 4.dp
            ) {
                TabRow(
                    selectedTabIndex = activeTab.ordinal,
                    containerColor = SurfaceLight,
                    contentColor = PrimaryBlack
                ) {
                    EditorTab.values().forEach { tab ->
                        Tab(
                            selected = activeTab == tab,
                            onClick = { activeTab = tab },
                            text = {
                                Text(
                                    text = tab.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (activeTab == tab) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            // 3. Tab Content Bottom Panel
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp),
                color = SurfaceLight
            ) {
                when (activeTab) {
                    EditorTab.ELEMENTS -> {
                        ElementsTabContent(
                            onAddText = {
                                pushHistory()
                                val newId = "el_" + UUID.randomUUID().toString().take(6)
                                elements.add(
                                    CanvasElement(
                                        id = newId,
                                        type = ElementType.TEXT,
                                        text = "New Text Block",
                                        fontSizeSp = 18f,
                                        fontColorHex = "#111827",
                                        xRatio = 0.15f,
                                        yRatio = 0.4f,
                                        widthRatio = 0.7f,
                                        heightRatio = 0.08f,
                                        layerOrder = elements.size + 1
                                    )
                                )
                                selectedElementId = newId
                                activeTab = EditorTab.STYLE
                            },
                            onAddShape = { shape ->
                                pushHistory()
                                val newId = "el_" + UUID.randomUUID().toString().take(6)
                                elements.add(
                                    CanvasElement(
                                        id = newId,
                                        type = ElementType.SHAPE,
                                        shapeType = shape,
                                        fillColorHex = "#3B82F6",
                                        xRatio = 0.2f,
                                        yRatio = 0.45f,
                                        widthRatio = 0.6f,
                                        heightRatio = 0.1f,
                                        layerOrder = elements.size + 1
                                    )
                                )
                                selectedElementId = newId
                                activeTab = EditorTab.STYLE
                            },
                            onAddImagePlaceholder = {
                                galleryLauncher.launch("image/*")
                            },
                            onAddSticker = { stickerElements ->
                                pushHistory()
                                val startOrder = elements.size + 1
                                val placed = stickerElements.mapIndexed { idx, el -> el.copy(layerOrder = startOrder + idx) }
                                elements.addAll(placed)
                                selectedElementId = placed.lastOrNull()?.id
                                activeTab = EditorTab.STYLE
                            },
                            onApplySmartLayout = { layoutElements ->
                                pushHistory()
                                elements.clear()
                                elements.addAll(layoutElements)
                                selectedElementId = layoutElements.firstOrNull()?.id
                                Toast.makeText(context, "Smart Layout Applied!", Toast.LENGTH_SHORT).show()
                            },
                            onAddInspiration = { inspiration ->
                                pushHistory()
                                val startOrder = elements.size + 1
                                val inspirationElements = TypographyHelper.createInspirationElements(inspiration, startLayer = startOrder)
                                elements.addAll(inspirationElements)
                                selectedElementId = inspirationElements.lastOrNull()?.id
                                activeTab = EditorTab.STYLE
                                Toast.makeText(context, "Added '${inspiration.title}'!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    EditorTab.STYLE -> {
                        if (selectedElement == null) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Select an element on canvas to customize", color = TextSecondaryLight, fontSize = 13.sp)
                            }
                        } else {
                            ElementStyleInspector(
                                element = selectedElement,
                                totalElements = elements.toList(),
                                extractedPalette = extractedPalette.toList(),
                                onUpdate = { updated ->
                                    pushHistory()
                                    val idx = elements.indexOfFirst { it.id == updated.id }
                                    if (idx != -1) {
                                        elements[idx] = updated
                                    }
                                },
                                onDistributeV = {
                                    pushHistory()
                                    val updated = SmartAlignmentHelper.distributeVertically(elements.toList())
                                    elements.clear()
                                    elements.addAll(updated)
                                },
                                onDistributeH = {
                                    pushHistory()
                                    val updated = SmartAlignmentHelper.distributeHorizontally(elements.toList())
                                    elements.clear()
                                    elements.addAll(updated)
                                },
                                onPickCustomColor = { target, initial ->
                                    initialPickerColor = initial
                                    colorPickerTarget = target
                                    showColorPickerDialog = true
                                },
                                onDelete = {
                                    pushHistory()
                                    elements.removeAll { it.id == selectedElement.id }
                                    selectedElementId = null
                                }
                            )
                        }
                    }

                    EditorTab.BACKGROUND -> {
                        BackgroundTabContent(
                            currentBg = background,
                            extractedPalette = extractedPalette.toList(),
                            onPickCustomColor = { target, initial ->
                                initialPickerColor = initial
                                colorPickerTarget = target
                                showColorPickerDialog = true
                            },
                            onUpdateBg = { newBg ->
                                background = newBg
                            }
                        )
                    }

                    EditorTab.LAYERS -> {
                        LayersTabContent(
                            elements = elements,
                            selectedId = selectedElementId,
                            onSelect = { selectedElementId = it },
                            onMoveUp = { el ->
                                pushHistory()
                                val idx = elements.indexOfFirst { it.id == el.id }
                                if (idx > 0) {
                                    val item = elements.removeAt(idx)
                                    elements.add(idx - 1, item)
                                    elements.forEachIndexed { i, e -> elements[i] = e.copy(layerOrder = i) }
                                }
                            },
                            onMoveDown = { el ->
                                pushHistory()
                                val idx = elements.indexOfFirst { it.id == el.id }
                                if (idx < elements.size - 1) {
                                    val item = elements.removeAt(idx)
                                    elements.add(idx + 1, item)
                                    elements.forEachIndexed { i, e -> elements[i] = e.copy(layerOrder = i) }
                                }
                            },
                            onToggleLock = { el ->
                                val idx = elements.indexOfFirst { it.id == el.id }
                                if (idx != -1) elements[idx] = el.copy(isLocked = !el.isLocked)
                            },
                            onToggleVisibility = { el ->
                                val idx = elements.indexOfFirst { it.id == el.id }
                                if (idx != -1) elements[idx] = el.copy(isVisible = !el.isVisible)
                            },
                            onDelete = { el ->
                                pushHistory()
                                elements.removeAll { it.id == el.id }
                                if (selectedElementId == el.id) selectedElementId = null
                            }
                        )
                    }

                    EditorTab.DYNAMIC_FIELDS -> {
                        DynamicFieldConfigurator(
                            selectedElement = selectedElement,
                            editableFields = editableFields,
                            onAddField = { field ->
                                editableFields.removeAll { it.fieldId == field.fieldId || it.elementId == field.elementId }
                                editableFields.add(field)
                                // Mark canvas element as editable
                                val idx = elements.indexOfFirst { it.id == field.elementId }
                                if (idx != -1) {
                                    elements[idx] = elements[idx].copy(
                                        isEditableField = true,
                                        editableFieldId = field.fieldId
                                    )
                                }
                                Toast.makeText(context, "Dynamic field '${field.label}' linked!", Toast.LENGTH_SHORT).show()
                            },
                            onRemoveField = { fieldId ->
                                val removed = editableFields.find { it.fieldId == fieldId }
                                editableFields.removeAll { it.fieldId == fieldId }
                                if (removed != null) {
                                    val idx = elements.indexOfFirst { it.id == removed.elementId }
                                    if (idx != -1) {
                                        elements[idx] = elements[idx].copy(isEditableField = false, editableFieldId = null)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ElementsTabContent(
    onAddText: () -> Unit,
    onAddShape: (ShapeType) -> Unit,
    onAddImagePlaceholder: () -> Unit,
    onAddSticker: (List<CanvasElement>) -> Unit,
    onApplySmartLayout: (List<CanvasElement>) -> Unit,
    onAddInspiration: (MarketingCopyInspiration) -> Unit
) {
    var selectedStickerCategory by remember { mutableStateOf(StickerCategory.SALE_DISCOUNT) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Add Content Elements", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Text Button
            Button(
                onClick = onAddText,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
            ) {
                Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Text", fontSize = 12.sp)
            }

            // Image Button
            OutlinedButton(
                onClick = onAddImagePlaceholder,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Image", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text("Basic Shapes", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLight)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(
                listOf(
                    Pair("Rounded Rect", ShapeType.ROUNDED_RECT),
                    Pair("Rectangle", ShapeType.RECTANGLE),
                    Pair("Circle", ShapeType.CIRCLE),
                    Pair("Line Divider", ShapeType.LINE),
                    Pair("Badge", ShapeType.BADGE)
                )
            ) { (label, shape) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3F4F6))
                        .clickable { onAddShape(shape) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimaryLight)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("🎨 Stickers & Ready Badges", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
        Spacer(modifier = Modifier.height(8.dp))

        // Category filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(StickerCategory.values()) { cat ->
                val isSel = selectedStickerCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSel) PrimaryBlack else Color(0xFFF1F5F9))
                        .clickable { selectedStickerCategory = cat }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) Color.White else Color(0xFF334155)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sticker items for selected category
        val categoryStickers = StickerAssetHelper.presets.filter { it.category == selectedStickerCategory }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(categoryStickers) { sticker ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .clickable { onAddSticker(sticker.elementGenerator()) }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "+ ${sticker.title}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text("💡 1-Tap Marketing Headlines & Quotes", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(TypographyHelper.marketingInspirations) { item ->
                Surface(
                    modifier = Modifier
                        .width(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .clickable { onAddInspiration(item) },
                    color = Color(0xFFF8FAFC)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = item.category,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = item.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlack,
                            maxLines = 1
                        )
                        Text(
                            text = item.subtitle,
                            fontSize = 10.sp,
                            color = TextSecondaryLight,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text("🪄 1-Tap Smart Layout Formations", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEFF6FF))
                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(10.dp))
                    .clickable { onApplySmartLayout(SmartAlignmentHelper.createHeroAnnouncementTemplate()) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("⭐ Hero Event", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFEF2F2))
                    .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(10.dp))
                    .clickable { onApplySmartLayout(SmartAlignmentHelper.createBigSaleTemplate()) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("🔥 Flash Sale", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                    .clickable { onApplySmartLayout(SmartAlignmentHelper.createQuoteCardTemplate()) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("💬 Quote Card", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF5F3FF))
                    .border(1.dp, Color(0xFFDDD6FE), RoundedCornerShape(10.dp))
                    .clickable { onApplySmartLayout(SmartAlignmentHelper.createEventShowcaseTemplate()) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("🗓️ Webinar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D28D9))
            }
        }
    }
}

@Composable
private fun ElementStyleInspector(
    element: CanvasElement,
    totalElements: List<CanvasElement>,
    extractedPalette: List<String> = emptyList(),
    onUpdate: (CanvasElement) -> Unit,
    onDistributeV: (() -> Unit)? = null,
    onDistributeH: (() -> Unit)? = null,
    onPickCustomColor: (ColorPickerTarget, String) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Styling: ${element.type.name}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight
            )

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
            }
        }

        when (element.type) {
            ElementType.TEXT -> {
                OutlinedTextField(
                    value = element.text,
                    onValueChange = { onUpdate(element.copy(text = it)) },
                    label = { Text("Text Content", color = TextSecondaryLight) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimaryLight,
                        unfocusedTextColor = TextPrimaryLight,
                        focusedContainerColor = SurfaceLight,
                        unfocusedContainerColor = SurfaceLight,
                        focusedBorderColor = PrimaryBlack,
                        unfocusedBorderColor = BorderLight,
                        cursorColor = PrimaryBlack
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Font Size Slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Size: ${element.fontSizeSp.toInt()}sp", fontSize = 11.sp, modifier = Modifier.width(70.dp))
                    Slider(
                        value = element.fontSizeSp,
                        onValueChange = { onUpdate(element.copy(fontSizeSp = it)) },
                        valueRange = 10f..60f,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Bold / Italic / Alignments
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (element.isBold) PrimaryBlack else Color(0xFFF3F4F6))
                            .clickable { onUpdate(element.copy(isBold = !element.isBold)) }
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.FormatBold,
                            contentDescription = "Bold",
                            tint = if (element.isBold) Color.White else PrimaryBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (element.isItalic) PrimaryBlack else Color(0xFFF3F4F6))
                            .clickable { onUpdate(element.copy(isItalic = !element.isItalic)) }
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.FormatItalic,
                            contentDescription = "Italic",
                            tint = if (element.isItalic) Color.White else PrimaryBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Alignment toggle
                    listOf("LEFT", "CENTER", "RIGHT").forEach { align ->
                        val isSel = element.textAlign.equals(align, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) PrimaryBlack else Color(0xFFF3F4F6))
                                .clickable { onUpdate(element.copy(textAlign = align)) }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = align.take(1),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else PrimaryBlack
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Extracted Image Palette Swatches (if available)
                if (extractedPalette.isNotEmpty()) {
                    Text("🎨 Extracted Image Palette", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(extractedPalette) { hex ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(hex)))
                                    .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                                    .clickable { onUpdate(element.copy(fontColorHex = hex)) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Quick Text Color Palette
                Text("Text Color", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val colors = listOf("#111827", "#FFFFFF", "#2563EB", "#DC2626", "#059669", "#D97706", "#7C3AED", "#DB2777")
                    items(colors) { hex ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(hex)))
                                .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                                .clickable { onUpdate(element.copy(fontColorHex = hex)) }
                        )
                    }

                    item {
                        IconButton(
                            onClick = { onPickCustomColor(ColorPickerTarget.TEXT_COLOR, element.fontColorHex) },
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFF3F4F6), CircleShape)
                        ) {
                            Icon(Icons.Default.ColorLens, contentDescription = "Custom Color", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Text Outline / Stroke controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = element.hasStroke,
                        onCheckedChange = { onUpdate(element.copy(hasStroke = it)) }
                    )
                    Text("Text Outline / Stroke", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    if (element.hasStroke) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(element.strokeColorHex)))
                                .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                                .clickable { onPickCustomColor(ColorPickerTarget.TEXT_STROKE_COLOR, element.strokeColorHex) }
                        )
                    }
                }

                // Text Drop Shadow
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = element.hasShadow,
                        onCheckedChange = { onUpdate(element.copy(hasShadow = it)) }
                    )
                    Text("Drop Shadow", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    if (element.hasShadow) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(element.shadowColorHex)))
                                .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                                .clickable { onPickCustomColor(ColorPickerTarget.TEXT_SHADOW_COLOR, element.shadowColorHex) }
                        )
                    }
                }
            }

            ElementType.SHAPE -> {
                // Extracted Image Palette Swatches
                if (extractedPalette.isNotEmpty()) {
                    Text("🎨 Extracted Image Palette", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(extractedPalette) { hex ->
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(hex)))
                                    .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                                    .clickable { onUpdate(element.copy(fillColorHex = hex)) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text("Shape Color", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val colors = listOf("#3B82F6", "#111827", "#FFFFFF", "#10B981", "#EF4444", "#F59E0B", "#8B5CF6", "#EC4899")
                    items(colors) { hex ->
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(hex)))
                                .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                                .clickable { onUpdate(element.copy(fillColorHex = hex)) }
                        )
                    }

                    item {
                        IconButton(
                            onClick = { onPickCustomColor(ColorPickerTarget.SHAPE_FILL, element.fillColorHex) },
                            modifier = Modifier
                                .size(30.dp)
                                .background(Color(0xFFF3F4F6), CircleShape)
                        ) {
                            Icon(Icons.Default.ColorLens, contentDescription = "Custom Color", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Corner radius slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Corner: ${element.shapeCornerRadiusDp.toInt()}dp", fontSize = 11.sp, modifier = Modifier.width(80.dp))
                    Slider(
                        value = element.shapeCornerRadiusDp,
                        onValueChange = { onUpdate(element.copy(shapeCornerRadiusDp = it)) },
                        valueRange = 0f..40f,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Opacity slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Opacity: ${(element.opacity * 100).toInt()}%", fontSize = 11.sp, modifier = Modifier.width(80.dp))
                    Slider(
                        value = element.opacity,
                        onValueChange = { onUpdate(element.copy(opacity = it)) },
                        valueRange = 0.1f..1f,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            ElementType.IMAGE -> {
                Text("Image Styling & Borders", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Corner: ${element.cornerRadiusDp.toInt()}dp", fontSize = 11.sp, modifier = Modifier.width(80.dp))
                    Slider(
                        value = element.cornerRadiusDp,
                        onValueChange = { onUpdate(element.copy(cornerRadiusDp = it)) },
                        valueRange = 0f..40f,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Border: ${element.borderWidthDp.toInt()}dp", fontSize = 11.sp, modifier = Modifier.width(80.dp))
                    Slider(
                        value = element.borderWidthDp,
                        onValueChange = { onUpdate(element.copy(borderWidthDp = it)) },
                        valueRange = 0f..10f,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(element.borderColorHex)))
                            .border(1.dp, Color(0xFFCBD5E1), CircleShape)
                            .clickable { onPickCustomColor(ColorPickerTarget.IMAGE_BORDER_COLOR, element.borderColorHex) }
                    )
                }
            }
        }

        // Dedicated Smart Formation & Alignment section for all elements
        QuickFormationSection(
            element = element,
            totalElements = totalElements,
            onUpdate = onUpdate,
            onDistributeV = onDistributeV,
            onDistributeH = onDistributeH
        )
    }
}

@Composable
private fun QuickFormationSection(
    element: CanvasElement,
    totalElements: List<CanvasElement>,
    onUpdate: (CanvasElement) -> Unit,
    onDistributeV: (() -> Unit)? = null,
    onDistributeH: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Smart Formation & Alignment",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight
            )
            Text(
                "1-Tap",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2563EB),
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEFF6FF))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Alignment Row
        Text("Horizontal Alignment", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLight)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FormationButton(
                label = "Left",
                icon = Icons.Default.AlignHorizontalLeft,
                modifier = Modifier.weight(1f),
                onClick = { onUpdate(SmartAlignmentHelper.alignLeft(element)) }
            )
            FormationButton(
                label = "Center",
                icon = Icons.Default.AlignHorizontalCenter,
                modifier = Modifier.weight(1.1f),
                onClick = { onUpdate(SmartAlignmentHelper.alignCenterH(element)) }
            )
            FormationButton(
                label = "Right",
                icon = Icons.Default.AlignHorizontalRight,
                modifier = Modifier.weight(1f),
                onClick = { onUpdate(SmartAlignmentHelper.alignRight(element)) }
            )
            FormationButton(
                label = "Fit Width",
                icon = Icons.Default.WidthNormal,
                modifier = Modifier.weight(1.1f),
                onClick = { onUpdate(SmartAlignmentHelper.matchCanvasWidth(element)) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Vertical Alignment Row
        Text("Vertical Alignment", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLight)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FormationButton(
                label = "Top",
                icon = Icons.Default.AlignVerticalTop,
                modifier = Modifier.weight(1f),
                onClick = { onUpdate(SmartAlignmentHelper.alignTop(element)) }
            )
            FormationButton(
                label = "Center",
                icon = Icons.Default.AlignVerticalCenter,
                modifier = Modifier.weight(1.1f),
                onClick = { onUpdate(SmartAlignmentHelper.alignCenterV(element)) }
            )
            FormationButton(
                label = "Bottom",
                icon = Icons.Default.AlignVerticalBottom,
                modifier = Modifier.weight(1f),
                onClick = { onUpdate(SmartAlignmentHelper.alignBottom(element)) }
            )
            FormationButton(
                label = "Center All",
                icon = Icons.Default.CenterFocusStrong,
                modifier = Modifier.weight(1.2f),
                isHighlight = true,
                onClick = { onUpdate(SmartAlignmentHelper.centerCanvas(element)) }
            )
        }

        // Multi-element Distribution
        if (totalElements.size >= 3) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Multi-Element Spacing", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLight)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FormationButton(
                    label = "Distribute Vertical",
                    icon = Icons.Default.FormatLineSpacing,
                    modifier = Modifier.weight(1f),
                    onClick = { onDistributeV?.invoke() }
                )
                FormationButton(
                    label = "Distribute Horizontal",
                    icon = Icons.Default.FormatAlignJustify,
                    modifier = Modifier.weight(1f),
                    onClick = { onDistributeH?.invoke() }
                )
            }
        }
    }
}

@Composable
private fun FormationButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isHighlight) Color(0xFF2563EB) else Color.White)
            .border(1.dp, if (isHighlight) Color(0xFF1D4ED8) else Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(13.dp),
                tint = if (isHighlight) Color.White else Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isHighlight) Color.White else Color(0xFF1E293B),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CanvasQuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(12.dp),
            tint = Color(0xFF1E293B)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
private fun BackgroundTabContent(
    currentBg: PosterBackground,
    extractedPalette: List<String> = emptyList(),
    onPickCustomColor: (ColorPickerTarget, String) -> Unit,
    onUpdateBg: (PosterBackground) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Poster Background", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
        Spacer(modifier = Modifier.height(10.dp))

        // Extracted Image Palette row (if available)
        if (extractedPalette.isNotEmpty()) {
            Text("🎨 Extracted Image Palette", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(extractedPalette) { hex ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(android.graphics.Color.parseColor(hex)))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                            .clickable {
                                onUpdateBg(PosterBackground(type = BackgroundType.SOLID, color1Hex = hex))
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        Text("Solid Palettes", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLight)
        Spacer(modifier = Modifier.height(6.dp))

        val solids = listOf("#FFFFFF", "#F3F4F6", "#0F172A", "#FFFBEB", "#18181B", "#FEF2F2", "#EFF6FF", "#ECFDF5")
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(solids) { hex ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(android.graphics.Color.parseColor(hex)))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                        .clickable {
                            onUpdateBg(PosterBackground(type = BackgroundType.SOLID, color1Hex = hex))
                        }
                )
            }

            item {
                IconButton(
                    onClick = { onPickCustomColor(ColorPickerTarget.BACKGROUND_COLOR1, currentBg.color1Hex) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.ColorLens, contentDescription = "Custom Color", modifier = Modifier.size(18.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text("✨ Curated Gradient Themes", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLight)
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(BackgroundPresets.gradients) { preset ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onUpdateBg(
                            PosterBackground(
                                type = BackgroundType.GRADIENT_LINEAR,
                                color1Hex = preset.color1,
                                color2Hex = preset.color2
                            )
                        )
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp, 36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(
                                        Color(android.graphics.Color.parseColor(preset.color1)),
                                        Color(android.graphics.Color.parseColor(preset.color2))
                                    )
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(preset.name, fontSize = 9.sp, color = TextSecondaryLight, maxLines = 1)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text("Custom Gradient Colors", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLight)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Color 1
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(android.graphics.Color.parseColor(currentBg.color1Hex)))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                        .clickable { onPickCustomColor(ColorPickerTarget.BACKGROUND_COLOR1, currentBg.color1Hex) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ColorLens, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Text("Start Color", fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
            }

            // Color 2
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(android.graphics.Color.parseColor(currentBg.color2Hex ?: currentBg.color1Hex)))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                        .clickable { onPickCustomColor(ColorPickerTarget.BACKGROUND_COLOR2, currentBg.color2Hex ?: currentBg.color1Hex) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ColorLens, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Text("End Color", fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
private fun LayersTabContent(
    elements: List<CanvasElement>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onMoveUp: (CanvasElement) -> Unit,
    onMoveDown: (CanvasElement) -> Unit,
    onToggleLock: (CanvasElement) -> Unit,
    onToggleVisibility: (CanvasElement) -> Unit,
    onDelete: (CanvasElement) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Layer Ordering (${elements.size} layers)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        elements.reversed().forEach { el ->
            val isSel = el.id == selectedId
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSel) Color(0xFFEFF6FF) else Color(0xFFF9FAFB))
                    .border(1.dp, if (isSel) Color(0xFF3B82F6) else CardBorderLight, RoundedCornerShape(10.dp))
                    .clickable { onSelect(el.id) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (el.type == ElementType.TEXT) "T: ${el.text.take(18)}" else "Shape: ${el.shapeType.name}",
                        fontSize = 12.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onMoveUp(el) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Up", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = { onMoveDown(el) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Down", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = { onToggleLock(el) }, modifier = Modifier.size(28.dp)) {
                        Icon(
                            if (el.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Lock",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = { onDelete(el) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DynamicFieldConfigurator(
    selectedElement: CanvasElement?,
    editableFields: List<EditableField>,
    onAddField: (EditableField) -> Unit,
    onRemoveField: (String) -> Unit
) {
    var fieldLabel by remember { mutableStateOf("") }
    var fieldType by remember { mutableStateOf(FieldType.TEXT) }
    var isRequired by remember { mutableStateOf(true) }

    LaunchedEffect(selectedElement) {
        if (selectedElement != null) {
            fieldLabel = if (selectedElement.type == ElementType.TEXT) selectedElement.text else "Custom Field"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Editable Field Configurator", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
        Text(
            "Mark canvas elements as user-fillable form fields so normal users can generate posters easily.",
            fontSize = 11.sp,
            color = TextSecondaryLight
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (selectedElement == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Select an element on canvas to convert it into a dynamic field", fontSize = 12.sp, color = TextSecondaryLight)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = fieldLabel,
                    onValueChange = { fieldLabel = it },
                    label = { Text("Field Label", fontSize = 11.sp, color = TextSecondaryLight) },
                    modifier = Modifier.weight(1.5f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimaryLight,
                        unfocusedTextColor = TextPrimaryLight,
                        focusedContainerColor = SurfaceLight,
                        unfocusedContainerColor = SurfaceLight,
                        focusedBorderColor = PrimaryBlack,
                        unfocusedBorderColor = BorderLight,
                        cursorColor = PrimaryBlack
                    )
                )

                // Field Type Selector
                var showTypeDropdown by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { showTypeDropdown = true },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(fieldType.name, fontSize = 11.sp)
                    }

                    DropdownMenu(
                        expanded = showTypeDropdown,
                        onDismissRequest = { showTypeDropdown = false }
                    ) {
                        FieldType.values().forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t.name) },
                                onClick = {
                                    fieldType = t
                                    showTypeDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val fieldId = "field_" + UUID.randomUUID().toString().take(6)
                    val field = EditableField(
                        fieldId = fieldId,
                        elementId = selectedElement.id,
                        type = fieldType,
                        label = fieldLabel.ifEmpty { "Field" },
                        defaultValue = selectedElement.text,
                        required = isRequired
                    )
                    onAddField(field)
                },
                modifier = Modifier.fillMaxWidth().height(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.DynamicForm, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Link as Editable Field", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerDialog(
    initialColorHex: String,
    onColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val hsv = remember {
        val floatArray = FloatArray(3)
        try {
            android.graphics.Color.colorToHSV(android.graphics.Color.parseColor(initialColorHex), floatArray)
        } catch (e: Exception) {
            floatArray[0] = 0f
            floatArray[1] = 0f
            floatArray[2] = 1f
        }
        floatArray
    }

    var hue by remember { mutableStateOf(hsv[0]) }
    var saturation by remember { mutableStateOf(hsv[1]) }
    var brightness by remember { mutableStateOf(hsv[2]) }

    val currentColor = remember(hue, saturation, brightness) {
        Color.hsv(hue, saturation, brightness)
    }

    val hexString = remember(currentColor) {
        String.format("#%06X", (0xFFFFFF and android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, brightness))))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "HSB Color Picker",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Color Preview Box
                Box(
                    modifier = Modifier
                        .size(120.dp, 60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(currentColor)
                        .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = hexString,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 1. Hue Slider (Spectrum)
                Text("Hue: ${hue.toInt()}°", fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.fillMaxWidth())
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red
                                )
                            )
                        )
                )
                Slider(
                    value = hue,
                    onValueChange = { hue = it },
                    valueRange = 0f..360f,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Saturation Slider
                Text("Saturation: ${(saturation * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.fillMaxWidth())
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.White, Color.hsv(hue, 1f, 1f))
                            )
                        )
                )
                Slider(
                    value = saturation,
                    onValueChange = { saturation = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Brightness Slider
                Text("Brightness: ${(brightness * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.fillMaxWidth())
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Black, Color.hsv(hue, saturation, 1f))
                            )
                        )
                )
                Slider(
                    value = brightness,
                    onValueChange = { brightness = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onColorSelected(hexString)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                    ) {
                        Text("Select Color")
                    }
                }
            }
        }
    }
}

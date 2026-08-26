package com.example.ui.editor

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
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
import com.example.domain.models.ShapeType
import com.example.domain.models.Template
import com.example.domain.models.TemplateStatus
import com.example.domain.models.UserRole
import com.example.ui.components.PosterCanvasView
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PrimaryBlack
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.TextTertiaryLight
import com.example.utils.CanvasUtils
import kotlinx.coroutines.launch
import java.util.UUID

enum class EditorTab(val label: String) {
    ELEMENTS("Add"),
    STYLE("Style"),
    BACKGROUND("Background"),
    LAYERS("Layers"),
    DYNAMIC_FIELDS("Field Config")
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
    var background by remember { mutableStateOf(PosterBackground()) }
    val elements = remember { mutableStateListOf<CanvasElement>() }
    val editableFields = remember { mutableStateListOf<EditableField>() }

    var selectedElementId by remember { mutableStateOf<String?>(null) }
    var activeTab by remember { mutableStateOf(EditorTab.ELEMENTS) }

    // Undo / Redo history stacks
    val undoStack = remember { mutableStateListOf<List<CanvasElement>>() }
    val redoStack = remember { mutableStateListOf<List<CanvasElement>>() }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = posterTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${canvasWidth}x${canvasHeight} • ${elements.size} layers",
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
            // 1. Canvas Work Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE5E7EB))
                    .padding(16.dp),
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
                    onElementSelected = { id ->
                        selectedElementId = id
                        if (id != null) {
                            activeTab = EditorTab.STYLE
                        }
                    },
                    onElementMoved = { elId, newX, newY ->
                        pushHistory()
                        val idx = elements.indexOfFirst { it.id == elId }
                        if (idx != -1) {
                            elements[idx] = elements[idx].copy(xRatio = newX, yRatio = newY)
                        }
                    }
                )
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
                    .height(260.dp),
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
                                pushHistory()
                                val newId = "el_" + UUID.randomUUID().toString().take(6)
                                elements.add(
                                    CanvasElement(
                                        id = newId,
                                        type = ElementType.IMAGE,
                                        xRatio = 0.25f,
                                        yRatio = 0.35f,
                                        widthRatio = 0.5f,
                                        heightRatio = 0.25f,
                                        cornerRadiusDp = 16f,
                                        layerOrder = elements.size + 1
                                    )
                                )
                                selectedElementId = newId
                                activeTab = EditorTab.STYLE
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
                                onUpdate = { updated ->
                                    pushHistory()
                                    val idx = elements.indexOfFirst { it.id == updated.id }
                                    if (idx != -1) {
                                        elements[idx] = updated
                                    }
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
    onAddImagePlaceholder: () -> Unit
) {
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
        Text("Shapes & Badges", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLight)
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
    }
}

@Composable
private fun ElementStyleInspector(
    element: CanvasElement,
    onUpdate: (CanvasElement) -> Unit,
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
                    label = { Text("Text Content") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
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

                // Quick Text Color Palette
                Text("Text Color", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                }
            }

            ElementType.SHAPE -> {
                Text("Shape Color", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                Text("Image Properties", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
            }
        }
    }
}

@Composable
private fun BackgroundTabContent(
    currentBg: PosterBackground,
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

        Text("Solid Palettes", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLight)
        Spacer(modifier = Modifier.height(6.dp))

        val solids = listOf("#FFFFFF", "#F3F4F6", "#0F172A", "#FFFBEB", "#18181B", "#FEF2F2", "#EFF6FF", "#ECFDF5")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text("Gradient Themes", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLight)
        Spacer(modifier = Modifier.height(6.dp))

        val gradients = listOf(
            Pair("#FF6B6B", "#4ECDC4"),
            Pair("#0F172A", "#1E3A8A"),
            Pair("#064E3B", "#047857"),
            Pair("#7C3AED", "#EC4899"),
            Pair("#EA580C", "#F59E0B"),
            Pair("#1E293B", "#334155")
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(gradients) { (c1, c2) ->
                Box(
                    modifier = Modifier
                        .size(50.dp, 36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color(android.graphics.Color.parseColor(c1)), Color(android.graphics.Color.parseColor(c2)))
                            )
                        )
                        .clickable {
                            onUpdateBg(
                                PosterBackground(
                                    type = BackgroundType.GRADIENT_LINEAR,
                                    color1Hex = c1,
                                    color2Hex = c2
                                )
                            )
                        }
                )
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
                    label = { Text("Field Label", fontSize = 11.sp) },
                    modifier = Modifier.weight(1.5f),
                    singleLine = true
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

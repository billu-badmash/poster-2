package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.PosterRepository
import com.example.domain.models.CanvasElement
import com.example.domain.models.EditableField
import com.example.domain.models.ElementType
import com.example.domain.models.PosterBackground
import com.example.domain.models.Project
import com.example.domain.models.Template
import com.example.domain.models.TemplateStatus
import com.example.domain.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class EditorViewModel(
    private val repository: PosterRepository,
    private val templateId: String?,
    private val projectId: String?
) : ViewModel() {

    val currentUser = repository.currentUser
    val isDarkMode = repository.isDarkMode

    private val _posterTitle = MutableStateFlow("Untitled Poster")
    val posterTitle: StateFlow<String> = _posterTitle.asStateFlow()

    private val _canvasWidth = MutableStateFlow(1080)
    val canvasWidth: StateFlow<Int> = _canvasWidth.asStateFlow()

    private val _canvasHeight = MutableStateFlow(1080)
    val canvasHeight: StateFlow<Int> = _canvasHeight.asStateFlow()

    private val _background = MutableStateFlow(PosterBackground())
    val background: StateFlow<PosterBackground> = _background.asStateFlow()

    private val _elements = MutableStateFlow<List<CanvasElement>>(emptyList())
    val elements: StateFlow<List<CanvasElement>> = _elements.asStateFlow()

    private val _editableFields = MutableStateFlow<List<EditableField>>(emptyList())
    val editableFields: StateFlow<List<EditableField>> = _editableFields.asStateFlow()

    private val _selectedElementId = MutableStateFlow<String?>(null)
    val selectedElementId: StateFlow<String?> = _selectedElementId.asStateFlow()

    private val _undoStack = MutableStateFlow<List<List<CanvasElement>>>(emptyList())
    val undoStack: StateFlow<List<List<CanvasElement>>> = _undoStack.asStateFlow()

    private val _redoStack = MutableStateFlow<List<List<CanvasElement>>>(emptyList())
    val redoStack: StateFlow<List<List<CanvasElement>>> = _redoStack.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private var _loaded = false

    suspend fun loadContent() {
        if (_loaded) return
        _loaded = true

        if (projectId != null) {
            val proj = repository.getProjectById(projectId)
            if (proj != null) {
                _posterTitle.value = proj.projectName
                _canvasWidth.value = proj.canvasWidth
                _canvasHeight.value = proj.canvasHeight
                _background.value = proj.background
                _elements.value = proj.elements
            }
        } else if (templateId != null) {
            val tmpl = repository.getTemplateById(templateId)
            if (tmpl != null) {
                _posterTitle.value = "${tmpl.name} (Custom)"
                _canvasWidth.value = tmpl.canvasWidth
                _canvasHeight.value = tmpl.canvasHeight
                _background.value = tmpl.background
                _elements.value = tmpl.elements
                _editableFields.value = tmpl.editableFields
            }
        } else {
            _posterTitle.value = "My New Poster"
            _elements.value = listOf(
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

    fun pushHistory() {
        val current = _undoStack.value.toMutableList()
        current.add(_elements.value.map { it.copy() })
        if (current.size > 20) current.removeAt(0)
        _undoStack.value = current
        _redoStack.value = emptyList()
    }

    fun undo() {
        val stack = _undoStack.value.toMutableList()
        if (stack.isNotEmpty()) {
            _redoStack.value = _redoStack.value + listOf(_elements.value.map { it.copy() })
            val prev = stack.removeAt(stack.lastIndex)
            _undoStack.value = stack
            _elements.value = prev
        }
    }

    fun redo() {
        val stack = _redoStack.value.toMutableList()
        if (stack.isNotEmpty()) {
            _undoStack.value = _undoStack.value + listOf(_elements.value.map { it.copy() })
            val next = stack.removeAt(stack.lastIndex)
            _redoStack.value = stack
            _elements.value = next
        }
    }

    fun selectElement(id: String?) {
        _selectedElementId.value = id
    }

    fun updateElement(updated: CanvasElement) {
        pushHistory()
        _elements.value = _elements.value.map { if (it.id == updated.id) updated else it }
    }

    fun addElement(element: CanvasElement) {
        pushHistory()
        _elements.value = _elements.value + element
    }

    fun removeElement(id: String) {
        pushHistory()
        _elements.value = _elements.value.filter { it.id != id }
        if (_selectedElementId.value == id) _selectedElementId.value = null
    }

    fun moveElement(id: String, newX: Float, newY: Float) {
        _elements.value = _elements.value.map {
            if (it.id == id) it.copy(xRatio = newX, yRatio = newY) else it
        }
    }

    fun resizeElement(id: String, newW: Float, newH: Float) {
        _elements.value = _elements.value.map {
            if (it.id == id) it.copy(widthRatio = newW, heightRatio = newH) else it
        }
    }

    fun updateBackground(bg: PosterBackground) {
        _background.value = bg
    }

    fun renamePoster(newName: String) {
        _posterTitle.value = newName.trim()
    }

    fun reorderElements(up: Boolean, element: CanvasElement) {
        pushHistory()
        val list = _elements.value.toMutableList()
        val idx = list.indexOfFirst { it.id == element.id }
        if (idx == -1) return
        val targetIdx = if (up) idx - 1 else idx + 1
        if (targetIdx < 0 || targetIdx >= list.size) return
        val item = list.removeAt(idx)
        list.add(targetIdx, item)
        _elements.value = list.mapIndexed { i, e -> e.copy(layerOrder = i) }
    }

    fun toggleLock(element: CanvasElement) {
        _elements.value = _elements.value.map {
            if (it.id == element.id) it.copy(isLocked = !it.isLocked) else it
        }
    }

    fun toggleVisibility(element: CanvasElement) {
        _elements.value = _elements.value.map {
            if (it.id == element.id) it.copy(isVisible = !it.isVisible) else it
        }
    }

    fun addEditableField(field: EditableField) {
        _editableFields.value = _editableFields.value.filter {
            it.fieldId != field.fieldId && it.elementId != field.elementId
        } + field
        _elements.value = _elements.value.map {
            if (it.id == field.elementId) it.copy(isEditableField = true, editableFieldId = field.fieldId) else it
        }
    }

    fun removeEditableField(fieldId: String) {
        val removed = _editableFields.value.find { it.fieldId == fieldId }
        _editableFields.value = _editableFields.value.filter { it.fieldId != fieldId }
        if (removed != null) {
            _elements.value = _elements.value.map {
                if (it.id == removed.elementId) it.copy(isEditableField = false, editableFieldId = null) else it
            }
        }
    }

    suspend fun save() {
        _isSaving.value = true
        val projId = projectId ?: ("proj_" + UUID.randomUUID().toString().take(8))
        val user = currentUser.value
        val project = Project(
            projectId = projId,
            userId = user.id,
            templateId = templateId ?: "custom",
            templateName = _posterTitle.value,
            projectName = _posterTitle.value,
            canvasWidth = _canvasWidth.value,
            canvasHeight = _canvasHeight.value,
            background = _background.value,
            elements = _elements.value,
            isDraft = false
        )
        repository.saveProject(project)

        if (user.role == UserRole.CREATOR || user.role == UserRole.ADMIN) {
            val tmplId = templateId ?: ("tmpl_" + UUID.randomUUID().toString().take(8))
            val tmpl = Template(
                templateId = tmplId,
                creatorId = user.id,
                creatorName = user.name,
                name = _posterTitle.value,
                description = "Custom designed template by ${user.name}",
                categoryId = "cat_business",
                categoryName = "Business",
                canvasWidth = _canvasWidth.value,
                canvasHeight = _canvasHeight.value,
                background = _background.value,
                elements = _elements.value,
                editableFields = _editableFields.value,
                status = if (user.role == UserRole.ADMIN) TemplateStatus.APPROVED else TemplateStatus.PENDING
            )
            repository.saveTemplate(tmpl)
        }
        _isSaving.value = false
    }

    class Factory(
        private val repository: PosterRepository,
        private val templateId: String?,
        private val projectId: String?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditorViewModel::class.java)) {
                return EditorViewModel(repository, templateId, projectId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

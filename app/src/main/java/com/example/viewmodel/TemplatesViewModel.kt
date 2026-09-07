package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.PosterRepository
import com.example.domain.models.Template
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TemplatesViewModel(private val repository: PosterRepository) : ViewModel() {

    val currentUser = repository.currentUser
    val isDarkMode = repository.isDarkMode

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _sortBy = MutableStateFlow("popular")
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
    }

    fun updateSort(sort: String) {
        _sortBy.value = sort
    }

    fun getAllTemplates() = repository.getAllTemplates()

    fun getPendingTemplates() = repository.getPendingTemplates()

    fun getTemplatesByCreator(creatorId: String) = repository.getTemplatesByCreator(creatorId)

    fun getFavoriteIds(userId: String) = repository.getFavoriteTemplates(userId)

    class Factory(private val repository: PosterRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TemplatesViewModel::class.java)) {
                return TemplatesViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

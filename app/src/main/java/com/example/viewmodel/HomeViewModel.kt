package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.PosterRepository
import com.example.domain.models.Category
import com.example.domain.models.Project
import com.example.domain.models.Template
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(private val repository: PosterRepository) : ViewModel() {

    val currentUser = repository.currentUser
    val categories = repository.categories
    val approvedTemplates = repository.getApprovedTemplates()
    val isDarkMode = repository.isDarkMode

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
    }

    fun getProjectsByUser(userId: String) = repository.getProjectsByUser(userId)

    fun getFavoriteIds(userId: String) = repository.getFavoriteTemplates(userId)

    fun getNotifications(userId: String) = repository.getNotifications(userId)

    fun markAllNotificationsRead(userId: String) = repository.markAllNotificationsAsRead(userId)

    class Factory(private val repository: PosterRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

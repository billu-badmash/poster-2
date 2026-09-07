package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.PosterRepository
import com.example.domain.models.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProjectsViewModel(private val repository: PosterRepository) : ViewModel() {

    val currentUser = repository.currentUser
    val isDarkMode = repository.isDarkMode

    private val _selectedTab = MutableStateFlow("all")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    fun getProjectsByUser(userId: String) = repository.getProjectsByUser(userId)

    suspend fun renameProject(projectId: String, newName: String) = repository.renameProject(projectId, newName)

    suspend fun duplicateProject(projectId: String) = repository.duplicateProject(projectId)

    suspend fun deleteProject(projectId: String) = repository.deleteProject(projectId)

    class Factory(private val repository: PosterRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProjectsViewModel::class.java)) {
                return ProjectsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

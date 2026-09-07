package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.PosterRepository
import com.example.domain.models.UserRole
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(private val repository: PosterRepository) : ViewModel() {

    val currentUser = repository.currentUser
    val isDarkMode = repository.isDarkMode

    fun toggleDarkMode() = repository.toggleDarkMode()

    fun switchRole(role: UserRole) = repository.switchRole(role)

    fun updateUserProfile(name: String, bio: String, avatarUrl: String? = null) {
        repository.updateUserProfile(name, bio, avatarUrl)
    }

    fun logout() = repository.logout()

    class Factory(private val repository: PosterRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

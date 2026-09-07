package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.PosterRepository
import com.example.domain.models.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuthFlowUnitTest {

    private lateinit var context: Context
    private lateinit var repository: PosterRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repository = PosterRepository(context)
    }

    @Test
    fun testAllThreeRolesExist() {
        val roles = UserRole.values()
        assertEquals(3, roles.size)
        assertTrue(roles.contains(UserRole.USER))
        assertTrue(roles.contains(UserRole.CREATOR))
        assertTrue(roles.contains(UserRole.ADMIN))
    }

    @Test
    fun testPresetUsersContainAllThreeRoles() {
        val presets = repository.getPresetUsers()
        assertEquals(3, presets.size)

        val userPersona = presets.find { it.role == UserRole.USER }
        val creatorPersona = presets.find { it.role == UserRole.CREATOR }
        val adminPersona = presets.find { it.role == UserRole.ADMIN }

        assertNotNull(userPersona)
        assertNotNull(creatorPersona)
        assertNotNull(adminPersona)

        assertEquals("Alex Rivera", userPersona?.name)
        assertEquals("Studio X Designs", creatorPersona?.name)
        assertTrue(creatorPersona?.isCreatorVerified == true)
        assertEquals("System Admin", adminPersona?.name)
    }

    @Test
    fun testLoginAsNormalUser() {
        val user = repository.loginWithCredentials("alex.rivera@example.com", UserRole.USER)
        assertTrue(repository.isLoggedIn.value)
        assertEquals(UserRole.USER, repository.currentUser.value.role)
        assertEquals("alex.rivera@example.com", repository.currentUser.value.email)
    }

    @Test
    fun testLoginAsCreator() {
        val creator = repository.loginWithCredentials("creator@studiox.io", UserRole.CREATOR)
        assertTrue(repository.isLoggedIn.value)
        assertEquals(UserRole.CREATOR, repository.currentUser.value.role)
        assertTrue(repository.currentUser.value.isCreatorVerified)
    }

    @Test
    fun testLoginAsAdministrator() {
        val admin = repository.loginWithCredentials("admin@postermaker.com", UserRole.ADMIN)
        assertTrue(repository.isLoggedIn.value)
        assertEquals(UserRole.ADMIN, repository.currentUser.value.role)
    }

    @Test
    fun testRegisterNewCreatorAccount() {
        val registered = repository.registerUser(
            name = "Aesthetic Posters Studio",
            email = "contact@aestheticposters.com",
            role = UserRole.CREATOR,
            bio = "Minimalist event and brand marketing posters"
        )

        assertTrue(repository.isLoggedIn.value)
        assertEquals("Aesthetic Posters Studio", registered.name)
        assertEquals("contact@aestheticposters.com", registered.email)
        assertEquals(UserRole.CREATOR, registered.role)
        assertEquals("Minimalist event and brand marketing posters", registered.bio)
        assertTrue(registered.isCreatorVerified)
    }

    @Test
    fun testLogoutClearsSession() {
        repository.loginWithCredentials("test@example.com", UserRole.USER)
        assertTrue(repository.isLoggedIn.value)

        repository.logout()
        assertFalse(repository.isLoggedIn.value)
    }

    @Test
    fun testSwitchRole() {
        repository.loginWithCredentials("user@example.com", UserRole.USER)
        assertEquals(UserRole.USER, repository.currentUser.value.role)

        repository.switchRole(UserRole.CREATOR)
        assertEquals(UserRole.CREATOR, repository.currentUser.value.role)

        repository.switchRole(UserRole.ADMIN)
        assertEquals(UserRole.ADMIN, repository.currentUser.value.role)
    }

    @Test
    fun testProjectCreationRenameDuplicateAndDelete() = kotlinx.coroutines.runBlocking {
        val user = repository.loginWithCredentials("creator@studiox.io", UserRole.CREATOR)
        val projId = "proj_test_123"
        val project = com.example.domain.models.Project(
            projectId = projId,
            userId = user.id,
            templateId = "tmpl_summer_fest",
            templateName = "Summer Music Festival",
            projectName = "My Custom Festival Poster",
            isDraft = true
        )

        repository.saveProject(project)
        val retrieved = repository.getProjectById(projId)
        assertNotNull(retrieved)
        assertEquals("My Custom Festival Poster", retrieved?.projectName)
        assertTrue(retrieved?.isDraft == true)

        // Rename
        repository.renameProject(projId, "Final Music Festival Flyer")
        val renamed = repository.getProjectById(projId)
        assertEquals("Final Music Festival Flyer", renamed?.projectName)

        // Duplicate
        val copy = repository.duplicateProject(projId)
        assertNotNull(copy)
        assertEquals("Final Music Festival Flyer (Copy)", copy?.projectName)

        // Delete
        repository.deleteProject(projId)
        val deleted = repository.getProjectById(projId)
        org.junit.Assert.assertNull(deleted)
    }

    @Test
    fun testTemplatePermissionLevels() {
        val levels = com.example.domain.models.TemplatePermissionLevel.values()
        assertEquals(3, levels.size)
        assertTrue(levels.contains(com.example.domain.models.TemplatePermissionLevel.BASIC))
        assertTrue(levels.contains(com.example.domain.models.TemplatePermissionLevel.FLEXIBLE))
        assertTrue(levels.contains(com.example.domain.models.TemplatePermissionLevel.FULLY_EDITABLE))
    }

    @Test
    fun testAdminModerationApproveAndRejectLifecycle() = kotlinx.coroutines.runBlocking {
        val admin = repository.loginWithCredentials("admin@postermaker.com", UserRole.ADMIN)
        val tmplId = "tmpl_sub_999"
        val submission = com.example.domain.models.Template(
            templateId = tmplId,
            creatorId = "creator_studio_x",
            creatorName = "Studio X Designs",
            name = "Awesome Night Club Party",
            description = "High energy nightclub flyer template",
            categoryId = "cat_events",
            categoryName = "Events",
            status = com.example.domain.models.TemplateStatus.PENDING,
            permissionLevel = com.example.domain.models.TemplatePermissionLevel.FULLY_EDITABLE
        )

        repository.saveTemplate(submission)
        val fetched = repository.getTemplateById(tmplId)
        assertNotNull(fetched)
        assertEquals(com.example.domain.models.TemplateStatus.PENDING, fetched?.status)

        // Admin approves
        repository.updateTemplateStatus(tmplId, com.example.domain.models.TemplateStatus.APPROVED)
        val approved = repository.getTemplateById(tmplId)
        assertEquals(com.example.domain.models.TemplateStatus.APPROVED, approved?.status)

        // Admin rejects with reason
        repository.updateTemplateStatus(tmplId, com.example.domain.models.TemplateStatus.REJECTED, "Low contrast on title text")
        val rejected = repository.getTemplateById(tmplId)
        assertEquals(com.example.domain.models.TemplateStatus.REJECTED, rejected?.status)
        assertEquals("Low contrast on title text", rejected?.rejectionReason)
    }
}

package com.safiislam.nexvault.domain

import com.safiislam.nexvault.domain.model.DesignStyle
import com.safiislam.nexvault.domain.model.UserPreferences
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DesignStyleTest {

    @Test
    fun designStyleEnumContainsAllStyles() {
        val names = DesignStyle.entries.map { it.name }
        assertTrue(names.contains("COLORFUL"))
        assertTrue(names.contains("IOS_GLASS"))
        assertTrue(names.contains("WARM"))
        assertTrue(names.contains("LAVENDER_3D"))
        assertTrue(names.contains("AMOLED_NEON"))
        assertEquals(5, DesignStyle.entries.size)
    }

    @Test
    fun designStyleValueOfResolvesNewStyles() {
        assertEquals(DesignStyle.LAVENDER_3D, DesignStyle.valueOf("LAVENDER_3D"))
        assertEquals(DesignStyle.AMOLED_NEON, DesignStyle.valueOf("AMOLED_NEON"))
    }

    @Test
    fun userPreferencesSupportsNewDesignStyles() {
        val lavenderPrefs = UserPreferences(designStyle = DesignStyle.LAVENDER_3D)
        assertEquals(DesignStyle.LAVENDER_3D, lavenderPrefs.designStyle)

        val amoledPrefs = UserPreferences(designStyle = DesignStyle.AMOLED_NEON)
        assertEquals(DesignStyle.AMOLED_NEON, amoledPrefs.designStyle)
    }
}

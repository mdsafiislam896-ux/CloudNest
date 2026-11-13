package com.safiislam.nexvault.domain

import com.safiislam.nexvault.core.cloud.FirebaseConfig
import com.safiislam.nexvault.domain.model.CloudAccount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CloudModelsTest {
    @Test fun cloudNestIdIsShortUppercasePrefix() {
        val account = CloudAccount(uid = "abcdef1234567890", email = "a@b.c", displayName = "A")
        assertEquals("CN-ABCDEF12", account.cloudNestId)
    }

    @Test fun blankFirebaseConfigIsNotConfigured() {
        assertFalse(FirebaseConfig("", "").isConfigured)
        assertFalse(FirebaseConfig("key", "").isConfigured)
        assertTrue(FirebaseConfig("key", "project").isConfigured)
    }
}

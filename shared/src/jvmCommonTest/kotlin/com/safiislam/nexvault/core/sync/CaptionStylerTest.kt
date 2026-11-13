package com.safiislam.nexvault.core.sync

import com.safiislam.nexvault.data.remote.telegram.CaptionStyler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CaptionStylerTest {
    private val manifest = "td1:{\"id\":\"x\",\"n\":\"holiday.jpg\",\"p\":\"Trips/2026\",\"m\":\"image/jpeg\",\"s\":2097152}"

    @Test fun headerThenManifestRoundTripsExactly() {
        val styled = CaptionStyler.style(manifest)
        assertTrue(styled.text.contains("#Photos"))
        assertTrue(styled.text.contains("📁 Trips/2026"))
        assertEquals(manifest, CaptionStyler.strip(styled.text))
    }

    @Test fun spoilerCoversOnlyTheManifest() {
        val styled = CaptionStyler.style(manifest)
        assertEquals(manifest, styled.text.substring(styled.spoilerOffset, styled.spoilerOffset + styled.spoilerLength))
    }

    @Test fun plainManifestWithoutHeaderIsUntouched() {
        assertEquals(manifest, CaptionStyler.strip(manifest))
    }

    @Test fun encryptedManifestNeverLeaksNames() {
        val styled = CaptionStyler.style("tde1:AAAA")
        assertTrue(styled.text.startsWith("☁️ CloudNest · 🔒 Encrypted"))
        assertEquals("tde1:AAAA", CaptionStyler.strip(styled.text))
    }

    @Test fun oversizedCaptionFallsBackToManifestOnly() {
        val big = "td1:{\"n\":\"" + "a".repeat(1000) + "\"}"
        assertEquals(big, CaptionStyler.style(big).text)
    }
}

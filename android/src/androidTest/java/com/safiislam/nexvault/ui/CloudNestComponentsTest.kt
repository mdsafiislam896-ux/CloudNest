package com.safiislam.nexvault.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import com.safiislam.nexvault.presentation.components.DonutSlice
import com.safiislam.nexvault.presentation.components.GradientButton
import com.safiislam.nexvault.presentation.components.IconTile
import com.safiislam.nexvault.presentation.components.StorageDonut
import com.safiislam.nexvault.presentation.theme.NexVaultTheme
import com.safiislam.nexvault.domain.model.DesignStyle
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** UI tests for the reusable CloudNest components, run in every design style. */
class CloudNestComponentsTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun gradientButtonClicksOnce() {
        var clicks = 0
        compose.setContent {
            NexVaultTheme { GradientButton(text = "Sign in", onClick = { clicks++ }) }
        }
        compose.onNodeWithText("Sign in").assertIsDisplayed().performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun disabledGradientButtonDoesNotClick() {
        var clicks = 0
        compose.setContent {
            NexVaultTheme { GradientButton(text = "Save", onClick = { clicks++ }, enabled = false) }
        }
        compose.onNodeWithText("Save").assertIsNotEnabled().performClick()
        assertEquals(0, clicks)
    }

    @Test
    fun loadingButtonIgnoresClicks() {
        var clicks = 0
        compose.setContent {
            NexVaultTheme { GradientButton(text = "Go", onClick = { clicks++ }, loading = true) }
        }
        assertEquals(0, clicks)
    }

    @Test
    fun donutShowsItsCenterContent() {
        compose.setContent {
            NexVaultTheme {
                StorageDonut(
                    slices = listOf(DonutSlice(3f, Color.Red), DonutSlice(1f, Color.Blue)),
                    trackColor = Color.Gray
                ) { Text("1.6 MB") }
            }
        }
        compose.onNodeWithText("1.6 MB").assertIsDisplayed()
    }

    @Test
    fun componentsRenderInEveryDesignStyle() {
        DesignStyle.entries.forEach { style ->
            compose.setContent {
                NexVaultTheme(designStyle = style) {
                    Column {
                        IconTile(Icons.Filled.Cloud, key = 3)
                        GradientButton(text = "Style ${style.name}", onClick = {})
                    }
                }
            }
            compose.onNodeWithText("Style ${style.name}").assertIsDisplayed()
        }
    }
}

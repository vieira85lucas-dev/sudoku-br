package com.kaajjo.libresudoku.ui.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppLanguageTest {
    @Test
    fun `a player who never chose a language and has no app language gets Portuguese`() {
        assertTrue(shouldApplyDefaultLanguage(userChose = false, currentTag = ""))
    }

    @Test
    fun `a player who chose a language is never changed`() {
        assertFalse(shouldApplyDefaultLanguage(userChose = true, currentTag = ""))
        assertFalse(shouldApplyDefaultLanguage(userChose = true, currentTag = "en"))
    }

    @Test
    fun `an app language that is already set is left alone`() {
        assertFalse(shouldApplyDefaultLanguage(userChose = false, currentTag = "en"))
        assertFalse(shouldApplyDefaultLanguage(userChose = false, currentTag = "pt-BR"))
    }

    @Test
    fun `the language list has only Portuguese and English and no follow-the-phone option`() {
        val langs = getLangs(ApplicationProvider.getApplicationContext<Context>())
        assertEquals(listOf("pt-BR", "en"), langs.keys.toList())
        assertFalse(langs.containsKey(""))
    }

    @Test
    fun `the default language is Brazilian Portuguese`() {
        assertEquals("pt-BR", DEFAULT_LANGUAGE_TAG)
    }
}

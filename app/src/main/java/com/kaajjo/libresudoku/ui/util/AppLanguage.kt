package com.kaajjo.libresudoku.ui.util

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.kaajjo.libresudoku.R
import org.xmlpull.v1.XmlPullParser
import java.util.Locale

/** Idioma de fabrica do app, independente do idioma do celular. */
const val DEFAULT_LANGUAGE_TAG = "pt-BR"

private const val LANGUAGE_PREFS = "language"
private const val USER_CHOSE_LANGUAGE = "user_chose_language"

/**
 * O app so se coloca em portugues quando o jogador nunca escolheu um idioma
 * e o app ainda esta sem idioma proprio (seguindo o do celular).
 * Quem ja escolheu outro idioma antes (tag preenchida) nao e mexido.
 */
internal fun shouldApplyDefaultLanguage(userChose: Boolean, currentTag: String): Boolean =
    !userChose && currentTag.isEmpty()

/** Chamado ao iniciar o app: poe o app em portugues do Brasil em qualquer celular. */
fun applyDefaultLanguage(context: Context) {
    val prefs = context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE)
    if (shouldApplyDefaultLanguage(prefs.getBoolean(USER_CHOSE_LANGUAGE, false), getCurrentLocaleTag())) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(DEFAULT_LANGUAGE_TAG))
    }
}

/** Guarda que o jogador escolheu o idioma, para o app nunca mais mexer nele. */
fun rememberUserChoseLanguage(context: Context) {
    context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE)
        .edit().putBoolean(USER_CHOSE_LANGUAGE, true).apply()
}

fun getCurrentLocaleString(context: Context): String {
    val langs = getLangs(context)
    langs.forEach {
        Log.d("lang", "${it.key} ${it.value}")
    }
    val locales = AppCompatDelegate.getApplicationLocales()
    if (locales == LocaleListCompat.getEmptyLocaleList()) {
        return context.getString(R.string.label_default)
    }
    return getDisplayName(locales.toLanguageTags())
}

fun getCurrentLocaleTag(): String {
    val locales = AppCompatDelegate.getApplicationLocales()
    if (locales == LocaleListCompat.getEmptyLocaleList()) {
        return ""
    }
    return locales.toLanguageTags()
}

fun getLangs(context: Context): Map<String, String> {
    val langs = mutableListOf<Pair<String, String>>()
    val parser = context.resources.getXml(R.xml.locales_config)
    var eventType = parser.eventType
    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG && parser.name == "locale") {
            for (i in 0 until parser.attributeCount) {
                if (parser.getAttributeName(i) == "name") {
                    val langTag = parser.getAttributeValue(i)
                    val displayName = getDisplayName(langTag)
                    if (displayName.isNotEmpty()) {
                        langs.add(Pair(langTag, displayName))
                    }
                }
            }
        }
        eventType = parser.next()
    }

    // Portugues primeiro; sem a opcao "Padrao" (seguir o idioma do celular).
    langs.sortWith(compareBy({ it.first != DEFAULT_LANGUAGE_TAG }, { it.second }))

    return langs.toMap()
}

private fun getDisplayName(lang: String?): String {
    if (lang == null) {
        return ""
    }

    val locale = when (lang) {
        "" -> LocaleListCompat.getAdjustedDefault()[0]
        else -> Locale.forLanguageTag(lang)
    }
    return locale!!.getDisplayName(locale).replaceFirstChar { it.uppercase(locale) }
}


/*
 * SPDX-FileCopyrightText: 2026 Paranoid Android
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.settings.gestures

import android.content.Context
import android.provider.Settings
import android.provider.Settings.Secure.PIE_SHORTCUT_TOP
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.android.settings.R

/** Shortcuts that can be assigned to a Pie slot. Values must match NavigationEdgeBackPlugin. */
internal enum class PieShortcut(
    val value: Int,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
) {
    NONE(0, R.string.pie_shortcut_none, R.drawable.ic_block),
    HOME(1, R.string.pie_shortcut_home, R.drawable.ic_pie_home),
    RECENTS(2, R.string.pie_shortcut_recents, R.drawable.ic_pie_recents),
    NOTIFICATIONS(3, R.string.pie_shortcut_notifications, R.drawable.ic_pie_notifications);

    companion object {
        /** Returns the shortcut assigned to the given slot, with the same defaults as SystemUI. */
        fun get(context: Context, setting: String): PieShortcut {
            val default = if (setting == PIE_SHORTCUT_TOP) HOME else RECENTS
            val value = Settings.Secure.getInt(context.contentResolver, setting, default.value)
            return entries.find { it.value == value } ?: NONE
        }
    }
}

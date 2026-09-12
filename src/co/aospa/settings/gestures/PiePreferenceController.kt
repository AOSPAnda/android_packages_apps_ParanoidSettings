/*
 * SPDX-FileCopyrightText: 2026 Paranoid Android
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.settings.gestures

import android.content.Context
import android.provider.Settings.Secure.PIE_SHORTCUT_BOTTOM
import android.provider.Settings.Secure.PIE_SHORTCUT_TOP
import com.android.settings.R
import com.android.settings.core.BasePreferenceController

/** Controller for the Pie entry, which summarizes the selected shortcuts. */
class PiePreferenceController(context: Context, key: String) :
    BasePreferenceController(context, key) {

    override fun getAvailabilityStatus() = AVAILABLE

    override fun getSummary(): CharSequence {
        val names =
            listOf(PIE_SHORTCUT_TOP, PIE_SHORTCUT_BOTTOM)
                .map { PieShortcut.get(mContext, it) }
                .filter { it != PieShortcut.NONE }
                .map { mContext.getString(it.label) }
        return when (names.size) {
            0 -> mContext.getString(R.string.pie_summary_off)
            1 -> names[0]
            else -> mContext.getString(R.string.pie_summary_two, names[0], names[1])
        }
    }
}

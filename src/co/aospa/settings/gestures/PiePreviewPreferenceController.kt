/*
 * SPDX-FileCopyrightText: 2026 Paranoid Android
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.settings.gestures

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.Secure.PIE_SHORTCUT_BOTTOM
import android.provider.Settings.Secure.PIE_SHORTCUT_TOP
import android.widget.ImageView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settings.core.BasePreferenceController
import com.android.settingslib.widget.LayoutPreference

/** Controller for the Pie preview, which shows the shortcuts assigned to each slot. */
class PiePreviewPreferenceController(context: Context, key: String) :
    BasePreferenceController(context, key), DefaultLifecycleObserver {

    private var topSlot: ImageView? = null
    private var bottomSlot: ImageView? = null
    private val observer =
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) = updatePreview()
        }

    override fun getAvailabilityStatus() = AVAILABLE

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        val preference = screen.findPreference<LayoutPreference>(preferenceKey)!!
        topSlot = preference.findViewById(R.id.pie_preview_top)
        bottomSlot = preference.findViewById(R.id.pie_preview_bottom)
        updatePreview()
    }

    override fun onStart(owner: LifecycleOwner) {
        for (setting in listOf(PIE_SHORTCUT_TOP, PIE_SHORTCUT_BOTTOM)) {
            mContext.contentResolver.registerContentObserver(
                Settings.Secure.getUriFor(setting),
                /* notifyForDescendants= */ false,
                observer,
            )
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        mContext.contentResolver.unregisterContentObserver(observer)
    }

    private fun updatePreview() {
        topSlot?.setImageResource(slotIcon(PIE_SHORTCUT_TOP))
        bottomSlot?.setImageResource(slotIcon(PIE_SHORTCUT_BOTTOM))
    }

    private fun slotIcon(setting: String): Int {
        val shortcut = PieShortcut.get(mContext, setting)
        return if (shortcut == PieShortcut.NONE) R.drawable.pie_preview_dot else shortcut.icon
    }
}

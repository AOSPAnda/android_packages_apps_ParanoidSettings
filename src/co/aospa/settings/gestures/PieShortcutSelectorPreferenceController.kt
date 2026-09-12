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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settings.core.BasePreferenceController
import com.android.settingslib.widget.LayoutPreference

/**
 * Controller for the shortcut selector of a single Pie slot. The preference key is the
 * name of the setting it writes.
 */
class PieShortcutSelectorPreferenceController(context: Context, key: String) :
    BasePreferenceController(context, key), DefaultLifecycleObserver {

    private val buttons = mutableMapOf<PieShortcut, View>()
    private val observer =
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) = updateButtons()
        }

    override fun getAvailabilityStatus() = AVAILABLE

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        val preference = screen.findPreference<LayoutPreference>(preferenceKey)!!
        val container = preference.findViewById<ViewGroup>(R.id.pie_shortcut_container)
        val inflater = LayoutInflater.from(container.context)

        container.removeAllViews()
        buttons.clear()
        for (shortcut in PieShortcut.entries) {
            val button = inflater.inflate(R.layout.pie_shortcut_button, container, false)
            button.requireViewById<ImageView>(android.R.id.icon).setImageResource(shortcut.icon)
            button.requireViewById<TextView>(android.R.id.title).setText(shortcut.label)
            button.setOnClickListener {
                Settings.Secure.putInt(mContext.contentResolver, preferenceKey, shortcut.value)
            }
            container.addView(button)
            buttons[shortcut] = button
        }
        updateButtons()
    }

    override fun onStart(owner: LifecycleOwner) {
        // The other slot's shortcut is disabled here, so observe both.
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

    private fun updateButtons() {
        val otherSetting =
            if (preferenceKey == PIE_SHORTCUT_TOP) PIE_SHORTCUT_BOTTOM else PIE_SHORTCUT_TOP
        val selected = PieShortcut.get(mContext, preferenceKey)
        val other = PieShortcut.get(mContext, otherSetting)

        buttons.forEach { (shortcut, button) ->
            // A shortcut can only be assigned to one slot, except for none.
            val enabled = shortcut == PieShortcut.NONE || shortcut != other
            button.isSelected = shortcut == selected
            button.isEnabled = enabled
            button.alpha = if (enabled) 1f else DISABLED_ALPHA
        }
    }

    private companion object {
        const val DISABLED_ALPHA = 0.38f
    }
}

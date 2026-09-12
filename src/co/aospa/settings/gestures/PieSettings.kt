/*
 * SPDX-FileCopyrightText: 2026 Paranoid Android
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.settings.gestures

import com.android.settings.R
import com.android.settings.dashboard.DashboardFragment

/** Gesture settings for Pie. */
class PieSettings : DashboardFragment() {
    override fun getPreferenceScreenResId() = R.xml.pie_settings

    override fun getLogTag() = "PieSettings"

    override fun getMetricsCategory() = -1
}

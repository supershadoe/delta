/*
 * Adopted from source of android.net.wifi.WifiManager
 *
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package dev.shadoe.hotspotapi.wrappers

import androidx.annotation.IntDef

object SoftApEnabledState {
    /**
     * Wi-Fi AP is currently being disabled. The state will change to
     * {@link #WIFI_AP_STATE_DISABLED} if it finishes successfully.
     */
    const val WIFI_AP_STATE_DISABLING = 10

    /**
     * Wi-Fi AP is disabled.
     */
    const val WIFI_AP_STATE_DISABLED = 11

    /**
     * Wi-Fi AP is currently being enabled. The state will change to
     * {@link #WIFI_AP_STATE_ENABLED} if it finishes successfully.
     */
    const val WIFI_AP_STATE_ENABLING = 12

    /**
     * Wi-Fi AP is enabled.
     */
    const val WIFI_AP_STATE_ENABLED = 13

    /**
     * Wi-Fi AP is in a failed state. This state will occur when an error occurs during
     * enabling or disabling
     */
    const val WIFI_AP_STATE_FAILED = 14

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        value = [
            WIFI_AP_STATE_DISABLING,
            WIFI_AP_STATE_DISABLED,
            WIFI_AP_STATE_ENABLING,
            WIFI_AP_STATE_ENABLED,
            WIFI_AP_STATE_FAILED,
        ],
    )
    annotation class EnabledStateType
}

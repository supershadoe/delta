/*
 * Adopted from source of android.net.wifi.SoftApConfiguration
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

package dev.shadoe.hotspotapi

object WifiSecurityTypes {
    /**
     * THe definition of security type OPEN.
     */
    const val SECURITY_TYPE_OPEN = 0

    /**
     * The definition of security type WPA2-PSK.
     */
    const val SECURITY_TYPE_WPA2_PSK = 1

    /**
     * The definition of security type WPA3-SAE Transition mode.
     */
    const val SECURITY_TYPE_WPA3_SAE_TRANSITION = 2

    /**
     * The definition of security type WPA3-SAE.
     */
    const val SECURITY_TYPE_WPA3_SAE = 3

    /**
     * The definition of security type WPA3-OWE Transition.
     */
    const val SECURITY_TYPE_WPA3_OWE_TRANSITION = 4

    /**
     * The definition of security type WPA3-OWE.
     */
    const val SECURITY_TYPE_WPA3_OWE = 5
}
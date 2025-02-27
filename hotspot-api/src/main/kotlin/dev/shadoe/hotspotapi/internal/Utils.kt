package dev.shadoe.hotspotapi.internal

import java.util.UUID

internal object Utils {
    /*
     * Copyright (C) 2023 The Android Open Source Project
     * SPDX-License-Identifier: Apache-2.0
     * From https://cs.android.com/android/platform/superproject/main/+/29fbb69343c063b65d71180d04f5d2acaf4f050c:packages/apps/Settings/src/com/android/settings/wifi/repository/WifiHotspotRepository.java;l=168
     */
    fun generateRandomPassword(): String {
        val randomUUID = UUID.randomUUID().toString()
        // first 12 chars from xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx
        return randomUUID.substring(0, 8) + randomUUID.substring(9, 13)
    }
}

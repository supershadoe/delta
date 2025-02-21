package dev.shadoe.hotspotapi

import java.util.UUID

internal object Utils {
    /*
         * Copyright (C) 2023 The Android Open Source Project
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
         * From https://cs.android.com/android/platform/superproject/main/+/29fbb69343c063b65d71180d04f5d2acaf4f050c:packages/apps/Settings/src/com/android/settings/wifi/repository/WifiHotspotRepository.java;l=168
         */
    fun generateRandomPassword(): String {
        val randomUUID = UUID.randomUUID().toString()
        //first 12 chars from xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx
        return randomUUID.substring(0, 8) + randomUUID.substring(9, 13)
    }
    infix fun Int.hasBit(other: Int): Boolean = (this and other) == other
    infix fun Long.hasBit(other: Long): Boolean = (this and other) == other
}


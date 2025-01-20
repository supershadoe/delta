/*
 * Copyright (C) 2024 The Android Open Source Project
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
 */

package android.net.wifi;

import android.net.TetheringManager;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A class representing the current state of SoftAp.
 */
public final class SoftApState implements Parcelable {
    /**
     * SoftApState constructor.
     *
     * @param state            Current state of the Soft AP.
     * @param failureReason    Failure reason if the current state is
     *                         @link WifiManager#WIFI_AP_STATE_FAILED.
     * @param tetheringRequest TetheringRequest if one was specified when Soft AP was requested,
     *                         else {@code null}.
     * @param iface            Interface name if an interface was created, else {@code null}.
     */
    public SoftApState(int state, int failureReason, @Nullable TetheringManager.TetheringRequest tetheringRequest, @Nullable String iface) {
        throw new RuntimeException("stub!");
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        throw new RuntimeException("stub!");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    public static final Creator<SoftApState> CREATOR = new Creator<SoftApState>() {
        @Override
        @NonNull
        public SoftApState createFromParcel(Parcel in) {
            throw new RuntimeException("stub!");
        }

        @Override
        @NonNull
        public SoftApState[] newArray(int size) {
            throw new RuntimeException("stub!");
        }
    };

    /**
     * Get the AP state.
     */
    public int getState() {
        throw new RuntimeException("stub!");
    }

    /**
     * Get the failure reason
     */
    public int getFailureReason() {
        throw new RuntimeException("stub!");
    }

    public int getFailureReasonInternal() {
        throw new RuntimeException("stub!");
    }

    /**
     * Gets the TetheringRequest of the Soft AP
     */
    @Nullable
    public TetheringManager.TetheringRequest getTetheringRequest() {
        throw new RuntimeException("stub!");
    }

    /**
     * Gets the interface name of the Soft AP (e.g. "wlan0") once the Soft AP starts enabling.
     * Returns {@code null} if the Soft AP hasn't started enabling yet, or if it failed with
     * WifiManager#WIFI_AP_STATE_FAILED without starting enabling.
     */
    @Nullable
    public String getIface() {
        throw new RuntimeException("stub!");
    }

    @Override
    public String toString() {
        throw new RuntimeException("stub!");
    }

    @Override
    public boolean equals(Object o) {
        throw new RuntimeException("stub!");
    }

    @Override
    public int hashCode() {
        throw new RuntimeException("stub!");
    }
}

/**
 * Copyright (c) 2008, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.net.wifi;

import android.net.wifi.IOnWifiDriverCountryCodeChangedListener;
import android.net.wifi.ISoftApCallback;
import android.net.wifi.IStringListener;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiAvailableChannel;

interface IWifiManager
{
    void registerDriverCountryCodeChangedListener(
            in IOnWifiDriverCountryCodeChangedListener listener, String packageName,
            String featureId);
    void unregisterDriverCountryCodeChangedListener(
            in IOnWifiDriverCountryCodeChangedListener listener);
    boolean is24GHzBandSupported();
    boolean is5GHzBandSupported();
    boolean is6GHzBandSupported();
    boolean is60GHzBandSupported();
    boolean validateSoftApConfiguration(in SoftApConfiguration config);
    int getWifiApEnabledState();
    SoftApConfiguration getSoftApConfiguration();
    void queryLastConfiguredTetheredApPassphraseSinceBoot(IStringListener listener);
    boolean setSoftApConfiguration(in SoftApConfiguration softApConfig, String packageName);
    void registerSoftApCallback(in ISoftApCallback callback);
    void unregisterSoftApCallback(in ISoftApCallback callback);
    List<WifiAvailableChannel> getUsableChannels(int band, int mode, int filter, String packageName, in Bundle extras);
}

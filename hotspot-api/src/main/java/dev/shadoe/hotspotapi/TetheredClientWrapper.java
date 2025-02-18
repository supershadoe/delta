package dev.shadoe.hotspotapi;

import android.net.LinkAddress;
import android.net.MacAddress;
import android.net.TetheredClient;
import android.net.TetheredClient.AddressInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A wrapper class to wrap {@link android.net.TetheredClient} to let
 * the app use the data without needing to compile with the stubs which
 * makes the app layer kinda messy.
 */
public final class TetheredClientWrapper {
    @NonNull
    private final MacAddress mMacAddress;
    @NonNull
    private final List<LinkAddress> mAddresses;
    @Nullable
    private final List<String> mHostnames;
    private final int mTetheringType;

    public TetheredClientWrapper(TetheredClient c) {
        this.mMacAddress = c.getMacAddress();
        List<LinkAddress> addresses = new ArrayList<>();
        List<String> hostnames = new ArrayList<>();
        this.mTetheringType = c.getTetheringType();

        for (final AddressInfo address : c.getAddresses()) {
            System.out.println(address);
            addresses.add(address.getAddress());
            hostnames.add(address.getHostname());
        }

        this.mAddresses = Collections.unmodifiableList(addresses);
        this.mHostnames = Collections.unmodifiableList(hostnames);
    }

    public MacAddress getMacAddress() {
        return mMacAddress;
    }

    public List<LinkAddress> getAddresses() {
        return mAddresses;
    }

    public List<String> getHostnames() {
        return mHostnames;
    }

    public int getTetheringType() {
        return mTetheringType;
    }
}

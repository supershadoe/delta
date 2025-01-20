package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public final class SoftApCapability implements Parcelable {
    public static final Creator<SoftApCapability> CREATOR = new Creator<>() {
        @Override
        public SoftApCapability createFromParcel(Parcel in) {
            throw new RuntimeException("stub!");
        }

        @Override
        public SoftApCapability[] newArray(int size) {
            throw new RuntimeException("stub!");
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        throw new RuntimeException("stub!");
    }
}
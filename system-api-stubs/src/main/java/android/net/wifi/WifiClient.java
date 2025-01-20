package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public final class WifiClient implements Parcelable {
    public static final Creator<WifiClient> CREATOR = new Creator<>() {
        @Override
        public WifiClient createFromParcel(Parcel in) {
            throw new RuntimeException("stub!");
        }

        @Override
        public WifiClient[] newArray(int size) {
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
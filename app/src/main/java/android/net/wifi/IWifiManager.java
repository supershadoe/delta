package android.net.wifi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;

public interface IWifiManager extends IInterface
{
    SoftApConfiguration getSoftApConfiguration() throws android.os.RemoteException;

    abstract class Stub extends Binder implements IWifiManager
    {
        public Stub()
        {
            throw new UnsupportedOperationException();
        }

        public static IWifiManager asInterface(IBinder obj)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public IBinder asBinder()
        {
            throw new UnsupportedOperationException();
        }
    }
}

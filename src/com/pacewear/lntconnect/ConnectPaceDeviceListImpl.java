
package com.pacewear.lntconnect;

import android.content.Context;

import com.lnt.connectfactorylibrary.ConnectDeviceListImpl;
import com.lnt.connectfactorylibrary.DeviceListImpl;

public class ConnectPaceDeviceListImpl implements ConnectDeviceListImpl {
    private static ConnectDeviceListImpl sInstance = null;
    private PaceApduClient mClient = null;

    private ConnectPaceDeviceListImpl(Context context) {
        mClient = PaceApduClient.getInstance(context);
    }

    public static ConnectDeviceListImpl getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ConnectDeviceListImpl.class) {
                sInstance = new ConnectPaceDeviceListImpl(context);
            }
        }
        return sInstance;
    }

    @Override
    public void getDeviceList(Context arg0, DeviceListImpl arg1) {
        mClient.scan(arg0, arg1);
    }

}

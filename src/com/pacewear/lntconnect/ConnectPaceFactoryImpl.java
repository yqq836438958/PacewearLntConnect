
package com.pacewear.lntconnect;

import android.content.Context;

import com.lnt.connectfactorylibrary.ConnectFactoryImpl;
import com.lnt.connectfactorylibrary.ConnectReturnImpl;

public class ConnectPaceFactoryImpl implements ConnectFactoryImpl {
    private static ConnectFactoryImpl sInstance = null;
    private PaceApduClient mPaceApduClient = null;

    public static ConnectFactoryImpl getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ConnectPaceFactoryImpl.class) {
                sInstance = new ConnectPaceFactoryImpl(context);
            }
        }
        return sInstance;
    }

    private ConnectPaceFactoryImpl(Context context) {
        mPaceApduClient = PaceApduClient.getInstance(context);
    }

    @Override
    public Object closeCommunication() {
        return null;
    }

    @Override
    public Object closeConnection() {
        mPaceApduClient.disconnect();
        return Boolean.TRUE;
    }

    @Override
    public void connection(Context arg0, String arg1, ConnectReturnImpl arg2) {
        mPaceApduClient.connect(arg0, arg1, arg2);
    }

    @Override
    public Object getConnectState() {
        return mPaceApduClient.getConnectState();
    }

    @Override
    public int getElectricQuantity() {
        return -2;
    }

    @Override
    public Object ledShow() {
        return null;
    }

    @Override
    public Object openCommunication() {
        return null;
    }

    @Override
    public Object powerOff() {
        return mPaceApduClient.powerOff();
    }

    @Override
    public Object powerOn() {
        return mPaceApduClient.powerOn();
    }

    @Override
    public Object shake() {
        return null;
    }

    @Override
    public byte[] transmit(byte[] arg0) {
        return mPaceApduClient.transmit(arg0);
    }

}

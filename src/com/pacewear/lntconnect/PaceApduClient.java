
package com.pacewear.lntconnect;

import android.content.Context;
import android.os.RemoteException;

import com.lnt.connectfactorylibrary.BlueToothDevice;
import com.lnt.connectfactorylibrary.ConnectReturnImpl;
import com.lnt.connectfactorylibrary.DeviceListImpl;
import com.pacewear.tws.phoneside.wallet.DeviceInfo;
import com.pacewear.tws.phoneside.wallet.IPaceApduService;
import com.pacewear.tws.phoneside.wallet.IPaceInvokeCallback;
import java.util.ArrayList;

public class PaceApduClient extends AIDLClient {

    private static PaceApduClient sInstance = null;
    private ConnectReturnImpl mLntConnectCallback = null;
    private DeviceListImpl mLntScanCallback = null;

    private boolean toDestroyFlag = false;
    private IPaceInvokeCallback.Stub mInvokeCallback = new IPaceInvokeCallback.Stub() {
        @Override
        public void onConnectResult(boolean isSuc, String mac) throws RemoteException {
            if (mLntConnectCallback != null) {
                mLntConnectCallback.connectResult(isSuc, mac);
            }
            toDestroyFlag = !isSuc;
        }

        @Override
        public void onScanResult(DeviceInfo[] infos) throws RemoteException {
            if (mLntScanCallback != null) {
                ArrayList<BlueToothDevice> list = new ArrayList<BlueToothDevice>();
                for (DeviceInfo info : infos) {
                    BlueToothDevice device = new BlueToothDevice();
                    device.setAddress(info.getMacAddr());
                    device.setName(info.getName());
                    list.add(device);
                }
                mLntScanCallback.devicesResult(list);
            }
        }
    };

    public static PaceApduClient getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PaceApduClient.class) {
                sInstance = new PaceApduClient(context);
            }
        }
        return sInstance;
    }

    public IPaceApduService get() {
        return mService;
    }

    private PaceApduClient(Context context) {
        super(context);
    }

    public void scan(final Context context, final DeviceListImpl arg1) {
        mLntScanCallback = arg1;
        ThreadUtil.getWorkerHandler().post(new Runnable() {

            @Override
            public void run() {
                if (!isServiceReady(context)) {
                    if (arg1 != null) {
                        arg1.devicesResult(null);
                    }
                    return;
                }
                try {
                    mService.scan();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void connect(final Context context, final String mac, final ConnectReturnImpl callback) {
        mLntConnectCallback = callback;
        ThreadUtil.getWorkerHandler().post(new Runnable() {

            @Override
            public void run() {
                if (!isServiceReady(context)) {
                    if (callback != null) {
                        callback.connectResult(false, "");
                    }
                    return;
                }
                try {
                    mService.connect(mac);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void disconnect() {
        if (mService != null) {
            try {
                mService.disconnect();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        toDestroyFlag = true;
    }

    public Object getConnectState() {
        if (mService == null) {
            return Boolean.FALSE;
        }
        DeviceInfo info = new DeviceInfo();
        try {
            mService.getDeviceInfo(info);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return info.isConnect();
    }

    public Object powerOff() {
        Boolean result = Boolean.FALSE;
        if (mService == null) {
            return result;
        }
        int exeRet = -1;
        try {
            exeRet = mService.close();
        } catch (RemoteException e) {
            result = Boolean.FALSE;
            e.printStackTrace();
        }
        result = (exeRet == 0) ? Boolean.TRUE : Boolean.FALSE;
        return result;
    }

    public Object powerOn() {
        Boolean result = Boolean.FALSE;
        if (mService == null) {
            return result;
        }
        int exeRet = -1;
        try {
            exeRet = mService.selectAid("A000000151000000");
        } catch (RemoteException e) {
            result = Boolean.FALSE;
            e.printStackTrace();
        }
        result = (exeRet == 0) ? Boolean.TRUE : Boolean.FALSE;
        return result;
    }

    public byte[] transmit(byte[] arg0) {
        if (mService == null) {
            return null;
        }
        try {
            return mService.transmit(arg0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onServiceConnect() {
        if (mService != null) {
            try {
                mService.create(mInvokeCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onServiceDisconnect() {
        if (mService != null) {
            try {
                mService.destory(mInvokeCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean needDestroy() {
        return toDestroyFlag;
    }

}

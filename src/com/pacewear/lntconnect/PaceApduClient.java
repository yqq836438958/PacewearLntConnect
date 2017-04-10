
package com.pacewear.lntconnect;

import android.app.ActivityManager;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.lnt.connectfactorylibrary.BlueToothDevice;
import com.lnt.connectfactorylibrary.ConnectReturnImpl;
import com.lnt.connectfactorylibrary.DeviceListImpl;
import com.tws.plugin.aidl.PaceServiceAIDL;
import com.tws.plugin.aidl.PaceInfo;
import com.tws.plugin.aidl.IPaceCallBack;
import java.util.ArrayList;

public class PaceApduClient extends AIDLClient {

    private static PaceApduClient sInstance = null;
    private ConnectReturnImpl mLntConnectCallback = null;
    private DeviceListImpl mLntScanCallback = null;

    private IPaceCallBack.Stub mInvokeCallback = new IPaceCallBack.Stub() {
        @Override
        public void onConnectResult(boolean isSuc, String mac) throws RemoteException {
            if (mLntConnectCallback != null) {
                mLntConnectCallback.connectResult(isSuc, mac);
            }
            if (isSuc) {
                aquireLock();
            } else {
                releaseLock();
            }
        }

        @Override
        public void onScanResult(PaceInfo[] infos) throws RemoteException {
            if (mLntScanCallback != null) {
                ArrayList<BlueToothDevice> list = new ArrayList<BlueToothDevice>();
                for (PaceInfo info : infos) {
                    if (info == null) {
                        continue;
                    }
                    BlueToothDevice device = new BlueToothDevice();
                    device.setAddress(info.getMacAddr());
                    device.setName(info.getDevName());
                    Log.d(TAG, "device:addr:" + info.getMacAddr() + ",sName:" + device.getName());
                    list.add(device);
                }
                mLntScanCallback.devicesResult(list);
            }
            releaseLock();
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

    public PaceServiceAIDL get() {
        return mService;
    }

    private PaceApduClient(Context context) {
        super(context);
    }

    public void scan(final Context context, final DeviceListImpl arg1) {
        mLntScanCallback = arg1;
        ThreadUtil.getWorkerHandler().removeCallbacksAndMessages(null);
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
        aquireLock();
    }

    public void connect(final Context context, final String mac, final ConnectReturnImpl callback) {
        mLntConnectCallback = callback;
        ThreadUtil.getWorkerHandler().removeCallbacksAndMessages(null);
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
        aquireLock();
    }

    public void disconnect() {
        if (!isPaceClientProccess()) {
            if (isServiceReady(mContext)) {
                try {
                    mService.disconnect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        releaseLock();
    }

    public Object getConnectState() {
        if (!isServiceReady(mContext)) {
            return Boolean.FALSE;
        }
        PaceInfo info = new PaceInfo();
        try {
            mService.getDeviceInfo(info);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return info.isConnect();
    }

    public Object powerOff() {
        Boolean result = Boolean.FALSE;
        if (!isServiceReady(mContext)) {
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
        if (!isServiceReady(mContext)) {
            return result;
        }
        int exeRet = -1;
        try {
            exeRet = mService.selectAid("5943542E55534552");
        } catch (RemoteException e) {
            result = Boolean.FALSE;
            e.printStackTrace();
        }
        result = (exeRet == 0) ? Boolean.TRUE : Boolean.FALSE;
        return result;
    }

    public byte[] transmit(byte[] arg0) {
        if (!isServiceReady(mContext)) {
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

    }

    private boolean isPaceClientProccess() {
        if (mContext == null) {
            return false;
        }
        ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == android.os.Process.myPid()) {
                if (appProcess.processName.equals(RunEnv.DM_PACKAGE)) {
                    return true;
                }
            }
        }
        return false;
    }
}


package com.pacewear.lntconnect;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.pacewear.lock.IJavaLock;
import com.pacewear.lock.JavaLockHelper;
import com.tws.plugin.aidl.PaceServiceAIDL;

public abstract class AIDLClient implements IServiceLocker {
    public static final String TAG = "Lnt";
    public static final String PACEAPDU_ACTION = "com.pacewear.tws.phoneside.wallet.action_apdu";
    protected Context mContext;
    protected volatile PaceServiceAIDL mService;
    private ServiceLockHandler mController = null;
    private IJavaLock mJavaLock = null;

    public AIDLClient(Context context) {
        mContext = context;
        mJavaLock = JavaLockHelper.newLock();
        if (!bindRemoteService(context)) {
            bindCustomRemoteService(context);
        }
        mController = new ServiceLockHandler(this);
    }

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            Log.e(TAG, "remote binder has died,binderDied ");
            if (mService == null)
                return;
            mService.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mService = null;
            onServiceDetroy();
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected Thread:" + Thread.currentThread().getName());
            mService = null;
            mJavaLock.unlock();
            onServiceDisconnect();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected Thread:" + Thread.currentThread().getName());
            try {
                service.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mService = PaceServiceAIDL.Stub.asInterface(service);
            mJavaLock.unlock();
            onServiceConnect();
        }
    };

    public boolean isServiceReady(Context context) {
        Log.d(TAG, "isServiceReady begin");
        if (mService != null) {
            Log.d(TAG, "isServiceReady: mService not null,direct use");
            return true;
        }
        Log.d(TAG, "isServiceReady: need bindservice sync");
        if (!bindRemoteService(context) && !bindCustomRemoteService(context)) {
            Log.e(TAG, "isServiceReady: binde service error");
            return false;
        }
        Log.d(TAG, "isServiceReady: wait service ok");
        mJavaLock.lock(0);

        return mService != null;
    }

    private boolean bindRemoteService(Context context) {
        Intent intent = new Intent(PACEAPDU_ACTION);
        intent.setPackage(RunEnv.DM_PACKAGE);
        return context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private boolean bindCustomRemoteService(Context context) {
//        Intent intent = new Intent();
//        intent.setComponent(new ComponentName(RunEnv.DM_PACKAGE, RunEnv.DM_PACE_SERVICE));
//        return context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        return false;
    }

    private void unbindRemoteService() {
        if (mContext != null && mService != null) {
            mContext.unbindService(mConnection);
        }
    }

    protected void onServiceDetroy() {
        Log.e(TAG, "service is Destory");
        // 预留接口
    }

    protected void aquireLock() {
        mController.onLockRequire();
    }

    protected void releaseLock() {
        mController.onLockRelease();
    }

    @Override
    public void onHandleLockMessage(boolean lock) {
        if (!lock) {
            // 不持锁的情况下解绑服务
            unbindRemoteService();
            onServiceDetroy();
        } else {
            // todo
        }
    }

    protected abstract void onServiceConnect();

    protected abstract void onServiceDisconnect();

}

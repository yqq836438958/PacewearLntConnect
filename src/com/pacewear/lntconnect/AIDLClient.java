
package com.pacewear.lntconnect;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.pacewear.tws.phoneside.wallet.IPaceApduService;

import java.util.concurrent.Semaphore;

public abstract class AIDLClient implements IServiceController {
    public static final String TAG = "Lnt";
    public static final String PACEAPDU_ACTION = "com.pacewear.tws.phoneside.wallet.IPaceApduService";
    protected Context mContext;
    protected IPaceApduService mService;
    private Semaphore mSemaphore = null;

    public AIDLClient(Context context) {
        mContext = context;
        mSemaphore = new Semaphore(0);
        if (!bindRemoteService(context)) {
            bindCustomRemoteService(context);
        }
        new LowPowerController(this).reset();
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
            mSemaphore.release();
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
            mService = IPaceApduService.Stub.asInterface(service);
            mSemaphore.release();
            onServiceConnect();
        }
    };

    public boolean isServiceReady(Context context) {
        if (mService != null) {
            return true;
        }
        if (!bindRemoteService(context) && !bindCustomRemoteService(context)) {
            return false;
        }
        try {
            mSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mService != null;
    }

    private boolean bindRemoteService(Context context) {
        Intent intent = new Intent(PACEAPDU_ACTION);
        intent.setPackage(RunEnv.DM_PACKAGE);
        return context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private boolean bindCustomRemoteService(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(RunEnv.DM_PACKAGE, RunEnv.DM_PACE_SERVICE));
        return context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindRemoteService() {
        if (mContext != null) {
            mContext.unbindService(mConnection);
        }
    }

    @Override
    public void destory() {
        unbindRemoteService();
        onServiceDetroy();
    }

    protected void onServiceDetroy() {
        Log.e(TAG, "service is Destory");
        // 预留接口
    }

    protected abstract void onServiceConnect();

    protected abstract void onServiceDisconnect();

}

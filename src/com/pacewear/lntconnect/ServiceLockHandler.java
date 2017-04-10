
package com.pacewear.lntconnect;

public class ServiceLockHandler {
    private long DURATION_SHUTDOWN = 5 * 60 * 1000; // TODO

    private Runnable mShutdownBusinessRun = null;
    public static final String TAG = "Lnt";
    private boolean mServiceLock = false;

    public ServiceLockHandler(final IServiceLocker serviceLocker) {
        mShutdownBusinessRun = new Runnable() {

            @Override
            public void run() {
                if (serviceLocker != null) {
                    serviceLocker.onHandleLockMessage(mServiceLock);
                }
            }
        };
        onLockChange();
    }

    private void onLockChange() {
//        ThreadUtil.getWorkerHandler().removeCallbacks(mShutdownBusinessRun);
//        if (!mServiceLock) {
//            ThreadUtil.getWorkerHandler().postDelayed(mShutdownBusinessRun, DURATION_SHUTDOWN);
//        }
    }

    public void onLockRequire() {
        mServiceLock = true;
        onLockChange();
    }

    public void onLockRelease() {
        mServiceLock = false;
        onLockChange();
    }
}

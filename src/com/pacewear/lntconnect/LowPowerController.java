
package com.pacewear.lntconnect;

import android.util.Log;

public class LowPowerController {
    // private final long DURATION_SHUTDOWN = 10 * 60 * 1000;
    private long DURATION_SHUTDOWN = 10 * 1000; // TODO

    private Runnable mShutdownBusinessRun = null;
    public static final String TAG = "Lnt";

    public LowPowerController(final IServiceController controller) {
        mShutdownBusinessRun = new Runnable() {

            @Override
            public void run() {
                if (controller != null) {
                    switch (controller.getStatus()) {
                        case IServiceController.STA_DESTROY:
                            Log.d(TAG, "needDestroy, for save power");
                            controller.destory();
                            break;
                        case IServiceController.STA_RESET:
                            Log.d(TAG, "service changed,reset count");
                            reset();
                            break;
                        case IServiceController.STA_COUNT:
                        default:
                            Log.d(TAG, "service is busy now, later it will shutdown");
                            break;
                    }
                }
            }
        };
    }

    public void reset() {
        ThreadUtil.getWorkerHandler().removeCallbacks(mShutdownBusinessRun);
        ThreadUtil.getWorkerHandler().postDelayed(mShutdownBusinessRun, DURATION_SHUTDOWN);
    }
}

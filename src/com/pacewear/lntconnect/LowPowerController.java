
package com.pacewear.lntconnect;

public class LowPowerController {
    // private final long DURATION_SHUTDOWN = 10 * 60 * 1000;
    private long DURATION_SHUTDOWN = 10 * 1000; // TODO

    private Runnable mShutdownBusinessRun = null;

    public LowPowerController(final IServiceController controller) {
        mShutdownBusinessRun = new Runnable() {

            @Override
            public void run() {
                if (controller != null) {
                    if (controller.needDestroy()) {
                        controller.destory();
                    } else {
                        ThreadUtil.getWorkerHandler().postDelayed(mShutdownBusinessRun,
                                DURATION_SHUTDOWN);
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


package com.pacewear.lntconnect;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * @author baodingzhou
 */

public class ThreadUtil {

    private static final HandlerThread mWorkerHandlerThread = new HandlerThread("PaceLntThread");

    private volatile static Looper mWorkerLooper = null;

    private volatile static Handler mWorkerHandler = null;

    /**
     * getWorkerlooper
     * 
     * @return
     */
    public static Looper getWorkerlooper() {
        if (mWorkerLooper == null) {
            synchronized (ThreadUtil.class) {
                if (mWorkerLooper == null) {
                    mWorkerHandlerThread.start();
                    mWorkerLooper = mWorkerHandlerThread.getLooper();
                }
            }
        }
        return mWorkerLooper;
    }

    /**
     * getWorkerHandler
     * 
     * @return
     */
    public static Handler getWorkerHandler() {
        if (mWorkerHandler == null) {
            synchronized (ThreadUtil.class) {
                if (mWorkerHandler == null) {
                    mWorkerHandler = new Handler(getWorkerlooper());
                }
            }
        }

        return mWorkerHandler;
    }

}

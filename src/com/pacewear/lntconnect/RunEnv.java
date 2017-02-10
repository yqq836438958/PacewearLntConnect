
package com.pacewear.lntconnect;

import android.content.Context;

public class RunEnv {
    // public static final String DM_PACKAGE = "com.tencent.tws.gdevicemanager";
    public static final String DM_PACKAGE = "com.clj.blesample";
    public static final String DM_PACE_SERVICE = "com.pacewear.tws.phoneside.wallet.service.PaceApduService";
    private static boolean isDmProcess = false;
    private static Context sGlobalContext = null;

    public static void initProcessEnv(Context context) {
        String packageName = context.getPackageName();
        isDmProcess = DM_PACKAGE.equalsIgnoreCase(packageName);
        if (context != sGlobalContext) {
            sGlobalContext = context;
        }
    }

    public static Context getContext() {
        return sGlobalContext;
    }

    public static boolean isInPacewearProccess() {
        return isDmProcess;
    }

}

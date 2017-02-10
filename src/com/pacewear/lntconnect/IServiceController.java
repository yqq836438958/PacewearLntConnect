
package com.pacewear.lntconnect;

public interface IServiceController {
    public static final int STA_COUNT = 0;
    public static final int STA_DESTROY = 1;
    public static final int STA_RESET = 2;

    public int getStatus();

    public void destory();
}

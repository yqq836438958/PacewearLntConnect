package com.pacewear.tws.phoneside.wallet;
import com.pacewear.tws.phoneside.wallet.DeviceInfo;

interface IPaceInvokeCallback {
	void onConnectResult(boolean isSuc,String mac);
	void onScanResult(out DeviceInfo[] infos);
}
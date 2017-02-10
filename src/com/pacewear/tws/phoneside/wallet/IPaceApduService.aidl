package com.pacewear.tws.phoneside.wallet;
import com.pacewear.tws.phoneside.wallet.IPaceInvokeCallback;
import com.pacewear.tws.phoneside.wallet.DeviceInfo;

interface IPaceApduService {
	int create(IPaceInvokeCallback callback);
	int destory(IPaceInvokeCallback callback);
	int connect(String macId);
	int disconnect();
	int scan();
	int getDeviceInfo(out DeviceInfo info);
	int selectAid(String aid);
	byte[] transmit(in byte[] apdus);
	int close();
}
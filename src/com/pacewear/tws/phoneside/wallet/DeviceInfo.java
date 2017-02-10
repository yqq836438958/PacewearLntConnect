
package com.pacewear.tws.phoneside.wallet;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceInfo implements Parcelable {
    private int iBatteryLv;
    private String sMacAddr;
    private String sName;
    private boolean bConnect;
    public static final Parcelable.Creator<DeviceInfo> CREATOR = new Creator<DeviceInfo>() {

        @Override
        public DeviceInfo[] newArray(int size) {
            return new DeviceInfo[size];
        }

        @Override
        public DeviceInfo createFromParcel(Parcel source) {
            return new DeviceInfo(source);
        }
    };

    public DeviceInfo() {
        super();
    }

    public DeviceInfo(Parcel source) {
        iBatteryLv = source.readInt();
        sMacAddr = source.readString();
        sName = source.readString();
        bConnect = (source.readByte() == 1);
    }

    public boolean isConnect() {
        return bConnect;
    }

    public void setConnect(boolean connect) {
        bConnect = connect;
    }

    public String getMacAddr() {
        return sMacAddr;
    }

    public String getName() {
        return sName;
    }

    public void setName(String name) {
        sName = name;
    }

    public void setMacAddr(String mac) {
        sMacAddr = mac;
    }

    public static Parcelable.Creator<DeviceInfo> getCreator() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(iBatteryLv);
        dest.writeString(sMacAddr);
        dest.writeString(sName);
        dest.writeByte((byte) (bConnect ? 1 : 0));
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void readFromParcel(Parcel _reply) {
        iBatteryLv = _reply.readInt();
        sMacAddr = _reply.readString();
        sName = _reply.readString();
        bConnect = (_reply.readByte() == 1);
    }

}

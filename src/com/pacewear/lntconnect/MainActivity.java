
package com.pacewear.lntconnect;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lnt.connectfactorylibrary.BlueToothDevice;
import com.lnt.connectfactorylibrary.ConnectFactoryImpl;
import com.lnt.connectfactorylibrary.ConnectReturnImpl;
import com.lnt.connectfactorylibrary.DeviceListImpl;

import java.util.ArrayList;

public class MainActivity extends Activity implements OnClickListener {
    public static final String TAG = "Lnt";
    private Handler mAsyncHandler = null;
    private Looper mAsyncLooper = null;
    private Button mLntConnectBtn = null;
    private Button mLntDisconnectBtn = null;
    private Button mLntTransmitBtn = null;
    private Button mLntScanBtn = null;
    private Button mGetInfBtn = null;
    private ConnectFactoryImpl impl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLntConnectBtn = (Button) findViewById(R.id.connect);
        mLntDisconnectBtn = (Button) findViewById(R.id.disconnect);
        mLntTransmitBtn = (Button) findViewById(R.id.transmit);
        mLntScanBtn = (Button) findViewById(R.id.scan);
        mGetInfBtn = (Button) findViewById(R.id.getinfo);
        mLntConnectBtn.setOnClickListener(this);
        mLntDisconnectBtn.setOnClickListener(this);
        mLntTransmitBtn.setOnClickListener(this);
        mLntScanBtn.setOnClickListener(this);
        mGetInfBtn.setOnClickListener(this);
        HandlerThread thread = new HandlerThread("Test");
        thread.start();
        mAsyncLooper = thread.getLooper();
        mAsyncHandler = new Handler(mAsyncLooper);
        // new Test().test();
        mAsyncHandler.post(new Runnable() {

            @Override
            public void run() {
                runLntTest();

            }
        });
    }

    @Override
    public void onClick(final View arg0) {/*
        mAsyncHandler.removeCallbacksAndMessages(null);
        mAsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (arg0.getId()) {
                    case R.id.connect:
                        impl.connection(getApplicationContext(), "66:36:46:66:30:FF",
                                new ConnectReturnImpl() {

                            @Override
                            public void connectResult(boolean arg0, String arg1) {
                                Log.d(TAG, "connect Result:" + arg0 + ",desc:" + arg1);
                            }

                        });
                        break;
                    case R.id.disconnect:
                        impl.closeConnection();
                        break;
                    case R.id.transmit:
                        impl.powerOn();
                        impl.transmit(ByteUtil.toByteArray("00A40400085943542E5553455200"));
                        impl.transmit(ByteUtil.toByteArray("00A4000002DDF1"));
                        impl.transmit(ByteUtil.toByteArray("00B0950058"));
                        impl.transmit(ByteUtil.toByteArray("00A4000002ADF3"));
                        impl.transmit(ByteUtil.toByteArray("805C000204"));
                        impl.powerOff();
                        break;
                    case R.id.scan:
                        ConnectPaceDeviceListImpl.getInstance(MainActivity.this)
                                .getDeviceList(MainActivity.this, new DeviceListImpl() {

                            @Override
                            public void devicesResult(ArrayList<BlueToothDevice> arg0) {
                                if (arg0 != null && arg0.size() > 0) {
                                    for (BlueToothDevice device : arg0) {
                                        Log.d(TAG, "devicesResult：" + device.getAddress() + ",name:"
                                                + device.getName());
                                    }
                                } else {
                                    Log.d(TAG, "devicesResult null");
                                }
                            }
                        });
                        break;
                    case R.id.getinfo:
                        impl.getConnectState();
                        break;
                    default:
                        break;
                }
            }
        });

    */}

    private void runLntTest() {
        // ConnectFactoryImpl impl = ConnectPaceFactoryImpl.getInstance(getApplicationContext());
        // impl.powerOn();
        // // LntConnectTest.transmit(ByteUtil.toByteArray("80500C0008D98B767CE221C2E1"));
        // impl.transmit(new byte[] {
        // 0x00, 0x01, 0x02, 0x04
        // });
        // impl.powerOff();
        impl = ConnectPaceFactoryImpl.getInstance(getApplicationContext());
    }
}

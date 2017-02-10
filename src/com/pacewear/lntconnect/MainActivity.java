
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

import com.lnt.connectfactorylibrary.ConnectFactoryImpl;
import com.lnt.connectfactorylibrary.ConnectReturnImpl;

public class MainActivity extends Activity implements OnClickListener {
    public static final String TAG = "Lnt";
    private Handler mAsyncHandler = null;
    private Looper mAsyncLooper = null;
    private Button mLntConnectBtn = null;
    private Button mLntDisconnectBtn = null;
    private Button mLntTransmitBtn = null;
    private ConnectFactoryImpl impl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLntConnectBtn = (Button) findViewById(R.id.connect);
        mLntDisconnectBtn = (Button) findViewById(R.id.disconnect);
        mLntTransmitBtn = (Button) findViewById(R.id.transmit);
        mLntConnectBtn.setOnClickListener(this);
        mLntDisconnectBtn.setOnClickListener(this);
        mLntTransmitBtn.setOnClickListener(this);
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
    public void onClick(final View arg0) {
        mAsyncHandler.removeCallbacksAndMessages(null);
        mAsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (arg0.getId()) {
                    case R.id.connect:
                        impl.connection(getApplicationContext(), "010203040506",
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
                        // LntConnectTest.transmit(ByteUtil.toByteArray("80500C0008D98B767CE221C2E1"));
                        impl
                                .transmit(new byte[] {
                                        0x00, 0x01, 0x02, 0x04
                        });
                        impl.powerOff();
                        break;
                    default:
                        break;
                }
            }
        });

    }

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
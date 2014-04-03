package com.elsinga.sample.bluetooth.glass;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ktlab.lib.connection.ConnectionCallback;
import ktlab.lib.connection.ConnectionCommand;
import ktlab.lib.connection.bluetooth.ClientBluetoothConnection;


public class MainActivity extends Activity implements GestureDetector.OnGestureListener {

    private static final String TAG = "SampleBluetooth";

    private static final int DEVICE_SELECT_REQUEST_CODE = 14123;
    private static final byte STRING_DATA = 43;
    private static final int STRING_DATA_COMMAND_ID = 11;

    private GestureDetector _gestureDetector;

    private ClientBluetoothConnection _senderConnection;

    private BluetoothDevice _device;

    private TextView _tvStatus;
    private ImageView _ivExplanation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        _tvStatus = (TextView) findViewById(R.id.status);
        _ivExplanation = (ImageView) findViewById(R.id.explanation);

        _gestureDetector = new GestureDetector(this, this);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        _gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onStop() {
        if (_senderConnection != null) {
            _senderConnection.stopConnection();
        }

        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DEVICE_SELECT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                BluetoothDevice device = data.getParcelableExtra(SelectDeviceActivity.BLUETOOTH_DEVICE);

                if (device != null) {
                    _device = device;
                    if (_senderConnection != null) {
                        _senderConnection.stopConnection();
                    }
                    startBluetoothSender();
                }
            }
        }
    }

    private void startBluetoothSender() {
        if (_device == null) {
            Log.i(TAG, "Not paired");
        } else {
            _senderConnection = new ClientBluetoothConnection(new ConnectionCallback() {

                @Override
                public void onConnectComplete() {
                    Log.i(TAG, "Client#onConnectComplete");
                    sendTextData("Connected with Glass");
                    _ivExplanation.setImageResource(R.drawable.pair_successful);
                    _tvStatus.setText(R.string.instruction_tap);

                }

                @Override
                public void onConnectionFailed() {
                    Log.i(TAG, "Client#onConnectionFailed");
                    _ivExplanation.setImageResource(R.drawable.pair_failed);
                    _tvStatus.setText(R.string.instruction_paired_failed);
                }

                @Override
                public void onDataSendComplete(int id) {
                    Log.i(TAG, "Client#onDataSendComplete: " + id);
                }

                @Override
                public void onCommandReceived(ConnectionCommand command) {
                    Log.i(TAG, "Client#onCommandReceived");
                }
            }, true, _device);
            _senderConnection.startConnection();
        }
    }

    public void sendTextData(String text) {
        byte[] base64;
        try {
            base64 = Base64.encode(text.getBytes("UTF-8"), Base64.DEFAULT);
            _senderConnection.sendData(STRING_DATA, base64, STRING_DATA_COMMAND_ID);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        if (_device != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            sendTextData("Yes " + sdf.format(cal.getTime()));
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        startActivityForResult(new Intent(this, SelectDeviceActivity.class), DEVICE_SELECT_REQUEST_CODE);

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }
}

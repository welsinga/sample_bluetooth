package com.elsinga.sample.bluetooth.device;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import ktlab.lib.connection.ConnectionCallback;
import ktlab.lib.connection.ConnectionCommand;
import ktlab.lib.connection.bluetooth.BluetoothConnection;
import ktlab.lib.connection.bluetooth.ServerBluetoothConnection;


public class MainActivity extends FragmentActivity {

    private static final String TAG = "SampleBluetooth";

    private TextView _tvInfo;
    private ImageView _imgView;

    private static final byte STRING_DATA = 43;

    private BluetoothConnection _listenerConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        _tvInfo = (TextView) findViewById(R.id.txt_info);
        _imgView = (ImageView) findViewById(R.id.image_view);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        startBluetoothListener();
    }

    @Override
    protected void onStop() {
        if (_listenerConnection != null) {
            _listenerConnection.stopConnection();
        }

        super.onStop();
    }

    private void startBluetoothListener() {
        _listenerConnection = new ServerBluetoothConnection(new ConnectionCallback() {

            @Override
            public void onConnectComplete() {
                Log.i(TAG, "Server#onConnectComplete");
            }

            @Override
            public void onConnectionFailed() {
                Log.i(TAG, "Server#onConnectionFailed");
            }

            @Override
            public void onDataSendComplete(int id) {
                Log.i(TAG, "Server#onDataSendComplete");
            }

            @Override
            public void onCommandReceived(ConnectionCommand command) {
                Log.i(TAG, "Server#onCommandReceived");

                switch (command.type) {
                    case STRING_DATA:
                        try {
                            String stringSent = new String(Base64.decode(command.option, Base64.DEFAULT), "UTF-8");
                            _tvInfo.setText(stringSent);
                            _imgView.setImageDrawable(null);
                            _imgView.setBackgroundResource(0);
                        } catch (UnsupportedEncodingException e) {
                            Log.e(TAG, "Not able to update information", e);
                        }
                        break;
                    default:
                        _tvInfo.setText("Received data of type: " + command.type);

                        break;
                }
            }
        }, true);
        _listenerConnection.startConnection();
    }

}

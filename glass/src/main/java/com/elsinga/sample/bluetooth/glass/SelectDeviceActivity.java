package com.elsinga.sample.bluetooth.glass;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SelectDeviceActivity extends Activity {
    public static final String    BLUETOOTH_DEVICE = "bluetooth_device";

    private List<BluetoothDevice> _devices;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        List<String> deviceNames = new ArrayList<String>();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();

        if (defaultAdapter != null)
        {
            _devices = new ArrayList<BluetoothDevice>(defaultAdapter.getBondedDevices());
            for (BluetoothDevice device : _devices)
            {
                deviceNames.add(device.getName());
            }
        }

        if (deviceNames.size() == 0)
        {
            Toast.makeText(this, "Unable to locate and Bluetooth Devices", Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            openOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        int position = 0;
        for (BluetoothDevice device : _devices)
        {
            menu.add(0, position, position, device.getName());
            position++;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent = new Intent();
        intent.putExtra(BLUETOOTH_DEVICE, _devices.get(item.getItemId()));
        setResult(Activity.RESULT_OK, intent);
        finish();
        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu)
    {
        super.onOptionsMenuClosed(menu);
        setResult(Activity.RESULT_OK, null);
        finish();
    }
}

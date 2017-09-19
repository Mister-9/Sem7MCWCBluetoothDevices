package com.mr9.bluetoothdevices;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //region declaration
    private static Switch bluetoothSwitch;
    private static int REQUEST_ENABLE_BT=1;
    private static ArrayList<String> pairedArray=new ArrayList<String>();
    private static ArrayList<String> availableArray=new ArrayList<String>();
    private ListView pairedList,availableList;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter pAdapter,aAdapter;
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        pairedList = (ListView) findViewById(R.id.paired_list);
        availableList=(ListView)findViewById(R.id.available_list);
        pAdapter= new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,pairedArray);
        aAdapter= new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,availableArray);
        bluetoothSwitch=(Switch)findViewById(R.id.switchBluetooth);
        //region bluetooth status check
        if(bluetoothAdapter.isEnabled())
        {
            bluetoothSwitch.setChecked(true);
            bluetoothSwitch.setText("Disable Bluetooth  ");
            Toast.makeText(MainActivity.this,"Bluetooth Already Enabled",Toast.LENGTH_SHORT).show();
        }
        //endregion
        //region bluetooth switch toggleing
        bluetoothSwitch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!bluetoothAdapter.isEnabled()) {
                            bluetoothAdapter.enable();
                            bluetoothSwitch.setText("Disable Bluetooth  ");
                            bluetoothSwitch.setChecked(true);
                            Toast.makeText(MainActivity.this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();

                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                            Toast.makeText(MainActivity.this, "Your devide is discoverable for 5 Mins:)", Toast.LENGTH_SHORT).show();
                        } else {
                            bluetoothAdapter.disable();
                            bluetoothSwitch.setChecked(false);
                            bluetoothSwitch.setText("Enable Bluetooth  ");
                            Toast.makeText(MainActivity.this, "Bluetooth Disabled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        //endregion
        //region get paired devices in pDevice
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                pairedArray.add(deviceName);
                pAdapter.notifyDataSetChanged();
            }
            pairedList.setAdapter(pAdapter);
        }
        //endregion
        //region get available devoces in aDevices
        bluetoothAdapter.startDiscovery();
        availableArray.add("Bhavin");
        BroadcastReceiver bReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                //Finding devices
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    availableArray.add(device.getName());
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bReceiver, filter);
        aAdapter.notifyDataSetChanged();
        availableList.setAdapter(aAdapter);
        //endregion
    }
}

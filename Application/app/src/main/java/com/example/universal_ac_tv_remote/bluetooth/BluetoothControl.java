package com.example.universal_ac_tv_remote.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothControl {
    
    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private final Context context;

    public BluetoothControl(Context context) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isBluetoothAvailable() {
        return bluetoothAdapter != null;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void enableBluetooth(Activity activity, int requestCode) {
        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBTIntent, requestCode);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public boolean isDevicePaired(String macAddress) {
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            Log.d("BT", "Uparen uređaj: " + device.getName() + " | MAC: " + device.getAddress());
            if (device.getAddress().equals(macAddress)) {
                return true;
            }
        }
        return false;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    public boolean connectToDevice(String macAddress, UUID uuid) {
        try {
            bluetoothAdapter.cancelDiscovery();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);

            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Greška pri povezivanju: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void sendData(String data) {
        if (outputStream != null) {
            try {
                outputStream.write(data.getBytes());
                Log.d("BT", "📤 Podaci poslati: " + data);
            } catch (IOException e) {
                Log.e("BT", "❌ Greška pri slanju podataka: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.e("BT", "❌ outputStream je null - nije povezan");
        }
    }

    public void closeConnection() {
        try {
            if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                bluetoothSocket.close();
                bluetoothSocket = null;
                Log.d("BT", "🔌 Bluetooth socket zatvoren (iz BluetoothControl)");
            }
        } catch (IOException e) {
            Log.w("BT", "⚠️ Greška pri zatvaranju socketa: " + e.getMessage());
        }
    }
    public BluetoothSocket getSocket() {
        return bluetoothSocket;
    }

}

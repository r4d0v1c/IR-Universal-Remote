package com.example.universal_ac_tv_remote.ui;

import android.Manifest;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.universal_ac_tv_remote.R;
import com.example.universal_ac_tv_remote.bluetooth.BluetoothControl;
import com.example.universal_ac_tv_remote.bluetooth.BluetoothControlSingleton;
import com.example.universal_ac_tv_remote.utils.Constants;
import com.example.universal_ac_tv_remote.utils.PermissionManager;

import java.io.IOException;
import java.net.DatagramSocket;

public class MainActivity extends AppCompatActivity {

    private BluetoothControl bluetoothControl;
    private TextView connectStatus;

    private Button connectBtn;

    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        connectStatus = findViewById(R.id.connectTEXT);
        connectBtn = findViewById(R.id.connectBTN);
        bluetoothControl = new BluetoothControl(this);

        PermissionManager.checkAndRequestBluetoothPermissions(this, Constants.REQUEST_BLUETOOTH_PERMISSIONS);

        connectBtn.setOnClickListener(v -> connectToESP32());
    }

    private void connectToESP32() {
        // Reset status svaki put kada se klikne dugme
        connectStatus.setText(R.string.not_connected);
        connectStatus.setTextColor(getColor(R.color.red));

        if (!bluetoothControl.isBluetoothAvailable()) {
            Toast.makeText(this, R.string.this_device_does_not_have_bluetooth, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothControl.isBluetoothEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothControl.enableBluetooth(this, Constants.REQUEST_ENABLE_BT);
            return;
        }

        if (!bluetoothControl.isDevicePaired(Constants.ESP32_MAC_ADDRESS)) {
            connectStatus.setText(R.string.esp32_is_not_paired);
            connectStatus.setTextColor(getColor(R.color.red));
            return;
        }

        connectStatus.setText("Connecting...");
        connectStatus.setTextColor(getColor(R.color.yellow));

        // Pokreni konekciju u pozadini
        new Thread(() -> {

            try {
                connected = bluetoothControl.connectToDevice(Constants.ESP32_MAC_ADDRESS, Constants.ESP32_UUID);
            } catch (Exception e) {
                Log.e("BT", "Greška pri konekciji: " + e.getMessage());
            }
            runOnUiThread(() -> {
                if (connected) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        connectStatus.setText("Connected ✓");
                        connectStatus.setTextColor(getColor(R.color.green));

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            BluetoothControlSingleton.setInstance(bluetoothControl);
                            startActivity(new Intent(MainActivity.this, ControlActivity.class));

                        }, 1000);
                    }, 1000);
                } else {
                    connectStatus.setText(R.string.not_connected);
                    connectStatus.setTextColor(getColor(R.color.red));
                }
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Zatvori socket kroz BluetoothControl (ako je otvoren)
        bluetoothControl.closeConnection();

        // Resetuj status
        connectStatus.setText(R.string.not_connected);
        connectStatus.setTextColor(getColor(R.color.red));
    }
}
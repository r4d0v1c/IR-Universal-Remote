package com.example.universal_ac_tv_remote;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import static com.example.universal_ac_tv_remote.utils.Constants.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    OutputStream outputStream;
    Button connectBtn;
    TextView connectStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        connectBtn = findViewById(R.id.connectBTN);
        connectStatus = findViewById(R.id.connectTEXT);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkAndRequestPermissions();

        connectBtn.setOnClickListener(v -> connectToESP32());
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                        REQUEST_BLUETOOTH_PERMISSIONS);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_BLUETOOTH_PERMISSIONS);
            }
        }
    }

    private void connectToESP32() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Ovaj uređaj nema Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
            return;
        }

        // Tvoja poznata MAC adresa ESP32 uređaja
        String espMac = ESP32_MAC_ADDRESS;

        // Dobij uređaj direktno po MAC adresi
        BluetoothDevice esp32Device = bluetoothAdapter.getRemoteDevice(espMac);

        // Proveri da li je uparen (opciono, ali korisno)
        boolean paired = false;
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            Log.d("BT", "Uparen uređaj: " + device.getName() + " | MAC: " + device.getAddress());

            if (device.getAddress().equals(espMac)) {
                Log.d("BT", "✅ Pronađen ESP32 sa MAC adresom: " + device.getAddress());
                paired = true;
                break;
            }
        }

        if (!paired) {
            Log.w("BT", "❌ ESP32 (" + espMac + ") nije pronađen među uparenim uređajima!");

            connectStatus.setText(R.string.esp32_is_not_paired);
            connectStatus.setTextColor(getColor(R.color.red));
            return;
        }

        // Povezivanje u posebnoj niti
        new Thread(() -> {
            try {
                bluetoothAdapter.cancelDiscovery();

                bluetoothSocket = esp32Device.createRfcommSocketToServiceRecord(ESP32_UUID);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();

                runOnUiThread(() -> {
                    connectStatus.setText(R.string.connected);
                    connectStatus.setTextColor(getColor(R.color.green));

                    Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                    startActivity(intent);
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    connectStatus.setText(R.string.not_connected);
                    connectStatus.setTextColor(getColor(R.color.red));
                    Toast.makeText(MainActivity.this, "Greška pri povezivanju: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }

            if (granted) {
                Log.d("TAG", "Sve Bluetooth permisije odobrene ✅");
            } else {
                Toast.makeText(this, "Bluetooth permisije nisu odobrene ❌", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Opcionalno: metoda za slanje podataka ka ESP32
    private void sendDataToESP32(String data) {
        if (outputStream != null) {
            try {
                outputStream.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Greška prilikom slanja podataka", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
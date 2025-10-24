package com.example.universal_ac_tv_remote.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.universal_ac_tv_remote.R;
import com.example.universal_ac_tv_remote.bluetooth.BluetoothControl;
import com.example.universal_ac_tv_remote.bluetooth.BluetoothControlSingleton;
import com.example.universal_ac_tv_remote.utils.Constants;

import java.io.IOException;
import java.util.Arrays;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener {

    TextView deviceText;
    TextView notificationText;
    Button tvButton;
    Button acButton;
    Button onButton;
    Button offButton;
    Button chPlusButton;
    Button chMinusButton;
    Button tempPlusButton;
    Button tempMinusButton;
    Button volPlusButton;
    Button volMinusButton;
    private BluetoothControl bluetoothControl;

    private String selectedDeviceType = ""; // "TV" ili "AC"
    private int selectedDeviceName;
    private int selectedAcDeviceValue = 0;

    String[] tvBrands = {"SAMSUNG", "LG", "FOX"};

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_control);

        bluetoothControl = BluetoothControlSingleton.getInstance();

        deviceText = findViewById(R.id.device);
        notificationText = findViewById(R.id.notifyText);
        tvButton = findViewById(R.id.tv);
        acButton = findViewById(R.id.ac);
        onButton = findViewById(R.id.on);
        offButton = findViewById(R.id.off);
        chPlusButton = findViewById(R.id.ch_plus);
        chMinusButton = findViewById(R.id.ch_minus);
        tempPlusButton = findViewById(R.id.temp_plus);
        tempMinusButton = findViewById(R.id.temp_minus);
        volPlusButton = findViewById(R.id.vol_plus);
        volMinusButton = findViewById(R.id.vol_minus);

        deviceText.setText("No device is selected");

        tvButton.setOnClickListener(this);
        acButton.setOnClickListener(this);
        onButton.setOnClickListener(this);
        offButton.setOnClickListener(this);
        chPlusButton.setOnClickListener(this);
        chMinusButton.setOnClickListener(this);
        tempPlusButton.setOnClickListener(this);
        tempMinusButton.setOnClickListener(this);
        volPlusButton.setOnClickListener(this);
        volMinusButton.setOnClickListener(this);
        disableButtons();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.tv) {
            selectedDeviceType = "TV";
            showTvSelectionDialog();
        } else if(id == R.id.ac) {
            selectedDeviceType = "AC";
            showAcSelectionDialog();
        } else if(id == R.id.on) {
            sendCommand("ON");
        } else if(id == R.id.off) {
            sendCommand("OFF");
        } else if(id == R.id.ch_plus) {
            sendCommand("CH+");
        } else if(id == R.id.ch_minus) {
            sendCommand("CH-");
        } else if(id == R.id.temp_plus) {
            sendCommand("TEMP+");
        } else if(id == R.id.temp_minus) {
            sendCommand("TEMP-");
        } else if(id == R.id.vol_minus) {
            sendCommand("VOL-");
        } else if(id == R.id.vol_plus) {
            sendCommand("VOL+");
        }
    }

    private void showTvSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
        builder.setTitle("Choose available TV brand")
                .setItems(tvBrands, (dialog, which) -> {
                    String chosen = tvBrands[which];
                   // selectedDeviceName = chosen;
                    deviceText.setText("TV: " + chosen);
                    disableAcButtons();
                    enableTvButtons();

                })
                .setNegativeButton("Cancel", (dialog, which) -> {})
                .setCancelable(false)
                .show();
    }

    private void showAcSelectionDialog() {
        String[] acDeviceNames = Arrays.stream(Constants.DecodeType.values())
                .filter(type -> type != Constants.DecodeType.UNKNOWN && type != Constants.DecodeType.UNUSED)
                .map(Enum::name)
                .toArray(String[]::new);

        AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
        builder.setTitle("Choose available AC device")
                .setItems(acDeviceNames, (dialog, which) -> {
                    String chosen = acDeviceNames[which];
                    Constants.DecodeType selectedType = Constants.DecodeType.valueOf(chosen);

                    selectedDeviceName = selectedType.getValue();
                    selectedAcDeviceValue = selectedType.getValue();
                    deviceText.setText("AC: " + chosen + " (" + selectedAcDeviceValue + ")");
                    disableTvButtons();
                    enableAcButtons();

                })
                .setNegativeButton("Cancel", (dialog, which) -> {})
                .setCancelable(false)
                .show();
    }

    private void sendCommand(String command) {
        if (selectedDeviceType.isEmpty()) {
            deviceText.setText("Prvo izaberite uređaj");
            return;
        }

        String data = "#" + selectedDeviceType + "|" + selectedDeviceName + "|" + command + "#";
        Log.d("ControlActivity", data);
        sendData(data);
    }

    // Ove metode možete dodati ako vam trebaju, ili koristite postojeće
    public void disableButtons() {
        onButton.setEnabled(false);
        offButton.setEnabled(false);
        chMinusButton.setEnabled(false);
        chPlusButton.setEnabled(false);
        tempPlusButton.setEnabled(false);
        tempMinusButton.setEnabled(false);
        volPlusButton.setEnabled(false);
        volMinusButton.setEnabled(false);

        onButton.setAlpha(0.5f);
        offButton.setAlpha(0.5f);
        chMinusButton.setAlpha(0.5f);
        chPlusButton.setAlpha(0.5f);
        tempPlusButton.setAlpha(0.5f);
        tempMinusButton.setAlpha(0.5f);
        volPlusButton.setAlpha(0.5f);
        volMinusButton.setAlpha(0.5f);
    }

    public void disableAcButtons() {
        tempPlusButton.setEnabled(false);
        tempMinusButton.setEnabled(false);
        tempPlusButton.setAlpha(0.5f);
        tempMinusButton.setAlpha(0.5f);
    }

    public void enableAcButtons() {
        tempPlusButton.setEnabled(true);
        tempMinusButton.setEnabled(true);
        onButton.setEnabled(true);
        offButton.setEnabled(true);
        tempPlusButton.setAlpha(1f);
        tempMinusButton.setAlpha(1f);
        onButton.setAlpha(1f);
        offButton.setAlpha(1f);
    }

    public void disableTvButtons() {
        chMinusButton.setEnabled(false);
        chPlusButton.setEnabled(false);
        volPlusButton.setEnabled(false);
        volMinusButton.setEnabled(false);
        chMinusButton.setAlpha(0.5f);
        chPlusButton.setAlpha(0.5f);
        volPlusButton.setAlpha(0.5f);
        volMinusButton.setAlpha(0.5f);
    }

    public void enableTvButtons() {
        chMinusButton.setEnabled(true);
        chPlusButton.setEnabled(true);
        volPlusButton.setEnabled(true);
        volMinusButton.setEnabled(true);
        onButton.setEnabled(true);
        offButton.setEnabled(true);
        chMinusButton.setAlpha(1f);
        chPlusButton.setAlpha(1f);
        volPlusButton.setAlpha(1f);
        volMinusButton.setAlpha(1f);
        onButton.setAlpha(1f);
        offButton.setAlpha(1f);
    }

    // DODATA METODA ZA SLANJE PODATAKA
    public void sendData(String data) {
        if (bluetoothControl != null) {
            bluetoothControl.sendData(data);
        }
    }
}
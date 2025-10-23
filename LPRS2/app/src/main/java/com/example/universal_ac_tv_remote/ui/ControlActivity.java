package com.example.universal_ac_tv_remote.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.universal_ac_tv_remote.R;
import com.example.universal_ac_tv_remote.utils.Constants;

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

    String [] tvBrands = {"SAMSUNG", "LG", "FOX"};
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

            AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
            builder.setTitle("Choose available TV brand")
                    .setItems(tvBrands, (dialog, which) -> {
                        String chosen = tvBrands[which];

                        // Postavi izabrani TV brend u TextView
                        deviceText.setText(chosen);
                        disableAcButtons();
                        enableTvButtons();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {})
                    .setCancelable(false)
                    .show();
        } else if(id == R.id.ac) {
            // Kreiraj listu enum naziva za prikaz u dialogu
            String[] acDeviceNames = Arrays.stream(Constants.DecodeType.values())
                    .filter(type -> type != Constants.DecodeType.UNKNOWN && type != Constants.DecodeType.UNUSED)
                    .map(Enum::name)
                    .toArray(String[]::new);

            AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
            builder.setTitle("Choose available AC device")
                    .setItems(acDeviceNames, (dialog, which) -> {
                        String chosen = acDeviceNames[which];

                        // Pronađi odgovarajući enum i uzmi njegovu int vrednost
                        Constants.DecodeType selectedType = Constants.DecodeType.valueOf(chosen);

                        // Postavi int vrednost u TextView
                        deviceText.setText(chosen);
                        disableTvButtons();
                        enableAcButtons();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {})
                    .setCancelable(false)
                    .show();
        }
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
}
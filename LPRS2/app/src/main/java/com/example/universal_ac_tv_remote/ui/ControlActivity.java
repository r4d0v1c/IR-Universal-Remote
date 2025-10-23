package com.example.universal_ac_tv_remote.ui;

import android.annotation.SuppressLint;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Kreira i inicijalizuje ControlActivity.
     * Poziva se kada se aktivnost prvi put pokreće.
     * 
     * @param savedInstanceState Sačuvano stanje aktivnosti (null ako se prvi put pokreće)
     */
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
            disableAcButtons();
            enableTvButtons();
        } else if(id == R.id.ac) {
            disableTvButtons();
            enableAcButtons();
        }
    }

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
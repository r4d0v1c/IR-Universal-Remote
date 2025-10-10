package com.example.universal_ac_tv_remote;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        logo.setAlpha(0f); // prozirnost 0

        logo.animate()
                // Fade In + Zoom
                .alpha(1f)      // logo postaje vidljiv
                .scaleX(1.1f)   // blago uvecanje po X osi
                .scaleY(1.2f)   // blago uvecanje po Y osi
                .setDuration(3000)    // traje 3 sekunde
                .setInterpolator(new OvershootInterpolator())  //ubrzavanje pa usporavanje
                .withEndAction(() -> {
                    // Pauza 2 sekunde pre fade-out
                    logo.postDelayed(() -> {
                        logo.animate()
                                .alpha(0f)
                                .setDuration(500)
                                .withEndAction(() -> {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    finish();
                                })
                                .start();
                    }, 2000);
                })
                .start();   // start Fade In + Zoom

    }
}
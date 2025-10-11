package com.example.universal_ac_tv_remote.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.universal_ac_tv_remote.R;

/**
 * ControlActivity - Aktivnost za upravljanje AC i TV uređajima preko daljinskog upravljača.
 * 
 * Ova aktivnost predstavlja glavni ekran za kontrolu uređaja nakon uspešne Bluetooth konekcije.
 * Korisnik može koristiti virtuelne dugmiće za slanje komandi ESP32 mikrokontroleru
 * koji emituje infracrvene signale za kontrolu klima uređaja i televizora.
 * 
 * Trenutno implementirane funkcionalnosti:
 * - Prikazivanje korisničkog interfejsa za kontrolu
 * - Rukovanje "Back" dugmetom za povratak na prethodni ekran
 * - Edge-to-Edge prikaz za moderan UI dizajn
 * 
 * Planirane funkcionalnosti:
 * - Dugmići za kontrolu temperature
 * - Dugmići za kontrolu jačine TV-a
 * - Dugmići za menjanje kanala
 * - Power on/off kontrole
 * - Slanje IR komandi preko Bluetooth veze
 * 
 * @author Universal AC/TV Remote Team
 * @version 1.0
 */
public class ControlActivity extends AppCompatActivity {

    /**
     * Rukuje pritiskom na "Back" dugme uređaja.
     * Poziva super.onBackPressed() i završava aktivnost da bi se oslobodili resursi.
     * Korisnik se vraća na MainActivity.
     */
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_control);

    }
}
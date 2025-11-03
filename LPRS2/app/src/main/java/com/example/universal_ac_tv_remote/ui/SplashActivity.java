package com.example.universal_ac_tv_remote.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.universal_ac_tv_remote.R;

/**
 * SplashActivity - Aktivnost za prikazivanje splash ekrana pri pokretanju aplikacije.
 * 
 * Ova aktivnost je prva koja se prikazuje korisniku prilikom pokretanja aplikacije.
 * Služi za:
 * 1. Prikazivanje brenda aplikacije (logo)
 * 2. Kreiranje profesionalnog prvog utiska
 * 3. Pružanje vizuelnog feedback-a tokom pokretanja aplikacije
 * 4. Pripremu pozadinskih resursa (ako je potrebno)
 * 
 * Animacija:
 * - Fade In: Logo postaje vidljiv sa transparentnog
 * - Zoom: Logo se uvećava tokom pojave (1.1x horizontalno, 1.2x vertikalno)
 * - Trajanje: 3 sekunde
 * - Pauza: 2 sekunde posle pojave
 * - Fade Out: Logo nestaje pre prelaska na MainActivity
 * 
 * Tok aplikacije:
 * SplashActivity (3s animacija + 2s pauza) → MainActivity
 * 
 * @author Universal AC/TV Remote Team
 * @version 1.0
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * ImageView koji prikazuje logo aplikacije.
     * Logo se animira sa fade-in, zoom i fade-out efektima.
     */
    ImageView logo;
    
    /**
     * Kreira i inicijalizuje SplashActivity.
     * Poziva se kada se aplikacija prvi put pokreće.
     * 
     * Procedura animacije:
     * 1. Postavlja Edge-to-Edge prikaz za full-screen efekat
     * 2. Učitava layout za splash ekran
     * 3. Povezuje logo ImageView sa view objektom
     * 4. Postavlja početnu transparentnost logoa na 0 (nevidljivo)
     * 5. Pokreće Fade In + Zoom animaciju:
     *    - Trajanje: 3 sekunde
     *    - Alpha: 0 → 1 (potpuno vidljivo)
     *    - ScaleX: 1.0 → 1.1 (blago uvećanje horizontalno)
     *    - ScaleY: 1.0 → 1.2 (blago uvećanje vertikalno)
     *    - Interpolator: OvershootInterpolator (ubrzavanje pa usporavanje sa prekoračenjem)
     * 6. Nakon Fade In animacije, čeka 2 sekunde
     * 7. Pokreće Fade Out animaciju (0.5 sekundi)
     * 8. Nakon Fade Out-a prelazi na MainActivity i završava SplashActivity
     * 
     * Ukupno vreme: ~5.5 sekundi (3s Fade In + 2s Pauza + 0.5s Fade Out)
     * 
     * @param savedInstanceState Sačuvano stanje aktivnosti (null pri prvom pokretanju)
     */
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
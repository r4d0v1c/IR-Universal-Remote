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
import com.example.universal_ac_tv_remote.utils.Constants;
import com.example.universal_ac_tv_remote.utils.PermissionManager;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * MainActivity - Glavna aktivnost aplikacije za univerzalni AC/TV daljinski upravljač.
 * 
 * Ova aktivnost služi kao početni ekran aplikacije nakon splash screena.
 * Omogućava korisniku da uspostavi Bluetooth konekciju sa ESP32 mikrokontrolerom
 * koji upravlja infrared (IR) emiterom za kontrolu AC i TV uređaja.
 * 
 * Glavni zadaci aktivnosti:
 * 1. Provera i zahtevanje Bluetooth dozvola
 * 2. Provera dostupnosti i stanja Bluetooth adaptera
 * 3. Provera da li je ESP32 uređaj uparen
 * 4. Uspostavljanje Bluetooth konekcije sa ESP32 uređajem
 * 5. Vizuelni prikaz statusa konekcije korisniku
 * 6. Prelazak na ControlActivity nakon uspešne konekcije
 * 
 * Tok aplikacije:
 * SplashActivity → MainActivity (konekcija) → ControlActivity (kontrola)
 * 
 * @author Universal AC/TV Remote Team
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    
    /**
     * Instanca BluetoothControl klase za upravljanje Bluetooth funkcionalnostima.
     * Koristi se za sve operacije povezane sa Bluetooth komunikacijom.
     */
    private BluetoothControl bluetoothControl;
    
    /**
     * TextView koji prikazuje trenutni status konekcije sa ESP32 uređajem.
     * Moguće vrednosti:
     * - "Not Connected" (crvena boja)
     * - "Connecting..." (žuta boja)
     * - "Connected ✓" (zelena boja)
     * - "ESP32 is not paired" (crvena boja)
     */
    private TextView connectStatus;
    
    /**
     * Dugme za pokretanje procesa povezivanja sa ESP32 uređajem.
     * Klikom se pokreće metoda connectToESP32().
     */
    private Button connectBtn;
    
    /**
     * Flag koji označava da li je Bluetooth konekcija uspešno uspostavljena.
     * true = povezano, false = nije povezano
     */
    boolean connected = false;
    
    /**
     * Kreira i inicijalizuje MainActivity.
     * Poziva se kada se aktivnost prvi put pokreće (nakon SplashActivity).
     * 
     * Procedura inicijalizacije:
     * 1. Postavlja Edge-to-Edge prikaz za moderan UI
     * 2. Povezuje UI elemente (TextView i Button) sa view objektima
     * 3. Kreira instancu BluetoothControl klase
     * 4. Proverava i zahteva potrebne Bluetooth dozvole
     * 5. Postavlja OnClickListener na dugme za povezivanje
     * 
     * @param savedInstanceState Sačuvano stanje aktivnosti (null ako se prvi put pokreće)
     */
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

    /**
     * Pokreće proceduru povezivanja sa ESP32 mikrokontrolerom preko Bluetooth-a.
     * 
     * Procedura povezivanja se sastoji od sledećih koraka:
     * 1. Resetuje status konekcije na "Not Connected"
     * 2. Proverava da li uređaj podržava Bluetooth
     * 3. Proverava da li je Bluetooth uključen (ako nije, traži da se uključi)
     * 4. Proverava da li je ESP32 uparen sa uređajem
     * 5. Prikazuje "Connecting..." status (žuta boja)
     * 6. Pokreće konekciju u pozadinskoj niti da ne blokira UI
     * 7. Ako je uspešno:
     *    - Prikazuje "Connected ✓" status (zelena boja) nakon 1 sekunde
     *    - Prelazi na ControlActivity nakon dodatne 1 sekunde
     * 8. Ako nije uspešno:
     *    - Prikazuje "Not Connected" status (crvena boja)
     * 
     * Koristi asinhronu komunikaciju sa Handler i Looper za ažuriranje UI-a.
     */
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

        // Prikaži "Connecting..." odmah
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
                    // ➤ Sačekaj 1 sekundu pre nego što prikažeš "Connected ✓"
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        connectStatus.setText("Connected ✓");
                        connectStatus.setTextColor(getColor(R.color.green));

                        // ➤ Još 1 sekunda pa prelazak na ControlActivity (ako želiš)
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
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

    /**
     * Poziva se kada se aktivnost ponovo prikazuje na ekranu.
     * Koristi se za resetovanje stanja aplikacije kada korisnik navigira nazad.
     * 
     * Funkcionalnosti:
     * 1. Zatvara postojeću Bluetooth konekciju (ako je otvorena)
     * 2. Resetuje status prikaza na "Not Connected" (crvena boja)
     * 
     * Ovo osigurava da svaki put kada se korisnik vrati na MainActivity,
     * mora ponovo da uspostavi konekciju sa ESP32 uređajem.
     */
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
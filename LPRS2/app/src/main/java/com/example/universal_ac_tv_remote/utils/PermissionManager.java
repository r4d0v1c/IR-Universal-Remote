package com.example.universal_ac_tv_remote.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * PermissionManager - Utility klasa za upravljanje runtime dozvolama u aplikaciji.
 * 
 * Ova klasa pojednostavljuje proces traženja i provere dozvola koje su potrebne
 * za funkcionisanje Bluetooth komunikacije na različitim verzijama Android OS-a.
 * 
 * Android dozvole za Bluetooth se razlikuju prema verziji OS-a:
 * 
 * Android 12+ (API level 31+):
 * - BLUETOOTH_CONNECT: Potrebna za povezivanje sa uparenim uređajima
 * - BLUETOOTH_SCAN: Potrebna za skeniranje i otkrivanje Bluetooth uređaja
 * 
 * Android 11 i niže (API level <= 30):
 * - ACCESS_FINE_LOCATION: Potrebna jer Bluetooth scan može otkriti lokaciju
 * - ACCESS_COARSE_LOCATION: Dodatna dozvola za lokaciju
 * 
 * Razlog za location dozvole na starijim verzijama:
 * Google je smatrao da Bluetooth skeniranje može kompromitovati privatnost
 * korisnika jer otkriva obližnje uređaje i njihovu lokaciju.
 * 
 * @author Universal AC/TV Remote Team
 * @version 1.0
 */
public class PermissionManager {
    
    /**
     * Proverava i zahteva potrebne Bluetooth dozvole prema verziji Android OS-a.
     * 
     * Ova metoda automatski detektuje verziju Android OS-a i traži odgovarajuće dozvole:
     * 
     * Za Android 12+ (API 31+):
     * - Proverava BLUETOOTH_CONNECT i BLUETOOTH_SCAN dozvole
     * - Ako nisu odobrene, prikazuje sistemski dijalog za traženje dozvola
     * 
     * Za Android 11 i niže (API <= 30):
     * - Proverava ACCESS_FINE_LOCATION i ACCESS_COARSE_LOCATION dozvole
     * - Ako nisu odobrene, prikazuje sistemski dijalog za traženje dozvola
     * 
     * Korisnik može:
     * 1. Prihvatiti dozvole → aplikacija funkcioniše normalno
     * 2. Odbiti dozvole → Bluetooth funkcije neće raditi
     * 3. Odbiti trajno ("Don't ask again") → aplikacija mora korisnika uputiti u Settings
     * 
     * Odgovor na zahtev se rukuje u onRequestPermissionsResult() callback-u aktivnosti.
     * 
     * @param activity Aktivnost koja traži dozvole (obično MainActivity)
     * @param requestCode Kod za identifikaciju zahteva (obično Constants.REQUEST_BLUETOOTH_PERMISSIONS)
     * 
     * @see Constants#REQUEST_BLUETOOTH_PERMISSIONS
     */
    public static void checkAndRequestBluetoothPermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                        requestCode);
            }
        } else {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        requestCode);
            }
        }
    }
}

package com.example.universal_ac_tv_remote.utils;

import java.util.UUID;

/**
 * Constants - Klasa za definisanje svih konstanti koje se koriste u aplikaciji.
 * 
 * Ova utility klasa sadrži statičke konstante koje se koriste kroz celu aplikaciju
 * za Bluetooth komunikaciju sa ESP32 mikrokontrolerom. Centralizacija konstanti
 * olakšava održavanje koda i smanjuje mogućnost grešaka.
 * 
 * Kategorije konstanti:
 * 1. Request kodovi za dozvole i Bluetooth zahteve
 * 2. UUID za RFCOMM Bluetooth komunikaciju (Serial Port Profile)
 * 3. MAC adresa ESP32 uređaja za uspostavljanje konekcije
 * 
 * @author Universal AC/TV Remote Team
 * @version 1.0
 */
public class Constants {
    
    /**
     * Request kod za zahtevanje Bluetooth dozvola od korisnika.
     * Koristi se u onRequestPermissionsResult() callback-u za identifikaciju odgovora.
     * 
     * Potrebne dozvole:
     * - Android 12+ (API 31+): BLUETOOTH_CONNECT, BLUETOOTH_SCAN
     * - Android 11 i niže: ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
     */
    public static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    
    /**
     * Request kod za zahtevanje omogućavanja Bluetooth-a.
     * Koristi se u onActivityResult() callback-u za identifikaciju odgovora
     * nakon što korisnik prihvati ili odbije uključivanje Bluetooth-a.
     */
    public static final int REQUEST_ENABLE_BT = 2;
    
    /**
     * UUID za RFCOMM serijski port profil (SPP - Serial Port Profile).
     * 
     * Standardni UUID: 00001101-0000-1000-8000-00805F9B34FB
     * 
     * SPP je Bluetooth profil koji omogućava bežičnu serijsku komunikaciju
     * između dva uređaja (Android telefon ↔ ESP32). Ovaj UUID je univerzalno
     * prepoznat za serijski prenos podataka preko Bluetooth-a.
     * 
     * ESP32 mora koristiti isti UUID za svoj RFCOMM server.
     */
    public static final UUID ESP32_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    /**
     * MAC adresa (Media Access Control) ESP32 mikrokontrolera.
     * 
     * Format: XX:XX:XX:XX:XX:XX (6 bajtova u heksadecimalnom zapisu)
     * Primer: "B0:B2:1C:44:C0:CE"
     * 
     * Ova adresa je jedinstvena za svaki ESP32 uređaj i koristi se za:
     * 1. Proveru da li je uređaj uparen sa Android telefonom
     * 2. Uspostavljanje direktne Bluetooth konekcije
     * 
     * NAPOMENA: Ovu vrednost treba zameniti sa MAC adresom vašeg ESP32 uređaja.
     * MAC adresa se može pronaći u Serial Monitor-u ESP32 pri pokretanju.
     */
    public static final String ESP32_MAC_ADDRESS = "B0:B2:1C:44:C0:CE";
}

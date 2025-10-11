package com.example.universal_ac_tv_remote.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * BluetoothControl - Klasa za upravljanje Bluetooth konekcijama i komunikacijom.
 * 
 * Ova klasa pruža funkcionalnosti za:
 * - Proveru dostupnosti i stanja Bluetooth adaptera
 * - Omogućavanje Bluetooth funkcionalnosti na uređaju
 * - Proveru da li je uređaj uparen sa određenim MAC adresom
 * - Uspostavljanje konekcije sa Bluetooth uređajem (ESP32)
 * - Slanje podataka preko Bluetooth veze
 * - Zatvaranje aktivne Bluetooth konekcije
 * 
 * @author Universal AC/TV Remote Team
 * @version 1.0
 */
public class BluetoothControl {
    
    /**
     * Bluetooth adapter koji predstavlja lokalni Bluetooth interfejs uređaja.
     * Koristi se za sve operacije vezane za Bluetooth.
     */
    private final BluetoothAdapter bluetoothAdapter;
    
    /**
     * Socket za komunikaciju sa udaljenim Bluetooth uređajem.
     * Null vrednost označava da konekcija nije aktivna.
     */
    private BluetoothSocket bluetoothSocket;
    
    /**
     * Izlazni tok podataka za slanje podataka preko Bluetooth veze.
     * Koristi se za pisanje bajtova na povezani uređaj.
     */
    private OutputStream outputStream;
    
    /**
     * Kontekst aplikacije koji se koristi za pristup resursima i prikazivanje poruka.
     */
    private final Context context;

    /**
     * Konstruktor klase BluetoothControl.
     * Inicijalizuje Bluetooth adapter i čuva kontekst aplikacije.
     * 
     * @param context Kontekst aplikacije koji se koristi za pristup sistemskim servisima
     */
    public BluetoothControl(Context context) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Proverava da li uređaj podržava Bluetooth funkcionalnost.
     * 
     * @return true ako je Bluetooth dostupan na uređaju, false ako nije
     */
    public boolean isBluetoothAvailable() {
        return bluetoothAdapter != null;
    }

    /**
     * Proverava da li je Bluetooth trenutno omogućen na uređaju.
     * 
     * @return true ako je Bluetooth uključen, false ako nije ili ako adapter ne postoji
     */
    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    /**
     * Pokreće sistemski zahtev za omogućavanje Bluetooth funkcionalnosti.
     * Prikazuje sistemski dijalog koji traži od korisnika da uključi Bluetooth.
     * 
     * @param activity Aktivnost iz koje se pokreće zahtev
     * @param requestCode Kod zahteva za identifikaciju rezultata u onActivityResult()
     * @requires Manifest.permission.BLUETOOTH_CONNECT dozvola
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void enableBluetooth(Activity activity, int requestCode) {
        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBTIntent, requestCode);
    }

    /**
     * Proverava da li je uređaj sa određenom MAC adresom već uparen sa ovim uređajem.
     * Prolazi kroz sve uparene uređaje i poredi MAC adrese.
     * 
     * @param macAddress MAC adresa uređaja koji se proverava (format: "XX:XX:XX:XX:XX:XX")
     * @return true ako je uređaj uparen, false ako nije
     * @requires Manifest.permission.BLUETOOTH_CONNECT dozvola
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public boolean isDevicePaired(String macAddress) {
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            Log.d("BT", "Uparen uređaj: " + device.getName() + " | MAC: " + device.getAddress());
            if (device.getAddress().equals(macAddress)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Uspostavlja RFCOMM konekciju sa udaljenim Bluetooth uređajem (ESP32).
     * 
     * Procedura:
     * 1. Prekida aktivno skeniranje Bluetooth uređaja
     * 2. Dobija referencu na udaljeni uređaj preko MAC adrese
     * 3. Kreira RFCOMM socket koristeći UUID
     * 4. Povezuje se na uređaj
     * 5. Otvara izlazni tok za slanje podataka
     * 
     * @param macAddress MAC adresa ESP32 uređaja (format: "XX:XX:XX:XX:XX:XX")
     * @param uuid UUID servisa za RFCOMM komunikaciju (SPP standard: 00001101-0000-1000-8000-00805F9B34FB)
     * @return true ako je konekcija uspešna, false ako nije
     * @requires Manifest.permission.BLUETOOTH_SCAN dozvola
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    public boolean connectToDevice(String macAddress, UUID uuid) {
        try {
            bluetoothAdapter.cancelDiscovery();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);

            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Greška pri povezivanju: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Šalje tekstualne podatke preko aktivne Bluetooth konekcije.
     * Podatci se konvertuju u bajtove i šalju preko izlaznog toka.
     * 
     * @param data String koji se šalje ESP32 uređaju (komanda, podatak, itd.)
     */
    public void sendData(String data) {
        if (outputStream != null) {
            try {
                outputStream.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Zatvara aktivnu Bluetooth konekciju i oslobađa resurse.
     * Proverava da li socket postoji i da li je povezan pre zatvaranja.
     * Postavlja socket na null nakon zatvaranja.
     * 
     * Koristi se prilikom:
     * - Napuštanja aplikacije
     * - Vraćanja na početni ekran (onResume)
     * - Prekida komunikacije sa ESP32 uređajem
     */
    public void closeConnection() {
        try {
            if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                bluetoothSocket.close();
                bluetoothSocket = null;
                Log.d("BT", "🔌 Bluetooth socket zatvoren (iz BluetoothControl)");
            }
        } catch (IOException e) {
            Log.w("BT", "⚠️ Greška pri zatvaranju socketa: " + e.getMessage());
        }
    }

}

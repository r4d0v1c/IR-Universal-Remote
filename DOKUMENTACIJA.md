# 📱 Dokumentacija Aplikacije: Universal AC/TV Remote

> **Autor:** Universal AC/TV Remote Team  
> **Verzija:** 1.0  
> **Datum:** 11. Oktobar 2025  
> **Platforma:** Android (Java)

---

## 📋 Sadržaj

1. [Pregled Projekta](#-pregled-projekta)
2. [Arhitektura Projekta](#-arhitektura-projekta)
3. [Struktura Paketa](#-struktura-paketa)
4. [Detaljne Dokumentacije Klasa](#-detaljne-dokumentacije-klasa)
   - [Bluetooth Paket](#1-bluetooth-paket)
   - [UI Paket](#2-ui-paket)
   - [Utils Paket](#3-utils-paket)
5. [Tok Aplikacije](#-tok-aplikacije)
6. [Bluetooth Komunikacija](#-bluetooth-komunikacija)
7. [Android Dozvole](#-android-dozvole)
---

## 🎯 Pregled Projekta

**Universal AC/TV Remote** je Android aplikacija koja omogućava korisnicima da kontrolišu klima uređaje i televizore korišćenjem svog mobilnog telefona kao univerzalnog daljinskog upravljača. Aplikacija komunicira sa ESP32 mikrokontrolerom preko Bluetooth veze, a ESP32 emituje infracrvene (IR) signale ka uređajima.

### Glavne Funkcionalnosti

- ✅ Bluetooth konekcija sa ESP32 mikrokontrolerom
- ✅ Provera i zahtevanje runtime dozvola
- ✅ Provera Bluetooth dostupnosti i stanja
- ✅ Provera uparivanja sa ESP32 uređajem
- ✅ Splash screen animacija pri pokretanju
- ✅ Vizuelni status konekcije
- ✅ Singleton patern za deljenje Bluetooth instance između aktivnosti
- ✅ Kontrola AC uređaja (ON/OFF, TEMP+, TEMP-)
- ✅ Kontrola TV uređaja (ON/OFF, VOL+, VOL-, CH+, CH-)
- ✅ Izbor AC uređaja iz 68+ dostupnih brendova
- ✅ Izbor TV uređaja iz 59+ dostupnih brendova
- ✅ Protokol za slanje komandi: `#DEVICE_TYPE|DEVICE_ID|COMMAND#`
- ✅ Dinamičko omogućavanje/onemogućavanje dugmića prema izabranom uređaju

### Tehnologije

- **Programski jezik:** Java
- **Platforma:** Android (API 21+)
- **Bluetooth protokol:** RFCOMM (Serial Port Profile)
- **Hardware:** ESP32 mikrocontroler sa IR emiterom
- **Build sistem:** Gradle

---

## 🏗️ Arhitektura Projekta

Projekat je organizovan prema **MVC (Model-View-Controller)** principu sa dodatnim utility klasama:

```
com.example.universal_ac_tv_remote/
├── bluetooth/          # Bluetooth komunikacija (Model)
├── ui/                 # Aktivnosti (View + Controller)
└── utils/              # Utility klase (Helper)
```

### Arhitektonski Dijagram

```
┌─────────────────┐
│  SplashActivity │ (Splash Screen - 5.5s animacija)
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│  MainActivity   │ (Glavna aktivnost - Bluetooth konekcija)
│                 │
│  ┌──────────────┴─────────────┐
│  │ BluetoothControl           │ (Bluetooth logika)
│  │ PermissionManager          │ (Dozvole)
│  │ Constants                  │ (Konstante)
│  └────────────────────────────┘
└────────┬────────┘
         │
         ↓ (Nakon uspešne konekcije)
┌─────────────────┐
│ ControlActivity │ (Kontrola AC/TV uređaja)
└─────────────────┘
```

---

## 📦 Struktura Paketa

### Pregled Paketa

| Paket | Fajlovi | Opis |
|-------|---------|------|
| `bluetooth/` | `BluetoothControl.java`<br>`BluetoothControlSingleton.java` | Upravljanje Bluetooth konekcijom i komunikacijom<br>Singleton patern za deljenje Bluetooth instance |
| `ui/` | `SplashActivity.java`<br>`MainActivity.java`<br>`ControlActivity.java` | Korisničke aktivnosti i UI logika |
| `utils/` | `Constants.java`<br>`PermissionManager.java` | Utility klase za konstante, Enum-ove uređaja i dozvole |

---

## 📚 Detaljne Dokumentacije Klasa

---

## 1. Bluetooth Paket

### `BluetoothControl.java`

**Putanja:** `com.example.universal_ac_tv_remote.bluetooth.BluetoothControl`

**Opis:**  
Centralna klasa za upravljanje Bluetooth funkcionalnostima. Pruža sve potrebne metode za proveru Bluetooth stanja, uspostavljanje konekcije sa ESP32 uređajem i slanje podataka.

#### 📌 Promenljive

| Tip | Ime | Modifikator | Opis |
|-----|-----|-------------|------|
| `BluetoothAdapter` | `bluetoothAdapter` | `private final` | Lokalni Bluetooth interfejs uređaja |
| `BluetoothSocket` | `bluetoothSocket` | `private` | RFCOMM socket za komunikaciju sa ESP32 |
| `OutputStream` | `outputStream` | `private` | Tok za slanje podataka preko Bluetooth veze |
| `Context` | `context` | `private final` | Kontekst aplikacije |

#### 🔧 Metode

##### 1. **Konstruktor**

```java
public BluetoothControl(Context context)
```

**Parametri:**
- `context` - Kontekst aplikacije

**Opis:** Inicijalizuje Bluetooth adapter i čuva kontekst aplikacije.

---

##### 2. **isBluetoothAvailable()**

```java
public boolean isBluetoothAvailable()
```

**Povratna vrednost:** `true` ako je Bluetooth dostupan, `false` ako nije

**Opis:** Proverava da li uređaj podržava Bluetooth funkcionalnost.

---

##### 3. **isBluetoothEnabled()**

```java
public boolean isBluetoothEnabled()
```

**Povratna vrednost:** `true` ako je Bluetooth uključen, `false` ako nije

**Opis:** Proverava da li je Bluetooth trenutno omogućen na uređaju.

---

##### 4. **enableBluetooth()**

```java
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
public void enableBluetooth(Activity activity, int requestCode)
```

**Parametri:**
- `activity` - Aktivnost iz koje se pokreće zahtev
- `requestCode` - Kod zahteva za identifikaciju u `onActivityResult()`

**Dozvole:** `BLUETOOTH_CONNECT`

**Opis:** Prikazuje sistemski dijalog koji traži od korisnika da uključi Bluetooth.

---

##### 5. **isDevicePaired()**

```java
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
public boolean isDevicePaired(String macAddress)
```

**Parametri:**
- `macAddress` - MAC adresa ESP32 uređaja (format: `"XX:XX:XX:XX:XX:XX"`)

**Povratna vrednost:** `true` ako je uređaj uparen, `false` ako nije

**Dozvole:** `BLUETOOTH_CONNECT`

**Opis:** Proverava da li je ESP32 uređaj sa određenom MAC adresom uparen sa Android telefonom.

---

##### 6. **connectToDevice()**

```java
@RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
public boolean connectToDevice(String macAddress, UUID uuid)
```

**Parametri:**
- `macAddress` - MAC adresa ESP32 uređaja
- `uuid` - UUID servisa za RFCOMM komunikaciju (SPP: `00001101-0000-1000-8000-00805F9B34FB`)

**Povratna vrednost:** `true` ako je konekcija uspešna, `false` ako nije

**Dozvole:** `BLUETOOTH_SCAN`

**Opis:** Uspostavlja RFCOMM konekciju sa ESP32 uređajem.

**Procedura:**
1. Prekida aktivno skeniranje Bluetooth uređaja
2. Dobija referencu na ESP32 preko MAC adrese
3. Kreira RFCOMM socket
4. Povezuje se na uređaj
5. Otvara izlazni tok za slanje podataka

---

##### 7. **sendData()**

```java
public void sendData(String data)
```

**Parametri:**
- `data` - String koji se šalje ESP32 uređaju (komanda, podatak, itd.)

**Opis:** Šalje tekstualne podatke preko aktivne Bluetooth konekcije. Podatci se konvertuju u bajtove i šalju preko izlaznog toka.

**Logging:**
- ✅ Uspeh: `📤 Podaci poslati: [data]`
- ❌ Greška: `❌ Greška pri slanju podataka: [error]`
- ⚠️ Upozorenje: `❌ outputStream je null - nije povezan`

---

##### 8. **closeConnection()**

```java
public void closeConnection()
```

**Opis:** Zatvara aktivnu Bluetooth konekciju i oslobađa resurse. Koristi se pri:
- Napuštanju aplikacije
- Vraćanju na početni ekran
- Prekidu komunikacije sa ESP32

**Logging:**
- ✅ Uspeh: `🔌 Bluetooth socket zatvoren (iz BluetoothControl)`
- ⚠️ Greška: `⚠️ Greška pri zatvaranju socketa: [error]`

---

##### 9. **getSocket()**

```java
public BluetoothSocket getSocket()
```

**Povratna vrednost:** Trenutni `BluetoothSocket` objekat ili `null` ako nije povezan

**Opis:** Vraća referencu na aktivni Bluetooth socket. Koristi se za proveru statusa konekcije ili pristup socket objektu iz drugih klasa.

---

### `BluetoothControlSingleton.java`

**Putanja:** `com.example.universal_ac_tv_remote.bluetooth.BluetoothControlSingleton`

**Opis:**  
Singleton patern koji omogućava deljenje jedne instance `BluetoothControl` klase između različitih aktivnosti. Ovim pristupom se osigurava da sve aktivnosti koriste istu Bluetooth konekciju bez potrebe za ponovnim povezivanjem.

**Šablonski patern:** Singleton Design Pattern

**Razlog korišćenja:**
- Izbegavanje višestrukih instanci BluetoothControl klase
- Održavanje jedne aktivne Bluetooth konekcije kroz celu aplikaciju
- Mogućnost pristupa Bluetooth funkcijama iz bilo koje aktivnosti
- Smanjenje resursa i optimizacija performansi

#### 📌 Promenljive

| Tip | Ime | Modifikator | Opis |
|-----|-----|-------------|------|
| `BluetoothControl` | `instance` | `private static` | Jedinstvena instanca BluetoothControl klase |

#### 🔧 Metode

##### 1. **setInstance()**

```java
public static void setInstance(BluetoothControl control)
```

**Parametri:**
- `control` - Instanca `BluetoothControl` klase koja se postavlja kao globalna

**Opis:** Postavlja globalnu instancu BluetoothControl klase. Poziva se u `MainActivity` nakon uspešne Bluetooth konekcije.

**Upotreba:**
```java
// U MainActivity nakon konekcije:
BluetoothControlSingleton.setInstance(bluetoothControl);
```

---

##### 2. **getInstance()**

```java
public static BluetoothControl getInstance()
```

**Povratna vrednost:** Trenutna globalna instanca `BluetoothControl` ili `null` ako nije postavljena

**Opis:** Vraća globalnu instancu BluetoothControl klase. Koristi se u drugim aktivnostima za pristup Bluetooth funkcijama.

**Upotreba:**
```java
// U ControlActivity:
BluetoothControl bluetoothControl = BluetoothControlSingleton.getInstance();
bluetoothControl.sendData("komanda");
```

---

## 2. UI Paket

### `SplashActivity.java`

**Putanja:** `com.example.universal_ac_tv_remote.ui.SplashActivity`

**Opis:**  
Prva aktivnost koja se prikazuje korisniku pri pokretanju aplikacije. Prikazuje animirani logo brenda tokom 5.5 sekundi pre nego što pređe na MainActivity.

#### 📌 Promenljive

| Tip | Ime | Modifikator | Opis |
|-----|-----|-------------|------|
| `ImageView` | `logo` | `package-private` | Logo aplikacije koji se animira |

#### 🔧 Metode

##### **onCreate()**

```java
@Override
protected void onCreate(Bundle savedInstanceState)
```

**Parametri:**
- `savedInstanceState` - Sačuvano stanje aktivnosti (null pri prvom pokretanju)

**Opis:** Kreira i inicijalizuje SplashActivity sa animiranim logom.

**Procedura animacije:**

1. **Edge-to-Edge prikaz:** Full-screen efekat
2. **Početna postavka:** Logo transparentan (alpha = 0)
3. **Fade In + Zoom (3s):**
   - Alpha: 0 → 1 (postaje vidljiv)
   - ScaleX: 1.0 → 1.1 (uvećanje horizontalno)
   - ScaleY: 1.0 → 1.2 (uvećanje vertikalno)
   - Interpolator: `OvershootInterpolator` (ubrzavanje sa prekoračenjem)
4. **Pauza (2s):** Logo ostaje vidljiv
5. **Fade Out (0.5s):** Logo nestaje
6. **Prelazak:** Prelazi na MainActivity

**Ukupno vreme:** ~5.5 sekundi

---

### `MainActivity.java`

**Putanja:** `com.example.universal_ac_tv_remote.ui.MainActivity`

**Opis:**  
Glavna aktivnost aplikacije koja omogućava korisniku da uspostavi Bluetooth konekciju sa ESP32 mikrokontrolerom. Služi kao most između korisnika i Bluetooth funkcionalnosti.

#### 📌 Promenljive

| Tip | Ime | Modifikator | Opis |
|-----|-----|-------------|------|
| `BluetoothControl` | `bluetoothControl` | `private` | Instanca za upravljanje Bluetooth-om |
| `TextView` | `connectStatus` | `private` | Prikaz statusa konekcije |
| `Button` | `connectBtn` | `private` | Dugme za povezivanje |
| `boolean` | `connected` | `package-private` | Flag za status konekcije |

#### 🔧 Metode

##### 1. **onCreate()**

```java
@Override
protected void onCreate(Bundle savedInstanceState)
```

**Parametri:**
- `savedInstanceState` - Sačuvano stanje aktivnosti

**Opis:** Inicijalizuje MainActivity, povezuje UI elemente i proverava dozvole.

**Procedura:**
1. Postavlja Edge-to-Edge prikaz
2. Povezuje TextView i Button
3. Kreira instancu `BluetoothControl`
4. Traži Bluetooth dozvole preko `PermissionManager`
5. Postavlja OnClickListener na dugme

---

##### 2. **connectToESP32()**

```java
private void connectToESP32()
```

**Opis:** Pokreće proceduru povezivanja sa ESP32 mikrokontrolerom.

**Procedura povezivanja:**

1. **Reset status:** "Not Connected" (crvena boja)
2. **Provera dostupnosti:** Da li uređaj ima Bluetooth?
3. **Provera stanja:** Da li je Bluetooth uključen?
4. **Provera uparivanja:** Da li je ESP32 uparen?
5. **Connecting status:** "Connecting..." (žuta boja)
6. **Pozadinska konekcija:** Pokretanje u novoj niti
7. **Uspešna konekcija:**
   - "Connected ✓" (zelena boja) nakon 1s
   - Prelazak na ControlActivity nakon dodatne 1s
8. **Neuspešna konekcija:** "Not Connected" (crvena boja)

**Moguće poruke:**

| Poruka | Boja | Značenje |
|--------|------|----------|
| "Not Connected" | Crvena | Nema konekcije sa ESP32 |
| "Connecting..." | Žuta | Pokušaj povezivanja u toku |
| "Connected ✓" | Zelena | Uspešna konekcija |
| "ESP32 is not paired" | Crvena | ESP32 nije uparen |

---

##### 3. **onResume()**

```java
@Override
protected void onResume()
```

**Opis:** Poziva se kada se aktivnost ponovo prikazuje na ekranu (povratak sa ControlActivity).

**Funkcionalnosti:**
1. Zatvara postojeću Bluetooth konekciju
2. Resetuje status na "Not Connected"

**Razlog:** Osigurava da korisnik mora ponovo da uspostavi konekciju svaki put.

---

### `ControlActivity.java`

**Putanja:** `com.example.universal_ac_tv_remote.ui.ControlActivity`

**Opis:**  
Aktivnost za upravljanje AC i TV uređajima nakon uspešne Bluetooth konekcije. Korisnik može izabrati tip uređaja (AC ili TV), specifični brend, i zatim slati IR komande preko ESP32 mikrokontrolera.

**Implementirani interfejsi:** `View.OnClickListener`

#### 📌 Promenljive

| Tip | Ime | Modifikator | Opis |
|-----|-----|-------------|------|
| `TextView` | `deviceText` | `package-private` | Prikaz trenutno izabranog uređaja |
| `TextView` | `notificationText` | `package-private` | Prikaz notifikacija korisnik u |
| `Button` | `tvButton` | `package-private` | Dugme za izbor TV uređaja |
| `Button` | `acButton` | `package-private` | Dugme za izbor AC uređaja |
| `Button` | `onButton` | `package-private` | Dugme za uključivanje uređaja |
| `Button` | `offButton` | `package-private` | Dugme za isključivanje uređaja |
| `Button` | `chPlusButton` | `package-private` | Dugme za sledeći TV kanal |
| `Button` | `chMinusButton` | `package-private` | Dugme za prethodni TV kanal |
| `Button` | `tempPlusButton` | `package-private` | Dugme za povećanje AC temperature |
| `Button` | `tempMinusButton` | `package-private` | Dugme za smanjenje AC temperature |
| `Button` | `volPlusButton` | `package-private` | Dugme za pojačavanje TV zvuka |
| `Button` | `volMinusButton` | `package-private` | Dugme za stišavanje TV zvuka |
| `BluetoothControl` | `bluetoothControl` | `private` | Instanca za slanje Bluetooth komandi |
| `String` | `selectedDeviceType` | `private` | Tip izabranog uređaja ("TV" ili "AC") |
| `int` | `selectedDeviceName` | `private` | ID izabranog uređaja (iz Enum-a) |
| `int` | `selectedAcDeviceValue` | `private` | Vrednost izabranog AC uređaja |
| `int` | `selectedTvDeviceValue` | `private` | Vrednost izabranog TV uređaja |

#### 🔧 Metode

##### 1. **onCreate()**

```java
@Override
protected void onCreate(Bundle savedInstanceState)
```

**Parametri:**
- `savedInstanceState` - Sačuvano stanje aktivnosti

**Opis:** Inicijalizuje ControlActivity i povezuje sve UI elemente.

**Procedura:**
1. Postavlja Edge-to-Edge prikaz
2. Dobija BluetoothControl instancu iz Singleton-a
3. Povezuje sve TextView i Button elemente
4. Postavlja početni tekst na deviceText: "No device is selected"
5. Postavlja OnClickListener na sve dugmiće
6. Onemogućava sve kontrolne dugmiće (dok se ne izabere uređaj)

---

##### 2. **onClick()**

```java
@Override
public void onClick(View v)
```

**Parametri:**
- `v` - View objekat koji je kliknut

**Opis:** Rukuje svim klikovima na dugmiće u aktivnosti.

**Logika:**
- `R.id.tv` → Prikazuje dijalog za izbor TV uređaja
- `R.id.ac` → Prikazuje dijalog za izbor AC uređaja
- `R.id.on` → Šalje komandu "ON"
- `R.id.off` → Šalje komandu "OFF"
- `R.id.ch_plus` → Šalje komandu "CH+"
- `R.id.ch_minus` → Šalje komandu "CH-"
- `R.id.temp_plus` → Šalje komandu "TEMP+"
- `R.id.temp_minus` → Šalje komandu "TEMP-"
- `R.id.vol_plus` → Šalje komandu "VOL+"
- `R.id.vol_minus` → Šalje komandu "VOL-"

---

##### 3. **showTvSelectionDialog()**

```java
private void showTvSelectionDialog()
```

**Opis:** Prikazuje AlertDialog sa listom svih dostupnih TV brendova.

**Procedura:**
1. Dobija listu TV uređaja iz `Constants.DecodeTypeTV` enum-a
2. Filtrira `UNKNOWN` vrednost
3. Kreira AlertDialog sa listom brendova
4. Korisnik bira brend iz liste
5. Postavlja `selectedDeviceName` na vrednost izabranog brenda
6. Ažurira `deviceText` sa izabranim brendom
7. Šalje informaciju o izboru na ESP32: `#TV|[ID]|null#`
8. Onemogućava AC dugmiće i omogućava TV dugmiće

**Format poruke:** `#TV|[DEVICE_ID]|null#`

**Primer:** Ako se izabere Samsung TV (ID=7): `#TV|7|null#`

---

##### 4. **showAcSelectionDialog()**

```java
private void showAcSelectionDialog()
```

**Opis:** Prikazuje AlertDialog sa listom svih dostupnih AC brendova.

**Procedura:**
1. Dobija listu AC uređaja iz `Constants.DecodeTypeAC` enum-a
2. Filtrira `UNKNOWN` vrednost
3. Kreira AlertDialog sa listom brendova
4. Korisnik bira brend iz liste
5. Postavlja `selectedDeviceName` na vrednost izabranog brenda
6. Ažurira `deviceText` sa izabranim brendom
7. Šalje informaciju o izboru na ESP32: `#AC|[ID]|null#`
8. Onemogućava TV dugmiće i omogućava AC dugmiće

**Format poruke:** `#AC|[DEVICE_ID]|null#`

**Primer:** Ako se izabere Samsung AC (ID=46): `#AC|46|null#`

---

##### 5. **sendCommand()**

```java
private void sendCommand(String command)
```

**Parametri:**
- `command` - Komanda koja se šalje ("ON", "OFF", "CH+", "TEMP+", itd.)

**Opis:** Šalje kontrolnu komandu na ESP32 preko Bluetooth veze.

**Procedura:**
1. Proverava da li je uređaj izabran
2. Ako nije, prikazuje poruku "Please choose a device first"
3. Formira poruku: `#[DEVICE_TYPE]|[DEVICE_ID]|[COMMAND]#`
4. Loguje poruku u Android Logcat
5. Poziva `sendData()` za slanje preko Bluetooth-a

**Format poruke:** `#[DEVICE_TYPE]|[DEVICE_ID]|[COMMAND]#`

**Primeri:**
- Samsung AC uključivanje: `#AC|46|ON#`
- Samsung TV povećanje zvuka: `#TV|7|VOL+#`
- Daikin AC povećanje temperature: `#AC|16|TEMP+#`

---

##### 6. **disableButtons()**

```java
public void disableButtons()
```

**Opis:** Onemogućava sve kontrolne dugmiće (ON, OFF, CH+, CH-, TEMP+, TEMP-, VOL+, VOL-).

**Funkcionalnost:**
- `setEnabled(false)` - Onemogućava klik
- `setAlpha(0.5f)` - Postavlja transparentnost na 50% (vizuelna indikacija)

**Koristi se:** Prilikom inicijalizacije pre nego što se izabere uređaj.

---

##### 7. **enableAcButtons()**

```java
public void enableAcButtons()
```

**Opis:** Omogućava dugmiće specifične za AC uređaje.

**Omogućeni dugmići:**
- `tempPlusButton` - Povećanje temperature
- `tempMinusButton` - Smanjenje temperature
- `onButton` - Uključivanje AC
- `offButton` - Isključivanje AC

**Funkcionalnost:**
- `setEnabled(true)` - Omogućava klik
- `setAlpha(1f)` - Postavlja punu vidljivost

---

##### 8. **disableTvButtons()**

```java
public void disableTvButtons()
```

**Opis:** Onemogućava dugmiće specifične za TV uređaje.

**Onemogućeni dugmići:**
- `chPlusButton` - Sledeći kanal
- `chMinusButton` - Prethodni kanal
- `volPlusButton` - Pojačaj zvuk
- `volMinusButton` - Utiši zvuk

**Koristi se:** Kada se izabere AC uređaj.

---

##### 9. **enableTvButtons()**

```java
public void enableTvButtons()
```

**Opis:** Omogućava dugmiće specifične za TV uređaje.

**Omogućeni dugmići:**
- `chPlusButton` - Sledeći kanal
- `chMinusButton` - Prethodni kanal
- `volPlusButton` - Pojačaj zvuk
- `volMinusButton` - Utiši zvuk

---

##### 10. **disableAcButtons()**

```java
public void disableAcButtons()
```

**Opis:** Onemogućava dugmiće specifične za AC uređaje (TEMP+, TEMP-).

**Napomena:** ON i OFF dugmići ostaju omogućeni jer se koriste i za TV.

**Koristi se:** Kada se izabere TV uređaj.

---

##### 11. **sendData()**

```java
public void sendData(String data)
```

**Parametri:**
- `data` - String koji se šalje preko Bluetooth-a

**Opis:** Helper metoda koja prosleđuje podatke `BluetoothControl` instanci za slanje.

**Provera:** Proverava da li je `bluetoothControl` instanca null pre slanja.

---

##### 12. **onBackPressed()**

```java
@Override
public void onBackPressed()
```

**Opis:** Rukuje pritiskom na "Back" dugme. Završava aktivnost i vraća korisnika na MainActivity.

---

## 3. Utils Paket

### `Constants.java`

**Putanja:** `com.example.universal_ac_tv_remote.utils.Constants`

**Opis:**  
Utility klasa koja sadrži sve statičke konstante za Bluetooth komunikaciju. Centralizacija konstanti olakšava održavanje koda.

#### 📌 Konstante

##### 1. **REQUEST_BLUETOOTH_PERMISSIONS**

```java
public static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
```

**Tip:** `int`  
**Vrednost:** `1`

**Opis:** Request kod za zahtevanje Bluetooth dozvola. Koristi se u `onRequestPermissionsResult()`.

**Potrebne dozvole:**
- Android 12+ (API 31+): `BLUETOOTH_CONNECT`, `BLUETOOTH_SCAN`
- Android 11 i niže: `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`

---

##### 2. **REQUEST_ENABLE_BT**

```java
public static final int REQUEST_ENABLE_BT = 2;
```

**Tip:** `int`  
**Vrednost:** `2`

**Opis:** Request kod za zahtevanje omogućavanja Bluetooth-a. Koristi se u `onActivityResult()`.

---

##### 3. **ESP32_UUID**

```java
public static final UUID ESP32_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
```

**Tip:** `UUID`  
**Vrednost:** `00001101-0000-1000-8000-00805F9B34FB`

**Opis:** UUID za RFCOMM serijski port profil (SPP - Serial Port Profile).

**SPP profil:**  
Standardni Bluetooth profil za serijsku komunikaciju između dva uređaja. ESP32 mora koristiti isti UUID.

---

##### 4. **ESP32_MAC_ADDRESS**

```java
public static final String ESP32_MAC_ADDRESS = "B0:B2:1C:44:C0:CE";
```

**Tip:** `String`  
**Vrednost:** `"B0:B2:1C:44:C0:CE"`  
**Format:** `XX:XX:XX:XX:XX:XX` (6 bajtova u heksadecimalnom zapisu)

**Opis:** MAC adresa (Media Access Control) ESP32 mikrokontrolera.

**Upotreba:**
1. Provera da li je ESP32 uparen
2. Uspostavljanje direktne Bluetooth konekcije

**NAPOMENA:** Ovu vrednost treba zameniti sa MAC adresom vašeg ESP32 uređaja! MAC adresa se može pronaći u Serial Monitor-u ESP32 pri pokretanju.

---

##### 5. **DecodeTypeAC (Enum)**

```java
public enum DecodeTypeAC { ... }
```

**Tip:** `Enum`  
**Broj vrednosti:** 69 (68 brendova + 1 UNKNOWN)

**Opis:** Enum koji definiše sve podržane AC (klima) brendove i njihove jedinstvene IR protokole.

**Struktura:**
- Svaki brend ima jedinstveni integer ID
- ID vrednosti odgovaraju ESP32 IRremote library protokolima
- UNKNOWN (-1) označava nepoznati ili nepodržani uređaj

**Metode:**
- `getValue()` - Vraća integer ID brenda

**Top 10 najpopularnijih brendova:**

| Brend | ID | Enum Vrednost |
|-------|----|--------------| 
| Daikin | 16 | `DAIKIN` |
| Samsung AC | 46 | `SAMSUNG_AC` |
| LG | 10 | (u DecodeTypeTV) |
| Mitsubishi AC | 20 | `MITSUBISHI_AC` |
| Panasonic AC | 49 | `PANASONIC_AC` |
| Gree | 24 | `GREE` |
| Hitachi AC | 40 | `HITACHI_AC` |
| Fujitsu AC | 33 | `FUJITSU_AC` |
| Toshiba AC | 32 | `TOSHIBA_AC` |
| Carrier AC | 37 | `CARRIER_AC` |

**Primeri korišćenja:**

```java
// Dobijanje ID-a za Samsung AC
int samsungId = Constants.DecodeTypeAC.SAMSUNG_AC.getValue(); // 46

// Iteracija kroz sve AC brendove
for (DecodeTypeAC type : DecodeTypeAC.values()) {
    if (type != DecodeTypeAC.UNKNOWN) {
        String name = type.name();
        int id = type.getValue();
    }
}

// Provera da li je uređaj podržan
String userChoice = "SAMSUNG_AC";
DecodeTypeAC selectedType = DecodeTypeAC.valueOf(userChoice);
if (selectedType != DecodeTypeAC.UNKNOWN) {
    // Uređaj je podržan
}
```

**Kompletan spisak brendova:**

<details>
<summary>Kliknite za prikaz svih 68 AC brendova</summary>

1. COOLIX (15)
2. DAIKIN (16)
3. KELVINATOR (18)
4. MITSUBISHI_AC (20)
5. GREE (24)
6. ARGO (27)
7. TROTEC (28)
8. TOSHIBA_AC (32)
9. FUJITSU_AC (33)
10. MIDEA (34)
11. CARRIER_AC (37)
12. HAIER_AC (38)
13. HITACHI_AC (40)
14. HITACHI_AC1 (41)
15. HITACHI_AC2 (42)
16. HAIER_AC_YRW02 (44)
17. WHIRLPOOL_AC (45)
18. SAMSUNG_AC (46)
19. ELECTRA_AC (48)
20. PANASONIC_AC (49)
21. DAIKIN2 (53)
22. VESTEL_AC (54)
23. TECO (55)
24. TCL112AC (57)
25. MITSUBISHI_HEAVY_88 (59)
26. MITSUBISHI_HEAVY_152 (60)
27. DAIKIN216 (61)
28. SHARP_AC (62)
29. GOODWEATHER (63)
30. DAIKIN160 (65)
31. NEOCLIMA (66)
32. DAIKIN176 (67)
33. DAIKIN128 (68)
34. AMCOR (69)
35. DAIKIN152 (70)
36. HITACHI_AC424 (73)
37. HITACHI_AC3 (77)
38. DAIKIN64 (78)
39. AIRWELL (79)
40. DELONGHI_AC (80)
41. CARRIER_AC40 (83)
42. CARRIER_AC64 (84)
43. HITACHI_AC344 (85)
44. CORONA_AC (86)
45. MIDEA24 (87)
46. SANYO_AC (89)
47. VOLTAS (90)
48. TECHNIBEL_AC (93)
49. PANASONIC_AC32 (96)
50. ECOCLIM (98)
51. TRUMA (100)
52. HAIER_AC176 (101)
53. TEKNOPOINT (102)
54. KELON (103)
55. TROTEC_3550 (104)
56. SANYO_AC88 (105)
57. RHOSS (108)
58. AIRTON (109)
59. COOLIX48 (110)
60. HITACHI_AC264 (111)
61. KELON168 (112)
62. HITACHI_AC296 (113)
63. DAIKIN200 (114)
64. HAIER_AC160 (115)
65. CARRIER_AC128 (116)
66. TOTO (117)
67. CLIMABUTLER (118)
68. TCL96AC (119)
69. BOSCH144 (120)
70. SANYO_AC152 (121)
71. DAIKIN312 (122)
72. CARRIER_AC84 (125)
73. YORK (126)
74. BLUESTARHEAVY (127)

</details>

---

##### 6. **DecodeTypeTV (Enum)**

```java
public enum DecodeTypeTV { ... }
```

**Tip:** `Enum`  
**Broj vrednosti:** 60 (59 brendova + 1 UNKNOWN)

**Opis:** Enum koji definiše sve podržane TV brendove i njihove jedinstvene IR protokole.

**Struktura:**
- Svaki brend ima jedinstveni integer ID
- ID vrednosti odgovaraju ESP32 IRremote library protokolima
- UNKNOWN (-1) označava nepoznati ili nepodržani uređaj
- UNUSED (0) je rezervisana vrednost

**Metode:**
- `getValue()` - Vraća integer ID brenda

**Top 10 najpopularnijih brendova:**

| Brend | ID | Enum Vrednost |
|-------|----|--------------| 
| Samsung | 7 | `SAMSUNG` |
| LG | 10 | `LG` |
| Sony | 4 | `SONY` |
| Panasonic | 5 | `PANASONIC` |
| Sharp | 14 | `SHARP` |
| Toshiba | (Koristi NEC) | `NEC` |
| Philips | 2 | `RC6` |
| JVC | 6 | `JVC` |
| Hitachi | (Koristi NEC) | `NEC` |
| Pioneer | 50 | `PIONEER` |

**Primeri korišćenja:**

```java
// Dobijanje ID-a za Samsung TV
int samsungId = Constants.DecodeTypeTV.SAMSUNG.getValue(); // 7

// Iteracija kroz sve TV brendove
for (DecodeTypeTV type : DecodeTypeTV.values()) {
    if (type != DecodeTypeTV.UNKNOWN && type != DecodeTypeTV.UNUSED) {
        String name = type.name();
        int id = type.getValue();
    }
}

// Provera da li je uređaj podržan
String userChoice = "SAMSUNG";
DecodeTypeTV selectedType = DecodeTypeTV.valueOf(userChoice);
if (selectedType != DecodeTypeTV.UNKNOWN) {
    // Uređaj je podržan
}
```

**Kompletan spisak brendova:**

<details>
<summary>Kliknite za prikaz svih 59 TV brendova</summary>

1. RC5 (1)
2. RC6 (2)
3. NEC (3)
4. SONY (4)
5. PANASONIC (5)
6. JVC (6)
7. SAMSUNG (7)
8. WHYNTER (8)
9. AIWA_RC_T501 (9)
10. LG (10)
11. SANYO (11)
12. MITSUBISHI (12)
13. DISH (13)
14. SHARP (14)
15. COOLIX (15)
16. DENON (17)
17. SHERWOOD (19)
18. RCMM (21)
19. SANYO_LC7461 (22)
20. RC5X (23)
21. PRONTO (25)
22. NEC_LIKE (26)
23. ARGO (27)
24. NIKAI (29)
25. RAW (30)
26. GLOBALCACHE (31)
27. MAGIQUEST (35)
28. LASERTAG (36)
29. GICABLE (43)
30. LUTRON (47)
31. PIONEER (50)
32. LG2 (51)
33. MWM (52)
34. SAMSUNG36 (56)
35. LEGOPF (58)
36. SONY_38K (74)
37. EPSON (75)
38. DOSHISHA (81)
39. MULTIBRACKETS (82)
40. ZEPEAL (88)
41. METZ (91)
42. TRANSCOLD (92)
43. MIRAGE (94)
44. ELITESCREENS (95)
45. MILESTAG2 (97)
46. XMP (99)
47. BOSE (106)
48. ARRIS (107)
49. WOWWEE (124)

</details>

**Napomena:** Neki brendovi kao Toshiba i Hitachi koriste NEC protokol jer dele isti IR standard.

---

### `PermissionManager.java`

**Putanja:** `com.example.universal_ac_tv_remote.utils.PermissionManager`

**Opis:**  
Utility klasa za upravljanje runtime dozvolama. Pojednostavljuje proces traženja dozvola za Bluetooth komunikaciju na različitim verzijama Android OS-a.

#### 🔧 Metode

##### **checkAndRequestBluetoothPermissions()**

```java
public static void checkAndRequestBluetoothPermissions(Activity activity, int requestCode)
```

**Parametri:**
- `activity` - Aktivnost koja traži dozvole (obično MainActivity)
- `requestCode` - Kod za identifikaciju zahteva (obično `Constants.REQUEST_BLUETOOTH_PERMISSIONS`)

**Opis:** Automatski detektuje verziju Android OS-a i traži odgovarajuće dozvole.

**Logika po verzijama:**

| Android verzija | API Level | Dozvole |
|----------------|-----------|---------|
| Android 12+ | 31+ | `BLUETOOTH_CONNECT`, `BLUETOOTH_SCAN` |
| Android 11 i niže | ≤30 | `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` |

**Razlog za location dozvole (Android ≤11):**  
Google je smatrao da Bluetooth skeniranje može kompromitovati privatnost jer otkriva obližnje uređaje i njihovu lokaciju.

**Korisnik može:**
1. ✅ Prihvatiti dozvole → aplikacija funkcioniše normalno
2. ❌ Odbiti dozvole → Bluetooth funkcije neće raditi
3. 🚫 Odbiti trajno ("Don't ask again") → korisnika treba uputiti u Settings

---

## 🔄 Tok Aplikacije

### Dijagram Toka

```
┌─────────────────────────────────────────────┐
│         1. SplashActivity (5.5s)            │
│                                             │
│  • Fade In + Zoom animacija (3s)           │
│  • Pauza (2s)                               │
│  • Fade Out (0.5s)                          │
└──────────────────┬──────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────┐
│         2. MainActivity                     │
│                                             │
│  • Provera Bluetooth dozvola                │
│  • Prikaz statusa konekcije                 │
│  • Dugme za povezivanje                     │
│                                             │
│  Klik na "Connect" dugme:                   │
│  ├─ Provera Bluetooth dostupnosti           │
│  ├─ Provera da li je BT uključen            │
│  ├─ Provera da li je ESP32 uparen           │
│  ├─ Pokretanje konekcije (pozadinska nit)   │
│  └─ Prikaz statusa                          │
│                                             │
│  Status opcije:                             │
│  • "Not Connected" (crvena)                 │
│  • "Connecting..." (žuta)                   │
│  • "Connected ✓" (zelena)                   │
│  • "ESP32 is not paired" (crvena)           │
└──────────────────┬──────────────────────────┘
                   │
                   │ (Uspešna konekcija)
                   ↓
┌─────────────────────────────────────────────┐
│         3. ControlActivity                  │
│                                             │
│  • Virtuelni daljinski upravljač            │
│  • Slanje IR komandi preko Bluetooth-a      │
│  • Kontrola AC uređaja                      │
│  • Kontrola TV uređaja                      │
│                                             │
│  [Back dugme] → Vraća se na MainActivity    │
└─────────────────────────────────────────────┘
```

### Detaljne Sekvence

#### Sekvenca 1: Pokretanje Aplikacije

```
Korisnik pokreće aplikaciju
         │
         ↓
SplashActivity.onCreate()
         │
         ├─ EdgeToEdge.enable()
         ├─ setContentView(R.layout.activity_splash)
         ├─ logo.setAlpha(0f)
         │
         ├─ logo.animate() [Fade In + Zoom]
         │   ├─ alpha(1f)
         │   ├─ scaleX(1.1f)
         │   ├─ scaleY(1.2f)
         │   ├─ duration(3000ms)
         │   └─ OvershootInterpolator
         │
         ├─ Pauza (2000ms)
         │
         ├─ logo.animate() [Fade Out]
         │   ├─ alpha(0f)
         │   └─ duration(500ms)
         │
         └─ startActivity(MainActivity)
```

#### Sekvenca 2: Bluetooth Konekcija

```
MainActivity.onCreate()
         │
         ├─ EdgeToEdge.enable()
         ├─ setContentView(R.layout.activity_main)
         ├─ connectStatus = findViewById(...)
         ├─ connectBtn = findViewById(...)
         ├─ bluetoothControl = new BluetoothControl(this)
         └─ PermissionManager.checkAndRequestBluetoothPermissions()
         
         
Korisnik klikne "Connect" dugme
         │
         ↓
MainActivity.connectToESP32()
         │
         ├─ connectStatus.setText("Not Connected")
         ├─ connectStatus.setTextColor(RED)
         │
         ├─ [Provera] bluetoothControl.isBluetoothAvailable()
         │   └─ Ako NE → Toast("This device does not have bluetooth")
         │
         ├─ [Provera] bluetoothControl.isBluetoothEnabled()
         │   └─ Ako NE → bluetoothControl.enableBluetooth()
         │
         ├─ [Provera] bluetoothControl.isDevicePaired(ESP32_MAC_ADDRESS)
         │   └─ Ako NE → connectStatus.setText("ESP32 is not paired")
         │
         ├─ connectStatus.setText("Connecting...")
         ├─ connectStatus.setTextColor(YELLOW)
         │
         ├─ new Thread() { // Pozadinska konekcija
         │   │
         │   ├─ connected = bluetoothControl.connectToDevice()
         │   │   │
         │   │   ├─ bluetoothAdapter.cancelDiscovery()
         │   │   ├─ device = bluetoothAdapter.getRemoteDevice(MAC)
         │   │   ├─ bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID)
         │   │   ├─ bluetoothSocket.connect()
         │   │   └─ outputStream = bluetoothSocket.getOutputStream()
         │   │
         │   └─ runOnUiThread() {
         │       │
         │       ├─ Ako USPEŠNO:
         │       │   ├─ Handler.postDelayed(1000ms)
         │       │   ├─ connectStatus.setText("Connected ✓")
         │       │   ├─ connectStatus.setTextColor(GREEN)
         │       │   ├─ Handler.postDelayed(1000ms)
         │       │   └─ startActivity(ControlActivity)
         │       │
         │       └─ Ako NEUSPEŠNO:
         │           ├─ connectStatus.setText("Not Connected")
         │           └─ connectStatus.setTextColor(RED)
         │   }
         └─ }.start()
```

---

## 📡 Bluetooth Komunikacija

### Bluetooth Protokol: RFCOMM (SPP)

**RFCOMM (Radio Frequency Communication)** je Bluetooth protokol koji emulira serijsku komunikaciju preko RS-232 porta. Koristi se za prenos tekstualnih i binarnih podataka između dva uređaja.

**SPP (Serial Port Profile)** je Bluetooth profil baziran na RFCOMM-u koji omogućava bežičnu serijsku komunikaciju.

### Arhitektura Komunikacije

```
┌─────────────────────────────────────────────────────┐
│              Android Telefon                        │
│                                                     │
│  ┌─────────────────────────────────────────┐       │
│  │         MainActivity.java               │       │
│  │  ┌───────────────────────────────────┐  │       │
│  │  │   BluetoothControl.java           │  │       │
│  │  │                                   │  │       │
│  │  │  • BluetoothAdapter               │  │       │
│  │  │  • BluetoothSocket (RFCOMM)       │  │       │
│  │  │  • OutputStream                   │  │       │
│  │  │  • sendData("komanda")            │  │       │
│  │  └───────────────────────────────────┘  │       │
│  └─────────────────────────────────────────┘       │
└────────────────────┬────────────────────────────────┘
                     │
                     │ Bluetooth (RFCOMM/SPP)
                     │ UUID: 00001101-0000-1000-8000-00805F9B34FB
                     │
                     ↓
┌─────────────────────────────────────────────────────┐
│              ESP32 Mikrocontroler                   │
│                                                     │
│  ┌─────────────────────────────────────────┐       │
│  │      BluetoothSerial Library            │       │
│  │                                         │       │
│  │  • SerialBT.begin("ESP32_Remote")      │       │
│  │  • SerialBT.available()                │       │
│  │  • SerialBT.read()                     │       │
│  │  • Parsiranje komandi                  │       │
│  └─────────────────┬───────────────────────┘       │
│                    │                               │
│                    ↓                               │
│  ┌─────────────────────────────────────────┐       │
│  │         IR LED Emiter                   │       │
│  │                                         │       │
│  │  • IRsend library                       │       │
│  │  • Emituje IR signale                   │       │
│  └─────────────────────────────────────────┘       │
└──────────────────────┬──────────────────────────────┘
                       │
                       │ Infracrveni signal (IR)
                       │
                       ↓
┌──────────────────────────────────────────────────────┐
│          AC Uređaj / TV Prijemnik                    │
│                                                      │
│  • Prima IR signale                                  │
│  • Izvršava komande (Power, Temp, Volume, etc.)     │
└──────────────────────────────────────────────────────┘
```

### Bluetooth Konekcija - Korak po Korak

#### 1. **Inicijalizacija Bluetooth Adaptera**

```java
BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
```

- Dobija referencu na lokalni Bluetooth adapter uređaja
- Ako je `null`, uređaj ne podržava Bluetooth

#### 2. **Provera Bluetooth Stanja**

```java
boolean enabled = bluetoothAdapter.isEnabled();
```

- Proverava da li je Bluetooth uključen
- Ako nije, zahteva omogućavanje

#### 3. **Provera Uparivanja**

```java
Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
```

- Dobija listu svih uparenih Bluetooth uređaja
- Proverava da li se MAC adresa ESP32 nalazi u listi

#### 4. **Kreiranje RFCOMM Socketa**

```java
BluetoothDevice device = bluetoothAdapter.getRemoteDevice(MAC_ADDRESS);
BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID);
```

- Dobija referencu na ESP32 preko MAC adrese
- Kreira RFCOMM socket sa standardnim SPP UUID-om

#### 5. **Povezivanje**

```java
socket.connect();
```

- Blokira nit dok se ne uspostavi konekcija
- Može baciti `IOException` ako konekcija ne uspe

#### 6. **Otvaranje Izlaznog Toka**

```java
OutputStream outputStream = socket.getOutputStream();
```

- Otvara tok za pisanje podataka na ESP32

#### 7. **Slanje Podataka**

```java
outputStream.write("POWER_ON".getBytes());
```

- Konvertuje String u bajtove
- Šalje bajtove preko Bluetooth veze

#### 8. **Zatvaranje Konekcije**

```java
socket.close();
```

- Zatvara socket i oslobađa resurse

### Primeri Komandi

#### Protokol Komunikacije

Aplikacija koristi strukturirani protokol za slanje komandi na ESP32:

**Format:** `#[DEVICE_TYPE]|[DEVICE_ID]|[COMMAND]#`

**Komponente:**
- `#` - Start marker (delimiter)
- `DEVICE_TYPE` - Tip uređaja: "TV" ili "AC"
- `|` - Separator
- `DEVICE_ID` - Integer ID brenda iz Enum-a
- `|` - Separator
- `COMMAND` - Komanda ili `null` (za izbor uređaja)
- `#` - End marker (delimiter)

#### Primeri Poruka

##### Izbor Uređaja

| Akcija | Poruka | Opis |
|--------|--------|------|
| Izbor Samsung AC | `#AC|46|null#` | Postavlja ESP32 da koristi Samsung AC protokol |
| Izbor Samsung TV | `#TV|7|null#` | Postavlja ESP32 da koristi Samsung TV protokol |
| Izbor Daikin AC | `#AC|16|null#` | Postavlja ESP32 da koristi Daikin AC protokol |
| Izbor LG TV | `#TV|10|null#` | Postavlja ESP32 da koristi LG TV protokol |

##### AC Komande

| Komanda | Poruka | ESP32 Akcija |
|---------|--------|--------------|
| Uključi AC | `#AC|46|ON#` | Emituje IR signal za Power On |
| Isključi AC | `#AC|46|OFF#` | Emituje IR signal za Power Off |
| Povećaj temperaturu | `#AC|46|TEMP+#` | Emituje IR signal za Temp+ |
| Smanji temperaturu | `#AC|46|TEMP-#` | Emituje IR signal za Temp- |

##### TV Komande

| Komanda | Poruka | ESP32 Akcija |
|---------|--------|--------------|
| Uključi/Isključi TV | `#TV|7|ON#` ili `#TV|7|OFF#` | Emituje IR signal za Power Toggle |
| Pojačaj zvuk | `#TV|7|VOL+#` | Emituje IR signal za Volume+ |
| Utiši zvuk | `#TV|7|VOL-#` | Emituje IR signal za Volume- |
| Sledeći kanal | `#TV|7|CH+#` | Emituje IR signal za Channel+ |
| Prethodni kanal | `#TV|7|CH-#` | Emituje IR signal za Channel- |

#### Tok Komunikacije

```
Android App                     ESP32                      AC/TV Uređaj
    │                             │                             │
    │  #AC|46|null#               │                             │
    ├────────────────────────────►│                             │
    │  (Izbor Samsung AC)         │                             │
    │                             │                             │
    │  #AC|46|ON#                 │                             │
    ├────────────────────────────►│                             │
    │  (Komanda ON)               │                             │
    │                             │  IR Signal (Power On)       │
    │                             ├────────────────────────────►│
    │                             │                             │ ✓ AC se uključuje
    │                             │                             │
    │  #AC|46|TEMP+#              │                             │
    ├────────────────────────────►│                             │
    │  (Povećaj temp)             │                             │
    │                             │  IR Signal (Temp+)          │
    │                             ├────────────────────────────►│
    │                             │                             │ ✓ Temperatura +1°C
```

#### ESP32 Parsiranje (Pseudokod)

```cpp
if (SerialBT.available()) {
    String message = SerialBT.readString();
    
    // Provera format a: mora početi i završiti sa #
    if (message.startsWith("#") && message.endsWith("#")) {
        // Ukloni # markere
        message = message.substring(1, message.length() - 1);
        
        // Podeli poruku po | separatoru
        String parts[] = split(message, '|');
        
        String deviceType = parts[0];  // "TV" ili "AC"
        int deviceId = parts[1].toInt(); // 46, 7, 16, itd.
        String command = parts[2];     // "ON", "OFF", "TEMP+", "null"
        
        // Ako je command "null", samo postavi tip uređaja
        if (command == "null") {
            currentDeviceType = deviceType;
            currentDeviceId = deviceId;
            return;
        }
        
        // Inače, emituj IR signal
        if (deviceType == "AC") {
            sendACCommand(deviceId, command);
        } else if (deviceType == "TV") {
            sendTVCommand(deviceId, command);
        }
    }
}
```

---

## 🔐 Android Dozvole

### Bluetooth Dozvole po Verzijama

Android 12 (API 31) je uveo nove Bluetooth dozvole koje zamenjuju stare location dozvole.

#### Android 12+ (API 31+)

**Potrebne dozvole:**

```xml
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
```

**`BLUETOOTH_CONNECT`:**
- Potrebna za povezivanje sa uparenim uređajima
- Potrebna za dobijanje liste uparenih uređaja
- Potrebna za uključivanje/isključivanje Bluetooth-a

**`BLUETOOTH_SCAN`:**
- Potrebna za skeniranje i otkrivanje novih Bluetooth uređaja
- Potrebna za `cancelDiscovery()`

#### Android 11 i niže (API ≤30)

**Potrebne dozvole:**

```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

**`ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`:**
- Google je smatrao da Bluetooth skeniranje može otkriti lokaciju korisnika
- Obavezne za Bluetooth operacije na Android ≤11

### Runtime Dozvole

Sve Bluetooth dozvole su **opasne dozvole** (dangerous permissions) i moraju se tražiti tokom izvršavanja aplikacije (runtime).

**Procedura:**

1. **Provera dozvole:**
```java
if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) 
    != PackageManager.PERMISSION_GRANTED) {
    // Dozvola nije odobrena
}
```

2. **Zahtevanje dozvole:**
```java
ActivityCompat.requestPermissions(this,
    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
    REQUEST_CODE);
```

3. **Rukovanje odgovorom:**
```java
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Dozvola odobrena ✓
        } else {
            // Dozvola odbijena ✗
        }
    }
}
```

### Manifest Fajl (AndroidManifest.xml)

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Bluetooth dozvole za Android 12+ -->
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    
    <!-- Bluetooth dozvole za Android 11 i niže -->
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

    <application>
        <!-- Aktivnosti -->
        <activity android:name=".ui.SplashActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <activity android:name=".ui.MainActivity" />
        <activity android:name=".ui.ControlActivity" />
    </application>
</manifest>
```
---

## 📊 Dijagram Klasa (UML)

```
┌─────────────────────────────────────────┐
│          BluetoothControl               │
├─────────────────────────────────────────┤
│ - bluetoothAdapter: BluetoothAdapter    │
│ - bluetoothSocket: BluetoothSocket      │
│ - outputStream: OutputStream            │
│ - context: Context                      │
├─────────────────────────────────────────┤
│ + BluetoothControl(Context)             │
│ + isBluetoothAvailable(): boolean       │
│ + isBluetoothEnabled(): boolean         │
│ + enableBluetooth(Activity, int)        │
│ + isDevicePaired(String): boolean       │
│ + connectToDevice(String, UUID): bool   │
│ + sendData(String)                      │
│ + closeConnection()                     │
│ + getSocket(): BluetoothSocket          │
└─────────────────────────────────────────┘
                   △
                   │
                   │ stored in
                   │
┌──────────────────┴──────────────────────┐
│     BluetoothControlSingleton           │
├─────────────────────────────────────────┤
│ - instance: BluetoothControl (static)   │
├─────────────────────────────────────────┤
│ + setInstance(BluetoothControl)         │
│ + getInstance(): BluetoothControl       │
└──────────────────┬──────────────────────┘
                   │
                   │ used by
                   │
┌──────────────────┴──────────────────────┐
│          MainActivity                   │
├─────────────────────────────────────────┤
│ - bluetoothControl: BluetoothControl    │
│ - connectStatus: TextView               │
│ - connectBtn: Button                    │
│ - connected: boolean                    │
├─────────────────────────────────────────┤
│ # onCreate(Bundle)                      │
│ # onResume()                            │
│ - connectToESP32()                      │
└─────────────────────────────────────────┘
                   │
                   │ navigates to
                   ↓
┌─────────────────────────────────────────┐
│         ControlActivity                 │
├─────────────────────────────────────────┤
│ - bluetoothControl: BluetoothControl    │
│ - deviceText: TextView                  │
│ - notificationText: TextView            │
│ - tvButton, acButton: Button            │
│ - onButton, offButton: Button           │
│ - chPlusButton, chMinusButton: Button   │
│ - tempPlusButton, tempMinusButton: Btn  │
│ - volPlusButton, volMinusButton: Button │
│ - selectedDeviceType: String            │
│ - selectedDeviceName: int               │
├─────────────────────────────────────────┤
│ # onCreate(Bundle)                      │
│ + onClick(View)                         │
│ - showTvSelectionDialog()               │
│ - showAcSelectionDialog()               │
│ - sendCommand(String)                   │
│ + disableButtons()                      │
│ + enableAcButtons()                     │
│ + disableTvButtons()                    │
│ + enableTvButtons()                     │
│ + disableAcButtons()                    │
│ + sendData(String)                      │
│ + onBackPressed()                       │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│         SplashActivity                  │
├─────────────────────────────────────────┤
│ - logo: ImageView                       │
├─────────────────────────────────────────┤
│ # onCreate(Bundle)                      │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│            Constants                    │
├─────────────────────────────────────────┤
│ + REQUEST_BLUETOOTH_PERMISSIONS: int    │
│ + REQUEST_ENABLE_BT: int                │
│ + ESP32_UUID: UUID                      │
│ + ESP32_MAC_ADDRESS: String             │
│                                         │
│ «enum» DecodeTypeAC                     │
│   - value: int                          │
│   + getValue(): int                     │
│   (68 AC brendova)                      │
│                                         │
│ «enum» DecodeTypeTV                     │
│   - value: int                          │
│   + getValue(): int                     │
│   (59 TV brendova)                      │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│         PermissionManager               │
├─────────────────────────────────────────┤
│ + checkAndRequestBluetoothPermissions(  │
│     Activity, int)                      │
└─────────────────────────────────────────┘
```

### Odnosi Između Klasa

```
MainActivity
    ├─ Kreira novu instancu BluetoothControl
    ├─ Uspostavlja Bluetooth konekciju
    ├─ Čuva instancu u BluetoothControlSingleton
    └─ Prelazi na ControlActivity nakon konekcije

ControlActivity
    ├─ Dobija BluetoothControl iz BluetoothControlSingleton
    ├─ Prikazuje dijaloge za izbor uređaja
    ├─ Šalje komande preko BluetoothControl.sendData()
    └─ Upravlja stanjem dugmića prema izabranom uređaju

BluetoothControlSingleton
    ├─ Čuva jednu globalnu instancu BluetoothControl
    ├─ Omogućava deljenje konekcije između aktivnosti
    └─ Sprečava potrebu za ponovnim povezivanjem

Constants
    ├─ Definiše Bluetooth parametre (UUID, MAC)
    ├─ DecodeTypeAC: 68 AC brendova sa ID-evima
    └─ DecodeTypeTV: 59 TV brendova sa ID-evima

PermissionManager
    └─ Automatski traži dozvole prema verziji Android OS-a
```

---

## 🚀 Implementirane i Planirane Funkcionalnosti

### ✅ Implementirane Funkcionalnosti

- [x] **Bluetooth Komunikacija:**
  - [x] Provera dostupnosti Bluetooth adaptera
  - [x] Provera da li je Bluetooth uključen
  - [x] Provera uparivanja sa ESP32
  - [x] Uspostavljanje RFCOMM konekcije
  - [x] Slanje podataka preko Bluetooth veze
  - [x] Zatvaranje konekcije i oslobađanje resursa
  - [x] Singleton patern za deljenje konekcije

- [x] **AC Kontrola:**
  - [x] Power On/Off dugmići
  - [x] Temperature + / - kontrola
  - [x] Izbor AC brendova iz liste (68 brendova)
  - [x] AlertDialog za izbor AC uređaja
  - [x] Dinamičko omogućavanje AC dugmića

- [x] **TV Kontrola:**
  - [x] Power On/Off dugmići
  - [x] Volume Up/Down kontrola
  - [x] Channel Up/Down kontrola
  - [x] Izbor TV brendova iz liste (59 brendova)
  - [x] AlertDialog za izbor TV uređaja
  - [x] Dinamičko omogućavanje TV dugmića

- [x] **UI Komponente:**
  - [x] Splash screen sa animacijom (Fade In + Zoom + Fade Out)
  - [x] MainActivity sa statusom konekcije (crvena/žuta/zelena)
  - [x] ControlActivity sa dugmićima za kontrolu
  - [x] TextView za prikaz izabranog uređaja
  - [x] AlertDialog za izbor brendova
  - [x] Dinamičko onemogućavanje/omogućavanje dugmića

- [x] **Protokol Komunikacije:**
  - [x] Strukturirani format poruka: `#TYPE|ID|COMMAND#`
  - [x] Parsiranje komandi u sendCommand() metodi
  - [x] Logging poslanih podataka u Logcat

- [x] **Android Dozvole:**
  - [x] Runtime zahtevanje Bluetooth dozvola
  - [x] Podrška za Android 12+ (BLUETOOTH_CONNECT, BLUETOOTH_SCAN)
  - [x] Podrška za Android ≤11 (ACCESS_FINE_LOCATION)

### 🔄 Planirane Funkcionalnosti

- [ ] **AC Napredne Kontrole:**
  - [ ] Temperature slider (16°C - 30°C)
  - [ ] Fan speed kontrola (Low, Medium, High, Auto)
  - [ ] Mode selection (Cool, Heat, Fan, Dry)
  - [ ] Swing kontrola (Up/Down, Left/Right)
  - [ ] Sleep mode
  - [ ] Timer funkcija

- [ ] **TV Napredne Kontrole:**
  - [ ] Numerička tastatura (0-9)
  - [ ] Mute dugme
  - [ ] Input source selekcija (HDMI1, HDMI2, AV, USB)
  - [ ] Menu navigation (Up, Down, Left, Right, OK)
  - [ ] Back/Return dugme
  - [ ] Smart TV funkcije

- [ ] **UI Poboljšanja:**
  - [ ] Material Design 3 komponente
  - [ ] Dark mode tema
  - [ ] Vibracija pri kliku na dugmiće (haptic feedback)
  - [ ] Animacije prelaza između aktivnosti (Shared Element Transitions)
  - [ ] Custom progress bar prilikom povezivanja
  - [ ] Snackbar notifikacije umesto Toast poruka
  - [ ] SwipeRefreshLayout za ponovno povezivanje

- [ ] **Backend Poboljšanja:**
  - [ ] Automatsko ponovno povezivanje pri gubitku konekcije
  - [ ] Praćenje stanja Battery Level-a ESP32
  - [ ] Čuvanje poslednje izabrane temperature u SharedPreferences
  - [ ] Čuvanje poslednje izabranog uređaja
  - [ ] Multi-device podrška (kontrola više AC/TV uređaja)
  - [ ] History log poslanih komandi
  - [ ] Backup i restore podešavanja

- [ ] **Networking:**
  - [ ] WiFi kontrola kao alternativa Bluetooth-u
  - [ ] MQTT protokol za IoT integraciju
  - [ ] HTTP REST API za remote kontrolu
  - [ ] WebSocket za real-time komunikaciju

- [ ] **Sigurnost:**
  - [ ] PIN zaštita za pristup aplikaciji
  - [ ] Biometric autentifikacija (fingerprint, face)
  - [ ] Enkripcija Bluetooth komunikacije

- [ ] **Dodatne Funkcije:**
  - [ ] Widget za brzu kontrolu sa Home Screen-a
  - [ ] Quick Settings Tile za Android
  - [ ] Voice commands (Google Assistant integracija)
  - [ ] Scheduling (programiranje uključivanja/isključivanja)
  - [ ] Geofencing (automatsko uključivanje kada dođete kući)
  - [ ] Statistika korišćenja (koliko puta poslata svaka komanda)

---

## 📝 Zaključak

Ova dokumentacija pokriva sve trenutno implementirane klase, metode i promenljive u projektu **Universal AC/TV Remote**. Projekat je u funkcionalnoj fazi razvoja sa potpuno implementiranom Bluetooth komunikacijom i osnovnim kontrolama za AC i TV uređaje.

### 📊 Statistika Projekta

**Broj klasa:** 7
- Bluetooth paket: 2 klase
- UI paket: 3 aktivnosti
- Utils paket: 2 utility klase

**Broj metoda:** 40+
- BluetoothControl: 9 metoda
- BluetoothControlSingleton: 2 metode
- ControlActivity: 12 metoda
- MainActivity: 3 metode
- SplashActivity: 1 metoda
- Constants: 2 enum-a sa getValue() metodama
- PermissionManager: 1 metoda

**Podržani uređaji:**
- **AC brendova:** 68
- **TV brendova:** 59
- **Ukupno:** 127 brendova

**Komande:** 8 osnovnih komandi (ON, OFF, TEMP+, TEMP-, VOL+, VOL-, CH+, CH-)

### ✅ Trenutno Funkcionalne Komponente

- ✅ Splash screen animacija (5.5s trajanje)
- ✅ Bluetooth konekcija sa ESP32 (RFCOMM/SPP protokol)
- ✅ Provera i zahtevanje runtime dozvola (Android 12+ i starije verzije)
- ✅ Vizuelni status konekcije (4 stanja sa bojama)
- ✅ Singleton patern za deljenje Bluetooth instance
- ✅ AlertDialog za izbor AC uređaja (68 brendova)
- ✅ AlertDialog za izbor TV uređaja (59 brendova)
- ✅ Dinamičko upravljanje dugmićima prema tipu uređaja
- ✅ Strukturirani protokol komunikacije (`#TYPE|ID|COMMAND#`)
- ✅ Logging sistema za debugging

### 🎯 Sledeći Koraci

1. **ESP32 Firmware:**
   - Implementacija parsiranja protokola `#TYPE|ID|COMMAND#`
   - Mapiranje device ID-eva na IRremote library protokole
   - Emitovanje IR signala prema primljenim komandama
   - Testiranje sa realnim AC i TV uređajima

2. **Android App Poboljšanja:**
   - Implementacija temperature slider-a za AC
   - Dodavanje numeričke tastature za TV
   - Material Design 3 redesign
   - Dark mode tema
   - Automatsko ponovno povezivanje

3. **Testiranje:**
   - Unit testovi za BluetoothControl klasu
   - UI testovi za ControlActivity
   - Integration testovi za Bluetooth komunikaciju
   - Testiranje sa različitim AC i TV brendovima

4. **Dokumentacija:**
   - Kreiranje ESP32 firmware dokumentacije
   - API dokumentacija za protokol komunikacije
   - User guide za krajnje korisnike
   - Video tutoriali za instalaciju i upotrebu

### 🏆 Postignuća

- ✅ Uspešna implementacija Singleton paterna
- ✅ Podrška za 127 različitih brendova
- ✅ Robusna Bluetooth komunikacija sa error handling-om
- ✅ Intuitivni korisničko interfejs
- ✅ Skalabilna arhitektura za buduća proširenja

---

**Poslednji put ažurirano:** 27. Oktobar 2025  
**Verzija dokumentacije:** 2.0  
**Autor:** Universal AC/TV Remote Team

---

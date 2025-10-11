# 📱 Dokumentacija Projekta: Universal AC/TV Remote

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
8. [Konfiguracija ESP32](#-konfiguracija-esp32)

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
- 🔄 Kontrola AC uređaja (u razvoju)
- 🔄 Kontrola TV uređaja (u razvoju)

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
| `bluetooth/` | `BluetoothControl.java` | Upravljanje Bluetooth konekcijom i komunikacijom |
| `ui/` | `SplashActivity.java`<br>`MainActivity.java`<br>`ControlActivity.java` | Korisničke aktivnosti i UI logika |
| `utils/` | `Constants.java`<br>`PermissionManager.java` | Utility klase za konstante i dozvole |

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

---

##### 8. **closeConnection()**

```java
public void closeConnection()
```

**Opis:** Zatvara aktivnu Bluetooth konekciju i oslobađa resurse. Koristi se pri:
- Napuštanju aplikacije
- Vraćanju na početni ekran
- Prekidu komunikacije sa ESP32

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
Aktivnost za upravljanje AC i TV uređajima nakon uspešne Bluetooth konekcije. Korisnik može slati komande ESP32 mikrokontroleru koji emituje IR signale.

#### 🔧 Metode

##### 1. **onCreate()**

```java
@Override
protected void onCreate(Bundle savedInstanceState)
```

**Parametri:**
- `savedInstanceState` - Sačuvano stanje aktivnosti

**Opis:** Inicijalizuje ControlActivity i učitava layout.

---

##### 2. **onBackPressed()**

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

### Primeri Komandi (Planirano)

| Komanda | Opis | ESP32 Akcija |
|---------|------|--------------|
| `AC_POWER_ON` | Uključi AC | Emituje IR signal za Power On |
| `AC_TEMP_UP` | Povećaj temperaturu | Emituje IR signal zaTemp+ |
| `AC_TEMP_DOWN` | Smanji temperaturu | Emituje IR signal za Temp- |
| `TV_POWER_TOGGLE` | Toggle TV Power | Emituje IR signal za Power |
| `TV_VOL_UP` | Pojačaj zvuk | Emituje IR signal za Volume+ |
| `TV_CH_UP` | Sledeći kanal | Emituje IR signal za Channel+ |

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

## ⚙️ Konfiguracija ESP32

### ESP32 Bluetooth Server (Arduino Kod)

```cpp
#include <BluetoothSerial.h>

BluetoothSerial SerialBT;

void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP32_Remote"); // Ime Bluetooth uređaja
  Serial.println("ESP32 Bluetooth je spreman za uparivanje!");
  Serial.print("MAC Adresa: ");
  Serial.println(ESP32.getEfuseMac(), HEX); // Prikaži MAC adresu
}

void loop() {
  if (SerialBT.available()) {
    String command = SerialBT.readString();
    Serial.println("Primljena komanda: " + command);
    
    // Parsiranje komandi
    if (command == "AC_POWER_ON") {
      // Emituj IR signal za AC Power On
    }
    else if (command == "AC_TEMP_UP") {
      // Emituj IR signal za AC Temp+
    }
    // ... ostale komande
  }
}
```

### Uparivanje ESP32 sa Android Telefonom

1. **Uključi ESP32** i otpočni Bluetooth server
2. **Otvori Bluetooth Settings** na Android telefonu
3. **Skeniraj uređaje** → "ESP32_Remote" treba da bude vidljiv
4. **Klikni na "ESP32_Remote"** → Upari uređaje
5. **PIN kod** (ako se traži): obično `1234` ili `0000`
6. **Proveri MAC adresu** u Serial Monitor-u ESP32
7. **Kopiraj MAC adresu** u `Constants.ESP32_MAC_ADDRESS`

### IR LED Emiter (Planirana Implementacija)

```cpp
#include <IRremoteESP8266.h>
#include <IRsend.h>

const uint16_t IR_LED_PIN = 4; // GPIO pin za IR LED
IRsend irsend(IR_LED_PIN);

void sendACPowerOn() {
  // Emituj IR signal za AC Power On (primer za Samsung AC)
  uint16_t rawData[67] = {4500,4500, 550,1650, 550,1650, ...};
  irsend.sendRaw(rawData, 67, 38); // 38kHz frekvencija
}
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
└─────────────────────────────────────────┘
                   △
                   │
                   │ uses
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
│ # onCreate(Bundle)                      │
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
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│         PermissionManager               │
├─────────────────────────────────────────┤
│ + checkAndRequestBluetoothPermissions(  │
│     Activity, int)                      │
└─────────────────────────────────────────┘
```

---

## 🚀 Buduće Implementacije

### Planirane Funkcionalnosti

- [ ] **AC Kontrola:**
  - [ ] Power On/Off dugme
  - [ ] Temperature slider (16°C - 30°C)
  - [ ] Fan speed kontrola (Low, Medium, High, Auto)
  - [ ] Mode selection (Cool, Heat, Fan, Dry)

- [ ] **TV Kontrola:**
  - [ ] Power Toggle dugme
  - [ ] Volume Up/Down
  - [ ] Channel Up/Down
  - [ ] Numerička tastatura (0-9)
  - [ ] Mute dugme

- [ ] **UI Poboljšanja:**
  - [ ] Material Design komponente
  - [ ] Dark mode tema
  - [ ] Vibracija pri kliku na dugmiće
  - [ ] Animacije prelaza između aktivnosti

- [ ] **Backend Poboljšanja:**
  - [ ] Automatsko ponovno povezivanje
  - [ ] Praćenje stanja Battery Level-a ESP32
  - [ ] Čuvanje poslednje korišćene temperature
  - [ ] Multi-device podrška (kontrola više AC/TV uređaja)

---

## 📝 Zaključak

Ova dokumentacija pokriva sve trenutno implementirane klase, metode i promenljive u projektu **Universal AC/TV Remote**. Projekat je u ranoj fazi razvoja sa solidnom osnovom za Bluetooth komunikaciju sa ESP32 mikrokontrolerom.

**Trenutno funkcionalne komponente:**
- ✅ Splash screen animacija
- ✅ Bluetooth konekcija sa ESP32
- ✅ Provera i zahtevanje dozvola
- ✅ Vizuelni status konekcije

**Sledeći koraci:**
1. Implementacija kontrolnih dugmića u `ControlActivity`
2. Slanje komandi preko `BluetoothControl.sendData()`
3. ESP32 parsiranje komandi i emitovanje IR signala
4. Testiranje sa realnim AC i TV uređajima

---

**Poslednji put ažurirano:** 11. Oktobar 2025  
**Verzija dokumentacije:** 1.0  
**Autor:** Universal AC/TV Remote Team

---
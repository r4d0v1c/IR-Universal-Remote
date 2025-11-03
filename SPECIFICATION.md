## SPECIFIKACIJA SISTEMA

### 1. Uvod

U savremenim domaćinstvima gotovo svaki uređaj (TV, klima, audio sistem, set-top box) dolazi sa sopstvenim daljinskim upravljačem. 
To dovodi do problema velike količine daljinskih upravljača koji se često zagube, oštete ili postanu nekompatibilni prilikom zamene uređaja. 
Korisnicima je potrebna praktična alternativa koja omogućava centralizovano i jednostavno upravljanje različitim uređajima.

Ovaj projekat ima za cilj da reši taj problem razvojem univerzalnog sistema za kontrolu TV i klima uređaja različitih proizvođača. 
Sistem kombinuje jednostavnu mobilnu aplikaciju i hardverski deo zasnovan na ESP32 mikrokontroleru koji generiše i šalje IR komande. 
Na taj način, korisnik može putem jedne aplikacije i jednog uređaja da kontroliše više različitih aparata u svom domu, bez potrebe za korišćenjem više daljinskih upravljača.
Komunikacija između aplikacije i ESP32 ostvaruje se putem Bluetooth veze, dok ESP32, koristeći biblioteku za IR komunikaciju, prevodi primljene komande u odgovarajuće IR signale i šalje ih ciljanom uređaju. 
Pored softverskog interfejsa, sistem obuhvata i fizičke tastere povezane na ESP32 koji omogućavaju nezavisno izvršavanje osnovnih funkcija, poput uključivanja i isključivanja uređaja ili podešavanja jačine zvuka i temperature.

---

### 2. Ciljevi

Glavni cilj ovog projekta je razvoj univerzalnog daljinskog sistema koji će omogućiti korisnicima da putem jedinstvene mobilne aplikacije i ESP32 mikrokontrolera upravljaju televizorima i klima uređajima različitih proizvođača.

#### Primarni ciljevi

Implementacija mobilne aplikacije koja omogućava:

- povezivanje sa ESP32 putem Bluetooth-a,

- izbor tipa uređaja (TV/klima),

- izbor proizvođača i odgovarajućih komandi.

Integracija ESP32 mikrokontrolera sa IR LED predajnikom radi slanja tačnih IR komandi uređajima.

- Upotreba postojeće IR biblioteke (npr. IRremoteESP8266) za rad sa bazom komandi različitih proizvođača.

#### Sekundarni ciljevi

- Omogućavanje osnovne kontrole pomoću fizičkih tastera povezanih na ESP32 (ON/OFF, promene kanala, regulacija jačine zvuka i temperature).

- Modularan dizajn sistema kako bi se lako dodavali novi proizvođači i komande.

---

### 3. Arhitektura sistema
Sistem se sastoji iz tri glavne komponente:

- Mobilna aplikacija – omogućava korisniku da preko jednostavnog interfejsa izabere uređaj (TV/klima), proizvođača i željenu komandu.

- ESP32 mikrokontroler – služi kao posrednik, prima komande preko Bluetooth veze, emituje odgovarajući IR signal ka uređaju i šalju potvrdu o izvšrenoj radnji.

- Kućni uređaj (TV/klima) – prima IR signal kao da dolazi sa originalnog daljinskog upravljača.

#### Dijagram arhitekture
```
+---------------------+        Bluetooth       +--------------------+        IR signal       +--------------------+
|  Mobilna aplikacija | <--------------------> |       ESP32        | ---------------------> |  TV / Klima uređaj |
| - Izbor uređaja     |                        | - Parsiranje       |                        | - Prima komande    |
| - Izbor komande     |                        | - IR emiter modul  |                        |   (ON/OFF, temp.)  | 
+---------------------+                        +-------------------+                         +--------------------+
```

---

### 4. Hardverska specifikiacija
- Naknadno


---

### 5. Softverska specifikacija
#### 5.1 ESP32 Firmware

ESP32 mikrokontroler služi kao posrednik između mobilne aplikacije i IR uređaja. 
Firmware implementira sledeće funkcionalnosti:

- Korišćena biblioteka:

  - IRremoteESP8266 – omogućava generisanje i slanje IR kodova prema TV i klima uređajima različitih proizvođača.

- Bluetooth server:

  - ESP32 prima komande od mobilne aplikacije koristeći Bluetooth SPP (Serial Port Profile) ili BLE (Bluetooth Low Energy).

  - Svaka primljena komanda se parsira i obrađuje.

- Mapiranje komandi:

  - String komande sa aplikacije (npr. TV_SAMSUNG_VOL+) se mapira na odgovarajući IR kod iz interne baze podataka.

  - Omogućava jednostavno dodavanje novih proizvođača i funkcija.

- Slanje IR signala:

  - ESP32 emituje IR signal putem IR LED diode

  - Signal odgovara originalnim daljinskim komandama ciljanog uređaja.

  - Salje ACK (potvrdu prijema) mobilnoj aplikaciji o izvršenoj radnji.

- Tasteri na ESP32:
  
  - Povezani tasteri omogućavaju osnovne funkcije nezavisno od aplikacije (ON/OFF, Volume +/-, Temperature +/-, CH +/-).

#### 5.2 Android aplikacija

Mobilna aplikacija služi za korisnički interfejs i upravljanje uređajima. Njene funkcionalnosti uključuju:

- Povezivanje sa ESP32:

  - Uspostavlja Bluetooth vezu sa odabranim ESP32 uređajem.

  - Prikazuje status veze i omogućava ponovno uparivanje ako je potrebno.

- Izbor uređaja i proizvođača:

  - Korisnik bira tip uređaja (TV ili klima) i proizvođača iz padajuće liste.

  - Omogućava lak pristup komandi specifičnim za taj uređaj.

- Meni komandi:

  - Prikazuje dugmad za osnovne funkcije (Power, Volume, Channel, Temperature).

  - Prilikom pritiska dugmeta, šalje odgovarajuću komandu ESP32-u u definisanom formatu (npr. AC_LG_TEMP+).

- Alat za izradu:

  - Android Studio za profesionalniji GUI i dodatne funkcionalnosti.
 
---

### 6. Komunikacioni protokol

- Poruke su tekstualnog formata (UTF-8), sa jasno definisanim delimiterima.
- Svaka poruka počinje i završava se sa #

#### Format komande (App -> ESP32)
```
#<DEVICE>|<BRAND>|<COMMAND>#
```
Polja:
- DEVICE  - tip uređaja [TV, AC]
- BRAND   - proizvođač (SAMSUNG, LG,...)
- COMMAND - akcija [ON, OFF, VOL+, VOL-, CH+, CH-, TEMP-, TEMP+]

Primeri:
- #TV|SAMSUNG|VOL+#
- #AC|DAIKIN|TEMP-#

#### Format komande (ESP32 -> App)
```
#<STATUS>|<INFO>#
```
- STATUS - [OK, ERR, ACK]
- INFO   - dodatne informacije (razlog greške ili potvrda komande)

Primeri:
- #ACK|TV|VOL+# - ESP32 primio poruku
- #OK|TV|VOL+#  - ESP32 izvršio komandu
- #ERR|CODE_ERROR#
  - 101 - INVALID_FORMAT
  - 102 - UKNOWN_DEVICE
  - 103 - UNKNOWN_BRAND
  - 104 - UNKNOWN_COMMAND
  - 105 - MISSING_PARAMETER
  - 106 - EXECUTION_FAILED
  - 107 - TIMEOUT
  - 108 - NOT_SUPPORTED
 
---

### 7. Baza IR komandi

#### Organizacija podataka

Baza je organizovana hijerarhijski:
```
Proizvođač -> Uređaj -> Komanda -> IR kod
```
- Proizvođač (SAMSUNG, LG, Sony,...)
- Uređaj (TV, AC)
- Komanda (VOL+, VOL-,...)
- IR kod - hex string ili binarni kod koji ESP32 šalje IR predajnikom

#### Način čuvanja

Pošto baza ne mora biti ogromna, najlakše je da bude ugrađena u firmware ESP32 (kao tabela ili JSON fajl u SPIFFS/LittleFS)

#### Pristup bazi

- Kada ESP32 primi poruku od aplikacije on:
  
  1. Parsira poruku - izdvoji uređaj, proizvođača i komandu
  2. Pogleda da li postoji u bazi
  3. Ako postoji - šalje IR kod
  4. Ako ne postoji - vraća grešku u skladu sa protokolom

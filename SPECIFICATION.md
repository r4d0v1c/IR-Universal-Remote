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

#### 3. Arhitektura sistema
Sistem se sastoji iz tri glavne komponente:

- Mobilna aplikacija – omogućava korisniku da preko jednostavnog interfejsa izabere uređaj (TV/klima), proizvođača i željenu komandu.

- ESP32 mikrokontroler – služi kao posrednik, prima komande preko Bluetooth veze, emituje odgovarajući IR signal ka uređaju i šalju potvrdu o izvšrenoj radnji.

- Kućni uređaj (TV/klima) – prima IR signal kao da dolazi sa originalnog daljinskog upravljača.

#### Dijagram arhitekture
+---------------------+        Bluetooth       +--------------------+        IR signal       +--------------------+
|  Mobilna aplikacija | <--------------------> |       ESP32        | ---------------------> |  TV / Klima uređaj |
| - Izbor uređaja     |                        | - Parsiranje       |                        | - Prima komande    |
| - Izbor komande     |                        | - IR emiter modul  |                        |   (ON/OFF, temp.)  | 
+---------------------+                        +-------------------+                         +--------------------+


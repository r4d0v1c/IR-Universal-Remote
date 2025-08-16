# IR-Universal-Remote
Univerzalni sistem za kontrolu TV i klima uređaja različitih proizvođača

Cilj projekta je razviti univerzalni sistem koji korisnicima omogućava kontrolu televizora i klima uređaja različitih proizvođača pomoću mobilne aplikacije i ESP32 mikrokontrolera. 
Sistem objedinjuje praktičnost softverskog interfejsa i fleksibilnost hardverskog dela, nudeći jednostavno i centralizovano rešenje za svakodnevno upravljanje kućnim uređajima.

Glavne čvorove komunikacije čine mobilna aplikacija i mikrokontroler ESP32.
Komunikacija između čvorova je ostvarena putem Bluetooth komunikacije.
Komunikacija započinje tako što se na korisničkoj aplikaciji korišćenjem Bluetooth tehnologije traži specijalan uređaj za ostvarivanje komunikacije, konkretno ESP32 određenog tipa.
Na ESP32 se integriše neka od biblioteka (na primer IRremoteESP8266) koja će sadržati komande za razne TV/klima uređaje koje je potrebno mapirati na ispravan način.
Na strani Android aplikacije će se slati odgovarajuća komanda ka ESP32 uredjaju koja određuje narednu akciju koju je potrebno izvršiti.

Na ESP32 mikrokontroler će biti povezani neki od periferijskih uređaja poput tastera, IR senzora, baterije kako bi se omogoćio nezavisni rad od aplikacije u određenim slučajevima.

Detaljniji komunikacioni protokol i hardversko povezivanje bice navedeno u SPECIFICATION.md

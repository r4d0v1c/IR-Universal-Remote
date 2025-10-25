#include <Arduino.h>
#include <IRremoteESP8266.h>
#include <IRac.h>
#include <IRsend.h>
#include <SPI.h>
#include <Adafruit_ST7789.h>
#include <Adafruit_GFX.h>
#include "BluetoothSerial.h"

// pin numbers (note - different from schematic)
#define DPAD_LEFT 33
#define DPAD_UP 25
#define DPAD_DOWN 26
#define DPAD_RIGHT 27
#define A_BUTTON 14 // power button
#define B_BUTTON 12 // function select button (eg. timer)
#define TEMP_SENSOR 35
#define IR_LED 19

#define TFT_CS    15
#define TFT_DC    2
#define TFT_RST   4
#define TFT_BLK   32  // backlight pin

#define DEBOUNCE_MS 100

IRac ac(IR_LED);
// state object to hold current AC settings
stdAc::state_t ir_state{};

// debouncing
unsigned long lastPressTime = 0;

bool screen_refresh = 1;
// Bluetooth configuration gate - remote inputs disabled until configured
bool bt_configured = false;


Adafruit_ST7789 tft = Adafruit_ST7789(TFT_CS, TFT_DC, TFT_RST);

BluetoothSerial SerialBT;
String inputBuffer = "";
void processCommand(String);

uint8_t set_temperature = 24;
bool power_state = false;	// track on/off state
byte button_enable = 0;

void setup() {
  pinMode(DPAD_LEFT, INPUT_PULLUP);
  pinMode(DPAD_UP, INPUT_PULLUP);
  pinMode(DPAD_DOWN, INPUT_PULLUP);
  pinMode(DPAD_RIGHT, INPUT_PULLUP);
  pinMode(A_BUTTON, INPUT_PULLUP);
  pinMode(B_BUTTON, INPUT_PULLUP);
  pinMode(TEMP_SENSOR, INPUT);
  pinMode(IR_LED, OUTPUT);

  Serial.begin(115200);
  Serial.println("Universal AC Remote Started");

  pinMode(TFT_BLK, OUTPUT);
  digitalWrite(TFT_BLK, HIGH); // turn on backlight

  // initialize SPI TFT (135x240 for 1.14" screen)
  tft.init(135, 240); 
  tft.setRotation(1);  // adjust if screen orientation is wrong

  tft.fillScreen(ST77XX_BLACK);
  tft.setTextColor(ST77XX_WHITE);
  tft.setTextSize(2);

  tft.print("Waiting for BT\nconnection...");
  
  SerialBT.begin("ESP32"); // ESP32 in slave mode, allows phone connections
  Serial.println("Bluetooth started, waiting for client/config...");
  // Do not block here. We'll accept a client and configuration in the loop.

  // TODO investigate
  ir_state.model = 1; // often just 1, but some protocols need a model number



  // initialize the state with sensible defaults
  ir_state.power = false;    // Start off
  ir_state.mode = stdAc::opmode_t::kCool;
  ir_state.celsius = true;
  ir_state.degrees = set_temperature;
  ir_state.fanspeed = stdAc::fanspeed_t::kMax;
  ir_state.swingv = stdAc::swingv_t::kOff;
  ir_state.swingh = stdAc::swingh_t::kOff;
  
  tft.fillScreen(ST77XX_BLACK);
}


void handle_inputs(){
  unsigned long now = millis();

  if (button_enable) {
    // Now check for individual button presses
    if (digitalRead(DPAD_LEFT) == LOW && now - lastPressTime > DEBOUNCE_MS) {
      lastPressTime = millis(); // reset debounce timer
      button_enable = 0;

      Serial.println("MODE pressed");

      // Cycle through the main operating modes
      switch (ir_state.mode) {
        case stdAc::opmode_t::kAuto:
          ir_state.mode = stdAc::opmode_t::kCool;
          break;
        case stdAc::opmode_t::kCool:
          ir_state.mode = stdAc::opmode_t::kHeat;
          break;
        case stdAc::opmode_t::kHeat:
          ir_state.mode = stdAc::opmode_t::kDry;
          break;
        case stdAc::opmode_t::kDry:
          ir_state.mode = stdAc::opmode_t::kFan;
          break;
        case stdAc::opmode_t::kFan:
          ir_state.mode = stdAc::opmode_t::kAuto;
          break;
        default:
          ir_state.mode = stdAc::opmode_t::kAuto;
          break;
      }
      // Send the updated state
      ac.sendAc(ir_state);
    } 
    else if (digitalRead(DPAD_UP) == LOW && now - lastPressTime > DEBOUNCE_MS) {
      lastPressTime = millis();
      button_enable = 0;

      Serial.println("TEMP UP pressed");
      if (ir_state.degrees < 30) { // set a reasonable max temp
        ir_state.degrees++;
        set_temperature = ir_state.degrees; // sync our variable
        ac.sendAc(ir_state);
      }
    } 
    else if (digitalRead(DPAD_DOWN) == LOW && now - lastPressTime > DEBOUNCE_MS) {
      lastPressTime = millis();
      button_enable = 0;

      Serial.println("TEMP DOWN pressed");
      if (ir_state.degrees > 16) { // set a reasonable min temp
        ir_state.degrees--;
        set_temperature = ir_state.degrees; // sync our variable
        ac.sendAc(ir_state);
      }
    } 
    else if (digitalRead(DPAD_RIGHT) == LOW && now - lastPressTime > DEBOUNCE_MS) {
      lastPressTime = millis();
      button_enable = 0;

      Serial.println("FAN pressed");

      // cycle through common fan speeds
      switch (ir_state.fanspeed) {
        case stdAc::fanspeed_t::kAuto:
          ir_state.fanspeed = stdAc::fanspeed_t::kMin;
          break;
        case stdAc::fanspeed_t::kMin:
          ir_state.fanspeed = stdAc::fanspeed_t::kMedium;
          break;
        case stdAc::fanspeed_t::kMedium:
          ir_state.fanspeed = stdAc::fanspeed_t::kHigh;
          break;
        case stdAc::fanspeed_t::kHigh:
          ir_state.fanspeed = stdAc::fanspeed_t::kMax;
          break;
        case stdAc::fanspeed_t::kMax:
          ir_state.fanspeed = stdAc::fanspeed_t::kAuto;
          break;
        default:
          ir_state.fanspeed = stdAc::fanspeed_t::kAuto;
          break;
      }
      ac.sendAc(ir_state);
    } 
    else if (digitalRead(A_BUTTON) == LOW && now - lastPressTime > DEBOUNCE_MS) {
      lastPressTime = millis();
      button_enable = 0;

      Serial.println("POWER pressed");
      
      // toggle the power state
      ir_state.power = !ir_state.power;
      power_state = ir_state.power; // sync our variable
      
      // if we're turning on, ensure a sensible default state is set.
      // this is helpful if the internal state gets out of sync.
      if (ir_state.power) {
        ir_state.mode = stdAc::opmode_t::kCool;
        ir_state.degrees = set_temperature;
        ir_state.fanspeed = stdAc::fanspeed_t::kAuto;
      }
      ac.sendAc(ir_state);
    } 
    else if (digitalRead(B_BUTTON) == LOW && now - lastPressTime > DEBOUNCE_MS) {
      lastPressTime = millis();
      button_enable = 0;

      // --TODO-- implement function displaying/choosing/applying logic
      Serial.println("B (FUNCTION) pressed");
    }
  } else if (digitalRead(DPAD_LEFT) &&
           digitalRead(DPAD_UP) &&
           digitalRead(DPAD_DOWN) &&
           digitalRead(DPAD_RIGHT) &&
           digitalRead(A_BUTTON) &&
           digitalRead(B_BUTTON)) {
    button_enable = 1;
  }
}

bool check_screen_state(){

    static uint8_t previous_temp = -1;
    static stdAc::opmode_t previous_mode;
    static stdAc::fanspeed_t previous_fanspeed;
    static bool previous_power = false;
    static int previous_curr_temp = 0;
    static long lastChecked = 0;

    if(previous_power != ir_state.power)
    {
      previous_power = ir_state.power;
      return true;
    }

    if(previous_temp != ir_state.degrees)
    {
      previous_temp = ir_state.degrees;
      return true;
    }

    if(previous_mode != ir_state.mode)
    {
      previous_mode = ir_state.mode;
      return true;
    }

    if(previous_fanspeed != ir_state.fanspeed)
    {
      previous_fanspeed = ir_state.fanspeed;
      return true;
    }
    
    if(millis() - lastChecked > 5000)
    {
      lastChecked = millis();
      return true;
    }

    return false;
}

String modeToString(stdAc::opmode_t mode){
  switch(mode){
    case stdAc::opmode_t::kOff:
      return kOffStr;
      break;
    case stdAc::opmode_t::kCool:
      return kCoolStr;
      break;
    case stdAc::opmode_t::kHeat:
      return kHeatStr;
      break;
    case stdAc::opmode_t::kDry:
      return kDryStr;
      break;
    case stdAc::opmode_t::kFan:
      return kFanStr;
      break;
    default:
      return kAutoStr;
      break;
  }

}

String fanToString(stdAc::fanspeed_t fan){

  switch(fan){
    case stdAc::fanspeed_t::kMin:
      return kMinStr;
      break;
    case stdAc::fanspeed_t::kLow:
      return kLowStr;
      break;
    case stdAc::fanspeed_t::kMedium:
      return kMedStr;
      break;
    case stdAc::fanspeed_t::kHigh:
      return kHighStr;
      break;
    case stdAc::fanspeed_t::kMax:
      return kMaxStr;
      break;
    default:
      return kAutoStr;
      break;
  }

}

float readCurrentTemp() {
  int raw = analogRead(TEMP_SENSOR);
  float voltage = (raw / 4095.0) * 3.3;  // Convert ADC reading to voltage (V)
  float temperatureC = voltage * 100.0;  // 10 mV per °C → multiply by 100
  return temperatureC;
}

void refresh_screen(){
  // clear screen
tft.fillScreen(ST77XX_BLACK);

// --- MODE (Top-left) ---
tft.setCursor(10, 10);
tft.print("MODE");
tft.setCursor(10, 50);
tft.print(modeToString(ir_state.mode));

// --- FAN (Top-right) ---
tft.setCursor(240 - 60, 10);  // about 160 px from left
tft.print("FAN");
tft.setCursor(240 - 60, 50);
tft.print(fanToString(ir_state.fanspeed));

// --- TGT (Center) ---
tft.setCursor(100, 10);
tft.print("TGT");
tft.setCursor(105, 30);
tft.print((int)ir_state.degrees);

// --- CURR (Bottom-center) ---
tft.setCursor(95, 105);
tft.print("CURR");
tft.setCursor(105, 120);
tft.print(readCurrentTemp());

if(ir_state.power){
  tft.setCursor(10, 105);
  tft.print("ON");
}else{
  tft.setCursor(10, 105);
  tft.print("OFF");
}

}

void processCommand(String command) {
  Serial.println("Primljena komanda: " + command);

  // Proveri da li je prazna komanda
  if (command.length() == 0) {
    Serial.println("Greska: Prazna komanda");
    return;
  }

  int first = command.indexOf('|');
  int second = command.indexOf('|', first + 1);

  if (first > 0 && second > first) {
    String device = command.substring(0, first);
    String model = command.substring(first + 1, second);
    String action = command.substring(second + 1);

    Serial.println("=== PARSIRANO ===");
    Serial.println("Device: " + device);
    Serial.println("Model: " + model);
    Serial.println("Action: " + action);
    Serial.println("=================");

    int modelNumber = model.toInt();

    ir_state.protocol = (decode_type_t)modelNumber;


  } else {
    Serial.println("Greska: Neispravan format komande");
  }
}


// Read incoming bluetooth bytes non-blocking, process when '#' terminator received
void handleBluetooth() {
  // If no client connected, nothing to do
  if (!SerialBT.hasClient()) return;

  while (SerialBT.available()) {
    char c = (char)SerialBT.read();
    if (c == '#') {
      if (inputBuffer.length() > 0) {
        processCommand(inputBuffer);
        Serial.println("Poslato nazad");
        SerialBT.print("OK#");
        // Mark config as received so remote controls are enabled
        bt_configured = true;
        inputBuffer = "";
      }
    } else {
      inputBuffer += c;
      // keep buffer reasonably bounded
      if (inputBuffer.length() > 256) inputBuffer = inputBuffer.substring(inputBuffer.length() - 256);
    }
  }
}

void loop() {
  // process incoming bluetooth config/commands
  // This will set bt_configured = true when a valid configuration command arrives
  // and only then will physical inputs be processed.
  handleBluetooth();

  // Only process physical buttons when bluetooth configuration is complete
  if (bt_configured) {
    handle_inputs();
  }

  if(check_screen_state())
    refresh_screen();
  
}

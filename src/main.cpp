#include <Arduino.h>
#include <IRremoteESP8266.h>
#include <IRac.h>
#include <IRsend.h>

// pin numbers (note - different from schematic)
#define DPAD_LEFT 33
#define DPAD_UP 25
#define DPAD_DOWN 26
#define DPAD_RIGHT 27
#define A_BUTTON 14 // power button
#define B_BUTTON 12 // function select button (eg. timer)
#define TEMP_SENSOR 35
#define IR_LED 19

#define DEBOUNCE_MS 50

IRac ac(IR_LED);
// state object to hold current AC settings
stdAc::state_t ir_state{};

// debouncing
unsigned long lastPressTime = 0;


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

  // 	IMPORTANT
  // this is where the BLE make/model recieving will happen

  ir_state.protocol = decode_type_t::COOLIX;
  // TODO investigate
  ir_state.model = 1; // often just 1, but some protocols need a model number


  // initialize the state with sensible defaults
  ir_state.power = false;    // Start off
  ir_state.mode = stdAc::opmode_t::kCool;
  ir_state.celsius = true;
  ir_state.degrees = set_temperature;
  ir_state.fanspeed = stdAc::fanspeed_t::kAuto;
  ir_state.swingv = stdAc::swingv_t::kOff;
  ir_state.swingh = stdAc::swingh_t::kOff;
}

void loop() {
  unsigned long now = millis();

  if (button_enable) {
    // Now check for individual button presses
    if (digitalRead(DPAD_LEFT) == LOW && now - lastPressTime > DEBOUNCE_MS) {
      lastPressTime = millis(); // reset debounce timer
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
      Serial.println("TEMP UP pressed");
      if (ir_state.degrees < 30) { // Set a reasonable max temp
        ir_state.degrees++;
        set_temperature = ir_state.degrees; // Sync our variable
        ac.sendAc(ir_state);
      }
    } 
    else if (digitalRead(DPAD_DOWN) == LOW && now - lastPressTime > DEBOUNCE_MS) {
      lastPressTime = millis();
      Serial.println("TEMP DOWN pressed");
      if (ir_state.degrees > 16) { // Set a reasonable min temp
        ir_state.degrees--;
        set_temperature = ir_state.degrees; // Sync our variable
        ac.sendAc(ir_state);
      }
    } 
    else if (digitalRead(DPAD_RIGHT) == LOW && now - lastPressTime > DEBOUNCE_MS) {
      lastPressTime = millis();
      Serial.println("FAN pressed");

      // Cycle through common fan speeds
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
      Serial.println("POWER pressed");
      
      // Toggle the power state
      ir_state.power = !ir_state.power;
      power_state = ir_state.power; // Sync our variable
      
      // If we're turning on, ensure a sensible default state is set.
      // This is helpful if the internal state gets out of sync.
      if (ir_state.power) {
        ir_state.mode = stdAc::opmode_t::kCool;
        ir_state.degrees = set_temperature;
        ir_state.fanspeed = stdAc::fanspeed_t::kAuto;
      }
      ac.sendAc(ir_state);
    } 
    else if (digitalRead(B_BUTTON) == LOW && now - lastPressTime > DEBOUNCE_MS) {
      lastPressTime = millis();
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
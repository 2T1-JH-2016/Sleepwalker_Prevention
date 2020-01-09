#include <SoftwareSerial.h>
#include "SNIPE.h"
#include <TinyGPS.h>
#include <Wire.h>
 

#define ATSerial Serial

#define RXPIN 6
#define TXPIN 5
#define TXpin 12
#define RXpin 11

#define GPSBAUD 9600

//16byte hex key
String lora_app_key = "11 22 33 44 55 66 77 88 99 aa bb cc dd ee ff 00";  

TinyGPS gps;
SoftwareSerial uart_gps(RXPIN, TXPIN);
SoftwareSerial DebugSerial(RXpin, TXpin);
SNIPE SNIPE(ATSerial);

void setup() {
  ATSerial.begin(115200);

  // put your setup code here, to run once:
  while (ATSerial.read() >= 0) {}
  while (!ATSerial);
  DebugSerial.begin(115200);
  uart_gps.begin(9600);
  Wire.begin();

/* SNIPE LoRa Initialization */
  if (!SNIPE.lora_init()) {
    DebugSerial.println("SNIPE LoRa Initialization Fail!");
    while (1);
  }

  /* SNIPE LoRa Set Appkey */
  if (!SNIPE.lora_setAppKey(lora_app_key)) {
    DebugSerial.println("SNIPE LoRa app key value has not been changed");
  }
  
  /* SNIPE LoRa Set Frequency */
  if (!SNIPE.lora_setFreq(LORA_CH_1)) {
    DebugSerial.println("SNIPE LoRa Frequency value has not been changed");
  }

  /* SNIPE LoRa Set Spreading Factor */
  if (!SNIPE.lora_setSf(LORA_SF_12)) {
    DebugSerial.println("SNIPE LoRa Sf value has not been changed");
  }
  if (!SNIPE.lora_setTxp(9)) {
    DebugSerial.println("SNIPE LoRa Sf value has not been changed");
  }
  /* SNIPE LoRa Set Rx Timeout 
   * If you select LORA_SF_12, 
   * RX Timout use a value greater than 5000  
  */ 
}

void loop() {
    String rate_data;
    int avg = 0;
    for(int i = 0; i<10;i++){
       Wire.requestFrom(0xA0 >> 1, 1);
       while (Wire.available()>0) {
            int c = Wire.read();
            Serial.println(c, DEC);
            avg += c;
       }
       delay(6000);
    }
    avg = avg/10;
    rate_data = "bt="+(String)avg; 
    
    for (unsigned long start = millis(); millis() - start < 1000;)
    {
      while (uart_gps.available())
      {
        char c = uart_gps.read();
        // Serial.write(c); // uncomment this line if you want to see the GPS data flowing
        if (gps.encode(c)) // Did a new valid sentence come in?
          rate_data += getgps(gps);
      }
    }
    if(rate_data.indexOf("&lt=")==-1){
      rate_data+="&lt=NULL&lo=NULL";
    }
    if (SNIPE.lora_send(rate_data)){
      Serial.println(rate_data);
    }else{
      Serial.println("send fail");
    }
        
}
// The getgps function will get and print the values we want.
String getgps(TinyGPS &gps)
{
    float latitude, longitude;
    gps.f_get_position(&latitude, &longitude);
    
    // Here you can print statistics on the sentences.
    unsigned long chars;
    unsigned short sentences, failed_checksum;
    gps.stats(&chars, &sentences, &failed_checksum);
  
    delay(300);
    String temp = String(latitude, 5).c_str();
    String gps_data = "&lt="+ temp;
    gps_data += "&lo=";
    temp = String(longitude, 5).c_str();
    gps_data += temp;
  
    if (chars == 0)
      DebugSerial.println("** No characters received from GPS: check wiring **");
        
    return gps_data;
}

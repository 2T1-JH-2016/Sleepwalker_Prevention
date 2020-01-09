//pong
#include <SoftwareSerial.h>
#include "SNIPE.h"
#include <ESP8266WiFi.h>

#ifndef STASSID
#define STASSID "와이파이 아이디"
#define STAPSK  "와이파이 비밀번호"
#endif

const char* ssid     = STASSID;
const char* password = STAPSK;

const char* host = "ip 주소";
const uint16_t port = 80;

#define TXpin 11
#define RXpin 10
#define ATSerial Serial

//16byte hex key
String lora_app_key = "11 22 33 44 55 66 77 88 99 aa bb cc dd ee ff 00";  


SoftwareSerial DebugSerial(RXpin,TXpin);
SNIPE SNIPE(ATSerial);

void setup() {

  Serial.begin(115200);
  ATSerial.begin(115200);

  // We start by connecting to a WiFi network

  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());


  // put your setup code here, to run once:
  while(ATSerial.read()>= 0) {}
  while(!ATSerial);

  DebugSerial.begin(115200);

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

  /* SNIPE LoRa Set Rx Timeout 
   * If you select LORA_SF_12, 
   * RX Timout use a value greater than 5000  
  */
  if (!SNIPE.lora_setRxtout(12000)) {
    DebugSerial.println("SNIPE LoRa Rx Timout value has not been changed");
  }    
}

void loop() {
    String ver = "GET /insert_data3.php?";
    String key = lora_app_key;
    ver += SNIPE.lora_recv();
    WiFiClient client;
    if (ver != "GET /insert_data3.php?AT_RX_TIMEOUT" && ver != "GET /insert_data3.php?AT_RX_ERROR" )
    {
          DebugSerial.println("recv success");
          DebugSerial.println(SNIPE.lora_getRssi());
          DebugSerial.println(SNIPE.lora_getSnr());
          ver += "&appkey=";        
          key.replace(" ","+");
          ver += key;
          if (client.connect(host, port)){
                client.println(ver);
                client.println();
          }else{
              Serial.println("recv fail");
              delay(500);
          }
    }
}

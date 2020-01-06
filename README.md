# 몽유병 환자를 위한 위험 방지 시스템
Danger Prevention System for Sleepwalker



## 1. 소개  
본 연구는 몽유병 환자가 처할 수 있는 위험을 방지하는 시스템 제작을 목표로 함  
해당 시스템의 시나리오는 다음과 같음  
* 사용자는 수면 전 심박센서, GPS(Global Positioning System)모듈, LoRa(Long Range)모듈이 내장된 손목 밴드형 웨어러블 기기를 착용  
* 웨어러블 기기를 통해 사용자의 심박 수와 위치를 실시간으로 측정  
* 측정된 사용자의 심박 수와 위치를 데이터베이스에 저장   
* 몽유병 증세로 인해, 사용자의 심박 수가 안전범위를 벗어나고 수면 상태에 들어간 위치에서 일정 거리 이상 멀어지면 사용자의 보호자에게 알람 형태의 알림을 전송  



## 2. 작품 구성도  

<img src="https://user-images.githubusercontent.com/52437364/71821197-d1d7c400-30d4-11ea-8c88-e8bcde1b7013.png"></img>

* 웨어러블 밴드(송신부)는 심박센서, GPS 모듈, LoRa 모듈로 구성됨
* 수신부는 LoRa 모듈, WiFi 모듈로 구성됨
* 송신부는 사용자의 심박 수와 위치를 측정하여 송신부의 LoRa로 데이터를 전송
* 수신부에서 서버로 데이터를 전송해 DB에 저장
* 사용자의 실시간 심박 수와 위치를 가져와 어플리케이션에서 확인
* 심박 수와 위치가 안정범위를 벗어났을 때 보호자의 단말기로 알림 전송



## 3. 하드웨어 [Arduino]
* 송신부 하드웨어:![하드웨어](https://user-images.githubusercontent.com/52437364/71821984-00ef3500-30d7-11ea-9124-59419d93f902.jpg){:.alignleft}
* 수신부 하드웨어:![image](https://user-images.githubusercontent.com/52437364/71822039-2e3be300-30d7-11ea-977f-18135fcbfc03.png)
* 송신부 하드웨어 착용 모습:![image](https://user-images.githubusercontent.com/52437364/71822070-3dbb2c00-30d7-11ea-8c56-401ffb714a56.png)

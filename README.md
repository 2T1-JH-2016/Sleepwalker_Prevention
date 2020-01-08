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
* 다음은 송신부, 수신부 하드웨어와 송신부 착용 모습임

![하드웨어](https://user-images.githubusercontent.com/52437364/71821984-00ef3500-30d7-11ea-9124-59419d93f902.jpg)
![image](https://user-images.githubusercontent.com/52437364/71822039-2e3be300-30d7-11ea-977f-18135fcbfc03.png)
![image](https://user-images.githubusercontent.com/52437364/71822070-3dbb2c00-30d7-11ea-8c56-401ffb714a56.png)
* 하드웨어는 송신부와 수신부로 나누어 구현
* 송신부는 사용자가 착용하는 웨어러블 기기로 ①심박센서, ②GPS모듈, ③Arduinouno, ④LoRa 통신모듈로 구성
* 충전식 배터리를 연결해 휴대 가능하게 함
* 수신부는 LoRa 통신 모듈과 WiFi 기능을 탑재한 아두이노 우노 호환보드인 WeMos D1보드로 구성
* 수신부는 사용자의 침실에 위치
* 사용자가 [그림 3]의 송신부를 착용하면 심박센서로 심박 수를, GPS 모듈로 현재위치를 측정하고 LoRa 통신 모듈을 통해 측정한 값을 수신부로 전달
* 심박 수는 10번 측정된 값의 평균을 계산해 수신부로 전송
* 하드웨어가 사용하는 DB를 테이블은 [그림 6]과 같음
* beat(심박), lati(위도), longi(경도), 사용자 확인을 위한 app-key, 시간, 보호자 전화번호로 구성



## 4. 소프트웨어 [Android]
![image](https://user-images.githubusercontent.com/52437364/71872858-fb3e3180-3160-11ea-8c28-d5285c3904c4.png)
* 어플리케이션의 아이콘은 위과 같음
* 몽유병 증상이 언제 나타날지 몰라 상시 긴장상태인 환자와 보호자의 편한 수면을 돕기 위한 어플리케이션이라는 의미를 가짐  
![image](https://user-images.githubusercontent.com/52437364/71874915-6fc79f00-3166-11ea-84c0-4c242ec7ec27.png)
![image](https://user-images.githubusercontent.com/52437364/71874921-72c28f80-3166-11ea-90eb-8a3567cce851.png)
* app-key는 송신부와 수신부의 일대일 통신을 위한 고유번호임
* app-key는 하드웨어마다 다르게 주어짐
* [그림 10]은 회원가입 창에서 사용자가 editText를 클릭하면 DB에서 가져온 값이 자동으로 입력되는 것을 보여줌
* 환자가 위험상태일 경우 연락 받을 보호자의 전화번호를 입력



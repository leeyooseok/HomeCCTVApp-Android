#include <Servo.h>

Servo servoX; // X축 서보 모터
Servo servoY; // Y축 서보 모터
int LEDpin_1 = 11, LEDpin_2 = 12, LEDpin_3 = 15, LEDpin_4 = 16;

void setup() {
  servoX.attach(9); // X축 서보 모터 핀 연결
  servoY.attach(10); // Y축 서보 모터 핀 연결

  Serial.begin(9600); // 시리얼 통신 시작

  servoX.write(70);
  servoY.write(90);
  pinMode(LEDpin_1, OUTPUT);
  pinMode(LEDpin_2, OUTPUT);
  pinMode(LEDpin_3, OUTPUT);
  pinMode(LEDpin_4, OUTPUT);
  digitalWrite(LEDpin_1, LOW); // LED1를 끈 상태로 초기화
  digitalWrite(LEDpin_2, LOW); // LED2를 끈 상태로 초기화
  digitalWrite(LEDpin_3, LOW); // LED3를 끈 상태로 초기화
  digitalWrite(LEDpin_4, LOW); // LED4를 끈 상태로 초기화
  
}

void loop() {
  if (Serial.available() > 0) {
    char command = Serial.read();
    Serial.println(command);
    switch(command) {
      case 'U':
        servoY.write(servoY.read() - 10); // 위로 이동
        break;
      case 'D':
        servoY.write(servoY.read() + 10); // 아래로 이동
        break;
      case 'L':
        servoX.write(servoX.read() + 10); // 왼쪽으로 이동
        break;
      case 'R':
        servoX.write(servoX.read() - 10); // 오른쪽으로 이동
        break;
      case 'a': // LED1을 켠다
        digitalWrite(LEDpin_1, HIGH);
        break;
      case 'b': // LED1을 끔
        digitalWrite(LEDpin_1, LOW);
        break;
      case 'c': // LED2을 켠다
        digitalWrite(LEDpin_2, HIGH);
        break;
      case 'd': // LED2를 끔
        digitalWrite(LEDpin_2, LOW);
        break;
      case 'e': // LED3를 켠다
        digitalWrite(LEDpin_3, HIGH);
        break;
      case 'f': // LED3를 끔
        digitalWrite(LEDpin_3, LOW);
        break;
      case 'g': // LED4를 켠다
        digitalWrite(LEDpin_4, HIGH);
        break;
      case 'h': // LED4를 끔
        digitalWrite(LEDpin_4, LOW);
        break;
      case 'i': // 전체 점등
        digitalWrite(LEDpin_1, HIGH);
        digitalWrite(LEDpin_2, HIGH);
        digitalWrite(LEDpin_3, HIGH);
        digitalWrite(LEDpin_4, HIGH);
        break;
      case 'j': // 전체 소등
        digitalWrite(LEDpin_1, LOW);
        digitalWrite(LEDpin_2, LOW);
        digitalWrite(LEDpin_3, LOW);
        digitalWrite(LEDpin_4, LOW);
        break;
    }
  }
}

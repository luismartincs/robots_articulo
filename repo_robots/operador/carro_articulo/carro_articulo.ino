
#include <SPI.h>
#include <Servo.h>
#include "nRF24L01.h"
#include "RF24.h"

#define DEL1 9
#define DEL2 10
#define TRA1 3
#define TRA2 4
#define CE_PIN 7
#define CS_PIN 8

Servo MyServo;
int vel_del = 255;
int vel_tra = 255;
const uint64_t pipe = 1;

RF24 radio(CE_PIN, CS_PIN);

void setup() {
  
  Serial.begin(9600);
  pinMode(DEL1,OUTPUT);
  pinMode(DEL2,OUTPUT);
  
  MyServo.attach(5);
  radio.begin();
  radio.openReadingPipe(1, pipe);
  radio.startListening();
}

void loop() {   
  
    byte buf[2];
    byte buflen = 2;

    if (radio.available()) // Non-blocking
    {
      
       radio.read( buf, 2 );

       char comando = buf[1];

       actuar(comando);
      
      Serial.println(comando);

        
    }else{
      detener();
    }
    
}


//PROCESAR COMANDOS

void actuar(char comando){

  switch(comando){
    case 'A':
      avanzar();
      delay(500);
      break;
    case 'I':
      izquierda();
      delay(400);
      break;
    case 'D':
      derecha();
      delay(400);
      break;
    case 'S':
      accion(170);
      break;
    case 'B':
      accion(0);
      break;
  }
}

void accion(int grados){
  MyServo.write(grados);
}

void izquierda(){
  digitalWrite(DEL1,HIGH);
  digitalWrite(DEL2,LOW);
  digitalWrite(TRA1,LOW);
  digitalWrite(TRA2,HIGH);
}

void derecha(){
  digitalWrite(DEL1,LOW);
  digitalWrite(DEL2,HIGH);
  digitalWrite(TRA1,HIGH);
  digitalWrite(TRA2,LOW);
}

void detener(){
  digitalWrite(DEL1,LOW);
  digitalWrite(DEL2,LOW);
  digitalWrite(TRA1,LOW);
  digitalWrite(TRA2,LOW);
}

void reversa(){
  digitalWrite(DEL1,LOW);
  digitalWrite(DEL2,HIGH);
  digitalWrite(TRA1,LOW);
  digitalWrite(TRA2,HIGH);
}

void avanzar(){
  digitalWrite(DEL1,HIGH);
  digitalWrite(DEL2,LOW);
  digitalWrite(TRA1,HIGH);
  digitalWrite(TRA2,LOW);
  //analogWrite(5,vel_tra); 
}

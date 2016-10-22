#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"        

#define ID L
#define CE_PIN 7
#define CS_PIN 8

char comandos[5] = {'A','R','I','D','S'};
char data[2];
 
const uint64_t pipe = 2;
RF24 radio(CE_PIN, CS_PIN);

int incomingByte = 0;

void setup() {
  Serial.begin(9600);
  radio.begin();
}

void loop() {
        
        if (Serial.available() > 0) {
                memset(data,0,2);
                incomingByte = Serial.readBytes(data,2);
                
                radio.openWritingPipe(data[0]);
                bool ok = radio.write(data,2);
        }
        
  
}

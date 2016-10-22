#include <Servo.h>

#define R1 11
#define R2 8
#define SR 12

#define L1 7
#define L2 6
#define SL 10

#define LED 2

#define SERVO 9

#define TRIGGER 5
#define ECHO 4

#define YES 1
#define NO 0 

#define FORWARD_DURATION 300
#define BACKWARD_DURATION 1000
#define LEFT_DURATION 1000
#define RIGHT_DURATION 1000
#define TURN_DURATION 500 


#define BUFFER_SIZE 32
#define INS_SIZE 202

int serialTask = 1;
int ledTask = 1000;

int forwardEnable = 0;
int backwardEnable = 0;
int leftEnable = 0;
int rightEnable = 0;


int forwardTask = 500;
int backwardTask = 500;
int leftTask = 500;
int rightTask = 500;

int ledStatus = 0;
int isReading = 1;

int incomingByte = 0; 
char currentCommand = 0;
char command[INS_SIZE];

long distance;
long time;
char orientation = 'N';

unsigned char writeBuffer[BUFFER_SIZE];
unsigned char visionMap = 0;

Servo myservo;


void setup() {
  
  Serial.begin(9600);
  pinMode(R1, OUTPUT);
  pinMode(R2, OUTPUT);
  pinMode(SR, OUTPUT);
  
  pinMode(L1, OUTPUT);
  pinMode(L2, OUTPUT);
  pinMode(SL, OUTPUT);
  
  pinMode(LED, OUTPUT);
  
  pinMode(TRIGGER, OUTPUT);
  pinMode(ECHO, INPUT); 
  
  myservo.attach(SERVO); 
  
  cleanBuffer();

}


int getDistance(){
  
  digitalWrite(TRIGGER,LOW); 
  delayMicroseconds(2);
  digitalWrite(TRIGGER, HIGH);
  delayMicroseconds(10);
  time=pulseIn(ECHO, HIGH); 
  distance= int(0.017*time); 
  return distance;
}

boolean isFree(int distance){
  if(distance <= 15){
    return false;
  }
  return true;
}

void fillBufferWithMap(){
  for(int i=0; i < BUFFER_SIZE; i++){
     writeBuffer[i] = visionMap; 
  }
}

void cleanBuffer(){
  for(int i=0; i < BUFFER_SIZE; i++){
     writeBuffer[i] = 0; 
  }
}

void sendBuffer(){
  //Serial.write(writeBuffer,BUFFER_SIZE);
  for(int i=0; i < BUFFER_SIZE; i++){
     Serial.write(writeBuffer[i]);
  }
}

void debugBuffer(){
  for(int i=0; i < BUFFER_SIZE; i++){
     Serial.println(writeBuffer[i]);
  }
}



void scan(){


    getDistance();
    
    myservo.write(0);
    delay(1000);
    distance = getDistance();
    
    if(!isFree(distance)){
      visionMap |= 1;
    }
    
    delay(1000);
    
    myservo.write(45);
    delay(1000);
    distance = getDistance();
    
    if(!isFree(distance)){
      visionMap |= 2;
    }
    
    delay(1000);
    
    myservo.write(90);
    delay(1000);
    distance = getDistance();
    
    if(!isFree(distance)){
      visionMap |= 4;
    }
    
    delay(1000);
    
    myservo.write(130);
    delay(1000);
    distance = getDistance();
    
    if(!isFree(distance)){
      visionMap |= 8;
    }
    
    delay(1000);

    myservo.write(175);
    delay(1000);
    distance = getDistance();
    
    if(!isFree(distance)){
      visionMap |= 16;
    }
    
    delay(1000);


    myservo.write(90);
    delay(1000);
 
   cleanBuffer();
   fillBufferWithMap();
   visionMap = 0;
    
   //debugBuffer();
    
    /*
    for(int i=0; i < 8; i++){
        Serial.println(visionMap&1);
        visionMap = visionMap>>1;    
    }*/
}

void avoidObstacle(){
  
    int distance = getDistance();
    
    while(!isFree(distance)){
        turnRight();
        delay(600);
        stop();
        delay(1000);
        distance = getDistance();
    }
    
    forward();
    delay(1000);
    stop();
    delay(2000);

}


//MOVIMIENTO

void rotate(int value, unsigned char isY){
 
  if(isY){
       if(value<0){
           switch(orientation){
               case 'N':
                       turnRight();
                       turnRight();
                       break;
               case 'S':
                       break;
               case 'E':
                       turnLeft();
                       break;
               case 'O':
                       turnRight();
                       break;                  
                       
           }
           
           orientation = 'S';
           
       }else{
           switch(orientation){
               case 'N':
                       break;
               case 'S':
                       turnRight();
                       turnRight();          
                       break;
               case 'E':
                       turnRight();
                       break;
               case 'O':
                       turnLeft();
                       break;                  
                       
           }
      
          orientation = 'N';     
       }
       
  }else{
    
      if(value<0){
           switch(orientation){
               case 'N':
                       turnRight();
                       break;
               case 'S':
                       turnLeft();
                       break;
               case 'E':
                       turnRight();
                       turnRight();  
                       break;
               case 'O':
                       break;                  
                       
           }
           
           orientation = 'O';
           
       }else{
           switch(orientation){
               case 'N':
                       turnLeft();
                       break;
               case 'S':
                       turnRight();        
                       break;
               case 'E':
                       break;
               case 'O':
                       turnRight();
                       turnRight();  
                       break;                  
                       
           }
      
           orientation = 'E';
                 
       }
    
  }
}



void startMovement(){

  orientation = command[1];

  int stx = command[2] - '0';
  int sty = command[3] - '0';
  //int stx = command[2];
  //int sty = command[3];
  
  int next = 4;
  
  while(next < (INS_SIZE-2)/2){
    
     if(command[next] == 127)break;
     
     //int cx = command[next];
     //int cy = command[next+1];
     int cx = command[next] - '0';
     int cy = command[next+1] - '0';
        
     int movx = stx - cx;
     int movy = sty - cy;

     rotate(movy,1); 
     forwardOn(movy);  
     rotate(movx,0);   
     forwardOn(movx);  
     /*
     Serial.print(movx);
     Serial.print(",");
     Serial.print(movy); 
     Serial.print(",");
     Serial.print(orientation); 
     Serial.println();
     */
     stx = cx;
     sty = cy;

     next+=2;
  }
  
}


void enableTask(){
  switch(currentCommand){
        case 'F': 
            forward();
            break;
        case 'B':
            backward();
            break;
        case 'L':
            turnLeft();
            break;
        case 'R':
            turnRight();
            break;
        case 'S':
            scan();
            break;
        case 'G':
            sendBuffer();
            break;
        case 'A':
            avoidObstacle();
            break;
        case 'M':
            startMovement();
            break;
        default:
            isReading = YES;
            break;
  }
}

void loop() {


     if (Serial.available() > 0) {
                  memset(command,0,INS_SIZE);
                  incomingByte = Serial.readBytes(command,INS_SIZE);
                  currentCommand = command[0];
                  enableTask();
     }
   
}

//================================= WHEELS CONTROL

void forwardOn(int steps){
  
  digitalWrite(L1, HIGH);  
  digitalWrite(L2, LOW);  
  
  digitalWrite(R1, HIGH);  
  digitalWrite(R2, LOW);  
  
  delay(FORWARD_DURATION*abs(steps));
  stop();
  
  
}

void stop(){
  digitalWrite(L1, LOW);  
  digitalWrite(L2, LOW);   
  
  digitalWrite(R1, LOW);  
  digitalWrite(R2, LOW);   
  delay(1000);
}

void turnRight(){
  digitalWrite(L1, LOW);  
  digitalWrite(L2, HIGH);   
  
  digitalWrite(R1, HIGH);  
  digitalWrite(R2, LOW);  
  delay(TURN_DURATION);
  stop();
}

void turnLeft(){
  digitalWrite(L1, HIGH);  
  digitalWrite(L2, LOW);   
  
  digitalWrite(R1, LOW);  
  digitalWrite(R2, HIGH);  
  delay(TURN_DURATION);
  stop();
}

void forward(){
  digitalWrite(L1, HIGH);  
  digitalWrite(L2, LOW);  
  
  digitalWrite(R1, HIGH);  
  digitalWrite(R2, LOW);  
  
  delay(FORWARD_DURATION);
  stop();
}

void backward(){
  digitalWrite(L1, LOW);  
  digitalWrite(L2, HIGH);  
  
  digitalWrite(R1, LOW);  
  digitalWrite(R2, HIGH);
  
  delay(FORWARD_DURATION);
  stop();  
}

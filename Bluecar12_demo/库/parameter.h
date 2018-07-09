#ifndef PARAMETER_H
#define PARAMETER_H

#include "Arduino.h"


#ifdef __cplusplus
extern "C" {
#endif

#define MAXCAR 4

//三车跟随展示专用变量
bool auto_follow;
int next_id;

int car_id=1,send_id,receive_id,message_id;
String message_wait_to_be_send[MAXCAR],message_wait_to_be_carry_out,message_carrying_out;
int send_tot[MAXCAR],receive_tot[MAXCAR],wait_ack[MAXCAR];
unsigned long last_send_t;

bool is_moving;
unsigned long nowx=0,nowy=0,nowdir=0,now_t,stop_t;
float nowrad=0;
int move_type;

int mh_left_pin=4,mh_right_pin=13;
int right_motor,left_motor;//0-255之间
float rotate_per_sec,move_per_sec;
bool auto_report_obstacle,auto_report_sensor;
int maxreportpersec_obstacle,maxreportpersec_sensor;
bool connect_status[MAXCAR];
unsigned long last_report_t;

int Echo=A1;  // Echo回声脚(P2.0)
int Trig=A0;  //  Trig 触发脚(P2.1)

int Front_Distance = 0;
int Left_Distance = 0;
int Right_Distance = 0;

int servopin=2;//设置舵机驱动脚到数字口2

int left_motor_pin=8;     //左电机(IN3) 输出0  前进   输出1 后退
int left_motor_pwm_pin=9;     //左电机PWM调速
int right_motor_pwm_pin=10;    // 右电机PWM调速
int right_motor_pin=11;    // 右电机后退(IN1)  输出0  前进   输出1 后退



void Serial_Prepare(){
  Serial.begin(9600);
  while(!Serial){}
  while (Serial.available()<=0){}
  return;
}

void init_parameter()
{
  last_send_t=millis();
  next_id=car_id%3+1;
  auto_follow=false;
  right_motor=175;
  left_motor=120;
  
  nowx=0;
  nowy=0;
  nowdir=0;
  now_t=millis();
  last_report_t=millis();

  for (int i=0;i<MAXCAR;i++){
    connect_status[i]=false;
    send_tot[i]=0;
    receive_tot[i]=0;
    wait_ack[i]=1;
    message_wait_to_be_send[i]="";
  }
  connect_status[car_id]=true;
  message_wait_to_be_carry_out="";
  message_carrying_out="";

  is_moving=false;

  pinMode(mh_left_pin,INPUT);
  pinMode(mh_right_pin,INPUT);
    
  pinMode(left_motor_pin,OUTPUT); // PIN 8 8脚无PWM功能
  pinMode(left_motor_pwm_pin,OUTPUT); // PIN 9 (PWM)
  pinMode(right_motor_pwm_pin,OUTPUT);// PIN 10 (PWM) 
  pinMode(right_motor_pin,OUTPUT);// PIN 11 (PWM)

  pinMode(Echo, INPUT);    // 定义超声波输入脚
  pinMode(Trig, OUTPUT);   // 定义超声波输出脚

  pinMode(servopin,OUTPUT);//设定舵机接口为输出接口
 
  return;
}

#ifdef __cplusplus
}
#endif




#endif

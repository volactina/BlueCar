#include "car.h"
#include "parameter.h"

void brake()         //刹车，停车
{
  digitalWrite(right_motor_pwm_pin,LOW);  // 右电机PWM 调速输出0      
  analogWrite(right_motor_pwm_pin,0);//PWM比例0~255调速，左右轮差异略增减

  digitalWrite(left_motor_pwm_pin,LOW);  //左电机PWM 调速输出0          
  analogWrite(left_motor_pwm_pin,0);//PWM比例0~255调速，左右轮差异略增减
  return;
}

void forward()     // 前进
{
  digitalWrite(right_motor_pin,LOW);  // 右电机前进
  digitalWrite(right_motor_pwm_pin,HIGH);  // 右电机前进     
  analogWrite(right_motor_pwm_pin,right_motor);//PWM比例0-255调速
  
  digitalWrite(left_motor_pin,LOW);  // 左电机前进
  digitalWrite(left_motor_pwm_pin,HIGH);  //左电机PWM     
  analogWrite(left_motor_pwm_pin,left_motor);//PWM比例0-255调速
  return;
}

void back()          //后退
{
  digitalWrite(right_motor_pin,HIGH);  // 右电机后退
  digitalWrite(right_motor_pwm_pin,HIGH);  // 右电机前进     
  analogWrite(right_motor_pwm_pin,right_motor);//PWM比例0~255调速，左右轮差异略增减
  
  
  digitalWrite(left_motor_pin,HIGH);  // 左电机后退
  digitalWrite(left_motor_pwm_pin,HIGH);  //左电机PWM     
  analogWrite(left_motor_pwm_pin,left_motor);//PWM比例0~255调速，左右轮差异略增减
}

void spin_left()         //左转(左轮后退，右轮前进)
{
  digitalWrite(right_motor_pin,LOW);  // 右电机前进
  digitalWrite(right_motor_pwm_pin,HIGH);  // 右电机前进     
  analogWrite(right_motor_pwm_pin,255);//PWM比例0~255调速，左右轮差异略增减
  
  digitalWrite(left_motor_pin,HIGH);  // 左电机后退
  digitalWrite(left_motor_pwm_pin,HIGH);  //左电机PWM     
  analogWrite(left_motor_pwm_pin,255);//PWM比例0~255调速，左右轮差异略增减
  return;
}
void spin_right()        //右转(右轮后退，左轮前进)
{
  digitalWrite(right_motor_pin,HIGH);  // 右电机后退
  digitalWrite(right_motor_pwm_pin,HIGH);  // 右电机PWM输出1     
  analogWrite(right_motor_pwm_pin,255);//PWM比例0~255调速，左右轮差异略增减
  
  
  digitalWrite(left_motor_pin,LOW);  // 左电机前进
  digitalWrite(left_motor_pwm_pin,HIGH);  //左电机PWM     
  analogWrite(left_motor_pwm_pin,255);//PWM比例0~255调速，左右轮差异略增减
  return;
}

float rad(int dir)//角度转弧度
{
  return float(dir)*PI/180;
}

int dir(float rad)//弧度转角度
{
  return int(rad*180/PI);
}

unsigned long mh_duration(int pin)
{
  unsigned long duration=0;
  pulseIn(pin,HIGH);
  duration=pulseIn(pin,LOW);
  return duration;
}

void Move(float s)//前进或后退
{
  unsigned long runtime=(s/move_per_sec)*1000;
  stop_t=millis()+runtime;
  if (s>0){
    forward();
    move_type=1;
  }
  if (s<0){
    back();
    move_type=-1;
  }
  is_moving=true;
  now_t=millis();
  return;
}

void Rotate(int dir)//顺时针旋转或逆时针旋转
{
  if (dir==0) return;
  if (dir>0){
    spin_left();
  }
  if (dir<0){
    spin_right();
  }
  delay(1000*dir/rotate_per_sec);
  brake();
  nowdir=(nowdir+dir)%360;
  return;
}

void Rotate_to(int dir)
{
  if (nowdir==dir) return;
  if ((nowdir-dir+360)%360<180){
    Rotate((nowdir-dir+360)%360);
  }else{
    Rotate(-(dir-nowdir+360)%360);
  }
  return;
}

void Move_to(float x,float y)
{
  float vx=x-nowx,vy=y-nowy;
  int d=atan(abs(vy)/abs(vx));
  if (vx>0&&vy>0){
    Rotate_to(d);
  }
  if (vx<0&&vy>0){
    Rotate_to(180-d);
  }
  if (vx<0&&vy<0){
    Rotate_to(180+d);
  }
  if (vx>0&&vy<0){
    Rotate_to(360-d);
  }
  float s=sqrt(vx*vx+vy*vy);
  Move(s);
  return;
}

void update_car_pos(int move_type,unsigned long t){
  float s=(float(t)/1000.0)*move_per_sec;
  nowx+=move_type*s*cos(nowdir);
  nowy-=move_type*s*sin(nowdir);
}

void update_car_detail(String s){
  left_motor=s.substring(s.indexOf('S')+1,s.indexOf('s')).toInt();
  s=s.substring(s.indexOf('s')+1);
  right_motor=s.substring(s.indexOf('S')+1,s.indexOf('s')).toInt();
  s=s.substring(s.indexOf('s')+1);
  rotate_per_sec=s.substring(s.indexOf('S')+1,s.indexOf('s')).toFloat();
  s=s.substring(s.indexOf('s')+1);
  move_per_sec=s.substring(s.indexOf('S')+1,s.indexOf('s')).toFloat();
  s=s.substring(s.indexOf('s')+1);
  auto_report_obstacle=s.substring(s.indexOf('S')+1,s.indexOf('s')).toInt();
  s=s.substring(s.indexOf('s')+1);
  maxreportpersec_obstacle=s.substring(s.indexOf('S')+1,s.indexOf('s')).toInt();
  s=s.substring(s.indexOf('s')+1);
  auto_report_sensor=s.substring(s.indexOf('S')+1,s.indexOf('s')).toInt();
  s=s.substring(s.indexOf('s')+1);
  maxreportpersec_sensor=s.substring(s.indexOf('S')+1,s.indexOf('s')).toInt();
  return;
}

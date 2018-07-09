#define VERSION 12.01
//12.00->12.01 加入wait_t 表示在wait_t前要一直等到上一个位移结束
#define MAXCAR 4
#define DEBUG_MODE false
#define DEBUG_OBSTACLE 20
#define CAR_W 15
#define CAR_H 25

bool auto_follow;
int next_id;

String multiple_obstacle_message;

int car_id=2,send_id,receive_id,message_id;
String message_wait_to_be_send[MAXCAR],message_wait_to_be_carry_out,message_carrying_out;
int send_tot[MAXCAR],receive_tot[MAXCAR],wait_ack[MAXCAR];
unsigned long last_send_t;

bool is_moving;
unsigned long nowx=0,nowy=0,nowdir=0,now_t,stop_t,wait_t;
float nowrad=0;
int move_type;

int mh_left_pin=4,mh_right_pin=13;
int right_motor,left_motor;//0-255之间
float rotate_per_sec=192,move_per_sec=30;
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

void servopulse(int servopin,int myangle);
float Distance_test();
void front_detection();
void left_detection();
void right_detection();
void report_obstacle();

void send_message();
void restore_message(int send_id,int receive_id,String message);
void give_confirm(int send_id,int receive_id,int message_id);
void confirm_message(int send_id,int receive_id,int message_id);
void receive_message(String raws);

void brake();
void forward();
void back();
void spin_left();
void spin_right();
float rad(int dir);
int dir(float rad);
unsigned long mh_duration(int pin);
void Move(float s);
void Rotate(int dir);
void Rotate_to(int dir);
void Move_to(float x,float y);
void update_car_pos(int move_type,unsigned long t);
void update_car_detail(String s);

bool check(String s);
bool is_ack(String s);
bool is_connect(String s);
void carry_out();

void Serial_Prepare(){
  Serial.begin(9600);
  while(!Serial){}
  while (Serial.available()<=0){}
  if (DEBUG_MODE) Serial.println("Serial prepared");
  return;
}

void init_parameter()
{
  wait_t=millis();
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

void setup() {
  init_parameter();
  Serial_Prepare();    
}

void loop() {
  if (!is_moving||(is_moving&&wait_t<millis())){
    message_wait_to_be_carry_out=carry_out(message_wait_to_be_carry_out);
  }
  if(Serial.available()>0){
    String s=Serial.readString();
    receive_message(s);
  }
  
  if (millis()-last_send_t>2000){ 
      send_message();
      last_send_t=millis();
  }
  
  if (is_moving){
    if (millis()-now_t>100){
      update_car_pos(move_type,millis()-now_t);
      now_t=millis();
    }
    if (stop_t<=millis()){
      brake();
      update_car_pos(move_type,millis()-now_t);
      now_t=millis();
      is_moving=false;
    }
  }
  if (!is_moving&&connect_status[0]){
    /*
    if (auto_report_obstacle){
      if (millis()-last_report_t>10000){
        report_obstacle();
        last_report_t=millis();
      }
    }
    */
    if (auto_follow){
      auto_follow=false;
      String s="DU2uX"+String(nowx)+"xY"+String(nowy)+"yd";
      restore_message(car_id,next_id,s);
    }
  }
}

bool check(String s){
  if (s[0]!='P'||s[s.length()-1]!='p'||s[1]!='A') return false;
  for (int i=0;i<26;i++){
    if (s.indexOf('A'+i)!=-1){
      if (s.indexOf('a'+i)==-1) return false;
    }
  }
  return true;
}

bool is_ack(String s){
  if (s.indexOf('R')!=-1) return true;
  return false;
}

bool is_connect(String s){
  if (s[s.length()-2]=='a') return true;
  return false;
}

String carry_out(String s){
  if (s.length()==0){
    return s;
  }
  String othercode="";
  if (s[0]=='P'){
    othercode=s.substring(s.indexOf('p')+1);
    s=s.substring(s.indexOf('P')+1,s.indexOf('p'));
  }
  if (DEBUG_MODE) Serial.println("enter carry_out()");
  if (DEBUG_MODE) Serial.println("s:"+s);
  if (DEBUG_MODE) Serial.println("othercode"+othercode);
  switch(s[0]){
    case 'S':
    {
      s=s.substring(s.indexOf('S')+1,s.indexOf('s'));
      break;
    }
  case 'D':
    {
      int utype=s.substring(s.indexOf('U')+1,s.indexOf('u')).toInt();
      switch(utype){
        case 0:
        {
          int distance=s.substring(s.indexOf('S')+1,s.indexOf('s')).toInt();
          Move(distance);
          break;
        }
        case 1:
        {
          int dir=s.substring(s.indexOf('Z')+1,s.indexOf('z')).toInt();
          Rotate(dir);
          break;
        }
        case 2:
        {
          int x=s.substring(s.indexOf('X')+1,s.indexOf('x')).toInt();
          int y=s.substring(s.indexOf('Y')+1,s.indexOf('y')).toInt();
          Move_to(x,y);
          break;
        }
        case 3:
        {
          brake();
          break;
        }
        case 4:
        {
          wait_t=s.substring(s.indexOf('T')+1,s.indexOf('t')).toInt()+millis();
          break;
        }
        
      }
      s=s.substring(s.indexOf('d')+1);
      break;
    }
  case 'E':
  {
    if (DEBUG_MODE) Serial.println("enter E");
    int type=s.substring(s.indexOf('T')+1,s.indexOf('t')).toInt();
    if (DEBUG_MODE) Serial.println("type:"+type);
    String ecode=s.substring(s.indexOf('E')+1,s.indexOf('e'));
    if (DEBUG_MODE) Serial.println("ecode"+ecode);
    s=s.substring(s.indexOf('e')+1);
    switch(type){
      case 0:
      {
        ecode="";
        break;
      }
      case 1:
      {
        int type1=ecode.substring(ecode.indexOf('U')+1,ecode.indexOf('u')).toInt();
        ecode=ecode.substring(ecode.indexOf('u')+1);
        int type2=ecode.substring(ecode.indexOf('U')+1,ecode.indexOf('u')).toInt();
        ecode="";
        if (DEBUG_MODE) Serial.println("type1:"+String(type1)+",type2:"+String(type2));
        switch(type1){
          case 0:
          {
            switch(type2){
              case 1:
              {
                ecode=String(nowx);
                break;
              }
              case 2:
              {
                ecode=String(nowy);
                break;
              }
              case 3:
              {
                ecode=String(nowdir);
                break;
              }
              case 4:
              {
                ecode=String(now_t);
                break;
              }
            }
            break;
          }
          case 1:
          {
            switch(type2){
              case 1:
              {
                int dis=Distance_test();
                ecode=String(dis);
                break;
              }
              default:
              {
                ecode="0";
                break;
              }
            }
            break;
          }
          case 2:
          {
            break;
          }
          case 3:
          {
            break;
          }
          case 4:
          {
            break;
          }
        }
        break;
      }
    }
    s=ecode+s;
    break;
  }
  case 'I':
  {
    if (DEBUG_MODE) Serial.println("enter I");
    String condition=s.substring(s.indexOf('J')+1,s.indexOf('j'));
    if (DEBUG_MODE) Serial.println("condition:"+condition);
    String execute=s.substring(s.indexOf('j')+1,s.indexOf('i'));
    if (DEBUG_MODE) Serial.println("execute:"+execute);
    s=s.substring(s.indexOf('i')+1);
    String leftexpression=condition.substring(0,condition.indexOf('N'));
    if (DEBUG_MODE) Serial.println("leftexpression:"+leftexpression);
    int relation=condition.substring(condition.indexOf('N')+1,condition.indexOf('n')).toInt();
    if (DEBUG_MODE) Serial.println("relation:"+String(relation));
    String rightexpression=condition.substring(condition.indexOf('n')+1);
    if (DEBUG_MODE) Serial.println("rightexpression:"+rightexpression);
    int leftnum=carry_out(leftexpression).toInt();
    if (DEBUG_MODE) Serial.println("leftnum:"+String(leftnum));
    int rightnum=carry_out(rightexpression).toInt();
    if (DEBUG_MODE) Serial.println("rightnum:"+String(rightnum));
    bool fit=false;
    switch(relation) {
      case 0:
      {
        if (leftnum==rightnum) {
          fit=true;
        }else {
          fit=false;
        }
        break;
      }
      case 1:
      {
        if (leftnum<rightnum) {
          fit=true;
        }else {
          fit=false;
        }
        break;
      }
      case 2:
      {
        if (leftnum>rightnum) {
          fit=true;
        }else {
          fit=false;
        }
        break;
      }
      case 3:
      {
        if (leftnum<=rightnum) {
          fit=true;
        }else {
          fit=false;
        }
        break;
      }
      case 4:
      {
        if (leftnum>=rightnum) {
          fit=true;
        }else {
          fit=false;
        }
        break;
      }
      }
      if (fit){
        if (DEBUG_MODE) Serial.println("fit!");
        s=execute+s;
      }else{
        if (DEBUG_MODE) Serial.println("not fit!");
      }
    break;
  }
  case 'F':
  {
    if (DEBUG_MODE) Serial.println("enter F");
    String condition=s.substring(s.indexOf('J')+1,s.indexOf('j'));
    if (DEBUG_MODE) Serial.println("condition:"+condition);
    String execute=s.substring(s.indexOf('j')+1,s.indexOf('f'));
    if (DEBUG_MODE) Serial.println("execute:"+execute);
    String leftexpression=condition.substring(0,condition.indexOf('N'));
    if (DEBUG_MODE) Serial.println("leftexpression:"+leftexpression);
    int relation=condition.substring(condition.indexOf('N')+1,condition.indexOf('n')).toInt();
    if (DEBUG_MODE) Serial.println("relation:"+String(relation));
    String rightexpression=condition.substring(condition.indexOf('n')+1);
    if (DEBUG_MODE) Serial.println("rightexpression:"+rightexpression);
    int leftnum=carry_out(leftexpression).toInt();
    if (DEBUG_MODE) Serial.println("leftnum:"+String(leftnum));
    int rightnum=carry_out(rightexpression).toInt();
    if (DEBUG_MODE) Serial.println("rightnum:"+String(rightnum));
    bool fit=false;
    switch(relation) {
      case 0:
      {
        if (leftnum==rightnum) {
          fit=true;
        }else {
          fit=false;
        }
        break;
      }
      case 1:
      {
        if (leftnum<rightnum) {
          fit=true;
        }else {
          fit=false;
        }
        break;
      }
      case 2:
      {
        if (leftnum>rightnum) {
          fit=true;
        }else {
          fit=false;
        }
        break;
      }
      case 3:
      {
        if (leftnum<=rightnum) {
          fit=true;
        }else {
          fit=false;
        }
        break;
      }
      case 4:
      {
        if (leftnum>=rightnum) {
          fit=true;
        }else {
          fit=false;
        }
        break;
      }
      }
      if (fit){
        if (DEBUG_MODE) Serial.println("fit!");
        s=execute+s;
      }else{
        if (DEBUG_MODE) Serial.println("not fit!");
        s=s.substring(s.indexOf('f')+1);
      }
    break;
  }
  case 'Q':
    {
      int utype=s.substring(s.indexOf('U')+1,s.indexOf('u')).toInt();
      report_obstacle();//方便演示直接添加
      switch(utype){
        case 1:
        {
          String s="QU0uX"+String(nowx)+"xY"+String(nowy)+"yZ"+String(nowdir)+"zT"+String(now_t)+"tq";
          restore_message(car_id,0,s);
          break;
        }
        case 2:
        {
          String s="QU0uX"+String(nowx)+"xY"+String(nowy)+"yZ"+String(nowdir)+"zT"+String(now_t)+"tS"+left_motor+"sS"+right_motor+"sS"+rotate_per_sec+"sS"+move_per_sec+"sS"+auto_report_obstacle+"sS"+maxreportpersec_obstacle+"sS"+auto_report_sensor+"sS"+maxreportpersec_sensor+"sq";
          restore_message(car_id,0,s);
          break;
        }
        default:
        {
          break;
        }
      }
      s=s.substring(s.indexOf('q')+1);
      break;
    }
    case 'V':
    {
      update_car_detail(s.substring(s.indexOf('V')+1,s.indexOf('v')));
      s=s.substring(s.indexOf('v')+1);
      break;
    }
  }
  return s+othercode;
}
/*
void carry_out(){
  //if (DEBUG_MODE) Serial.println("enter carry_out()");
  if (message_carrying_out.length()==0){
    if (message_wait_to_be_carry_out.length()==0) return;
    message_carrying_out=message_wait_to_be_carry_out.substring(1,message_wait_to_be_carry_out.indexOf('p'));
    message_wait_to_be_carry_out=message_wait_to_be_carry_out.substring(message_wait_to_be_carry_out.indexOf('p')+2);//这里有可能会错误，因为可能会超出最后一个字符，调试的时候看看
  }
  String s=message_carrying_out;
  if (DEBUG_MODE) Serial.println("message_carrying_out:"+s);
  int endindex;
  switch(s[0]){
    case 'D':
    {
      int utype=s.substring(s.indexOf('U')+1,s.indexOf('u')).toInt();
      switch(utype){
        case 0:
        {
          int distance=s.substring(s.indexOf('S')+1,s.indexOf('s')).toInt();
          Move(distance);
          break;
        }
        case 1:
        {
          int dir=s.substring(s.indexOf('Z')+1,s.indexOf('z')).toInt();
          Rotate(dir);
          break;
        }
        case 2:
        {
          int x=s.substring(s.indexOf('X')+1,s.indexOf('x')).toInt();
          int y=s.substring(s.indexOf('Y')+1,s.indexOf('y')).toInt();
          Move_to(x,y);
          break;
        }
        case 3:
        {
          brake();
          break;
        }
      }
      endindex=s.indexOf('d');
      break;
    }
    case 'E':
    {
      break;
    }
    case 'F':
    {
      break;
    }
    case 'G':
    {
      break;
    }
    case 'H':
    {
      report_multiple_obstacle_start();
      for (int i=0;i<5;i++){
        report_multiple_obstacle_add();
        delay(500);
        Rotate(20);
        delay(500);
      }
      report_multiple_obstacle_finish();
      String sss="QU0uX"+String(nowx)+"xY"+String(nowy)+"yZ"+String(nowdir)+"zT"+String(now_t)+"tq";
      restore_message(car_id,0,sss);
      endindex=s.indexOf('h');
      break;
    }
    case 'I':
    {
      break;
    }
    case 'J':
    {
      break;
    }
    case 'K':
    {
      endindex=s.indexOf('k');
      break;
    }
    case 'O':
    {
      //小车巡逻demo代码专用指令
      auto_follow=true;      
      endindex=s.indexOf('o');
      break;
    }
    case 'Q':
    {
      int utype=s.substring(s.indexOf('U')+1,s.indexOf('u')).toInt();
      report_obstacle();//方便演示直接添加
      switch(utype){
        case 1:
        {
          String s="QU0uX"+String(nowx)+"xY"+String(nowy)+"yZ"+String(nowdir)+"zT"+String(now_t)+"tq";
          restore_message(car_id,0,s);
          break;
        }
        case 2:
        {
          String s="QU0uX"+String(nowx)+"xY"+String(nowy)+"yZ"+String(nowdir)+"zT"+String(now_t)+"tS"+left_motor+"sS"+right_motor+"sS"+rotate_per_sec+"sS"+move_per_sec+"sS"+auto_report_obstacle+"sS"+maxreportpersec_obstacle+"sS"+auto_report_sensor+"sS"+maxreportpersec_sensor+"sq";
          restore_message(car_id,0,s);
          break;
        }
        default:
        {
          break;
        }
      }
      endindex=s.indexOf('q');
      break;
    }
    case 'V':
    {
      update_car_detail(s.substring(s.indexOf('V')+1,s.indexOf('v')));
      endindex=s.indexOf('v');
      break;
    }
    default:
    {
      break;
    }
  }
  message_carrying_out=message_carrying_out.substring(endindex+1);
  return;
}
*/

void brake()         //刹车，停车
{
  if (DEBUG_MODE) Serial.println("brake!");
  digitalWrite(right_motor_pwm_pin,LOW);  // 右电机PWM 调速输出0      
  analogWrite(right_motor_pwm_pin,0);//PWM比例0~255调速，左右轮差异略增减

  digitalWrite(left_motor_pwm_pin,LOW);  //左电机PWM 调速输出0          
  analogWrite(left_motor_pwm_pin,0);//PWM比例0~255调速，左右轮差异略增减
  return;
}

void forward()     // 前进
{
  if (DEBUG_MODE) Serial.println("forward!");
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
  if (DEBUG_MODE) Serial.println("back!");
  digitalWrite(right_motor_pin,HIGH);  // 右电机后退
  digitalWrite(right_motor_pwm_pin,HIGH);  // 右电机前进     
  analogWrite(right_motor_pwm_pin,right_motor);//PWM比例0~255调速，左右轮差异略增减
  
  
  digitalWrite(left_motor_pin,HIGH);  // 左电机后退
  digitalWrite(left_motor_pwm_pin,HIGH);  //左电机PWM     
  analogWrite(left_motor_pwm_pin,left_motor);//PWM比例0~255调速，左右轮差异略增减
}

void spin_left()         //左转(左轮后退，右轮前进)
{
  if (DEBUG_MODE) Serial.println("spin_left!");
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
  if (DEBUG_MODE) Serial.println("spin_right");
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
  if (DEBUG_MODE) Serial.println("Move "+String(s));
  if (DEBUG_MODE) Serial.println("now move_per_sec:"+String(move_per_sec));
  unsigned long runtime=(abs(s)/move_per_sec)*1000;
  stop_t=millis()+runtime;
  if (DEBUG_MODE) Serial.println("stop_t:"+String(stop_t));
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
  if (DEBUG_MODE) Serial.println("Rotate:"+String(dir));
  if (dir==0) return;
  if (dir>0){
    spin_left();
  }
  if (dir<0){
    spin_right();
  }
  unsigned long rotate_t=(abs(dir)/rotate_per_sec)*1000;
  if (DEBUG_MODE) Serial.println("rotate_t:"+String(rotate_t));
  delay(rotate_t);
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
  if (DEBUG_MODE) Serial.println("Move_to:x "+String(x)+" y "+String(y));
  float vx=x-nowx,vy=y-nowy;
  int d=dir(atan(abs(vy)/abs(vx)));
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
  if (DEBUG_MODE) Serial.println("update_car_pos move_type:"+String(move_type)+" t:"+String(t));
  float s=(float(t)/1000.0)*move_per_sec;
  if (DEBUG_MODE) Serial.println("s:"+String(s));
  nowx+=move_type*s*cos(rad(nowdir));
  nowy-=move_type*s*sin(rad(nowdir));
  if (DEBUG_MODE) Serial.println("nowx:"+String(nowx));
  if (DEBUG_MODE) Serial.println("nowy:"+String(nowy));
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

void send_message(){
  //if (DEBUG_MODE) Serial.println("try send message");
  for (int i=0;i<MAXCAR;i++){
    if (message_wait_to_be_send[i].length()>0){
      String s=message_wait_to_be_send[i].substring(0,message_wait_to_be_send[i].indexOf('p')+1);
      Serial.println(s);
      Serial.flush();
    }
  }
  return;
}

void restore_message(int send_id,int receive_id,String message){
  if (DEBUG_MODE) Serial.println("enter restore_message,send_id:"+String(send_id)+",receive_id:"+String(receive_id)+",message:"+message);
  send_tot[receive_id]++;
  int message_id=send_tot[receive_id];
  String s="PA"+String(send_id)+"aB"+String(receive_id)+"bC"+message_id+"c"+message+"p";
  if (DEBUG_MODE) Serial.println("s:"+s);
  message_wait_to_be_send[receive_id]+=s;
  if (DEBUG_MODE) Serial.println("message_wait_to_be_send:"+message_wait_to_be_send[receive_id]);
  return;
}

void give_confirm(int send_id,int receive_id,int message_id){
  String s="PA"+String(send_id)+"aB"+String(receive_id)+"bR"+message_id+"rp";
  Serial.println(s);
  Serial.flush();
  return;
}

void confirm_message(int send_id,int receive_id,int message_id){
  if (DEBUG_MODE) Serial.println("confirm_message,send_id:"+String(send_id)+",receive_id:"+String(receive_id)+",message_id");
  if (receive_id!=car_id) return;
  if (message_id==wait_ack[send_id]){
    wait_ack[send_id]++;
    message_wait_to_be_send[send_id]=message_wait_to_be_send[send_id].substring(message_wait_to_be_send[send_id].indexOf('p')+1);
  }
  return;
}

void receive_message(String raws){
  if (DEBUG_MODE) Serial.println("enter receive_message(),raws:"+raws);
  while (raws.length()>0){
    if (raws.indexOf('P')==-1||raws.indexOf('p')==-1) return;
    String s=raws.substring(raws.indexOf('P'),raws.indexOf('p')+1);
    if (DEBUG_MODE) Serial.println("s:"+s);
    raws=raws.substring(raws.indexOf('p')+1);
    if (DEBUG_MODE) Serial.println("raws:"+raws);
    if (!check(s)) return;
    int send_id=s.substring(s.indexOf('A')+1,s.indexOf('a')).toInt();
    if (DEBUG_MODE) Serial.println("send_id:"+String(send_id));
    if (is_connect(s)){
      if (DEBUG_MODE) Serial.println("is connect!");
      String s="PA"+String(car_id)+"ap";
      Serial.println(s);
      Serial.flush();
      connect_status[send_id]=true;
      return;
    }
    int receive_id=s.substring(s.indexOf('B')+1,s.indexOf('b')).toInt();
    if (DEBUG_MODE) Serial.println("receive_id:"+String(receive_id));
    if (receive_id!=car_id) return;
    int message_id=s.substring(s.indexOf('C')+1,s.indexOf('c')).toInt();
    if (DEBUG_MODE) Serial.println("message_id:"+String(message_id));
    if (is_ack(s)){
      if (DEBUG_MODE) Serial.println("is ack!");
      int message_id=s.substring(s.indexOf('R')+1,s.indexOf('r')).toInt();
      if (DEBUG_MODE) Serial.println("message_id:"+String(message_id));
      confirm_message(send_id,receive_id,message_id);
      return;
    }
    give_confirm(car_id,send_id,message_id);
    if (DEBUG_MODE) Serial.println("now receive_tot:"+String(receive_tot[send_id]));
    if (message_id==receive_tot[send_id]+1){
      receive_tot[send_id]++;
      message_wait_to_be_carry_out+="P"+s.substring(s.indexOf('c')+1,s.indexOf('p'))+"p";
      if (DEBUG_MODE) Serial.println("message add to message_wait_to_be_carry_out!now message_wait_to_be_carry_out:"+message_wait_to_be_carry_out);
    }
  }
  return;
}


void servopulse(int servopin,int myangle)/*定义一个脉冲函数，用来模拟方式产生PWM值舵机的范围是0.5MS到2.5MS 1.5MS 占空比是居中周期是20MS*/ 
{
  int pulsewidth=(myangle*11)+500;//将角度转化为500-2480 的脉宽值 这里的myangle就是0-180度  所以180*11+50=2480  11是为了换成90度的时候基本就是1.5MS
  digitalWrite(servopin,HIGH);//将舵机接口电平置高                                      90*11+50=1490uS  就是1.5ms
  delayMicroseconds(pulsewidth);//延时脉宽值的微秒数  这里调用的是微秒延时函数
  digitalWrite(servopin,LOW);//将舵机接口电平置低
 // delay(20-pulsewidth/1000);//延时周期内剩余时间  这里调用的是ms延时函数
  delay(20-(pulsewidth*0.001));//延时周期内剩余时间  这里调用的是ms延时函数
}

float Distance_test()   // 量出前方距离 
{
  if (DEBUG_MODE){
    return DEBUG_OBSTACLE;
  }
  digitalWrite(Trig, LOW);   // 给触发脚低电平2μs
  delayMicroseconds(2);
  digitalWrite(Trig, HIGH);  // 给触发脚高电平10μs，这里至少是10μs
  delayMicroseconds(10);
  digitalWrite(Trig, LOW);    // 持续给触发脚低电
  float Fdistance = pulseIn(Echo, HIGH);  // 读取高电平时间(单位：微秒)
  Fdistance= Fdistance/58;       //为什么除以58等于厘米，  Y米=（X秒*344）/2
  // X秒=（ 2*Y米）/344 ==》X秒=0.0058*Y米 ==》厘米=微秒/58
  //Serial.print("Distance:");      //输出距离（单位：厘米）
  //Serial.println(Fdistance);         //显示距离
  //Distance = Fdistance;
  return Fdistance+CAR_H/2;
} 

void front_detection()
{
  //此处循环次数减少，为了增加小车遇到障碍物的反应速度
  for(int i=0;i<=5;i++) //产生PWM个数，等效延时以保证能转到响应角度
  {
    servopulse(servopin,90);//模拟产生PWM
  }
  Front_Distance = Distance_test();
}

void left_detection()
{
  for(int i=0;i<=15;i++) //产生PWM个数，等效延时以保证能转到响应角度
  {
    servopulse(servopin,175);//模拟产生PWM
  }
  Left_Distance = Distance_test();
}

void right_detection()
{
  for(int i=0;i<=15;i++) //产生PWM个数，等效延时以保证能转到响应角度
  {
    servopulse(servopin,5);//模拟产生PWM
  }
  Right_Distance = Distance_test();
}

void report_obstacle(){
  float dis=Distance_test();
  if (DEBUG_MODE){
    Serial.println("dis="+String(dis));
    Serial.println("nowx="+String(nowx));
    Serial.println("nowy="+String(nowy));
    Serial.println("nowdir="+String(nowdir));
    Serial.println("cos(nowdir)="+String(cos(rad(nowdir))));
    Serial.println("sin(nowdir)="+String(sin(rad(nowdir))));
  }
  float obs_x=nowx+dis*cos(rad(nowdir)),obs_y=nowy+dis*sin(rad(nowdir));
  String s="HU0uX"+String(obs_x)+"xY"+String(obs_y)+"yh";
  restore_message(car_id,0,s);
  return;
}

void report_multiple_obstacle_start(){
  multiple_obstacle_message="H";
  return;
}

void report_multiple_obstacle_add(){
  float dis=Distance_test();
  float obs_x=nowx+dis*cos(rad(nowdir)),obs_y=nowy+dis*sin(rad(nowdir));
  multiple_obstacle_message+="U0uX"+String(obs_x)+"xY"+String(obs_y)+"y";
  return;
}

void report_multiple_obstacle_finish(){
  multiple_obstacle_message+="h";
  restore_message(car_id,0,multiple_obstacle_message);
  multiple_obstacle_message="";
  return;
}


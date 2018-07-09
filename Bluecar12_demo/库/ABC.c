#include "Arduino.h"
#include "ABC.h"
#include "parameter.h"
#include "car.h"
#include "message.h"

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

void carry_out(){
  if (message_carrying_out.length()==0){
    if (message_wait_to_be_carry_out.length()==0) return;
    message_carrying_out=message_wait_to_be_carry_out.substring(1,message_wait_to_be_carry_out.indexOf('p'));
    message_wait_to_be_carry_out=message_wait_to_be_carry_out.substring(message_wait_to_be_carry_out.indexOf('p')+2);//这里有可能会错误，因为可能会超出最后一个字符，调试的时候看看
  }
  String s=message_carrying_out;
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

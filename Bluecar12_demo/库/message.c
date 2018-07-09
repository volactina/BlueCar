
#include "message.h"
#include "parameter.h"
#include "ABC.h"

void send_message(){
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
  send_tot[receive_id]++;
  int message_id=send_tot[receive_id];
  String s="PA"+String(send_id)+"aB"+String(receive_id)+"bC"+message_id+"c"+message+"p";
  message_wait_to_be_send[receive_id]+=s;
  return;
}

void give_confirm(int send_id,int receive_id,int message_id){
  String s="PA"+String(send_id)+"aB"+String(receive_id)+"bR"+message_id+"rp";
  Serial.println(s);
  Serial.flush();
  return;
}

void confirm_message(int send_id,int receive_id,int message_id){
  if (receive_id!=car_id) return;
  if (message_id==wait_ack[send_id]){
    wait_ack[send_id]++;
    message_wait_to_be_send[send_id]=message_wait_to_be_send[send_id].substring(message_wait_to_be_send[send_id].indexOf('p')+1);
  }
  return;
}

void receive_message(String raws){
  while (raws.length()>0){
    if (raws.indexOf('P')==-1||raws.indexOf('p')==-1) return;
    String s=raws.substring(raws.indexOf('P'),raws.indexOf('p')+1);
    raws=raws.substring(raws.indexOf('p')+1);
    if (!check(s)) return;
    int send_id=s.substring(s.indexOf('A')+1,s.indexOf('a')).toInt();
    if (is_connect(s)){
      String s="PA"+String(car_id)+"ap";
      Serial.println(s);
      Serial.flush();
      connect_status[send_id]=true;
      return;
    }
    int receive_id=s.substring(s.indexOf('B')+1,s.indexOf('b')).toInt();
    if (receive_id!=car_id) return;
    int message_id=s.substring(s.indexOf('C')+1,s.indexOf('c')).toInt();
    if (is_ack(s)){
      confirm_message(send_id,receive_id,message_id);
      return;
    }
    give_confirm(car_id,send_id,message_id);
    if (message_id==receive_tot[send_id]+1){
      receive_tot[send_id]++;
      message_wait_to_be_carry_out+="P"+s.substring(s.indexOf('c')+1,s.indexOf('p'))+"p";
    }
  }
  return;
}

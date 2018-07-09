#ifndef MESSAGE_H
#define MESSAGE_H

#include "Arduino.h"


#ifdef __cplusplus
extern "C" {
#endif

void send_message();
void restore_message(int send_id,int receive_id,String message);
void give_confirm(int send_id,int receive_id,int message_id);
void confirm_message(int send_id,int receive_id,int message_id);
void receive_message(String raws);

#ifdef __cplusplus
}
#endif



#endif

#ifndef ABC_H
#define ABC_H

#include "Arduino.h"

#ifdef __cplusplus
extern "C" {
#endif

bool check(String s);
bool is_ack(String s);
bool is_connect(String s);
void carry_out();

#ifdef __cplusplus
}
#endif



#endif

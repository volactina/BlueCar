#ifndef CAR_H
#define CAR_H

#include "Arduino.h"

#ifdef __cplusplus
extern "C" {
#endif

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

#ifdef __cplusplus
}
#endif



#endif

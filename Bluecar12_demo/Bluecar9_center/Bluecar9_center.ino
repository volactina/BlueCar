#include "U8glib.h"
U8GLIB_SSD1306_128X64 u8g(13, 11, 10, 9);  // SW SPI Com: SCK = 13, MOSI = 11, CS = 10, A0 = 9

int dotnum=0;
struct dot
{
  int x;
  int y;
};
typedef struct dot dot;

dot dot_record[250];

bool has_send=true;

int cnt=0;

void draw()
{
  if (has_send) u8g.drawCircle(20,20, 14);

  u8g.setFont(u8g_font_5x7);
  u8g.setPrintPos(40,50);
  u8g.print(dotnum);
  u8g.setPrintPos(100,50);
  u8g.print(cnt);
  for (int i=0;i<dotnum;i++){
    u8g.drawPixel(dot_record[i].x, dot_record[i].y);
    //Serial.println("dot drawed!");
  }
  return;
}

void setup() 
{
  // put your setup code here, to run once:
  Serial.begin(9600);
  while(!Serial){}
  //Serial.println("prepared!");
}

void loop() 
{
  if (has_send)
    {
      Serial.println("PHUuhp");
    }
  if (Serial.available()>0)
  {
    while (Serial.read()=='P')
    {
      cnt++;
      String getorder;
      int getorder_s=0;
      getorder=Serial.readStringUntil('p');
      while (getorder_s<getorder.length())
      {
        switch(getorder[getorder_s])
        {
          case 'H':
          {
            //Serial.println("received!");
            while (getorder_s<getorder.indexOf('h',getorder_s))
            {
              int xx=getorder.substring(getorder.indexOf('X',getorder_s)+1,getorder.indexOf('x',getorder_s)).toInt();
              int yy=getorder.substring(getorder.indexOf('Y',getorder_s)+1,getorder.indexOf('y',getorder_s)).toInt();
              dot newdot={xx,yy};
              dot_record[dotnum++]=newdot;
              //Serial.println("dot record!");
              getorder_s=getorder.indexOf('y',getorder_s)+1;
              //Serial.println(getorder_s);
            }
            getorder_s=getorder.indexOf('h',getorder_s)+1;
            break;
          }
          case 'R':
          {
            has_send=false;
            getorder_s=getorder.indexOf('r',getorder_s)+1;
          }
          default:
          {
            break;
          }
        }
      }
    }
    //Serial.flush();
    //while(Serial.read()>=0){}
    String clean=Serial.readString();
    Serial.println(clean);
  }
  u8g.firstPage();
  do{
    draw();
    
    while (Serial.read()!='B')
    {
      String ss=Serial.readString();
      if (ss[0]=='A')
      {
        u8g.drawPixel(random(0,50),random(0,50));
      }
    }
    
  }while(u8g.nextPage());

}

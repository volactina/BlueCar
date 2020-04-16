/*这篇把8266作为TCPcleint，加入电脑创建的tcpServer中
来控制一个继电器*/
 
#include <ESP8266WiFi.h>

const char *ssid     = "ywh";//这里是我的wifi，你使用时修改为你要连接的wifi ssid
const char *password = "dymlz2057929";//你要连接的wifi密码
const char *host = "192.168.43.152";//修改为你建立的Server服务端的IP地址
WiFiClient client;
const int tcpPort = 1234;//修改为你建立的Server服务端的端口号
 
 
void setup()
{
    Serial.begin(9600);    
    //Serial.print("Connecting to ");//写几句提示，哈哈
    //Serial.println(ssid);
 
    WiFi.begin(ssid, password);
 
    while (WiFi.status() != WL_CONNECTED)//WiFi.status() ，这个函数是wifi连接状态，返回wifi链接状态
                                         //这里就不一一赘述它返回的数据了，有兴趣的到ESP8266WiFi.cpp中查看
    {
        delay(500);        
    }//如果没有连通向串口发送.....
 
    //Serial.println("WiFi connected");
    //Serial.println("IP address: ");
    //Serial.println(WiFi.localIP());//WiFi.localIP()返回8266获得的ip地址

    
}
 
 
void loop()
{
    while (!client.connected())//几个非连接的异常处理
    {
        if (!client.connect(host, tcpPort))
        {
            //Serial.println("connection....");
            //client.stop();
            delay(500);
        }
    }
    if (Serial.available()>0) {
      String ss=Serial.readString();
      client.print(ss+'\n');
      client.flush();
    }
    if (client.available()>0)//available()同ARDUINO，不解释了
    {
        
        String val=client.readString();
        Serial.println(val);
        Serial.flush();
        //char val = client.read();//read()同arduino
        /*
        if(val[0]=='a'){//pc端发送0和1来控制
           //digitalWrite(relay1, LOW);
           Serial.println("a");
           client.print("received_a\n");
        }
        if(val[0]=='b')
        {
            //digitalWrite(relay1, HIGH);
            Serial.println("b");
            client.print("received_b\n");
        }
        */
    }
}
/*
当一个模块怎么烧也烧不进去的时候，可以试着nodemcu-flasher-master win64版本来flash一下，断点重连再烧

烧写模式接法
GND GND
GPI02 3.3V
GPIO0 GND
RXD
TXD
CH_PD 3.3V
RST 3.3V
VCC 3.3V
注意：烧写模式在Arduino IDE下请选择NodeMCU 1.0(ESP-12E Module) 
https://www.arduino.cn/thread-17895-1-1.html
具体参数选择参考http://www.cnblogs.com/ticktack/p/8043344.html
由于未知原因，电源使用功能转接板的话则会失败

工作模式接法
GND GND
GPI02 3.3V
GPIO0 悬空
RXD
TXD
CH_PD 3.3V
RST 3.3V
VCC 3.3V
*/

package version_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class SocketTest {

	private static final int SOCKET_PORT = 1234;  
    private ServerSocket serverSocket = null;  
    private boolean flag = true;  
    private BufferedReader reader;  
    private BufferedWriter writer;  
 
    /** 
     * @param args 
     */  
    public static void main(String[] args)throws Exception {  
    	  SocketTest socketServer = new SocketTest();  
          socketServer.initSocket();  
    }  
 
    private void initSocket() {  
           try {  
               serverSocket = new ServerSocket( SOCKET_PORT);  
               System. out.println("服务已经启动，端口号:" + SOCKET_PORT);  
               while (flag){  
            	   Socket clientSocket = serverSocket.accept();  
                   System.out.println("有客户端连接" );   
                   SocketThread socketThread = new SocketThread(clientSocket);  
                   socketThread.start();                               
               }  
          } catch (IOException e) {  
               e.printStackTrace();  
          } finally {  
                try {  
                	writer.close();  
                }catch(IOException e) {  
                    e.printStackTrace();  
               }  
          }  
    }  
 
    public class SocketThread extends Thread {  
 
           private Socket socket;  
 
           public SocketThread(Socket clientSocket) {  
                this. socket = clientSocket;  
          }  
 
           @Override  
           public void run(){  
        	   super.run();  
               InputStream inputStream = null;  
	           //获取输入流  
	           try{
	        	   inputStream = socket.getInputStream();
			   }catch(IOException e2){
				   e2.printStackTrace();
			   }  
	           //得到读取BufferedReader对象  
	           try{
	        	   reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
			   }catch (UnsupportedEncodingException e2){
					e2.printStackTrace();
			   }  
	           try{
					writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
			   }catch (UnsupportedEncodingException e2){
					e2.printStackTrace();
			   }catch (IOException e2){
					e2.printStackTrace();
			   }

             //循环读取客户端发过来的消息  
             while (flag){  
                   try{
						while ( reader.ready()){  
						       String result= reader.readLine(); 
						       if (result.length()==0) continue;
						       
						       try{
						           Class.forName("com.mysql.jdbc.Driver");
						           System.out.println("注册驱动成功!");
						       }catch(ClassNotFoundException e1){
						           System.out.println("注册驱动失败!");
						           e1.printStackTrace();
						           return;
						       }
						       
						       String url="jdbc:mysql://localhost:3306/bluecarproject";    
						       
						       Connection conn = null;
						       try {
						           conn = DriverManager.getConnection(url, "root", "123456");
						           
						           String s="insert into raw_message values(?)";
							       PreparedStatement stmt=conn.prepareStatement(s);
							       stmt.setString(1, result);
						           
						           //Statement stmt = conn.createStatement(); 
						           //System.out.println("创建Statement成功！");      
						           
						           stmt.executeUpdate();
						           
						           stmt.close();
						           
						       } catch (SQLException e){
						           e.printStackTrace();
						       }
						       finally
						       {
						       	try
						       	{
						       		if(null != conn)
						       		{
						           		conn.close();
						           	}
						       	}
						       	catch (SQLException e){
						               e.printStackTrace();
						       	}        	
						       }
						       
						       System. out.println("客户端发过来的消息为：" + result); 
						   }
				}catch(IOException e){
					e.printStackTrace();
				}
                   
                   try { 
                	   socket.sendUrgentData(0); 
                	   } catch (IOException e) { 
                		    System.out.println("断开了连接");
                	        flag= false;    //如果抛出了异常，那么就是断开连接了  跳出无限循环
                	   } 
             }
           }

    }

}

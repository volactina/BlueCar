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
               System. out.println("�����Ѿ��������˿ں�:" + SOCKET_PORT);  
               while (flag){  
            	   Socket clientSocket = serverSocket.accept();  
                   System.out.println("�пͻ�������" );   
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
	           //��ȡ������  
	           try{
	        	   inputStream = socket.getInputStream();
			   }catch(IOException e2){
				   e2.printStackTrace();
			   }  
	           //�õ���ȡBufferedReader����  
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

             //ѭ����ȡ�ͻ��˷���������Ϣ  
             while (flag){  
                   try{
						while ( reader.ready()){  
						       String result= reader.readLine(); 
						       if (result.length()==0) continue;
						       
						       try{
						           Class.forName("com.mysql.jdbc.Driver");
						           System.out.println("ע�������ɹ�!");
						       }catch(ClassNotFoundException e1){
						           System.out.println("ע������ʧ��!");
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
						           //System.out.println("����Statement�ɹ���");      
						           
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
						       
						       System. out.println("�ͻ��˷���������ϢΪ��" + result); 
						   }
				}catch(IOException e){
					e.printStackTrace();
				}
                   
                   try { 
                	   socket.sendUrgentData(0); 
                	   } catch (IOException e) { 
                		    System.out.println("�Ͽ�������");
                	        flag= false;    //����׳����쳣����ô���ǶϿ�������  ��������ѭ��
                	   } 
             }
           }

    }

}

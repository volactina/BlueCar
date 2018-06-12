package version_2;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.awt.*;

public class SocketTest {

	private static final int SOCKET_PORT = 1234;  
	private static int cnt;
 
    /** 
     * @param args 
     */  
    public static void main(String[] args)throws Exception {   
          cnt=0;
          @SuppressWarnings("resource")
		  ServerSocket serverSocket = new ServerSocket(SOCKET_PORT);  
          System. out.println("服务已经启动，端口号:" + SOCKET_PORT);  
          while (true){  
       	      Socket clientSocket = serverSocket.accept();  
              System.out.println("有客户端连接" );   
              cnt++;
              SocketThread socketThread = new SocketThread(clientSocket);  
              socketThread.start();
              System.out.println("连接总次数:"+cnt);
          }
    }  
 
    public static class SocketThread extends Thread {  
    	 
           private Socket socket;
           private final int id=cnt;
           private final int maxcar=3;
           String url="jdbc:mysql://localhost:3306/bluecarproject?useSSL=false";
           Connection conn = null;
           PreparedStatement stmt=null;
           String sql_order="";
           InputStream inputStream=null;
    	   OutputStream outputStream=null;
    	   PrintWriter pw=null;
    	   BufferedReader reader=null;
    	   Robot robot=null;
    	   Timer timer=null;
    	   int car_id=-1;
    	   ResultSet rs=null;
    	   int[][] tot=new int[maxcar+1][maxcar+1];
    	   boolean update_have_send=false;
    	   int update_message_id;
 
           public SocketThread(Socket clientSocket) {  
                this. socket = clientSocket;  
           }  
 
           @Override  
           public void run(){ 
        	   System.out.println(id+"号线程开始运作");
               //循环读取客户端发过来的消息  
			   boolean flag=true;
               try{ 
            	   inputStream = socket.getInputStream();
	        	   outputStream= socket.getOutputStream();
	        	   pw = new PrintWriter(outputStream);//包装为打印流  
            	   reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
    	           boolean ready=false;
    	           
    	           robot=new Robot();
    	           boolean first=true;
            	   while (flag){
            		   if (ready) {
            			   if (first) {
            				   timer = new Timer();
    					       timer.scheduleAtFixedRate(new TimerTask() {
    					         public void run() {
    					           System.out.println("发送消息");
    					           send_message();
    					         }
    					       },Calendar.getInstance().getTime(), 10000);// 这里设定将延时每天固定执行
    					       first=false;
            			   }
            			   while ( reader.ready()){  
						       String s= reader.readLine(); 
						       if (s.length()==0) continue;
						       System. out.println(id+"号线程：客户端发过来的消息为"+s); 
						       analyze_message(s);
						   }
            		   }
            		   if (!ready) {
        	        	   pw.println("PA0ap");
        		           pw.flush();//缓冲输出  
        		           System.out.println("发送了PA0ap");
        		           robot.delay(10000);
        	        	   while (reader.ready()) {
        	        		   String s=reader.readLine();
        	        		   System. out.println(id+"号线程：客户端发过来的消息为"+s); 
        	        		   Analyze_result ans=analyze_message(s);
        	        		   if (ans.success) {
        	        			   car_id=ans.send_id;
        	        			   ready=true;
            		        	   update_car_status(true);
        	        		   }
        		        	   
        		        	   break;
        	        	   }
        	           }
            		   
            		   /*
					   //心脏检测客户端是否连接
		               try { 
		            	   socket.sendUrgentData(0); 
		            	   }catch (IOException e) { 
		            		    System.out.println(id+"号线程断开了连接");
		            	        flag= false;    //如果抛出了异常，那么就是断开连接了  跳出无限循环
		            	        pw.close();
		            	        if (ready) timer.cancel();
		            	        update_car_status(false);
		            	   } 
		            	*/
		            	
            	   }	
				}catch(IOException | AWTException e){
					e.printStackTrace();
				} 
           }
           
           public boolean check_message(String s) {
        	   if (s.charAt(0)!='P'||s.charAt(s.length()-1)!='p') return false;
        	   if (s.charAt(1)!='A'||s.indexOf('a')==-1) return false;
        	   for (int i=0;i<26;i++) {
        		   if (s.indexOf('A'+i)!=-1) {
        			   if (s.indexOf('a'+i)==-1) return false;
        		   }
        	   }
        	   return true;
           }
           
           class Analyze_result{
        	   boolean success;
        	   public int send_id,receive_id,message_id;
        	   public String message;
           }
           
           public Analyze_result analyze_message(String s) {
        	   Analyze_result ans=new Analyze_result();
        	   if (!check_message(s)) {
        		   ans.success=false;
        		   return ans;
        	   }
        	   ans.send_id=Integer.parseInt(s.substring(s.indexOf('A')+1,s.indexOf('a')));
        	   if (s.indexOf('B')!=-1) {
        		   ans.receive_id=Integer.parseInt(s.substring(s.indexOf('B')+1,s.indexOf('b')));
        		   if (s.indexOf('C')!=-1) {
            		   ans.message_id=Integer.parseInt(s.substring(s.indexOf('C')+1,s.indexOf('c')));
            		   if (ans.receive_id==0) give_confirm(ans.message_id);
            		   if (ans.message_id!=tot[ans.send_id][ans.receive_id]+1) {
            			   ans.success=false;
            			   return ans;
            		   }
            		   tot[ans.send_id][ans.receive_id]++;
            		   ans.message=s.substring(s.indexOf('c')+1,s.indexOf('p'));
            		   restore_message(ans.send_id,ans.receive_id,ans.message_id,ans.message);
            		   if (ans.receive_id==0) {
            			   analyze_message(ans.send_id,ans.message);
            		   }
            	   }
        		   if (s.indexOf('R')!=-1) {
        			   int confirm_id=Integer.parseInt(s.substring(s.indexOf('R')+1,s.indexOf('r')));
        			   confirm_message(ans.receive_id,ans.send_id,confirm_id,"");
        			   restore_message(ans.send_id,ans.receive_id,-1,"R"+confirm_id+"r");
        		   }
        	   }
        	   ans.success=true;
        	   return ans;
           }
           
           public void analyze_message(int send_id,String s) {
        	   if (s.indexOf('Q')!=-1){
        		   String ss=s.substring(s.indexOf('Q')+1,s.indexOf('q'));
        		   int utype=Integer.parseInt(ss.substring(ss.indexOf('U')+1,ss.indexOf('u')));
        		   if (utype==0) {
        			   float x=0,y=0,dir=0;
        			   if (ss.indexOf('X')!=-1) {
        				   x=Float.parseFloat(ss.substring(ss.indexOf('X')+1,ss.indexOf('x')));
        			   }
        			   if (ss.indexOf('Y')!=-1) {
        				   y=Float.parseFloat(ss.substring(ss.indexOf('Y')+1,ss.indexOf('y')));
        			   }
        			   if (ss.indexOf('Z')!=-1) {
        				   dir=Float.parseFloat(ss.substring(ss.indexOf('Z')+1,ss.indexOf('z')));
        			   }
        			   if (ss.indexOf('T')!=-1) {
        				   
        			   }
        			   if (ss.indexOf('S')!=-1) {
        				   String[] temp=new String[8];
        				   int k=0;
        				   for (int i=0;i<ss.length();i++) {
        					   if (ss.charAt(i)=='S') {
        						   temp[k++]=ss.substring(i+1,ss.indexOf('s',i));
        					   }
        				   }
        				   update_car_detail(temp);
        			   }
        			   update_car_pos(send_id,x,y,dir);
        		   }
        	   }
        	   if (s.indexOf('H')!=-1) {
        		   //障碍物数据
        		   String ss=s.substring(s.indexOf('H')+1,s.indexOf('h'));
        		   while (ss.indexOf('U')!=-1) {
        			   int utype=Integer.parseInt(ss.substring(ss.indexOf('U')+1,ss.indexOf('u')));
            		   if (utype==0) {
            			   float x=0,y=0;
            			   if (ss.indexOf('X')!=-1) {
            				   x=Float.parseFloat(ss.substring(ss.indexOf('X')+1,ss.indexOf('x')));
            			   }
            			   if (ss.indexOf('Y')!=-1) {
            				   y=Float.parseFloat(ss.substring(ss.indexOf('Y')+1,ss.indexOf('y')));
            			   }
            			   update_obstacle(send_id,x,y);
            		   }
            		   ss=ss.substring(ss.indexOf('y')+1);
        		   }
        	   }
        	   if (s.indexOf('K')!=-1) {
        		   //传感器数据
        		   String ss=s.substring(s.indexOf('K')+1,s.indexOf('k'));
        	   }
           }
           
           public void update_car_detail(String[] temp) {
        	   try{
		           Class.forName("com.mysql.jdbc.Driver");
		           System.out.println("注册驱动成功!");
		       }catch(ClassNotFoundException e1){
		           System.out.println("注册驱动失败!");
		           e1.printStackTrace();
		           return;
		       }      
		       try {
		    	   Connection conn = DriverManager.getConnection(url, "root", "123456");
	    		   String sql_order="update car_detail set update_status=1,left_motor=?,right_motor=?,rotate_per_sec=?,move_per_sec=?,auto_report_obstacle=?,maxreportpersec_obstacle=?,auto_report_sensor=?,maxreportpersec_sensor=? where car_id=?";
	    	       stmt=conn.prepareStatement(sql_order);
	    	       stmt.setInt(1,Integer.parseInt(temp[0]));
	    	       stmt.setInt(2,Integer.parseInt(temp[1]));
	    	       stmt.setFloat(3,Float.parseFloat(temp[2]));
	    	       stmt.setFloat(4,Float.parseFloat(temp[3]));
	    	       stmt.setBoolean(5,Boolean.parseBoolean(temp[4]));
	    	       stmt.setInt(6,Integer.parseInt(temp[5]));
	    	       stmt.setBoolean(7,Boolean.parseBoolean(temp[6]));
	    	       stmt.setInt(8,Integer.parseInt(temp[7]));
	    	       stmt.setInt(9,car_id);
	    	       stmt.executeUpdate();
		           stmt.close();
		           
		       } catch (SQLException e1){
		           e1.printStackTrace();
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
		       	catch (SQLException e1){
		               e1.printStackTrace();
		       	}        	
		       }
           }
           
           public void update_obstacle(int car_id,float x,float y) {
        	   try{
		           Class.forName("com.mysql.jdbc.Driver");
		           System.out.println("注册驱动成功!");
		       }catch(ClassNotFoundException e1){
		           System.out.println("注册驱动失败!");
		           e1.printStackTrace();
		           return;
		       }
		       
		       try {
		    	   Connection conn = DriverManager.getConnection(url, "root", "123456");
		           
		    	   
	    		   String sql_order="insert into obstacle values(?,?,?)";
		    	   PreparedStatement stmt=conn.prepareStatement(sql_order);
		    	   stmt.setInt(1,car_id);
		    	   stmt.setFloat(2,x);
		    	   stmt.setFloat(3,y);
		    	   stmt.executeUpdate();
		    	   
		           stmt.close();
		           
		       } catch (SQLException e1){
		           e1.printStackTrace();
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
		       	catch (SQLException e1){
		               e1.printStackTrace();
		       	}        	
		       }
           }
           
           public void update_car_pos(int car_id,float x,float y,float dir) {
        	   try{
		           Class.forName("com.mysql.jdbc.Driver");
		           System.out.println("注册驱动成功!");
		       }catch(ClassNotFoundException e1){
		           System.out.println("注册驱动失败!");
		           e1.printStackTrace();
		           return;
		       }
		       
		       try {
		    	   Connection conn = DriverManager.getConnection(url, "root", "123456");
		           
		    	   
	    		   String sql_order="update car set x=?,y=?,z=? where car_id=?";
		    	   PreparedStatement stmt=conn.prepareStatement(sql_order);
		    	   stmt.setFloat(1,x);
		    	   stmt.setFloat(2,y);
		    	   stmt.setFloat(3,dir);
		    	   stmt.setInt(4,car_id);
		    	   stmt.executeUpdate();
		    	   

		           stmt.close();
		           
		       } catch (SQLException e1){
		           e1.printStackTrace();
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
		       	catch (SQLException e1){
		               e1.printStackTrace();
		       	}        	
		       }
           }
           
           public void give_confirm(int message_id) {
        	   String s="PA0aB"+car_id+"bR"+message_id+"rp";
        	   pw.println(s);
        	   pw.flush();
           }
           
           public void restore_message(int send_id,int receive_id,int message_id,String message) {
        	   try{
		           Class.forName("com.mysql.jdbc.Driver");
		           System.out.println("注册驱动成功!");
		       }catch(ClassNotFoundException e1){
		           System.out.println("注册驱动失败!");
		           e1.printStackTrace();
		           return;
		       }
		       
		       try {
		    	   conn = DriverManager.getConnection(url, "root", "123456");
		           
		    	   sql_order="insert into raw_send values(?,?,?,?,?)";
		    	   stmt=conn.prepareStatement(sql_order);
		    	   stmt.setInt(1,send_id);
		    	   stmt.setInt(2,receive_id);
		    	   stmt.setInt(3,message_id);
		    	   if (message_id==-1) {
		    		   stmt.setInt(4,-1);
		    	   }else {
		    		   stmt.setInt(4,0);
		    	   }
		    	   stmt.setString(5,message);
		    	   stmt.executeUpdate();
		    	   
		           stmt.close();
		           
		       } catch (SQLException e1){
		           e1.printStackTrace();
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
		       	catch (SQLException e1){
		               e1.printStackTrace();
		       	}        	
		       }
           }
           
           public void confirm_message(int send_id,int receive_id,int message_id,String confirm) {
        	   try{
		           Class.forName("com.mysql.jdbc.Driver");
		           System.out.println("注册驱动成功!");
		       }catch(ClassNotFoundException e1){
		           System.out.println("注册驱动失败!");
		           e1.printStackTrace();
		           return;
		       }
		       
		       try {
		    	   Connection conn = DriverManager.getConnection(url, "root", "123456");
		           
		    	   if (message_id>0) {
		    		   String sql_order="update raw_send set status=2 where send_id=? and receive_id=? and message_id=?";
			    	   PreparedStatement stmt=conn.prepareStatement(sql_order);
			    	   stmt.setInt(1,send_id);
			    	   stmt.setInt(2,receive_id);
			    	   stmt.setInt(3,message_id);
			    	   stmt.executeUpdate();
		    	   }else {
		    		   String sql_order="update raw_send set status=2 where send_id=? and receive_id=? and message=?";
		    		   PreparedStatement stmt=conn.prepareStatement(sql_order);
			    	   stmt.setInt(1,send_id);
			    	   stmt.setInt(2,receive_id);
			    	   stmt.setString(3,confirm);
			    	   stmt.executeUpdate();
		    	   }

		           stmt.close();
		           
		       } catch (SQLException e1){
		           e1.printStackTrace();
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
		       	catch (SQLException e1){
		               e1.printStackTrace();
		       	}        	
		       }
           }
           
           public void send_message() {
        	   try{
		           Class.forName("com.mysql.jdbc.Driver");
		           System.out.println("注册驱动成功!");
		       }catch(ClassNotFoundException e1){
		           System.out.println("注册驱动失败!");
		           e1.printStackTrace();
		           return;
		       }
		       
		       try {
		    	   Connection conn = DriverManager.getConnection(url, "root", "123456");
		           
		    	   String sql_order="select * from raw_send where receive_id=? and status!=2";
			       PreparedStatement stmt=conn.prepareStatement(sql_order);
			       stmt.setInt(1,car_id);
		           rs=stmt.executeQuery();
		           while (rs.next()) {
		        	   int send_id=rs.getInt(1);
		        	   int message_id=rs.getInt(3);
		        	   tot[0][car_id]=Integer.max(tot[0][car_id],message_id);
		        	   int status=rs.getInt(4);
		        	   String send_message=rs.getString(5);
		        	   if (status==0||status==1) {
		        		   send_message="PA"+send_id+"aB"+car_id+"bC"+message_id+"c"+send_message+"p";
		        	   }else {
		        		   //status为-1时表明发送一个确认帧(不需要回复)
		        		   confirm_message(send_id,car_id,-1,send_message);
		        		   send_message="PA"+send_id+"aB"+car_id+"b"+send_message+"p";
		        	   }
		        	   
		        	   pw.println(send_message);
		        	   pw.flush();
		        	   System.out.println(send_message);
		           }
		           sql_order="update raw_send set status=1 where receive_id=? and status=0";
			       stmt=conn.prepareStatement(sql_order);
			       stmt.setInt(1,car_id);
		           stmt.executeUpdate();
		           
		           if (update_have_send) {
		        	   sql_order="select status from raw_send where receive_id=? and message_id=?";
		        	   stmt=conn.prepareStatement(sql_order);
		        	   stmt.setInt(1,car_id);
		        	   stmt.setInt(2,update_message_id);
		        	   rs=stmt.executeQuery();
		        	   while (rs.next()) {
		        		   if (rs.getInt(1)==2) {
		        			   sql_order="update car_detail set update_status=2 where car_id=?";
		        			   stmt=conn.prepareStatement(sql_order);
		        			   stmt.setInt(1,car_id);
		        			   stmt.executeUpdate();
		        			   update_have_send=false;
		        		   }
		        	   }
		           }else {
		        	   sql_order="select * from car_detail where car_id=? and update_status=0";
			           stmt=conn.prepareStatement(sql_order);
			           stmt.setInt(1,car_id);
			           rs=stmt.executeQuery();
			           while (rs.next()) {
			           	update_message_id=tot[0][car_id]+1;
			           	tot[0][car_id]++;
			           	String order="VU1uS"
			           			     +rs.getString(3)+"sU2uS"
			           			     +rs.getString(4)+"sU3uS"
			           			     +rs.getString(5)+"sU4uS"
			           			     +rs.getString(6)+"sU5uS"
			           			     +rs.getString(7)+"sU6uS"
			           			     +rs.getString(8)+"sU7uS"
			           			     +rs.getString(9)+"sU8uS"
			           			     +rs.getString(10)+"sv";
			           	restore_message(0,car_id,update_message_id,order);
			           	update_have_send=true;
			           }
			           
		           }
		           
		           rs.close();
		           stmt.close();
		           
		       } catch (SQLException e1){
		           e1.printStackTrace();
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
		       	catch (SQLException e1){
		               e1.printStackTrace();
		       	}        	
		       }
           }
           
           public void update_car_status(boolean status) {
        	   try{
		           Class.forName("com.mysql.jdbc.Driver");
		           System.out.println("注册驱动成功!");
		       }catch(ClassNotFoundException e1){
		           System.out.println("注册驱动失败!");
		           e1.printStackTrace();
		           return;
		       }
		       
		       try {
		    	   conn = DriverManager.getConnection(url, "root", "123456");
		           
		    	   if (status) {
		    		   sql_order="update car set status=1 where car_id=?";
				       stmt=conn.prepareStatement(sql_order);
				       stmt.setInt(1,car_id);
			           stmt.executeUpdate();
		    	   }else {
		    		   sql_order="update car set status=0 where car_id=?";
				       stmt=conn.prepareStatement(sql_order);
				       stmt.setInt(1,car_id); 
			           stmt.executeUpdate();
			           
			           sql_order="update car_detail set update_status=0 where car_id=?";
				       stmt=conn.prepareStatement(sql_order);
				       stmt.setInt(1,car_id); 
			           stmt.executeUpdate();
			           
			           sql_order="delete from raw_send where send_id=? or receive_id=?";
			           stmt=conn.prepareStatement(sql_order);
			           stmt.setInt(1,car_id);
			           stmt.setInt(2,car_id);
			           stmt.executeUpdate();
			           
			           sql_order="delete from raw_receive where send_id=?";
			           stmt=conn.prepareStatement(sql_order);
			           stmt.setInt(1,car_id);
			           stmt.executeUpdate();
			           
			           sql_order="delete from obstacle where car_id=?";
			           stmt=conn.prepareStatement(sql_order);
			           stmt.setInt(1,car_id);
			           stmt.executeUpdate();
		    	   }
		           
		           stmt.close();
		           
		       } catch (SQLException e1){
		           e1.printStackTrace();
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
		       	catch (SQLException e1){
		               e1.printStackTrace();
		       	}        	
		       }
           }
    }

}

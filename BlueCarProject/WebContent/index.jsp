
<%@page import="java.util.ArrayList"%>
<%@page import="java.sql.*"%>
<%@page import="projectclass.*" %>


<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>主页</title>

<%
        ArrayList<Message> message=new ArrayList<Message>();
		ArrayList<Car> car=new ArrayList<Car>();
		Car user=new Car();
		user.car_id=0;
		car.add(user);
		ArrayList<Task> task=new ArrayList<Task>();
		ArrayList<Pos> pos=new ArrayList<Pos>();
		ArrayList<HistoryPoint> historypoint=new ArrayList<HistoryPoint>();
		ArrayList<Obstacle> obstacle=new ArrayList<Obstacle>();
		try{
		    Class.forName("com.mysql.jdbc.Driver");
		    System.out.println("注册驱动成功!");
		}catch(ClassNotFoundException e1){
		    System.out.println("注册驱动失败!");
		    e1.printStackTrace();
		    return;
		}

		String url="jdbc:mysql://localhost:3306/bluecarproject?useSSL=false";        
		Connection conn = null;
		try {
		    conn = DriverManager.getConnection(url, "root", "123456");
		    String s;
		    PreparedStatement stmt;
		    ResultSet rs;
		    
		    s="select * from history_point";
		    stmt=conn.prepareStatement(s);
		    rs=stmt.executeQuery();
		    while (rs.next()){
		    	HistoryPoint newhistorypoint=new HistoryPoint();
		    	newhistorypoint.car_id=rs.getInt(1);
		    	newhistorypoint.x=rs.getFloat(2);
		    	newhistorypoint.y=rs.getFloat(3);
		    	newhistorypoint.dir=rs.getFloat(4);
		    	//newhistorypoint.t=rs.getString(5);
		    	historypoint.add(newhistorypoint);
		    }
		        
		    s="select * from obstacle";
		    stmt=conn.prepareStatement(s);
		    rs=stmt.executeQuery();
		    while (rs.next()){
		    	Obstacle newobstacle=new Obstacle();
		    	newobstacle.car_id=rs.getInt(1);
		    	newobstacle.x=rs.getFloat(2);
		    	newobstacle.y=rs.getFloat(3);
		    	obstacle.add(newobstacle);
		    }
		    
		    s="select * from car";
		    stmt=conn.prepareStatement(s);
		    rs=stmt.executeQuery();
		    while(rs.next()){
		    	Car newcar=new Car();
		    	newcar.car_id=rs.getInt(1);
		    	newcar.status=rs.getBoolean(2);
		    	newcar.pos_id=rs.getInt(3);
		    	newcar.x=rs.getFloat(4);
		    	newcar.y=rs.getFloat(5);
		    	newcar.dir=rs.getFloat(6);
		    	//newcar.start_time=rs.getString(7);
		    	car.add(newcar);
		    }
		    for (int i=0;i<car.size();i++){
		    	car.get(i).send_tot=new ArrayList<Integer>();
		    	car.get(i).receive_tot=new ArrayList<Integer>();
		    	for (int j=0;j<car.size();j++){
		    		car.get(i).send_tot.add(0);
		    		car.get(i).receive_tot.add(0);
		    	}
		    }
		    
		    s="select * from car_detail";
		    stmt=conn.prepareStatement(s);
		    rs=stmt.executeQuery();
		    while (rs.next()){
		    	int car_id=rs.getInt(1);
		    	car.get(car_id).update_status=rs.getInt(2);
		    	car.get(car_id).left_motor=rs.getInt(3);
		    	car.get(car_id).right_motor=rs.getInt(4);
		    	car.get(car_id).rotate_per_sec=rs.getFloat(5);
		    	car.get(car_id).move_per_sec=rs.getFloat(6);
		    	car.get(car_id).auto_report_obstacle=rs.getBoolean(7);
		    	car.get(car_id).maxreportpersec_obstacle=rs.getInt(8);
		    	car.get(car_id).auto_report_sensor=rs.getBoolean(9);
		    	car.get(car_id).maxreportpersec_sensor=rs.getInt(10);
		    }
		    s="update car_detail set update_status=2 where update_status=1";
		    stmt=conn.prepareStatement(s);
		    stmt.executeUpdate();
		    
		    
		    s="select * from raw_receive";
		    stmt=conn.prepareStatement(s);
		    rs = stmt.executeQuery();
		    while(rs.next())
		    {
		    	Message newmessage=new Message();
		    	newmessage.send_id=rs.getInt(1);
		    	newmessage.receive_id=0;
		    	newmessage.message_id=rs.getInt(2);
		    	newmessage.status=2;
		    	newmessage.message=rs.getString(3);
		    	message.add(newmessage);
		    	car.get(newmessage.send_id).send_tot.set(newmessage.receive_id,Integer.max(newmessage.message_id,car.get(newmessage.send_id).send_tot.get(newmessage.receive_id)));
		    	
		    }
		    
		    s="select * from raw_send";
		    stmt=conn.prepareStatement(s);
		    rs = stmt.executeQuery();
		    while(rs.next())
		    {
		    	Message newmessage=new Message();
		    	newmessage.send_id=rs.getInt(1);
		    	newmessage.receive_id=rs.getInt(2);
		    	newmessage.message_id=rs.getInt(3);
		    	newmessage.status=rs.getInt(4);
		    	newmessage.message=rs.getString(5);
		    	message.add(newmessage);
		    	car.get(newmessage.send_id).send_tot.set(newmessage.receive_id,Integer.max(newmessage.message_id,car.get(newmessage.send_id).send_tot.get(newmessage.receive_id)));
		    	car.get(newmessage.receive_id).receive_tot.set(newmessage.receive_id,Integer.max(newmessage.message_id,car.get(newmessage.receive_id).receive_tot.get(newmessage.send_id)));
		    }
		    
		    s="select * from task";
		    stmt=conn.prepareStatement(s);
		    rs=stmt.executeQuery();
		    while(rs.next()){
		    	Task newtask=new Task();
		    	newtask.task_id=rs.getInt(1);
		    	newtask.car_id=rs.getInt(2);
		    	newtask.task_status=rs.getInt(3);
		    	newtask.task_cycle=rs.getBoolean(4);
		    	newtask.pos_id=rs.getInt(5);
		    	newtask.wait_t=rs.getInt(6);
		    	task.add(newtask);
		    }
		    
		    s="select * from pos";
		    stmt=conn.prepareStatement(s);
		    rs=stmt.executeQuery();
		    while(rs.next()){
		    	Pos newpos=new Pos();
		    	newpos.pos_id=rs.getInt(1);
		    	newpos.pos_x=rs.getFloat(2);
		    	newpos.pos_y=rs.getFloat(3);
		    	newpos.pos_wait_num=rs.getInt(4);
		    	pos.add(newpos);
		    }
		    
		    stmt.close();
		    rs.close();
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
%>
</head>

<body>

<h1>蓝车操作平台</h1>

<h2>小车位置与障碍物分布模拟图</h2>
<svg width="1500" height="600"> 
<rect x="0" y="0"  width="1500" height="600" style="fill:rgb(200,200,200);"></rect> 
<%
  int ox=750,oy=300;
  for (int i=1;i<car.size();i++){
	  if (car.get(i).status){
		  %>
		    <rect x=<%=ox-car.get(i).y-Car.w/2%> y=<%=oy-car.get(i).x-Car.h/2%> width=<%=Car.w%> height=<%=Car.h%> transform="rotate(<%=-car.get(i).dir%>,<%=ox+-car.get(i).y%>,<%=oy-car.get(i).x%>)" style="fill:blue"></rect>
		  <%
	  }
  }
  for (int i=0;i<obstacle.size();i++){
	  %>
	    <circle cx=<%=ox-obstacle.get(i).y%> cy=<%=oy-obstacle.get(i).x%> r="1" fill="red"></circle>
	  <%
  }
%>
</svg>  


<table>
<tr>
<td>小车总数：<%=car.size()-1 %></td>
</tr>
<tr>
<td>小车编号</td>
<td>小车状态</td>
<td>小车位置(巡逻点编号)</td>
<td>小车位置X(单位:cm)</td>
<td>小车位置Y(单位:cm)</td>
<td>小车朝向(单位：°)</td>
</tr>
<%for (int i=1;i<car.size();i++){ %>
<tr>
<td><%=car.get(i).car_id %></td>
<td>
<%
boolean status=car.get(i).status; 
if (status) {%>已连接<%}else {%>未连接<%} %>
</td>
<td><%=car.get(i).pos_id %></td>
<td><%=car.get(i).x %></td>
<td><%=car.get(i).y %></td>
<td><%=car.get(i).dir %></td>
</tr>
<%} %>
</table>


<h2>直接通讯</h2>

<table>
<tr>
<td>接收的消息</td>
</tr>
<tr>
<td>发送方编号</td>
<td>消息编号</td>
<td>消息</td>
</tr>

<%
  for (int i=0;i<message.size();i++){
	  if (message.get(i).receive_id==0&&message.get(i).message_id!=-1){
		  %>
		  <tr>
		  <td><%=message.get(i).send_id %></td>
		  <td><%=message.get(i).message_id %></td>
		  <td><%=message.get(i).message %></td>
		  </tr>
		  <%
	  }
  }
%>

</table>



<table>
<tr>
<td>发送的消息</td>
</tr>
<tr>
<td>发送方编号</td>
<td>接收方编号</td>
<td>消息编号</td>
<td>消息状态</td>
<td>消息</td>
</tr>
<%
  for (int i=0;i<message.size();i++){
	  if (message.get(i).send_id==0){
		  %>
		  <tr>
		  <td><%=message.get(i).send_id %></td>
		  <td><%=message.get(i).receive_id %></td>
		  <td><%=message.get(i).message_id %></td>
		  <td>
		  <%
		    switch(message.get(i).status){
		    case 0:
		    {
		    	%>未发送<%
		    	break;
		    }
		    case 1:
		    {
		    	%>已发送，未确认<%
		    	break;
		    }
		    case 2:
		    {
		    	%>已发送，已确认<%
		    	break;
		    }
		    }
		  %>
		  </td>
		  <td><%=message.get(i).message %></td>
		  </tr>
		  <%
	  }
  }
%>
</table>





<form action="task_restore.jsp?type=0" method="post">
<input type="hidden" name="send_id" value=0>
接收方小车编号:
<select name="receive_id">
<%
  for (int i=1;i<car.size();i++){
	  if (car.get(i).status){
		  %><option value=<%=car.get(i).car_id %>><%=car.get(i).car_id %></option><%
	  }
  }
%>
</select><br>
<input type="text" name="message" value=""><br>
<input type="submit" value="确认发送"><br>
</form>

<h2>快速操作</h2>
<%
  for (int i=0;i<car.size();i++){
	  if (car.get(i).status){
		  %>
		           小车编号:<%=car.get(i).car_id%><br>
		    <form action="task_restore.jsp?type=2&car_id=<%=car.get(i).car_id%>&operation=0" method="post">
		    <button>前进</button>
		    <input type="text" name="d" value=20 onkeypress="return event.keyCode>=48&&event.keyCode<=57">(单位:cm)
		    </form><br>
		    <form action="task_restore.jsp?type=2&car_id=<%=car.get(i).car_id%>&operation=1" method="post">
		    <button>后退</button>
		    <input type="text" name="d" value=20 onkeypress="return event.keyCode>=48&&event.keyCode<=57">(单位:cm)
		    </form><br>
		    <form action="task_restore.jsp?type=2&car_id=<%=car.get(i).car_id%>&operation=2" method="post">
		    <button>左转</button>
		    <input type="text" name="d" value=20 onkeypress="return event.keyCode>=48&&event.keyCode<=57">(单位:°)
		    </form><br>
		    <form action="task_restore.jsp?type=2&car_id=<%=car.get(i).car_id%>&operation=3" method="post">
		    <button>右转</button>
		    <input type="text" name="d" value=20 onkeypress="return event.keyCode>=48&&event.keyCode<=57">(单位:°)
		    </form><br>
		    <form action="task_restore.jsp?type=5&car_id=<%=car.get(i).car_id%>&operation=3" method="post">
		    <button>快速绘制（障碍物分布图）</button>
		    </form><br>
		    <h2>小车详细参数</h2>
		    <form action="task_restore.jsp?type=3&car_id=<%=car.get(i).car_id%>&operation=3" method="post">
		    <button>获取最新参数</button>
		    </form>
		    <%if (car.get(i).wait_update&&car.get(i).update_status==2){ %>
		    <p>更新中...</p><br>
		    <%}else if (car.get(i).wait_update&&car.get(i).update_status==1){%>
		    <p>更新完成</p><br>
		    <%
		    car.get(i).update_status=2;
		    car.get(i).wait_update=false;
		    }
		    %>
		    <form action="task_restore.jsp?type=4&car_id=<%=car.get(i).car_id%>&operation=3" method="post">
                                左电机参数(1-255)<input type="text" name="left_motor" value=<%=car.get(i).left_motor %> onkeypress="return event.keyCode>=48&&event.keyCode<=57"><br>
                                右电机参数(1-255)<input type="text" name="right_motor" value=<%=car.get(i).right_motor %> onkeypress="return event.keyCode>=48&&event.keyCode<=57"><br>
                                每秒转动角度(以90°为标准)<input type="text" name="rotate_per_sec" value=<%=car.get(i).rotate_per_sec %> onkeypress="return event.keyCode>=48&&event.keyCode<=57"><br>
                                每秒前进距离(cm)<input type="text" name="move_per_sec" value=<%=car.get(i).move_per_sec %> onkeypress="return event.keyCode>=48&&event.keyCode<=57"><br>
            <select name="auto_report_obstacle">
            <option value="true">开启</option>
            <%if (car.get(i).auto_report_obstacle){%>
            <option value="false">关闭</option>
            <%}else{ %>
            <option value="false" selected="selected">关闭</option>
            <%} %>
            
            </select>                    
                                每秒最多报告的数据个数（障碍物）<input type="text" name="maxreportpersec_obstacle" value=<%=car.get(i).maxreportpersec_obstacle %> onkeypress="return event.keyCode>=48&&event.keyCode<=57"><br>
            <select name="auto_report_sensor">
            <option value="true" >开启</option>
            <%if (car.get(i).auto_report_sensor){%>
            <option value="false">关闭</option>
            <%}else{ %>
            <option value="false" selected="selected">关闭</option>
            <%} %>
            </select>                    
                                每秒最多报告的数据个数（传感器）<input type="text" name="maxreportpersec_sensor" value=<%=car.get(i).maxreportpersec_sensor %> onkeypress="return event.keyCode>=48&&event.keyCode<=57"><br>
		    <input type="submit" value="上传参数">
		    </form>
		    <%if (car.get(i).update_status==0){ %>
		    <p>上传中</p><br>
		    <%} %>
		  <%
	  }
  }
%>

<h2>烧写程序(Arduino-Based Car Language)</h2>

<h2>巡逻系统</h2>

<table>
<tr>
<td>巡逻点总数:<%=pos.size() %></td>
</tr>
<tr>
<td>巡逻点编号</td>
<td>巡逻点位置(x)</td>
<td>巡逻点位置(y)</td>
<td>当前停留小车数量</td>
</tr>
<%for (int i=0;i<pos.size();i++){ %>
<tr>
<td><%=pos.get(i).pos_id %></td>
<td><%=pos.get(i).pos_x %></td>
<td><%=pos.get(i).pos_y %></td>
<td><%=pos.get(i).pos_wait_num %></td>
</tr>
<%} %>
</table>


<table>
<tr>
<td>任务总数:<%=task.size() %></td>
</tr>
<tr>
<td>任务编号</td>
<td>小车编号</td>
<td>任务状态</td>
<td>是否循环</td>
<td>位置(巡逻点编号)</td>
<td>等待时长</td>
</tr>
<%for (int i=0;i<task.size();i++){ %>
<tr>
<td><%=task.get(i).task_id %></td>
<td><%=task.get(i).car_id %></td>
<td><%=task.get(i).task_status %></td>
<td><%=task.get(i).task_cycle %></td>
<td><%=task.get(i).pos_id %></td>
<td><%=task.get(i).wait_t %></td>
</tr>
<%} %>
</table>

<form action="task_restore.jsp?type=1" method="post">
<input type="hidden" name="task_id" value=<%=task.size()+1 %>>
小车编号:
<select name="car_id">
<%for (int i=1;i<car.size();i++){ %>
<option value=<%=i %>><%=i %></option>
<%} %>
</select><br>
巡逻点编号:
<select name="pos_id">
<%for (int i=1;i<=pos.size();i++){ %>
<option value=<%=i %>><%=i %></option>
<%} %>
</select><br>
到达巡逻点后停留时间:
<input type="text" name="wait_t" value=0><br>
任务类型：
<select name="task_cycle">
<option value="true">循环执行</option>
<option value="false">单次执行</option>
</select><br>
<input type="submit" value="确认添加"><br>
</form>


</body>
</html>
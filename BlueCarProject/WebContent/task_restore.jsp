<%@page import="java.util.ArrayList"%>
<%@page import="projectclass.*"%>
<%@page import="java.sql.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>任务存储</title>

<%
int type=Integer.parseInt(request.getParameter("type"));
ArrayList<Car> car=new ArrayList<Car>();
Car user=new Car();
user.car_id=0;
car.add(user);
ArrayList<Message> message=new ArrayList<Message>();
ArrayList<HistoryPoint> historypoint=new ArrayList<HistoryPoint>();

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
    PreparedStatement stmt=null;
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
    	car.get(newmessage.receive_id).receive_tot.set(newmessage.send_id,Integer.max(newmessage.message_id,car.get(newmessage.receive_id).receive_tot.get(newmessage.send_id)));
    	//System.out.println("original receive_tot:"+car.get(newmessage.receive_id)
    	//System.out.println("receive_id"+newmessage.receive_id);
    	//System.out.println("message_id"+newmessage.message_id);
    }
    
    switch (type){
    case 0:
    {
    	Message newmessage=new Message();
    	newmessage.send_id=0;
    	newmessage.receive_id=Integer.parseInt(request.getParameter("receive_id"));
    	newmessage.message_id=car.get(newmessage.receive_id).receive_tot.get(newmessage.send_id)+1;
    	System.out.println(newmessage.message_id);
    	newmessage.status=0;
    	newmessage.message=request.getParameter("message");
    	s="insert into raw_send values(?,?,?,?,?)";
    	stmt=conn.prepareStatement(s);
    	stmt.setInt(1,newmessage.send_id);
    	stmt.setInt(2,newmessage.receive_id);
    	stmt.setInt(3,newmessage.message_id);
    	stmt.setInt(4,newmessage.status);
    	stmt.setString(5,newmessage.message);
    	stmt.executeUpdate();
    	%><script>alert("消息添加成功！")</script><%
    	break;
    }
    case 1:
    {
    	Task newtask=new Task();
    	newtask.task_id=Integer.parseInt(request.getParameter("task_id"));
    	newtask.car_id=Integer.parseInt(request.getParameter("car_id"));
    	newtask.pos_id=Integer.parseInt(request.getParameter("pos_id"));
    	newtask.wait_t=Integer.parseInt(request.getParameter("wait_t"));
    	newtask.task_cycle=Boolean.parseBoolean(request.getParameter("task_cycle"));
    	
    	s="insert into task values (?,?,?,?,?,?)";
        stmt=conn.prepareStatement(s);
        stmt.setInt(1, newtask.task_id);
        stmt.setInt(2, newtask.car_id);
        stmt.setInt(3, newtask.task_status);
        stmt.setBoolean(4, newtask.task_cycle);
        stmt.setInt(5, newtask.pos_id);
        stmt.setInt(6, newtask.wait_t);
        stmt.executeUpdate();
        %><script>alert("任务添加成功！")</script><%
    	break;
    }
    case 2:
    {
    	int car_id=Integer.parseInt(request.getParameter("car_id"));
    	int operation=Integer.parseInt(request.getParameter("operation"));
    	int d=Integer.parseInt(request.getParameter("d"));
    	s="insert into raw_send values (?,?,?,?,?)";
    	stmt=conn.prepareStatement(s);
    	stmt.setInt(1,0);
    	stmt.setInt(2,car_id);
    	stmt.setInt(3,car.get(car_id).receive_tot.get(0)+1);
    	stmt.setInt(4,0);
    	String order="DU";
    	switch(operation){
    	case 0:
    	{
    		order+="0uS";
    		order+=d;
    		order+="s";
    		break;
    	}
    	case 1:
    	{
    		order+="0uS";
    		order+=-d;
    		order+="s";
    		break;
    	}
    	case 2:
    	{
    		order+="1uZ";
    		order+=d;
    		order+="z";
    		break;
    	}
    	case 3:
    	{
    		order+="1uZ";
    		order+=-d;
    		order+="z";
    		break;
    	}
    	}
    	order+="dQU1uq";
    	
    	stmt.setString(5,order);
    	stmt.executeUpdate();
    	break;
    }
    case 3:
    {
    	int car_id=Integer.parseInt(request.getParameter("car_id"));
    	s="insert into raw_send values(?,?,?,?,?)";
    	stmt=conn.prepareStatement(s);
    	stmt.setInt(1,0);
    	stmt.setInt(2,car_id);
    	stmt.setInt(3,car.get(car_id).receive_tot.get(0)+1);
    	stmt.setInt(4,0);
    	stmt.setString(5,"QU2uq");
    	stmt.executeUpdate();
    	car.get(car_id).wait_update=true;
    	break;
    }
    case 4:
    {
    	s="select update_status from car_detail where car_id=?";
    	stmt=conn.prepareStatement(s);
    	stmt.setInt(1,Integer.parseInt(request.getParameter("car_id")));
    	rs=stmt.executeQuery();
    	boolean permit=true;
    	while (rs.next()){
    		if (rs.getInt(1)==0){
    			permit=false;
    			break;
    		}
    	}
    	if (!permit){
    		%><script>alert("当前不可上传，请等待先前的参数完成！")</script><%
    		break;
    	}
    	int car_id=Integer.parseInt(request.getParameter("car_id"));
    	s="update car_detail set update_status=0,left_motor=?,right_motor=?,rotate_per_sec=?,move_per_sec=?,auto_report_obstacle=?,maxreportpersec_obstacle=?,auto_report_sensor=?,maxreportpersec_sensor=? where car_id=?";
    	stmt=conn.prepareStatement(s);
    	stmt.setInt(1,Integer.parseInt(request.getParameter("left_motor")));
    	stmt.setInt(2,Integer.parseInt(request.getParameter("right_motor")));
    	stmt.setFloat(3,Float.parseFloat(request.getParameter("rotate_per_sec")));
    	stmt.setFloat(4,Float.parseFloat(request.getParameter("move_per_sec")));
    	stmt.setBoolean(5,Boolean.parseBoolean(request.getParameter("auto_report_obstacle")));
    	stmt.setInt(6,Integer.parseInt(request.getParameter("maxreportpersec_obstacle")));
    	stmt.setBoolean(7,Boolean.parseBoolean(request.getParameter("auto_report_sensor")));
    	stmt.setInt(8,Integer.parseInt(request.getParameter("maxreportpersec_sensor")));
    	stmt.setInt(9,Integer.parseInt(request.getParameter("car_id")));
    	stmt.executeUpdate();
    	break;
    }
    case 5:
    {
    	int car_id=Integer.parseInt(request.getParameter("car_id"));
    	s="insert into raw_send values(?,?,?,?,?)";
    	stmt=conn.prepareStatement(s);
    	stmt.setInt(1,0);
    	stmt.setInt(2,car_id);
    	stmt.setInt(3,car.get(car_id).receive_tot.get(0)+1);
    	stmt.setInt(4,0);
    	stmt.setString(5,"Hh");
    	stmt.executeUpdate();
    	break;
    }
    default:
    {
    	break;
    }
    }
    
    
    
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

%>

<meta http-equiv="refresh" content="0;url=index.jsp">
</head>
<body>

</body>
</html>
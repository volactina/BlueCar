package javapkg;

import java.io.IOException;

/*
 * ����Ӧ�û����runtime error
 * 0 brake��wait -1��brake ����0��wait��ʱ��
 * 1 ǰ��/���� ������ʾ����
 * 2 ��ת ������ʾ��ת/��ת ����ʾ��ʱ��
 */
public class SimCar {
	int carid;
	int posx;
	int posy;
	int posdir;
	int timesinceboot;
	boolean debugmode=false;
	SimField simfield=new SimField();
	ErrorType newerror=new ErrorType();
	ABC_compiler compiler=null;
	public SimCar(ABC_compiler c) {
		posx=0;
		posy=0;
		posdir=0;
		timesinceboot=0;
		compiler=c;
	}
	public SimCar(int x,int y,int z) {
		posx=x;
		posy=y;
		posdir=z;
		timesinceboot=0;
	}
	void Move(int dis) throws IOException {
		if (dis==0) return;
		System.out.println(carid+" "+1+" "+dis);
		posx+=dis*Math.cos(Math.toRadians(posdir));
		posy+=dis*Math.sin(Math.toRadians(posdir));
		Operation newop=new Operation(carid,1,dis);
		compiler.sendMsg(newop);
		Simulator.oplock.lock();
		Simulator.operations.add(newop);
		Simulator.nowopnum++;
		Simulator.oplock.unlock();
		return;
	}
	void Rotate(int dir) throws IOException {
		System.out.println(carid+" "+2+" "+dir);
		posdir=(posdir+dir)%360;
		Operation newop=new Operation(carid,2,dir);
		compiler.sendMsg(newop);
		Simulator.oplock.lock();
		Simulator.operations.add(newop);
		Simulator.nowopnum++;
		Simulator.oplock.unlock();
		return;
	}
	void RotateTo(int dir) throws IOException {
		if (posdir==dir) return;
		  if ((posdir-dir+360)%360<180){
		    Rotate((posdir-dir+360)%360);
		  }else{
		    Rotate(-(dir-posdir+360)%360);
		  }
		return;
	}
	void MoveTo(int x,int y) throws IOException {
		double vx=x-posx,vy=y-posy;
		int d=(int)Math.toDegrees((Math.atan(Math.abs(vy)/Math.abs(vx))));
		  if (vx>0&&vy>0){
		    RotateTo(d);
		  }
		  if (vx<0&&vy>0){
		    RotateTo(180-d);
		  }
		  if (vx<0&&vy<0){
		    RotateTo(180+d);
		  }
		  if (vx>0&&vy<0){
		    RotateTo(360-d);
		  }
		Move((int)Math.sqrt((x-posx)*(x-posx)+(y-posy)*(y-posy)));
		return;
	}
	void Brake() throws IOException {
		System.out.println(carid+" 0 -1");
		Operation newop=new Operation(carid,0,-1);
		compiler.sendMsg(newop);
		Simulator.oplock.lock();
		Simulator.operations.add(newop);
		Simulator.nowopnum++;
		Simulator.oplock.unlock();
		return;
	}
	void Wait(int t) throws IOException {
		System.out.println(carid+" 0 "+t);
		Operation newop=new Operation(carid,0,t);
		compiler.sendMsg(newop);
		Simulator.oplock.lock();
		Simulator.operations.add(newop);
		Simulator.nowopnum++;
		Simulator.oplock.unlock();
		return;
	}
	int GetObstacle() {
		double ans=10000000;
		for (int i=0;i<simfield.obstaclenum;i++) {
			double obsx=simfield.simobstacles.get(i).centerx;
			double obsy=simfield.simobstacles.get(i).centery;
			double obsr=simfield.simobstacles.get(i).centerr;
			double obs_dis=Math.sqrt((posx-obsx)*(posx-obsx)+(posy-obsy)*(posy-obsy))-obsr;
			ans=Math.min(ans, obs_dis);
		}
		return (int)ans;
	}
	int GetSensor() {
		double ans=0;
		for (int i=0;i<simfield.firenum;i++) {
			double firex=simfield.simfires.get(i).centerx;
			double firey=simfield.simfires.get(i).centery;
			double fire_dis=Math.sqrt((posx-firex)*(posx-firex)+(posy-firey)*(posy-firey));
			double temperature=500.0/(0.01+fire_dis);
			ans=Math.max(ans, temperature);
		}
		return (int)ans;
	}
	void handle_code(String code) throws IOException {
		if (debugmode) {
			System.out.println("debug-code:"+code);
		}
		code=code.substring(code.indexOf("P")+1, code.indexOf("p"));
		if (debugmode) {
			System.out.println("debug-code:"+code);
		}
		int fromid=Integer.parseInt(code.substring(code.indexOf("A")+1,code.indexOf("a")));
		int toid=Integer.parseInt(code.substring(code.indexOf("B")+1,code.indexOf("b")));
		carid=toid;
		int confirmid=Integer.parseInt(code.substring(code.indexOf("C")+1,code.indexOf("c")));
		if (debugmode) {
			System.out.println(fromid+" "+toid+" "+confirmid);
		}
		code=code.substring(code.indexOf("c")+1);
		if (debugmode) {
			System.out.println(code);
		}
		while(code.length()>0) {
			code=carry_out(code);
			if (newerror.errortype==3) return;
		}
		
	}
	
	String carry_out(String code) throws IOException {
		if (code.length()==0) return code;
		//if (Integer.toString(Integer.parseInt(code))==code) return Integer.toString(Integer.parseInt(code));
		switch(code.charAt(0)) {
		case 'D':
		{
			int op=Integer.parseInt(code.substring(code.indexOf("U")+1,code.indexOf("u")));
			switch(op) {
			case 0:
			{
				Move(Integer.parseInt(carry_out(code.substring(code.indexOf("S")+1,code.indexOf("s")))));
				if (newerror.errortype==3) return "";
				break;
			}
			case 1:
			{
				Rotate(Integer.parseInt(carry_out(code.substring(code.indexOf("Z")+1,code.indexOf("z")))));
				if (newerror.errortype==3) return "";
				break;
			}
			case 2:
			{
				MoveTo(Integer.parseInt(carry_out(code.substring(code.indexOf("X")+1,code.indexOf("x")))),Integer.parseInt(carry_out(code.substring(code.indexOf("Y")+1,code.indexOf("y")))));
				if (newerror.errortype==3) return "";
				break;
			}
			case 3:
			{
				Brake();
				break;
			}
			case 4:
			{
				int t=Integer.parseInt(carry_out(code.substring(code.indexOf("T")+1,code.indexOf("t"))));
				if (newerror.errortype==3) return "";
				Wait(t);
				break;
			}
			}
			code=code.substring(code.indexOf("d")+1);
			break;
		}
		case 'E':
		{
			if (debugmode) {
				System.out.println("enter E");
			}
			int type=Integer.parseInt(code.substring(code.indexOf("T")+1,code.indexOf("t")));
			if (debugmode) {
				System.out.println("type:"+type);
			}
			switch(type) {
			case 0://д
			{
				break;
			}
			case 1://��
			{
				int type1=Integer.parseInt(code.substring(code.indexOf("U")+1,code.indexOf("u")));
				code=code.substring(code.indexOf("u")+1);
				if (debugmode) {
					System.out.println("type1:"+type1);
					System.out.println("code:"+code);
				}
				int type2=Integer.parseInt(code.substring(code.indexOf("U")+1,code.indexOf("u")));
				code=code.substring(code.indexOf("e")+1);
				if (debugmode) {
					System.out.println("type2:"+type2);
					System.out.println("code:"+code);
					System.out.println("codelen:"+code.length());
				}
				switch(type1) {
				case 0:
				{
					switch(type2) {
					case 1:
					{
						code=Integer.toString(posx);
						break;
					}
					case 2:
					{
						code=Integer.toString(posy);
						break;
					}
					case 3:
					{
						code=Integer.toString(posdir);
						break;
					}
					case 4:
					{
						code=Integer.toString(timesinceboot);
						break;
					}
					}
					break;
				}
				case 1:
				{
					switch(type2) {
					case 1:
					{
						code=Integer.toString(GetObstacle())+code;
						break;
					}
					case 2://�ϰ�������x
					{
						break;
					}
					case 3://�ϰ�������y
					{
						break;
					}
					}
					break;
				}
				case 2:
				{
					switch(type2) {
					case 1:
					{
						code=Integer.toString(GetSensor())+code;
						break;
					}
					default:
					{
						newerror.carid=carid;
						newerror.errortype=3;
						newerror.info="runtime error:car "+carid+" does not have sensor numbered "+type2+"!";
						compiler.sendErrorMsg(newerror);
						System.out.println(newerror.info);
						Simulator.errorlock.lock();
						Simulator.carerrors.add(newerror);
						Simulator.errorlock.unlock();
						return "0";
					}
					}
					break;
				}
				case 3:
				{
					break;
				}
				case 4:
				{
					break;
				}
				}
				break;
			}
			}
			break;
		}
		case 'I':
		{
			String condition=code.substring(code.indexOf("J")+1,code.indexOf("j"));
			String execute=code.substring(code.indexOf("j")+1,code.indexOf("i"));
			code=code.substring(code.indexOf("i")+1);
			String leftexpression=condition.substring(0,condition.indexOf("N"));
			int leftnum=Integer.parseInt(carry_out(leftexpression));
			if (newerror.errortype==3) return "";
			int relation=Integer.parseInt(condition.substring(condition.indexOf("N")+1,condition.indexOf("n")));
			String rightexpression=condition.substring(condition.indexOf("n")+1);
			int rightnum=Integer.parseInt(carry_out(rightexpression));
			if (newerror.errortype==3) return "";
			if (debugmode) {
				System.out.println("condition:"+condition);
				System.out.println("execute"+execute);
				System.out.println("othercode"+code);
				System.out.println("leftexpression"+leftexpression);
				System.out.println("rightexpression"+rightexpression);
			}
			boolean fit=false;
			switch(relation) {
			case 0:
			{
				if (leftnum==rightnum) {
					fit=true;
				}else {
					fit=false;
				}
				break;
			}
			case 1:
			{
				if (leftnum<rightnum) {
					fit=true;
				}else {
					fit=false;
				}
				break;
			}
			case 2:
			{
				if (leftnum>rightnum) {
					fit=true;
				}else {
					fit=false;
				}
				break;
			}
			case 3:
			{
				if (leftnum<=rightnum) {
					fit=true;
				}else {
					fit=false;
				}
				break;
			}
			case 4:
			{
				if (leftnum>=rightnum) {
					fit=true;
				}else {
					fit=false;
				}
				break;
			}
			}
			if (fit) {
				System.out.println("fit");
				code=execute+code;
			}
			break;
		}
		case 'F':
		{
			String condition=code.substring(code.indexOf("J")+1,code.indexOf("j"));
			String execute=code.substring(code.indexOf("j")+1,code.indexOf("f"));
			String othercode=code.substring(code.indexOf("f")+1);
			String leftexpression=condition.substring(0,condition.indexOf("N"));
			String rightexpression=condition.substring(condition.indexOf("n")+1);
			if (debugmode) {
				System.out.println("condition:"+condition);
				System.out.println("execute"+execute);
				System.out.println("othercode"+othercode);
				System.out.println("leftexpression"+leftexpression);
				System.out.println("rightexpression"+rightexpression);
			}
			int leftnum=Integer.parseInt(carry_out(leftexpression));
			if (newerror.errortype==3) return "";
			if (debugmode) {
				System.out.println("leftnum:"+leftnum);
			}
			int relation=Integer.parseInt(condition.substring(condition.indexOf("N")+1,condition.indexOf("n")));
			int rightnum=Integer.parseInt(carry_out(rightexpression));
			if (newerror.errortype==3) return "";
			boolean fit=false;
			switch(relation) {
			case 0:
			{
				if (leftnum==rightnum) {
					fit=true;
				}else {
					fit=false;
				}
				break;
			}
			case 1:
			{
				if (leftnum<rightnum) {
					fit=true;
				}else {
					fit=false;
				}
				break;
			}
			case 2:
			{
				if (leftnum>rightnum) {
					fit=true;
				}else {
					fit=false;
				}
				break;
			}
			case 3:
			{
				if (leftnum<=rightnum) {
					fit=true;
				}else {
					fit=false;
				}
				break;
			}
			case 4:
			{
				if (leftnum>=rightnum) {
					fit=true;
				}else {
					fit=false;
				}
				break;
			}
			}
			if (fit) {
				code=execute+code;
			}else {
				code=othercode;
			}
			break;
		}
		case 'S':
		{
			int num=Integer.parseInt(code.substring(code.indexOf("S")+1,code.indexOf("s")));
			code=code.substring(code.indexOf("s")+1);
			code=Integer.toString(num)+code;
			break;
		}
		}
		return code;
	}
}

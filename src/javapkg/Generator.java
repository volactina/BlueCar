package javapkg;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Generator {
    /*
     * main����Ϊ���Ե���������ʹ�õĻ�ֱ�ӵ���generate_code,���÷�ʽ�ο�main����
     */
	static boolean debugmode=false;
	static Scanner generator_in=null;
	public static void main(String[] args) {
		generator_in=new Scanner(System.in);
		ArrayList<Sentence> sentences=new ArrayList<Sentence>();
		while (generator_in.hasNext()) {
			/*Sentence newsentence=new Sentence();
			newsentence.ObjectId=in.nextInt();
			newsentence.Objectnum=in.nextInt();
			newsentence.FuncId=in.nextInt();
			newsentence.varnum=in.nextInt();
			for (int i=0;i<newsentence.varnum;i++) {
				newsentence.Funcvar.add(in.nextInt());
			}
			sentences.add(newsentence);*/
			sentences.add(read_sentence());
		}
		generator_in.close();
		/*ABC_compiler compiler=new ABC_compiler();*/
//		generate_code(/*compiler,*/sentences);
	}
	
	static Sentence read_sentence() {
		Sentence newsentence=new Sentence();
		newsentence.ObjectId=generator_in.nextInt();
		switch(newsentence.ObjectId) {
		case 0://ֱ����
		{
			newsentence.Funcvar.add(generator_in.nextInt());
			break;
		}
		case 1:case 2://Car��Team
		{
			newsentence.Objectnum=generator_in.nextInt();
			newsentence.FuncId=generator_in.nextInt();
			newsentence.varnum=generator_in.nextInt();
			for (int i=0;i<newsentence.varnum;i++) {
				newsentence.Funcvar.add(generator_in.nextInt());
			}
			break;
		}
		case 3:case 4://if��while
		{
			newsentence.left=new Sentence();
			newsentence.left=read_sentence();
			newsentence.relation=generator_in.nextInt();
			newsentence.right=new Sentence();
			newsentence.right=read_sentence();
			newsentence.executenum=generator_in.nextInt();
			newsentence.execute=new ArrayList<Sentence>();
			for (int i=0;i<newsentence.executenum;i++) {
				newsentence.execute.add(read_sentence());
			}
			break;
		}
		default:
		{
			System.out.println("Object not exists error!");
			break;
		}
		}
		return newsentence;
	}
	//�������Sentence�ṹ��ArrayList����ӡ����Ӧ��Ŀ����룬��ǰʵ�ֵ����ݷ�Χ�ο�Sentence.java
	/*
	 * ���ش���˵���� ErrorType 
	 * ����errortype=0��ʾû�д������
	 *     errortype=1��ʾwarning
	 *     errortype=2��ʾerror
	 */
	
	
	static ErrorType generate_code(ABC_compiler compiler,ArrayList<Sentence> sentences) throws IOException {
		int MAXN=10;
		String[] output=new String[MAXN+1];
		//��ŵ�ǰTeam�ļ���
		ArrayList<Set<Integer>> team=new ArrayList<Set<Integer>>();
		ErrorType errortype=new ErrorType();
		errortype.errortype=0;
		for (int i=0;i<=MAXN;i++) {
			Set<Integer> newset=new HashSet<Integer>();
			team.add(newset);
		}
		for (int i=1;i<=MAXN;i++) {
			output[i]="PA0aB";
			output[i]+=Integer.valueOf(i).toString();
			output[i]+="bC0c";
		}
		System.out.println(sentences.size());
		for (int i=0;i<sentences.size();i++) {
			int ObjectId=sentences.get(i).ObjectId;
			int Objectnum=sentences.get(i).Objectnum;
			int FuncId=sentences.get(i).FuncId;
			int varnum=sentences.get(i).varnum;
			if (debugmode) {
				System.out.println("ObjectId:"+ObjectId);
				System.out.println("Objectnum:"+Objectnum);
				System.out.println("FuncId:"+FuncId);
				System.out.println("varnum:"+varnum);
			}
			if (ObjectId==1||ObjectId==2) {
				if (varnum<Sentence.funcvarnummin[FuncId]) {
					errortype.errortype=2;
					errortype.info="error:too few arguments for function "+Sentence.funcname[FuncId]+" which needs at least "+Sentence.funcvarnummin[FuncId]+" variables while you only have "+varnum+" variables!";
					System.out.println(errortype.info);
					return errortype;
				}
				if (varnum>Sentence.funcvarnummax[FuncId]) {
					errortype.errortype=1;
					errortype.info="warning:too many arguments for function "+Sentence.funcname[FuncId]+" which needs at most "+Sentence.funcvarnummax[FuncId]+" variables while you give "+varnum+" variables!";
					System.out.println(errortype.info);
				}
			}
			ArrayList<Integer> Funcvar=new ArrayList<Integer>();
			for (int j=0;j<varnum;j++) {
					Funcvar.add(sentences.get(i).Funcvar.get(j));
			}
			switch(ObjectId) {
			//1 Car
			case 1:
			{
				output[Objectnum]+=generate_string(ObjectId,FuncId,Funcvar,errortype);
				if (errortype.errortype==2) return errortype;
				break;
			}
			//2 Team
			case 2:
			{
				if (FuncId==11) {
					//Team��add
					//������δ��
					for (int j=0;j<varnum;j++) {
						int cnt=0;
						team.get(Objectnum).add(Funcvar.get(j));
						for (int jj=0;jj<team.size();jj++) {
							if (team.get(jj).contains(Funcvar.get(j))) {
								cnt++;
							}
						}
						if (cnt>=2) {
							errortype.errortype=1;
							errortype.info="warning:car "+Funcvar.get(j)+" is added to more than one team";
							System.out.println(errortype.info);
						}
					}
				}else if (FuncId==12) {
					//Team��remove
					//������δ��
					for (int j=0;j<varnum;j++) {
						if (!team.get(Objectnum).contains(Funcvar.get(j))) {
							errortype.errortype=2;
							errortype.info="error:cannot delete "+Funcvar.get(j)+" from team "+Objectnum+" for it does not exist";
							System.out.println(errortype.info);
							return errortype;
						}
						team.get(Objectnum).remove(Funcvar.get(j));
					}
				}else {
					Iterator<Integer> it=team.get(Objectnum).iterator();
					while (it.hasNext()) {
						int carid=it.next();
						//(δд)FuncId����Car��Func���л��������������ô����
						output[carid]+=generate_string(1,FuncId,Funcvar,errortype);
						if (errortype.errortype==2) return errortype;
					}
				}
				break;
			}
			// if
			case 3:
			{
				int object_in_if=sentences.get(i).left.Objectnum;
				boolean[] object_in_execute=new boolean[11];
				for (int ii=0;ii<11;ii++) object_in_execute[ii]=false;
				for (int j=0;j<sentences.get(i).executenum;j++) {
					object_in_execute[sentences.get(i).execute.get(j).Objectnum]=true;
					if (sentences.get(i).execute.get(j).Objectnum!=object_in_if) {
						errortype.errortype=1;//���ô�������Ϊwarning
						errortype.info="warning:Car "+sentences.get(i).execute.get(j).Objectnum+" need information from Car "+object_in_if;
						System.out.println(errortype.info);
					}
				}
				//�����и���Ϣ��Ҫ�󣬼�������ִ�����еĶ������Ҫ�ܻ�ȡ�����������Ϣ
				//��������ʱ�����������Ǳ������ʱ���󶼱�warning
				for (int j=0;j<11;j++) {
					if (object_in_execute[j]) {
						output[j]+="IJ";
						output[j]+=generate_string(sentences.get(i).left.ObjectId,sentences.get(i).left.FuncId,sentences.get(i).left.Funcvar,errortype);
						if (errortype.errortype==2) return errortype;
						output[j]+="N";
						output[j]+=Integer.valueOf(sentences.get(i).relation).toString();
						output[j]+="n";
						output[j]+=generate_string(sentences.get(i).right.ObjectId,sentences.get(i).right.FuncId,sentences.get(i).right.Funcvar,errortype);
						if (errortype.errortype==2) return errortype;
						output[j]+="j";
						for (int jj=0;jj<sentences.get(i).executenum;jj++) {
							output[j]+=generate_string(sentences.get(i).execute.get(jj).ObjectId,sentences.get(i).execute.get(jj).FuncId,sentences.get(i).execute.get(jj).Funcvar,errortype);
							if (errortype.errortype==2) return errortype;
						}
						output[j]+="i";
					}
				}
				break;
			}
			//while
			case 4:
			{
				int object_in_if=sentences.get(i).left.Objectnum;
				boolean[] object_in_execute=new boolean[11];
				for (int ii=0;ii<11;ii++) object_in_execute[ii]=false;
				for (int j=0;j<sentences.get(i).executenum;j++) {
					object_in_execute[sentences.get(i).execute.get(j).Objectnum]=true;
					if (sentences.get(i).execute.get(j).Objectnum!=object_in_if) {
						errortype.errortype=1;//���ô�������Ϊwarning
						errortype.info="warning:Car "+sentences.get(i).execute.get(j).Objectnum+" need information from Car "+object_in_if;
						System.out.println(errortype.info);
					}
				}
				//�����и���Ϣ��Ҫ�󣬼�������ִ�����еĶ������Ҫ�ܻ�ȡ�����������Ϣ
				//��������ʱ�����������Ǳ������ʱ���󶼱�warning
				for (int j=0;j<11;j++) {
					if (object_in_execute[j]) {
						output[j]+="FJ";
						output[j]+=generate_string(sentences.get(i).left.ObjectId,sentences.get(i).left.FuncId,sentences.get(i).left.Funcvar,errortype);
						if (errortype.errortype==2) return errortype;
						output[j]+="N";
						output[j]+=Integer.valueOf(sentences.get(i).relation).toString();
						output[j]+="n";
						output[j]+=generate_string(sentences.get(i).right.ObjectId,sentences.get(i).right.FuncId,sentences.get(i).right.Funcvar,errortype);
						if (errortype.errortype==2) return errortype;
						output[j]+="j";
						for (int jj=0;jj<sentences.get(i).executenum;jj++) {
							output[j]+=generate_string(sentences.get(i).execute.get(jj).ObjectId,sentences.get(i).execute.get(jj).FuncId,sentences.get(i).execute.get(jj).Funcvar,errortype);
							if (errortype.errortype==2) return errortype;
						}
						output[j]+="f";
					}
				}
				break;
			}
			case 5:
			{
				break;
			}
			}
		}
		ArrayList<String> code=new ArrayList<String>();
		for (int i=1;i<=MAXN;i++) {
			output[i]+="p";
			//System.out.print("car"+i+"code:");
			System.out.println(output[i]);
			//����SimCar
			code.add(output[i]);
		}
		if (errortype.errortype!=2) {
			compiler.onGenSuccess(code);
			if (errortype.errortype==1) {
				compiler.sendErrorMsg(errortype);
			}
			//要真机运行请取消这条注释
			send_to_real_car(code);
		}
		if (errortype.errortype==2) {
			compiler.onGenFailure(errortype);
		}
		return errortype;
	}
	static String generate_string(int objid,int funcid,ArrayList<Integer> var,ErrorType errortype) {
		if (objid==1||objid==2) {
			if (var.size()<Sentence.funcvarnummin[funcid]) {
				errortype.errortype=2;
				errortype.info="error:too few arguments for function "+Sentence.funcname[funcid]+" which needs at least "+Sentence.funcvarnummin[funcid]+" variables while you only have "+var.size()+" variables!";
				System.out.println(errortype.info);
				return "";
			}
			if (var.size()>Sentence.funcvarnummax[funcid]) {
				errortype.errortype=1;
				errortype.info="warning:too many arguments for function "+Sentence.funcname[funcid]+" which needs at most "+Sentence.funcvarnummax[funcid]+" variables while you give "+var.size()+" variables!";
				System.out.println(errortype.info);
			}
		}
		String out="";
		switch(objid)
		{
		case 0://ֱ����
		{
			out+="S";
			out+=var.get(0).toString();
			out+="s";
			break;
		}
		case 1:
		{
			if (funcid>=1&&funcid<=6) {
				out+="D";
				switch(funcid) {
				case 1:case 2:
				{
					out+="U0uS";
					if (funcid==1) {
						out+=var.get(0).toString();
					}else {
						Integer temp=new Integer(-var.get(0));
						out+=temp.toString();
					}
					out+="s";
					break;
				}
				case 3:
				{
					out+="U1uZ";
					out+=var.get(0).toString();
					out+="z";
					break;
				}
				case 4:
				{
					out+="U2uX";
					out+=var.get(0).toString();
					out+="xY";
					out+=var.get(1).toString();
					out+="yZ";
					out+=var.get(2).toString();
					out+="z";
					break;
				}
				case 5:
				{
					out+="U3uSs";
					break;
				}
				case 6:
				{
					out+="U4uT";
					out+=var.get(0).toString();
					out+="t";
					break;
				}
				}
				out+="d";
			}else if (funcid==7) {//Car().Obstacle()
				out+="ET1tU1uU1ue";
			}else if (funcid==8) {//Car().x()
				out+="ET1tU0uU1ue";
			}else if (funcid==9) {//Car().y()
				out+="ET1tU0uU2ue";
			}else if (funcid==10) {//Car().dir()
				out+="ET1tU0uU3ue";
			}else if (funcid==13) {
				out+="ET1tU2uU";
				out+=var.get(0).toString();
				out+="ue";
			}else if (funcid==14) {
				
			}
			}
			
		}
		return out;
	}
	
	static void send_to_real_car(ArrayList<String> code){
		try{
		    Class.forName("com.mysql.jdbc.Driver");
		    System.out.println("娉ㄥ唽椹卞姩鎴愬姛!");
		}catch(ClassNotFoundException e1){
		    System.out.println("娉ㄥ唽椹卞姩澶辫触!");
		    e1.printStackTrace();
		    return;
		}

		String url="jdbc:mysql://localhost:3306/bluecarproject?useSSL=false";        
		Connection conn = null;
		try {
		    conn = DriverManager.getConnection(url, "root", "123456");
		    String s;
		    s="delete from car_code";
		    PreparedStatement stmt=conn.prepareStatement(s);
	    	stmt.executeUpdate();
	    	stmt.close();
		    for (int i=0;i<code.size();i++){
		    	String nowcode=code.get(i);
		    	int carid=Integer.parseInt(nowcode.substring(nowcode.indexOf("B")+1,nowcode.indexOf("b")));
		    	nowcode=nowcode.substring(nowcode.indexOf("c")+1,nowcode.indexOf("p"));
		    	s="insert into car_code values("+Integer.valueOf(carid).toString()+",0,\""+nowcode+"\")";
		    	stmt=conn.prepareStatement(s);
		    	stmt.executeUpdate();
		    	stmt.close();
		    }
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
	}

}

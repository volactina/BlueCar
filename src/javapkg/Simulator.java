package javapkg;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Simulator {
	public static int nowopnum=0;
	public static ArrayList<Operation> operations=new ArrayList<Operation>();
	public static ArrayList<ErrorType> carerrors=new ArrayList<ErrorType>();
	public static Lock oplock=new ReentrantLock();
	public static Lock errorlock=new ReentrantLock();
	public static boolean debugmode=false;
	public static void main(String[] args) {
		
		Scanner in=new Scanner(System.in);
		ArrayList<String> code=new ArrayList<String>();
		while(in.hasNext()) {
			code.add(in.next());
		}
		in.close();
//		ABC_compiler compiler;
//		simulate(compiler,code);
	}
	
	static void simulate(ABC_compiler compiler,ArrayList<String> code) throws IOException {
//		SimCar test=new SimCar();
//		test.handle_code(code.get(0));
		
		if (debugmode) {
			SimCar test=new SimCar(compiler);
			test.handle_code(code.get(0));
		}else {
			TestThread t=new TestThread(compiler);
			Thread[] ts=new Thread[10];
			for (int i=0;i<code.size();i++) {
				ts[i]=new Thread(t);
				ts[i].setName(code.get(i));
				ts[i].start();
			}
		}
		return;
	}
// �������᲻ͣ���2 1 10
//	PA0aB1bC0cp
//	PA0aB2bC0cFJET1tU1uU1ueR2rS100sjDU0uS10sdfDU3uSsdp
//	PA0aB3bC0cIJET1tU2uU1ueR2rS100sjDU2uX0xY0yZ0zdip
//	PA0aB4bC0cp
//	PA0aB5bC0cp
//	PA0aB6bC0cp
//	PA0aB7bC0cp
//	PA0aB8bC0cp
//	PA0aB9bC0cp
//	PA0aB10bC0cp
	
//	PA0aB1bC0cp
//	PA0aB2bC0cFJET1tU1uU1ueR2rS100sjDU0uS10sdfDU3uSsdp
//	PA0aB3bC0cIJET1tU2uU2ueR2rS100sjDU2uX0xY0yZ0zdip //����ᱨruntimeerror
//	PA0aB4bC0cp
//	PA0aB5bC0cp
//	PA0aB6bC0cp
//	PA0aB7bC0cp
//	PA0aB8bC0cp
//	PA0aB9bC0cp
//	PA0aB10bC0cp
	
public static class TestThread implements Runnable{
		public ABC_compiler compiler=null;
		public TestThread(ABC_compiler c) {
			compiler=c;
		}

		@Override
		public void run() {
			System.out.println("receive:"+Thread.currentThread().getName());
			SimCar car=new SimCar(compiler);
			try {
				car.handle_code(Thread.currentThread().getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

package javapkg;

public class Operation {
	public int carid;
	public int op;
	public int num;
	public Operation(int x,int y,int z) {
		carid=x;
		op=y;
		num=z;
	}

	public void display() {
		System.out.println("Operation display:");
		System.out.println("carId: " + carid);
		System.out.println("op: " + op);
		System.out.println("num: " + num);
	}
}

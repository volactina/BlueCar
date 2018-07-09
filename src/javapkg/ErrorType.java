package javapkg;

public class ErrorType {
	/*
	 * �������ͱ�
	 * 0 û�д���
	 * 1 warning
	 * 2 error
	 * 3 runtime_error
	 */
	public int carid;
	public int errortype;
	String info=new String();

	public ErrorType() {
	}

	public ErrorType(int errortype, String info) {
		this.errortype = errortype;
		this.info = info;
	}

	public void display() {
		System.out.println("Error Display:");
		System.out.println("Error Type: " + errortype);
		System.out.println("car id: " + carid);
		System.out.println("info: " + info);
	}
}

package javapkg;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * @author Cyril
 * @date 2018/6/17
 */
public class Message {

	/**
	 * 1 operation
	 * 2 error
	 * 3 code
	 * 4 tree
	 */
	@Expose
	private int msgType;

	private Operation oper;

	private ErrorType err;

	private ArrayList<String> code;

	@Expose
	private SimpleNode tree;

	public Message(Operation oper) {
		this.msgType = 1;
		this.oper = oper;
	}

	public Message(ErrorType err) {
		this.msgType = 2;
		this.err = err;
	}

	public Message(ArrayList<String> code) {
		this.msgType = 3;
		this.code = code;
	}

	public Message(SimpleNode tree) {
		this.msgType = 4;
		this.tree = tree;
	}

	public void display() {
		System.out.println("Message Display:");
		System.out.println("msgType: " + msgType);
		if (msgType == 1) {
			oper.display();
		} else if (msgType == 2) {
			err.display();
		} else if (msgType == 3) {
			System.out.println(code);
		} else {
			tree.dump("");
		}
	}
}

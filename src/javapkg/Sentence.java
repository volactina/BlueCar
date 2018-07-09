package javapkg;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class Sentence {
	// NORMAL_STMT IF_STMT WHILE_STMT
	int sentenceType;
	// for if/while statement
	int relation;
	Sentence left = null;
	Sentence right = null;
	int executenum;
	ArrayList<Sentence> execute = new ArrayList<Sentence>();
	//for normal sentence
	// INT DOUBLE ABC_OBJECT
	int valType;
	//// expression
	String val;
	//// ABC object
	int ObjectId;
	int Objectnum;
	int FuncId;
	int varnum;
	ArrayList<Integer> Funcvar=new ArrayList<Integer>();

	public Sentence() {

	}

	// expression
	public Sentence(ABC_compiler compiler, int valType, String val) throws IOException {
		this.sentenceType = ABC_Constant.NORMAL_STMT;
		this.valType = valType;
		this.val = val;
		ObjectId = 0;
		Funcvar.add(Util.forceInt(compiler, val));
	}

	// ABC object
	public Sentence(int objectId, int objectnum, int funcId, int varnum, ArrayList<Integer> funcvar) {
		this.sentenceType = ABC_Constant.NORMAL_STMT;
		this.valType = ABC_Constant.ABC_OBJECT;
		ObjectId = objectId;
		Objectnum = objectnum;
		FuncId = funcId;
		this.varnum = varnum;
		Funcvar = funcvar;
	}

	// if/while statement
	public Sentence(int sentenceType,
	                int relation, Sentence left, Sentence right,
	                ArrayList<Sentence> execute) {
		this.sentenceType = sentenceType;
		ObjectId = sentenceType;
		this.relation = relation;
		this.left = left;
		this.right = right;
		this.execute = execute;
		this.executenum = execute.size();
	}


	public void setObjectId(int objectId) {
		ObjectId = objectId;
	}

	public void setObjectnum(int objectnum) {
		Objectnum = objectnum;
	}

 	public void display() {
 //		System.out.println(new Gson().toJson(this));
 		System.out.println("Sentence Type: " + sentenceType);
 		if (sentenceType == ABC_Constant.IF_STMT) {
 			System.out.println("[If Statement]");
 			System.out.println("[Condition]");
 			left.display();
 			System.out.println("[relation]");
 			System.out.println(relation);
 			right.display();
 			System.out.println("[Execute]");
 			for (Sentence s : execute)
 				s.display();
 		} else if (sentenceType == ABC_Constant.WHILE_STMT) {
 			System.out.println("[While Statement]");
 			System.out.println("[Condition]");
 			left.display();
 			System.out.println("[relation]");
 			System.out.println(relation);
 			right.display();
 			System.out.println("[Execute]");
 			for (Sentence s : execute)
 				s.display();
 		} else {
 				System.out.println("Val Type: " + valType);
 				if (valType == ABC_Constant.ABC_OBJECT) {
 					System.out.println("Object Name: " + ObjectId);
 					System.out.println("Object ID: " + Objectnum);
 					System.out.println("Function ID: " + FuncId);
 					System.out.println("Param List: " + Funcvar);
 				} else {
 					System.out.println("Val: " + val);
 				}
 			}
 		}

    public String jsonStr() {
		return new Gson().toJson(this);
    }

	public static String funcname[]= {
			"",
			"Forward",//1
			"Back",//2
			"Rotate",//3
			"MoveTo",//4
			"Brake",//5
			"Wait",//6
			"Obstacle",//7
			"x",//8
			"y",//9
			"dir",//10
			"add",//11
			"remove",//12
			"Sensor",//13
			"Report",//14
	};
	public static int funcvarnummin[]= {
			0,
			1,//1
			1,//2
			1,//3
			3,//4
			0,//5
			1,//6
			0,//7
			0,//8
			0,//9
			0,//10
			1,//11
			1,//12
			1,//13
			1,//14
	};
	public static int funcvarnummax[]= {
			0,
			1,//1
			1,//2
			1,//3
			3,//4
			0,//5
			1,//6
			0,//7
			0,//8
			0,//9
			0,//10
			1000,//11
			1000,//12
			1,//13
			2,//14
	};
}

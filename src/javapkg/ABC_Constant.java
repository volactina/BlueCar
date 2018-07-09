package javapkg;

public class ABC_Constant {
	public static final int NONE=0;
	
	// Object Table
	public static final int DIGIT = 0;
	public static final int CAR = 1;
	public static final int TEAM = 2;
	public static final int IF = 3;
	public static final int WHILE = 4;

	// Car Function Table
	public static final int FUNCTION = 0;
	public static final int FORWARD = 1;
	public static final int BACK = 2;
	public static final int ROTATE = 3;
	public static final int MOVE_TO = 4;
	public static final int BRAKE = 5;
	public static final int WAIT = 6;
	public static final int OBSTACLE = 7;
	public static final int X = 8;
	public static final int Y = 9;
	public static final int DIR = 10;
	public static final int TEAM_ADD=11;
	public static final int TEAM_REMOVE=12;
	public static final int SENSOR = 13;
	public static final int REPORT = 14;


	public static final String SENSOR_FIRE = "1";
	public static final String SENSOR_LIGHT = "2";
	public static final String SENSOR_SHAKE = "3";
	public static final String SENSOR_PLANE = "4";
	public static final String SENSOR_GAS = "5";

    //Value Type
	public static final int INT=1;
	public static final int DOUBLE=2;
	public static final int ABC_OBJECT=3;

	//Operator Table
	public static final int ADD=1;
	public static final int SUB=2;
	public static final int MUL=3;
	public static final int DIV=4;

	//Assign Operator Table
	public static final int ASSIGN = 1;
	public static final int SELF_ADD = 2;
	public static final int SELF_SUB = 3;
	public static final int SELF_MUL = 4;
	public static final int SELF_DIV = 5;

	// Relational Operator Table
	public static final int EQUAL = 0;
	public static final int LESS_THAN = 1;
	public static final int MORE_THAN = 2;
	public static final int LESS_EQUAL = 3;
	public static final int MORE_EQUAL = 4;

	// Statement Type
	public static final int NORMAL_STMT = 0;
	public static final int IF_STMT = 3;
	public static final int WHILE_STMT = 4;

	// Sentence Type
	public static final int DECLARATION = 1;
	public static final int COPERATION = 2;
	public static final int ABCOPERATION = 3;

	// Error Type
	public static final int CORRECT = 0;
	public static final int WARNING = 1;
	public static final int ERROR = 2;
	public static final int RUNTIME_ERROR = 3;


}
package javapkg;
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
import java.io.IOException;
import java.util.ArrayList;

public
class ASTABCOperation extends SimpleNode {
	private int objectName;
	private int objectID;
	private int functionName;
	private String myFuncName;
	private ArrayList<String> paramList;

  public ASTABCOperation(int id) {
    super(id);
  }

  public ASTABCOperation(ABC_compiler p, int id) {
    super(p, id);
  }

	public int getObjectName() {
		return objectName;
	}

	public int getObjectID() {
		return objectID;
	}

	public int getFunctionName() {
		return functionName;
	}

	public String getMyFuncName() {
		return myFuncName;
	}

	public ArrayList<String> getParamList() {
		return paramList;
	}

	public void setObject(ASTObject object) {
  	this.objectName = object.getName();
  	this.objectID = object.getObjectId();
  }

  public void setAction(ASTAction action) {
  	this.functionName = action.getName();
  	this.myFuncName = action.getFuncName();
  	this.paramList = action.getParamList();
  }

	public void outputStatement() {
		System.out.println("[Action]");
		System.out.println("Object Name: " + objectName);
		System.out.println("Object ID: " + objectID);
		System.out.println("Function Name: " + functionName);
		System.out.println("Param List: " + paramList + "\n");
	}

	public Command toCommand() {
	    return new Command(objectName, objectID, functionName,
                            myFuncName, paramList);
	}

  public Sentence toSentence(ABC_compiler compiler) throws IOException {
  	return toCommand().toSentence(compiler);
  }

}
/* JavaCC - OriginalChecksum=409b28b5dbfbc76d7c3ce0afa5587779 (do not edit this line) */

package javapkg;
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */

import java.io.IOException;
import java.util.ArrayList;

public
class Command {
	private int objectName;
	private int objectID;
	private int functionName;
	private String myFuncName;
	private ArrayList<String> paramList;

	public Command(int objectName, int objectID, int functionName,
	               String myFuncName, ArrayList<String> paramList) {
		this.objectName = objectName;
		this.objectID = objectID;
		this.functionName = functionName;
		this.myFuncName = myFuncName;
		this.paramList = paramList;
	}

	public int getFunctionName() {
		return functionName;
	}

	public String getMyFuncName() {
		return myFuncName;
	}

	public int getObjectName() {
		return objectName;
	}

	public int getObjectID() {
		return objectID;
	}

	public Sentence toSentence(ABC_compiler compiler) throws IOException {
		ArrayList<Integer> params = new ArrayList<>();
		for (int i = 0; i < paramList.size(); i++)
			params.add(Util.forceInt(compiler, paramList.get(i)));
        return new Sentence(objectName, objectID, functionName,
                        paramList.size(), params);
  }

}
/* JavaCC - OriginalChecksum=409b28b5dbfbc76d7c3ce0afa5587779 (do not edit this line) */

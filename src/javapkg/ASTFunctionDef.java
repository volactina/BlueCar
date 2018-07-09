package javapkg;
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */

public
class ASTFunctionDef extends SimpleNode {
//  private int objectName = 0;
//  private int objectID = 0;
//  private int functionName;
//  private String funcName;
//  private ArrayList<String> paramList;

  public ASTFunctionDef(int id) {
    super(id);
  }

  public ASTFunctionDef(ABC_compiler p, int id) {
    super(p, id);
  }

//  public void setAction(ASTAction action) {
//    this.functionName = action.getName();
//    this.paramList = action.getParamList();
//  }
//
//	public void outputStatement() {
//		System.out.println("For Function Definition----");
//		System.out.println("Object Name: " + objectName);
//		System.out.println("Object ID: " + objectID);
//		System.out.println("Function Name: " + functionName);
//		System.out.println("Param List: " + paramList + "\n");
//	}
//
//  public Sentence toCommand() {
//    ArrayList<Integer> params = new ArrayList<>();
//    for (int i = 0; i < paramList.size(); i++)
//      params.add(Util.forceInt(paramList.get(i)));
//    return new Command(objectName, objectID, functionName,
//            paramList.size(), params);
//  }

}
/* JavaCC - OriginalChecksum=45d7794c585c51df41615506e09584e8 (do not edit this line) */

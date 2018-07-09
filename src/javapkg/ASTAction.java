package javapkg;
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
import java.util.ArrayList;

public
class ASTAction extends SimpleNode {
  private int name;
  private String funcName;
  private ArrayList<String> paramList;

  public ASTAction(int id) {
    super(id);
  }

  public ASTAction(ABC_compiler p, int id) {
    super(p, id);
  }

	public int getName() {
		return name;
	}

	public void setName(int name) {
		this.name = name;
	}

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public ArrayList<String> getParamList() {
		return paramList;
	}

	public void setParamList(ArrayList<String> paramList) {
		this.paramList = paramList;
	}
}
/* JavaCC - OriginalChecksum=4984b669a379cdf6d102f7bace62eaa4 (do not edit this line) */

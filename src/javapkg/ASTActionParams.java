package javapkg;
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
import java.util.ArrayList;

public
class ASTActionParams extends SimpleNode {
  
  private ArrayList<String> paramList = new ArrayList<>();

  public ASTActionParams(int id) {
    super(id);
  }

  public ASTActionParams(ABC_compiler p, int id) {
    super(p, id);
  }

  public void addParam(String val) {
  	paramList.add(val);
  }

	public ArrayList<String> getParamList() {
		return paramList;
	}
}
/* JavaCC - OriginalChecksum=b0261ff74de27195e6e277c4dd177926 (do not edit this line) */

package javapkg;

/* Generated By:JJTree: Do not edit this line. ASTObjectName.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTObjectName extends SimpleNode {
  private int val;
  public ASTObjectName(int id) {
    super(id);
  }

  public ASTObjectName(ABC_compiler p, int id) {
    super(p, id);
  }

	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
	}
}
/* JavaCC - OriginalChecksum=37c69636d4d49455dd1e6238be52ed0c (do not edit this line) */

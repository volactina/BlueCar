package javapkg;

/* Generated By:JJTree: Do not edit this line. ASTTerm.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTTerm extends SimpleNode {

	private String val;
	
	private int type;

  public ASTTerm(int id) {
    super(id);
  }

  public ASTTerm(ABC_compiler p, int id) {
    super(p, id);
  }


      public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
    
    public int getType() {
      return type;
    }

    public void setType(int type) {
        this.type=type;
    }

}
/* JavaCC - OriginalChecksum=9fc08c3506a52246a3db0d2d8a50da30 (do not edit this line) */

package javapkg;
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */

public
class ASTBoolVariable extends SimpleNode {

    Sentence val;

  public ASTBoolVariable(int id) {
    super(id);
  }

  public ASTBoolVariable(ABC_compiler p, int id) {
    super(p, id);
  }

    public Sentence getVal() {
        return val;
    }

    public void setVal(Sentence val) {
        this.val = val;
    }

}
/* JavaCC - OriginalChecksum=5979d1eb2c9037683e8a80ba221a6eb2 (do not edit this line) */

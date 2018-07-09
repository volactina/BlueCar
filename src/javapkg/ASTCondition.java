package javapkg;
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */

public
class ASTCondition extends SimpleNode {
  private int operator;
  private Sentence left;
  private Sentence right;

  public ASTCondition(int id) {
    super(id);
  }

  public ASTCondition(ABC_compiler p, int id) {
    super(p, id);
  }

  public int getOperator() {
    return operator;
  }

  public void setOperator(int operator) {
    this.operator = operator;
  }

  public Sentence getLeft() {
    return left;
  }

  public void setLeft(Sentence left) {
    this.left = left;
  }

  public Sentence getRight() {
    return right;
  }

  public void setRight(Sentence right) {
    this.right = right;
  }

}
/* JavaCC - OriginalChecksum=b79ee21f90a97d8a9c231b1cd9daeac1 (do not edit this line) */

/* Generated By:JJTree: Do not edit this line. ASTExpression_p_ne.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package javapkg;

public
class ASTExpression_p_ne extends SimpleNode {

  private String inh_val;
  private int inh_type;
  private String syn_val;
  private int syn_type;

  private int operator;

  public ASTExpression_p_ne(int id) {
    super(id);
  }

  public ASTExpression_p_ne(ABC_compiler p, int id) {
    super(p, id);
  }

  public String getInhVal() {
    return inh_val;
  }

  public void setInhVal(String val) {
    this.inh_val = val;
  }

  public int getInhType() {
    return inh_type;
  }

  public void setInhType(int type) {
    this.inh_type=type;
  }
  //syn
  public String getSynVal() {
    return syn_val;
  }

  public void setSynVal(String val) {
    this.syn_val = val;
  }

  public int getSynType() {
    return syn_type;
  }

  public void setSynType(int type) {
    this.syn_type=type;
  }

  public int getOperator() {
    return operator;
  }

  public void setOperator(int type) {
    this.operator=type;
  }


}
/* JavaCC - OriginalChecksum=233f2e5ae6d52dc5fac8dbb026cb004b (do not edit this line) */
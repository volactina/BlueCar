package javapkg;
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
import java.io.IOException;
import java.util.ArrayList;

public
class ASTIfConditionStatement extends SimpleNode {
  int operator;
  Sentence leftSentence;
  Sentence rightSentence;
  ArrayList<Sentence> execList = new ArrayList<Sentence>();

  public ASTIfConditionStatement(int id) {
    super(id);
  }

  public ASTIfConditionStatement(ABC_compiler p, int id) {
    super(p, id);
  }

  public void setCondition(ASTCondition condition) {
  	this.operator = condition.getOperator();
  	this.leftSentence = condition.getLeft();
  	this.rightSentence = condition.getRight();
  }

  public void addExecStmt(ABC_compiler compiler, ASTStatement stmt) throws IOException {
  	if (stmt.getStatementType() == ABC_Constant.ABCOPERATION)
  	    execList.add(stmt.toSentence(compiler));
  }

	public void addExecStmt(Sentence sentence) {
		execList.add(sentence);
	}

	public void outputIfStatement() {
		System.out.println("[If Statement]");
		System.out.println("[Condition] ");
		leftSentence.display();
		System.out.println("[operator]");
		System.out.println(operator);
		rightSentence.display();
		System.out.println("[Execute] ");
		for (Sentence s : execList)
			s.display();
	}

	public Sentence toSentence() {
  	    return new Sentence(ABC_Constant.IF_STMT,
		                    operator, leftSentence, rightSentence,
		                    execList);
	}

}
/* JavaCC - OriginalChecksum=52cdda634169fb172c70c5f3ed649d4e (do not edit this line) */

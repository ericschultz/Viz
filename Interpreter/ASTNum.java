/* Generated By:JJTree: Do not edit this line. ASTNum.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package Interpreter;

public class ASTNum extends SimpleNode {

	private int value;
  public ASTNum(int id) {
    super(id);
  }

  public ASTNum(VizParser p, int id) {
    super(p, id);
  }
  
  public void setValue(int value)
  {
  	this.value = value;
  }
  
  public String getValue()
  {
  	return this.value;
  }


  /** Accept the visitor. **/
  public Object jjtAccept(VizParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=bc3d1f9bf048581dd3e0484ca74eecd8 (do not edit this line) */

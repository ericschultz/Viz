/* Generated By:JJTree: Do not edit this line. ASTCall.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package Interpreter;

public class ASTCall extends SimpleNode {
  public ASTCall(int id) {
    super(id);
  }

  public ASTCall(VizParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(VizParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=027b3b41c675e4ac97fc15171968eb26 (do not edit this line) */

/* Generated By:JJTree: Do not edit this line. ASTargs.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
public class ASTargs extends SimpleNode {
  public ASTargs(int id) {
    super(id);
  }

  public ASTargs(VizParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(VizParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=5ec7b03be473ba5031258d7bd1a706db (do not edit this line) */
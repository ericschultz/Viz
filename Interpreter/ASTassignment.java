/* Generated By:JJTree: Do not edit this line. ASTassignment.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
public class ASTassignment extends SimpleNode {
  public ASTassignment(int id) {
    super(id);
  }

  public ASTassignment(VizParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(VizParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=cdb640fb26a60f2fb7f34b5362bbcaa2 (do not edit this line) */
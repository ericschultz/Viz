package Interpreter;
import viz.*;
import java.util.*;

public class ByMacroVisitor implements VizParserVisitor, VizParserTreeConstants, UpdateReasons
{
	private XAALConnector connector;
		private static final int QUESTION_FREQUENCY = 65;
	public static final int LINE_NUMBER_END = -1;
	
	private int callLineNumber = 3;
	private int currentLineNumber = 1;
	
	private ASTProgram program;
	
	private boolean inNested = false;
	public static String nextCodePageId;
	private boolean readyForCall = false;
	private ASTCall theCall;

	
	public void setXAALConnector(XAALConnector xc)
	{
		this.connector = xc;
	}

	
	public Object visit(SimpleNode node, Object data)
	{
		int id = node.getId();
		Object retVal = null;
if (Global.debug) {		System.out.println("Visiting " + id);
}		switch (id)
		{
			case JJTSTATEMENTLIST:
if (Global.debug) {				System.out.println("STMT LIST");
}							currentLineNumber++;
							node.childrenAccept(this, null);
							currentLineNumber++;
							break;
			case JJTVARDECL:
				handleVarDecl((ASTVarDecl)node);
				node.childrenAccept(this, null);
				break;
			case JJTCALL:
if (Global.debug) {				System.out.println("CALL");
}				handleCall((ASTCall)node);
				node.childrenAccept(this, null);
				break;
			case JJTVAR:
				handleVar((ASTVar)node);
				node.childrenAccept(this, null);
				break;
			case JJTASSIGNMENT:
				handleAssignment((ASTAssignment)node);
				node.childrenAccept(this, null);
				break;
			default:
				for (int i = 0; i < node.jjtGetNumChildren(); i++)
				{
if (Global.debug) {					System.out.println(node.jjtGetChild(i));
}					SimpleNode s = (SimpleNode)node.jjtGetChild(i);
					s.jjtAccept(this, null);
				}
if (Global.debug) {				System.out.println("AFTER");
}		}
		return retVal;
	}
	
	public void handleVar(ASTVar node)
	{
		
if (Global.debug) {		System.out.println(node.getName());
}		
		SimpleNode subscript = null;
		if (node.getIsArray())
		{
			ByValVariable v = (ByValVariable) Global.getCurrentSymbolTable().getVariable(node.getName());
			if (v == null)
			{
				v = (ByValVariable) Global.getFunction("foo").getSymbolTable().getVariable(node.getName());
			}
if (Global.debug) {			System.out.println("LLL" + v.getIsArray() + " " + node.getName());
}			ASTExpression sub = (ASTExpression)node.jjtGetChild(0);
			v.setSubscript(sub);
if (Global.debug) {			System.out.println(sub.getCode());
}		}
		
		if (!inNested)
		{
			return;
		}
		
		String argName = Global.getCurrentParamToArg().get(node.getName());
if (Global.debug) {		System.out.println("Var " + argName);
}		if (argName != null)		//It's one of the args
		{
if (Global.debug) {			System.out.println(argName + ": " + node.getName());
}			String name = node.getName();
			node.setName(argName);
if (Global.debug) {			System.out.println("Substituted " + argName);
}			ByValVariable arg =(ByValVariable) Global.getCurrentSymbolTable().getVariable(argName);
			if (arg.getIsArray())
			{
if (Global.debug) {				System.out.println("An array");
}				node.setIsArray(true);
				//here we want to get the correct subscript
				//ASTFunction fn = Global.getFunction("main");
				ASTArgs args = (ASTArgs) theCall.jjtGetChild(0);

				
				if (name.equals("x"))
				{
					subscript = (SimpleNode) args.jjtGetChild(0).jjtGetChild(0);
				}
				else if (name.equals("y"))
				{
					subscript = (SimpleNode) args.jjtGetChild(1).jjtGetChild(0);
				}
				else if (name.equals("z"))
				{
					subscript = (SimpleNode) args.jjtGetChild(2).jjtGetChild(0);
				}
				else
				{
if (Global.debug) {					System.out.println("Something is wrong");
}				}
if (Global.debug) {				System.out.println(subscript.getCode());
}				node.jjtAddChild(subscript, 0);
			}
			
			//Add the graphical move
			int pos = 2;
			int endPos = 0;
			int lineNumber = 0;
			Object[] params =  Global.getCurrentParamToArg().keySet().toArray();
			String expectedArgName = Global.getCurrentParamToArg().get((String)params[1]);
			if (argName.equals(expectedArgName))
			{
if (Global.debug) {				System.out.println("**");
}				pos = 1;
			}
			else if (params.length > 2 && argName.equals(Global.getCurrentParamToArg().get((String)params[2])))
			{
if (Global.debug) {				System.out.println("^^");
}				pos = 0;
			}
			
			//Three cases
			SimpleNode sn = (SimpleNode)node.jjtGetParent();
			
			if (sn instanceof ASTAssignment)  //LHS of an assignment
			{
				endPos = 0;
				lineNumber = sn.getLineNumber();
if (Global.debug) {				System.out.println("Line no: " + lineNumber);
}			}
			else if (sn instanceof ASTOp) // Left hand operand of an op
			{
				endPos = 1;
				//The parent's parent knows line number
				SimpleNode temp = (SimpleNode) sn.jjtGetParent();
				temp = (SimpleNode) temp.jjtGetParent();
				lineNumber = temp.getLineNumber();
if (Global.debug) {				System.out.println("Line yes: " + lineNumber);
}			}
			else if (sn instanceof ASTExpression) //This could either be a standalone assignment or the rhs of an op
			{
				SimpleNode temp = (SimpleNode) sn.jjtGetParent();
				if (temp instanceof ASTAssignment) //Standalone assigment, so temp knows line number
				{
					endPos = 1;
					lineNumber = temp.getLineNumber();
if (Global.debug) {					System.out.println("Line maybe: " + lineNumber);
}				}
				else if (temp instanceof ASTOp) // RHS of op grandparent knows lineNumber
				{
					endPos = 2;
					SimpleNode temp2 = (SimpleNode) temp.jjtGetParent();
					temp2 = (SimpleNode) temp2.jjtGetParent();
					lineNumber = temp2.getLineNumber();
				}
				else
				{
if (Global.debug) {					System.out.println("You forgot something Tom");
}				}
			}
			else
			{
if (Global.debug) {				System.out.println("You've lost...");
}			}
			if (node.getIsArray()) //An array, we have to put the subscript on for the move
			{
				argName = argName + "[" + subscript.getCode() + "]";
			}
if (Global.debug) {			System.out.println(argName + ": " + NewTest.currentPage + " " + callLineNumber + " " + pos + " " + argName + " " + lineNumber + " " + endPos);
}if (Global.debug) {			System.out.println("Moving to");
}			connector.moveArgs(NewTest.currentPage, callLineNumber, pos, argName, lineNumber, endPos);
			
if (Global.debug) {			System.out.println("...");
}		}
	}
	
	public void handleVarDecl(ASTVarDecl node)
	{
		return;
	}
	public void handleAssignment(ASTAssignment node)
	{
		if (!inNested)
		{
			return;
		}
		String argName = Global.getCurrentParamToArg().get(node.getName());
if (Global.debug) {		System.out.println("Assignemnt " + argName);
}		if (argName != null)
		{
			node.setName(argName);
if (Global.debug) {			System.out.println("Subbed in assignment " + argName);
}if (Global.debug) {			System.out.println(node.getName());
}			ByValVariable arg = (ByValVariable) Global.getCurrentSymbolTable().getVariable(argName);
			if (arg.getIsArray())
			{
				//node.setIsArray(true);
			}
		}
	}
	
	public void handleCall(ASTCall node)
	{	
		theCall = node;
		//Get the correct function head node
		ASTFunction fun = Global.getFunction(node.getName());
if (Global.debug) {		System.out.println("Calling: " + fun.getName());
}		
		//The call's grandparent knows the line number and we need to know it later
		SimpleNode sn = (SimpleNode) node.jjtGetParent();
		sn = (SimpleNode) sn.jjtGetParent();
		
		callLineNumber = sn.getLineNumber();
if (Global.debug) {		System.out.println("AHHHH " + callLineNumber);
}		
		
		//Get the parameters and put the correct values in the symbolTable
		SymbolTable st = fun.getSymbolTable();
		st.setPrevious(Global.getCurrentSymbolTable());
if (Global.debug) {		System.out.println(Global.getCurrentSymbolTable());
}
		String name = fun.getName();
if (Global.debug) {		System.out.println("FUNNAME: " + name);
}		
		
		ArrayList<String> parameters = fun.getParameters();		
		ASTArgs argsNode = (ASTArgs) node.jjtGetChild(0);
if (Global.debug) {		System.out.println(argsNode);
}		ArrayList<ASTVar> args = node.getArgs();
if (Global.debug) {		System.out.println("args: " + args.size() + " params: " + parameters.size());
}		
		
		HashMap<String, String> pa = new HashMap<String, String>(); //Maps args to params
		for (int i = 0; i < parameters.size(); i++)
		{
			pa.put(parameters.get(i), args.get(i).getName());
			args.get(i).jjtAccept(this, null);
		}
		Global.setCurrentParamToArg(pa);
		for (String s : Global.getCurrentParamToArg().keySet())
		{
if (Global.debug) {			System.out.println(s + ": " + Global.getCurrentParamToArg().get(s));
}		}
if (Global.debug) {		System.out.println("FYF");
}		inNested = true;
		fun.jjtAccept(this, null);
		inNested = false;
		
	}
	

	public Object visit(ASTProgram node, Object data)
	{
		program = node;
		node.childrenAccept(this, null);
		//readyForCall = true;
		//call.jjtAccept(this, null);
		return null;
	}
	
	public Object visit(ASTDeclarationList node, Object data)
	{	
if (Global.debug) {		System.out.println("Handle decl list");
}		node.childrenAccept(this, null);
		return null;
	}
	
	public Object visit(ASTDeclaration node, Object data)
	{
		node.childrenAccept(this, null);
		return null;
	}
	
	public Object visit(ASTVarDecl node, Object data)
	{
		if (! (node.jjtGetParent() instanceof ASTStatement))
		{
			currentLineNumber++;
		}
		handleVarDecl((ASTVarDecl)node);
		node.childrenAccept(this, null);
		return null;
	}
	
	//FIXME
	public Object visit(ASTArrayDeclaration node, Object data)
	{
		node.childrenAccept(this, null);
		return null;
	}
	
	public Object visit(ASTFunction node, Object data)
	{	
		currentLineNumber+=2;
if (Global.debug) {		System.out.println("Visiting function");
}		
		node.childrenAccept(this, null);
		currentLineNumber++;
		return null;
	}
	
	public Object visit(ASTStatementList node, Object data)
	{
if (Global.debug) {		System.out.println("here");
}		for (int i = 0; i < node.jjtGetNumChildren(); i++)
		{
if (Global.debug) {			System.out.println(node.jjtGetChild(i));
}			node.jjtGetChild(i).jjtAccept(this, null);
		}
		return null;
	}
  	public Object visit(ASTStatement node, Object data)
  	{
  		currentLineNumber++;
	  	for (int i = 0; i < node.jjtGetNumChildren(); i++)
		{
if (Global.debug) {			System.out.println(node.jjtGetChild(i));
}			node.jjtGetChild(i).jjtAccept(this, null);
		}
  		return null;
  	}
  	public Object visit(ASTCall node, Object data)
  	{
  		handleCall((ASTCall)node);
 		node.childrenAccept(this, null);
  		return null;
  	}
  	public Object visit(ASTVar node, Object data)
  	{
if (Global.debug) {  		System.out.println("VAR " + node.getName());
}  		handleVar((ASTVar)node);
  		node.childrenAccept(this, null);
  		return null;
  	}
  	public Object visit(ASTAssignment node, Object data)
  	{
  		handleAssignment((ASTAssignment)node);
if (Global.debug) {  		System.out.println("asdf");
}if (Global.debug) {  		System.out.println(node.getName());
}if (Global.debug) {  		System.out.println(Global.getCurrentSymbolTable());
}  		node.childrenAccept(this, null);
  		return null;
  	}
 	public Object visit(ASTExpression node, Object data)
 	{
		for (int i = 0; i < node.jjtGetNumChildren(); i++)
		{
if (Global.debug) {			System.out.println(node.jjtGetChild(i));
}			node.jjtGetChild(i).jjtAccept(this, null);
		}
		return null;
 	}
  	public Object visit(ASTArgs node, Object data)
  	{
  		node.childrenAccept(this, null);
  		return null;
  	}
  	public Object visit(ASTOp node, Object data)
  	{
  		node.childrenAccept(this, null);
  		return null;
  	}
  	public Object visit(ASTNum node, Object data)
  	{
  		node.childrenAccept(this, null);
  		return null;
  	}
  	

}

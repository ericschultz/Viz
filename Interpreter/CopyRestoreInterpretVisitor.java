package Interpreter;
import viz.*;
import java.util.*;

public class CopyRestoreInterpretVisitor implements VizParserVisitor, VizParserTreeConstants, UpdateReasons
{
	private QuestionFactory questionFactory;
	
	private Question assignmentQuestion;
	private Question callQuestion;
	private Question startQuestion; 
	
	private ArrayList<Question> startQuestions = new ArrayList<Question>();//FIXME use this?
	private XAALConnector connector;
	public static final int LINE_NUMBER_END = -1;
		private static final int QUESTION_FREQUENCY = 65;

	public void setQuestionFactory(QuestionFactory questionFactory)
	{
		this.questionFactory = questionFactory;
	}
	
	public void setXAALConnector(XAALConnector xc)
	{
		this.connector = xc;
	}

	//FIXME use this?
	public void update(int lineNumber, int reason)
	{
if (Global.debug) {		System.out.println("Update on " + lineNumber);
}		//System.out.println(Global.getCurrentSymbolTable().toString());
		//questionFactory.addAnswers(lineNumber, reason);
	}
	
	public void setAssignmentQuestionAnswer(int value)
	{
		if (assignmentQuestion instanceof FIBQuestion)
		{
			((FIBQuestion)assignmentQuestion).addAnswer(value + "");
		}
	}
	
	public Object visit(SimpleNode node, Object data)
	{
		int id = node.getId();
		Object retVal = null;

		switch (id)
		{
			case JJTPROGRAM:
				handleProgram((ASTProgram)node);
				break;
			case JJTDECLARATIONLIST:
				handleDeclarationList((ASTDeclarationList)node);
				break;
			case JJTDECLARATION:
				retVal = handleDeclaration((ASTDeclaration)node);
				break;
			case JJTVARDECL:
				handleVarDecl((ASTVarDecl)node);
				break;
			case JJTARRAYDECLARATION:
				retVal = handleArrayDeclaration((ASTArrayDeclaration)node);
				break;
			case JJTSTATEMENTLIST:
				handleStatementList((ASTStatementList)node);
				break;
			case JJTSTATEMENT:
				handleStatement((ASTStatement)node);
				break;
			case JJTCALL:
				retVal = handleCall((ASTCall)node);
				break;
			case JJTVAR:
				retVal = handleVar((ASTVar)node);
				break;
			case JJTASSIGNMENT:
				handleAssignment((ASTAssignment)node);
				break;
			case JJTEXPRESSION:
				retVal = handleExpression((ASTExpression)node);
				break;
			case JJTARGS:
				retVal = handleArgs((ASTArgs)node);
				break;
			case JJTOP:
				retVal = handleOp((ASTOp)node);
				break;
			case JJTNUM:
				retVal = handleNum((ASTNum)node);
				break;
			case JJTFUNCTION:
				handleFunction((ASTFunction)node);
				break;
			default:
if (Global.debug) {				System.out.println("Unimplemented");
}		}
		return retVal;
	}
	
	public void handleProgram(ASTProgram node)
	{
if (Global.debug) {		System.out.println("visiting program");
}		Global.setCurrentSymbolTable(Global.getSymbolTable()); 
		update(1, UPDATE_REASON_BEGIN);
		
		//Drawing Stuff
		connector.addScope(Global.getSymbolTable(), "Global", null);
		connector.startSnap(1);
		connector.startPar();
			connector.showScope("Global");
		connector.endPar();
		connector.endSnap();
		
		node.jjtGetChild(0).jjtAccept(this, null);
		update(LINE_NUMBER_END, UPDATE_REASON_END);
		int value = Global.getCurrentSymbolTable().get(startQuestion.getVariable());
		if (startQuestion instanceof FIBQuestion)
		{
		
			((FIBQuestion)startQuestion).addAnswer(value+"");
		}
		else if (startQuestion instanceof TFQuestion)
		{
			Random r = new Random();
			int prob = r.nextInt(10);
			int qa = value;
			if (prob >= 3 && value != startQuestion.getValue())
			{
				qa = startQuestion.getValue();
				((TFQuestion)startQuestion).setAnswer(false);
			}
			else
			{
				((TFQuestion)startQuestion).setAnswer(true);
				
			}
			startQuestion.setText(startQuestion.getText() + qa + ".");
		}
						
		//TODO Write the last snap nicely
		connector.startSnap(node.getPseudocode().length);
			connector.startPar();
				connector.hideScope("foo");
			connector.endPar();
		connector.endSnap();
if (Global.debug) {		System.out.println("Done");
}	}
	
	public void handleDeclarationList(ASTDeclarationList node)
	{
		connector.startSnap(Global.getFunction("main").getLineNumber());
		connector.startPar();
		
if (Global.debug) {		System.out.println("Visiting declList");
}		int numDecls = node.jjtGetNumChildren();
		for (int i = 0; i < numDecls; i++)
		{
			//A Declaration returned false which means we ran.
			if(((Boolean)node.jjtGetChild(i).jjtAccept(this, null)) == false)
			{
				return;
			}
		}
	}
	
	//returning false means we're done executing now.
	public Boolean handleDeclaration(ASTDeclaration node)
	{
		//System.out.println("Visiting decl");
		SimpleNode child = (SimpleNode) node.jjtGetChild(0);
		if (child.getId() == JJTFUNCTION)
		{
			startQuestion = questionFactory.getStartQuestion();
			connector.addQuestion(startQuestion);
			connector.endPar();
			connector.endSnap();
			ASTFunction main = Global.getFunction("main");
			connector.addScope(main.getSymbolTable(), "main", "Global");
			connector.startSnap(Global.getFunction("main").getLineNumber());
			connector.startPar();
				//connector.addQuestion(questionFactory.addBeginQuestion());
			connector.endPar();
			connector.startPar();
				connector.showScope("main");
			connector.endPar();
			connector.endSnap();
			main.jjtAccept(this, null);
			return false;
		}
		else
		{
			node.jjtGetChild(0).jjtAccept(this, null);
			//update();
			return true;
		}	
		
	}
	
	public void handleVarDecl(ASTVarDecl node)
	{
if (Global.debug) {		System.out.println("Visiting var decl");
}		String name = node.getName();
		node.setLineNumber(((SimpleNode)node.jjtGetParent()).getLineNumber());
		SymbolTable s = Global.getCurrentSymbolTable();
		ArrayList<Integer> values;
		if (node.getIsArray())
		{
if (Global.debug) {			System.out.println("This is an array " + name);
}			ByValVariable v = (ByValVariable) s.getVariable(name);
			v.setArray();
if (Global.debug) {			System.out.println("BLAH" + node.jjtGetChild(0));
}			 values = (ArrayList<Integer>)handleArrayDeclaration((ASTArrayDeclaration)node.jjtGetChild(0));
if (Global.debug) {			System.out.println("Values: " + values);
}			v.setValues(values);
		}
		else
		{
			Integer value =(Integer) node.jjtGetChild(0).jjtAccept(this, null);
			s.setValue(name, value);
		}
			//Drawing Stuff
			connector.addVariable(s.getVariable(name), name, s.getName());
			
			//This is a snapshot
					connector.showVar(Global.getCurrentSymbolTable().getVariable(name));
	}
	
	public void handleFunction(ASTFunction node)
	{	
		//Get the function's symbol table, set it's previous to the
		// calling function's, and then set it to current.
		
if (Global.debug) {		System.out.println("Visiting function");
}		SymbolTable currentSymbolTable = node.getSymbolTable();
		for (String p : node.getParameters())
		{
			ByCopyRestoreVariable v = new ByCopyRestoreVariable();
			v.setParam();
			currentSymbolTable.put(p, v);
		}
		Global.setCurrentSymbolTable(currentSymbolTable);

		
if (Global.debug) {		System.out.println("Executing function: " + node.getName());
}		update(node.getLineNumber(), UPDATE_REASON_FUNCTION);
		node.jjtGetChild(0).jjtAccept(this, null);
		
		for (String p : node.getParameters())
		{
if (Global.debug) {			System.out.println("Copying " + p + " out");
}			((ByCopyRestoreVariable)currentSymbolTable.getVariable(p)).copyOut();
		}
if (Global.debug) {		System.out.println("LEAVING");
}		
if (Global.debug) {		System.out.println(Global.getCurrentSymbolTable());
}		leaveScope();
	}
	public ArrayList<Integer> handleArrayDeclaration(ASTArrayDeclaration node)
	{
		ArrayList<Integer> values = new ArrayList<Integer>();
		for (int i = 0; i < node.jjtGetNumChildren(); i++)
		{
			Integer value = (Integer)node.jjtGetChild(i).jjtAccept(this, null);
if (Global.debug) {			System.out.println("ff " + value);
}			values.add(value);
		}
if (Global.debug) {		System.out.println(values);
}		return values;
	}
	
	public void handleStatementList(ASTStatementList node)
	{
		int numStatements = node.jjtGetNumChildren();
		for (int i = 0; i < numStatements; i++)
		{
			node.jjtGetChild(i).jjtAccept(this, null);
		}
	}
	
	public void handleStatement(ASTStatement node)
	{

		//System.out.println(node.getCode());
		
		//Drawing
		connector.startSnap(node.getLineNumber());
			connector.startPar();
		
		//FIXME we'll see how this works	
		node.jjtGetChild(0).jjtAccept(this, null);
		if (((SimpleNode)node.jjtGetChild(0)).getId() == JJTCALL)
		{
			((ASTCall)(node.jjtGetChild(0))).setLineNumber(node.getLineNumber());
		}
			
			connector.endPar();
		connector.endSnap();
		update(node.getLineNumber(), UPDATE_REASON_STATEMENT);
	}
	
	public Integer handleCall(ASTCall node)
	{
		boolean gotAQuestion = true; //FIXME HACK
		//Get the correct function head node
		ASTFunction fun = Global.getFunction(node.getName());
if (Global.debug) {		System.out.println("Calling: " + fun.getName());
}		//Get the parameters and put the correct values in the symbolTable
		SymbolTable st = fun.getSymbolTable();
		String name = fun.getName();
		ArrayList<String> parameters = fun.getParameters();		
		ArrayList<Integer> args = (ArrayList<Integer>) node.jjtGetChild(0).jjtAccept(this, null);
		ArrayList<ASTVar> argNames = ((ASTArgs)node.jjtGetChild(0)).getArgs();
if (Global.debug) {		System.out.println("Ready to set args");
}		for (int i = 0; i < args.size(); i++)
		{
			Variable vv = st.getVariable(parameters.get(i));
if (Global.debug) {			System.out.println(vv + " ");
}			ByCopyRestoreVariable v = (ByCopyRestoreVariable)st.getVariable(parameters.get(i));
			ByValVariable ref = (ByValVariable)Global.getCurrentSymbolTable().getVariable(argNames.get(i).getName());
if (Global.debug) {			System.out.println("Got ref " + argNames.get(i).getName());
}			if (ref.getIsArray())
			{
if (Global.debug) {				System.out.println("Setting ref to index " + argNames.get(i).getIndex());
}				v.setRef(ref, argNames.get(i).getIndex());
			}
			else
			{
				v.setRef(ref);
			}
		}
if (Global.debug) {		System.out.println("Set args");
}		HashMap<String, String> pa = new HashMap<String, String>(); //Maps args to params
		for (int i = 0; i < parameters.size(); i++)
		{
			pa.put(parameters.get(i), argNames.get(i).getName());
		}
		Global.setCurrentParamToArg(pa);
		
			//QUESTION!!!
		callQuestion = questionFactory.getCallQuestion(name, pa);
		//Ok lets set refs now	
	
		//Drawing Stuff
		connector.addScope(fun.getSymbolTable(), fun.getName(), "Global");
		connector.startSnap(node.getLineNumber());
			connector.startPar();
				connector.showScope(node.getName());
				connector.addQuestion(callQuestion);
			connector.endPar();
			
			connector.startPar();
			Variable v1 = null;
			ByCopyRestoreVariable v2 = null;
				for (int i = 0; i < parameters.size(); i++)
				{ 	
					v1 = Global.getCurrentSymbolTable().getVariable(argNames.get(i).getName());				
					v2 = (ByCopyRestoreVariable)st.getVariable(parameters.get(i));		
	
					//((ByRefVariable)v2).setRef(((ByValVariable)v1)); 
					//Now in interpreter we should be pointing correctly.  				
					
if (Global.debug) {					System.out.println("Adding a reference from " + argNames.get(i).getName() +
}						" to " + parameters.get(i));
					if (v1.getIsArray())
					{
						connector.addVariableReference(v2, v1, v2.getRefIndex());
						connector.moveValue(v1, v2.getRefIndex(), v2);
					}
					else
					{
						connector.addVariableReference(v2, v1);
						connector.moveValue(v1, v2);
					}
				}
				
			connector.endPar();
		connector.endSnap();
				
		fun.jjtAccept(this, null);
		if(gotAQuestion)
		{

			int answer = Global.getFunction("main").getSymbolTable().get(callQuestion.getVariable());
			if (callQuestion instanceof FIBQuestion)
			{
if (Global.debug) {				System.out.println("AAAA " + answer);
}				((FIBQuestion)callQuestion).addAnswer(answer+"");
			}
			else if (callQuestion instanceof TFQuestion)
			{
				int qa = answer;
				//Getting the value of the var at the end of the function
				String paramName = Global.getCurrentParamToArg().get(callQuestion.getVariable());
				int prevVal = Global.getFunction("foo").getSymbolTable().get(paramName);
				
				Random r = new Random();
				int choose = r.nextInt(3);
				switch (choose)
				{
					case 0:
						qa = callQuestion.getValue();
						((TFQuestion)callQuestion).setAnswer(false);
						if (qa == answer) // Value is the same anyway
						{
							((TFQuestion)callQuestion).setAnswer(true);
						}
						break;
					case 1:
						qa = prevVal;
						((TFQuestion)callQuestion).setAnswer(false);
						if (qa == answer) // Value is the same anyway
						{
							((TFQuestion)callQuestion).setAnswer(true);
						}
						break;
					case 2:
						((TFQuestion)callQuestion).setAnswer(true);
						break;
				}
						
			
				callQuestion.setText(callQuestion.getText() + qa);
			}	
			else
			{
if (Global.debug) {				System.out.println("CQC " + callQuestion);
}			}
		}
		//Drawing the copy out stage

				for (int i = 0; i < parameters.size(); i++)
				{
					//HACK to get the right line to highlight
					connector.startSnap(Global.getFunction("main").getLineNumber() -1);
							connector.startPar();
					v1 = Global.getFunction("main").getSymbolTable().getVariable(argNames.get(i).getName());				
					v2 = (ByCopyRestoreVariable)st.getVariable(parameters.get(i));					if (v1.getIsArray())
					{
if (Global.debug) {						System.out.println("Copying out to an array " + v2.getRefIndex());
}						connector.moveValue(v2, v1, v2.getRefIndex());
					}
					else
					{
						connector.moveValue(v2, v1);
					}
					connector.endPar();
					connector.endSnap();
				}

		return 0;
	}
	
	public Integer handleVar(ASTVar node)
	{
		Integer value;
		Variable v = Global.getCurrentSymbolTable().getVariable(node.getName());
		if (node.getIsArray())
		{
			int index = (Integer) node.jjtGetChild(0).jjtAccept(this, null);
			node.setIndex(index);
			value = v.getValue(index);
		}
		else
		{
			value = v.getValue();
if (Global.debug) {			System.out.println("Got value " + value);
}		}
		return value;
	}
	
	public void handleAssignment(ASTAssignment node)
	{
		Random r = new Random();
		int q = r.nextInt(100);
		
			boolean gotAQuestion = q < QUESTION_FREQUENCY;//HACK FOR NOW FIXME
		String name = node.getName();

		Integer value = (Integer)node.jjtGetChild(1).jjtAccept(this, null);
if (Global.debug) {		System.out.println("Assigning to " + name + " value of " + value);
}		int index = 0;
		Variable v = Global.getCurrentSymbolTable().getVariable(name);

		if (v.getIsArray())
		{
if (Global.debug) {			System.out.println("AN ARRRAY");
}			index = (Integer) node.jjtGetChild(0).jjtGetChild(0).jjtAccept(this, null);
			v.setValue(value, index);
			if (gotAQuestion)
			{
if (Global.debug) {				System.out.println("asdf");
}				assignmentQuestion = questionFactory.getAssignmentQuestion(node.getLineNumber(), name, index);
			}
		}
		else
		{
					v.setValue(value);
				if (gotAQuestion)
		{
		assignmentQuestion = questionFactory.getAssignmentQuestion(node.getLineNumber(), name);
		}
		}
		//QUESTION!!!
if (Global.debug) {		System.out.println("Ok, set value");
}		//Drawing stuff. snap and par should be opened from enclosing statement
		if (gotAQuestion)
		{
if (Global.debug) {			System.out.println(assignmentQuestion);
}			int i;
			if (assignmentQuestion.getIndex() != -1)
			{
if (Global.debug) {				System.out.println("This might be wrong");
}				i = Global.getCurrentSymbolTable().get(name, assignmentQuestion.getIndex());
			}
			else
			{
				i = Global.getCurrentSymbolTable().get(assignmentQuestion.getVariable());
			}
			setAssignmentQuestionAnswer(i);
			connector.addQuestion(assignmentQuestion);
			connector.endPar();
			connector.endSnap();
			connector.startSnap(node.getLineNumber());
			connector.startPar();
		}
			if (v.getIsArray())
			{
				connector.modifyVar(Global.getCurrentSymbolTable().getVariable(name), index, value);
			}
			else if (v instanceof ByValVariable)
			{
				connector.modifyVar(Global.getCurrentSymbolTable().getVariable(name), value);
			}
			
			else
			{
				ByCopyRestoreVariable var = 
				(ByCopyRestoreVariable)Global.getCurrentSymbolTable().getVariable(name);
if (Global.debug) {				System.out.println("It's a CR variable named " + name);
}				connector.modifyVar(var, value);
			}

		update(node.getLineNumber(), UPDATE_REASON_ASSIGNMENT);
if (Global.debug) {		System.out.println("Leaving assignment");
}		
	}
	
	public Integer handleExpression(ASTExpression node)
	{
 		Integer value = (Integer) node.jjtGetChild(0).jjtAccept(this, null);
 		return value;
 	}
 	
 	public ArrayList<Integer> handleArgs(ASTArgs node)
 	{
 		ArrayList<Integer> args = new ArrayList<Integer>();
 		int numArgs = node.jjtGetNumChildren();
 		for (int i = 0; i < numArgs; i++)
 		{
 			args.add((Integer)node.jjtGetChild(i).jjtAccept(this, null));
 		}
 		node.gatherArgs();
 		return args;
 	}
	
	public Integer handleOp(ASTOp node)
	{
		if (node.getOp().equals("+"))
		{
			return (Integer)node.jjtGetChild(0).jjtAccept(this, null) +
				(Integer)node.jjtGetChild(1).jjtAccept(this, null);
		}
		else if (node.getOp().equals("-"))
		{
			return (Integer)node.jjtGetChild(0).jjtAccept(this, null) -
				(Integer)node.jjtGetChild(1).jjtAccept(this, null);
		}
		return 0;
	}
	
	public Integer handleNum(ASTNum node)
	{
if (Global.debug) {		System.out.println("gg" + node.getValue());
}		return node.getValue();
	}
	public Object visit(ASTProgram node, Object data)
	{
		handleProgram(node);
		return null;
	}
	
	public Object visit(ASTDeclarationList node, Object data)
	{
		handleDeclarationList(node);
		return null;
	}
	
	public Object visit(ASTDeclaration node, Object data)
	{
		handleDeclaration(node);
		return null;
	}
	
	public Object visit(ASTVarDecl node, Object data)
	{
		handleVarDecl(node);
		return null;
	}
	
	//FIXME
	public Object visit(ASTArrayDeclaration node, Object data)
	{
		handleArrayDeclaration(node);
		return null;
	}
	
	public Object visit(ASTFunction node, Object data)
	{	
		handleFunction(node);
		return null;
	}
	
	public Object visit(ASTStatementList node, Object data)
	{
		handleStatementList(node);
		return null;
	}
  	public Object visit(ASTStatement node, Object data)
  	{
  		handleStatement(node);
  		return null;
  	}
  	public Object visit(ASTCall node, Object data)
  	{
  		return handleCall(node);
  	}
  	public Object visit(ASTVar node, Object data)
  	{
  		return handleVar(node);
  	}
  	public Object visit(ASTAssignment node, Object data)
  	{
  		handleAssignment(node);
  		return null;
  	}
 	public Object visit(ASTExpression node, Object data)
 	{
		return handleExpression(node);
 	}
  	public Object visit(ASTArgs node, Object data)
  	{
  		return handleArgs(node);
  	}
  	public Object visit(ASTOp node, Object data)
  	{
  		return handleOp(node);
  	}
  	public Object visit(ASTNum node, Object data)
  	{
  		return handleNum(node);
  	}
  	
  	public void leaveScope()
  	{
if (Global.debug) {  		System.out.println("Leaving scope " + Global.getCurrentSymbolTable().getName());
}		
  		update(-1, UPDATE_REASON_LEAVEFUN);
  	}
}

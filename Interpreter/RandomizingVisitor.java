package Interpreter;

import java.util.*;

public class RandomizingVisitor implements VizParserVisitor {

	
	String[] possVars = {"g","m","n", "v", "w"};
	
	@Override
	public Object visit(SimpleNode node, Object data) {
		
		return null;
	}

	@Override
	public Object visit(ASTProgram node, Object data) {
		// add 1-3 var decls
		Random r = new Random();
		int numOfVars = r.nextInt(3) + 1;
		
		SymbolTable symbols;
		for (int i = 0; i < numOfVars; i++)
		{
			symbols = Global.getSymbolTable();
			
			int varId = r.nextInt();
			ASTVarDecl var = new ASTVarDecl(varId);
			var.jjtSetParent(node);
			
			String varName = getRandomItem(possVars);
			while(symbols.get(varName) != -255)
			{
				varName = getRandomItem(possVars);
			}
			
			var.setName(varName);
			//TODO: how do I get a Variable to put into the symbolTable
			
			ASTExpression exp = new ASTExpression(r.nextInt());
			
			exp.jjtSetParent(var);
			
			ASTNum num = new ASTNum(r.nextInt());
			
			num.jjtSetParent(exp);
			
			num.setValue(r.nextInt(5)+1);
			
			exp.jjtAddChild(num, 0);
			
			var.jjtAddChild(exp, 0);
			
			//add var to ASTprogram
			node.jjtAddChild(var, i);
			
		}
		
		//TODO: add an array
		
		node.childrenAccept(this, null);
		
		return null;
	}

	@Override
	public Object visit(ASTVarDecl node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTId node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTFunction node, Object data) {
		if (node.getName().equals("main"))
		{
			visitMain(node, data);
		}
		else
		{
			visitFunc(node, data);
		}
		return null;
	}

	@Override
	public Object visit(ASTCall node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTVar node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTassignment node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTExpression node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTargs node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTparams node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTop node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object visit(ASTDeclarationList node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTDeclaration node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTNum node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * this limits the entire randomizer to one function other than main
	 * @param node
	 * @param data
	 * @return
	 */
	private Object visitMain(ASTFunction node, Object data)
	{
		// add 0-2 var decls
		Random r = new Random();
		int numOfVars = r.nextInt(3);
		
		SymbolTable symbols;
		for (int i = 0; i < numOfVars; i++)
		{
			symbols = Global.getSymbolTable();
			
			int varId = r.nextInt();
			ASTVarDecl var = new ASTVarDecl(varId);
			var.jjtSetParent(node);
			
			//TODO: how do we decide which var names to use in here?
			String varName = getRandomItem(possVars);
			
			
			var.setName(varName);
			//TODO: how do I get a Variable to put into the symbolTable
			
			ASTExpression exp = new ASTExpression(r.nextInt());
			
			exp.jjtSetParent(var);

			ASTNum num = new ASTNum(r.nextInt());
			
			num.jjtSetParent(exp);
			
			num.setValue(r.nextInt(5)+1);
			
			exp.jjtAddChild(num, 0);
			
			var.jjtAddChild(exp, 0);
			
			//add var to ASTprogram
			node.jjtAddChild(var, i);
			
		}
		// TODO: add 0-1 array decl
		
		// add funcCall
		
		ASTCall call = new ASTCall(r.nextInt());
		call.jjtSetParent(node);
		
		HashMap<String, ASTFunction> funcs = Global.getFunctions();
		
		Set<String> keys = funcs.keySet();
		String callName;
		Iterator<String> iter = keys.iterator();
		while(iter.hasNext())
		{
			callName = iter.next();
			if (!callName.equals("main"))
				break;
		}
		
		call.setName(callName);
		
		//decide on the number of params the second func will have.
		
		HashSet<String> varNames = node.getSymbolNames();
		
		//if true 3 params, else 2
		if (r.nextBoolean())
		{
			String[] parameters = new String[3];
			String[] varNameArray = new String[varNames.size()]; 
			varNames.toArray(varNameArray);
			
			//TODO: get it so there's repeated params sometimes
			for (int i = 0; i < 3; i++)
			{
				parameters[i] = getRandomItem(varNameArray);
			}

			call.addParams(parameters);
			
			funcs.get(callName).addParams("x","y","z");
		}
		else
		{
			String[] parameters = new String[2];
			String[] varNameArray = new String[varNames.size()]; 
			varNames.toArray(varNameArray);
			
			//TODO: get it so there's repeated params sometimes
			for (int i = 0; i < 2; i++)
			{
				parameters[i] = getRandomItem(varNameArray);
			}
			
			call.addParams(parameters);
			funcs.get(callName).addParams("x", "y");
		}
		
		node.jjtAddChild(call, node.jjtGetNumChildren());
		
		return null;
	}
	
	private Object visitFunc(ASTFunction node, Object data)
	{
		
		return null;
	}
	
	private <T> T getRandomItem(T[] array)
	{
		Random r = new Random();
		int rand = r.nextInt(array.length);
		return array[rand];
	}

	/**
	 * 
	 * @param set HashSet of T to get and remove from
	 * @param num number of items to remove from set
	 * @return an array of T
	 */
	/*
	private <T> T[] getAndRemoveRandomly (T[] set, int num)
	{
		Random r = new Random();
		ArrayList<T> list = new ArrayList<T>(num);
		for (int i = 0; i < num; i++)
		{
			int rNum = r.nextInt();
			set.
		}
		
		
	}
*/
}

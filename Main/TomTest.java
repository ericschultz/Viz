package Main;
import java.io.*;
import java.util.*;

public class TomTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	 
	public static XAALScripter scripter;
	public static void main(String[] args) throws Exception 
	{
		scripter = new XAALScripter();
		
		Scope global = new Scope("Global", "blue");
		Variable var1 = new Variable("x", 3, false);
		Variable var2 = new Variable("y", 12, false);
		var2.addCopy();
		global.addVariable(var1);
		global.addVariable(var2);
		Scope main = new Scope("main", "red");
		main.setHidden(true);
		Variable var3 = new Variable("a", 11, true);
		var3.setReference(var1);
		main.addVariable(var3);
		global.addScope(main);
		
		Scope foo = new Scope("foo", "green");
		foo.setHidden(true);
		Variable var4 = new Variable("q", 0, false);
		Variable var5 = new Variable("p", 5, true);
		var5.setReference(var3);
		global.addScope(foo);
		global.draw(scripter);
		
		
		//Start writing slides
		
		scripter.startSlide();
		scripter.startPar();
			showScope(main);
			showVar(var3);
		scripter.endPar();	
		scripter.endSlide();
		
		scripter.startSlide();
		scripter.startPar();
			showScope(foo);
			showVar(var4);
			showVar(var5);
		scripter.endPar();
		
		scripter.startPar();
			//Move a copy down
			moveCopy(var2, var4);
		scripter.endPar();
		scripter.endSlide();
		
		
		FileWriter writer = new FileWriter("C:\\Users\\Eric\\Desktop\\tomxaal.xaal");
		
		writer.write(scripter.toString());
		
		writer.close();
	}
	
	public static void moveCopy(Variable var1, Variable var2)
	{
		ArrayList<String> ids = var1.getIds();
		String lastCopy = ids.get(ids.size() -1 );
		
		int startX = var1.getXPos();
		int startY = var1.getYPos();
		
		int endX = var2.getXPos();
		int endY = var2.getXPos();
		
		int moveX = startX - endX;
		int moveY = startY - endY;
		
		try
		{
			scripter.addTranslate(moveX, moveY, lastCopy);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		
		
	}
	public static void showScope(Scope s)
	{
			ArrayList<String> ids = s.getIds();
			for (String id : ids)
			{
				try
				{
					scripter.addShow(id);
				}
				catch (Exception e)
				{
					System.out.println(e);
				}
			}

	}
	
	public static void showVar(Variable v)
	{
		ArrayList<String> ids = v.getIds();
		for (String id : ids)
		{
			try
			{
				scripter.addShow(id);
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		}
	}

}

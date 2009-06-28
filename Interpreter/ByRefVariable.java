package Interpreter;
import java.util.*;

public class ByRefVariable extends AbstractVariable implements Variable
{
	//The ByValVariable we're referring to
	private ByValVariable ref;
	private int refIndex = -1;
	
	public ByRefVariable(ByValVariable ref)
	{
		setRef(ref);
	}
	
	public void setRef(ByValVariable ref)
	{
		this.ref = ref;
	}
	
	public void setRef(ByValVariable ref, int index)
	{
		this.ref = ref;
		this.refIndex = index;
	}	
	
	public int getRefIndex()
	{
		return this.refIndex;
	}
	
	public ByValVariable getRef()
	{
		return this.ref;
	}
	
	public void setValue(int value)
	{
		System.out.println("ref: " + ref);
		System.out.println("Setting value");
		this.ref.setValue(value);
		System.out.println("Done setting value");
	}
	
	public int getValue()
	{
		System.out.println("Getting value");
		if (this.refIndex == -1) //Normal var
		{
			return this.ref.getValue();
		}
		return this.ref.getValue(refIndex);
	}
	
	public void setParam()
	{
		this.isParam = true;
	}
	
	public boolean getIsParam()
	{
		return this.isParam;
	}
	
	public void setArray()
	{
		System.out.println("A Reference variable can't be an array, ERROR");
	}
	
	public boolean getIsArray()
	{
		return false;
	}
	
	public ArrayList<Integer> getValues()
	{
		System.out.println("Not an array, ERROR");
		return null;
	}
	
	public int getValue(int subscript)
	{
		System.out.println("Not an array, ERROR");
		return 0;
	}

	public void setValue(int value, int index)
	{
		System.out.println("Not an array, ERROR");
	}
}
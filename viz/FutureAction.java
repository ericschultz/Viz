package viz;

public abstract class FutureAction {

	private int snapNum;
	
	public FutureAction(int snapNum)
	{
		this.snapNum = snapNum;
	}

	public int getSnapNum()
	{
		return snapNum;
	}
	
	public void setSnapNum(int snapNum)
	{
		this.snapNum = snapNum;
	}
}

package viz;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 *
 * @author Eric
 *
 */
public class CodePage implements Drawable {
	private String[] code;

	// each position 0,1 or 2 has a list of strings it has to make
	private ArrayList<LinkedList<String>> copiesToMake = new ArrayList<LinkedList<String>>();
	
	//each position 0,1 or 2 has a list of copy ids it owns
	private ArrayList<LinkedList<String>> copiesOwned = new ArrayList<LinkedList<String>>();
	
	//all the ids for normal text strings and ones that have been used already
	private ArrayList<String> ids;
	
	//the XAALIds for each line
	private ArrayList<String> lineToXaalId;
	
	//the line number at which the call will happen and the copies should be written
	private int callLineNum = -1;

	private String id;
	
	private int lineHeight;
	
	public final int[] fromPosX = {70,110,140};
	public final int[] toPosX = {10, 40, 70};
	//where the top of the codepage starts on the page
	public final int y = 10;
	
	//where the left side of the codepage starts
	public final int x = 0;
	
	public CodePage(String id, String[] code)
	{
		setup();
		this.code = code;
		this.id = id;
		for (int i = 0; i < 3; i++)
		{
			copiesToMake.add(i, new LinkedList<String>());
			copiesOwned.add(i, new LinkedList<String>());
		}
	}
	
	private void setup()
	{
		copiesToMake = new ArrayList<LinkedList<String>>();
		copiesOwned = new ArrayList<LinkedList<String>>();
		lineToXaalId = new ArrayList<String>();
		ids = new ArrayList<String>();
	}

	
	public void addCopy(int pos, String str)
	{
		copiesToMake.ensureCapacity(pos);
	
		copiesToMake.get(pos).push(str);

			
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public ArrayList<String> getIds()
	{
		return ids;
	}
	
	public ArrayList<String> getLineToXaalId()
	{
		return lineToXaalId;
	}
	
	public void setCallLineNum(int lineNum)
	{
		if (callLineNum == -1)
			callLineNum = lineNum;
	}
	
	public int numOfCopies(int pos)
	{
		return copiesOwned.get(pos).size();
	}

	public String peekCopy(int pos)
	{
		return copiesOwned.get(pos).peek();
	}
	
	public String popCopy(int pos)
	{
		return copiesOwned.get(pos).pop();
	}
	
	public void receiveCopyOwnership(String copyId)
	{
		ids.add(copyId);
	}
	
	public int getLineHeight()
	{
		return lineHeight;
	}
	
	@Override
	public void draw(XAALScripter scripter) {

		int dy = 0;
		int x = 0;
		Graphics g = (new BufferedImage(1,1, 
                BufferedImage.TYPE_INT_RGB)).getGraphics();
		//change to Lucida Bright when we post
		g.setFont(new Font("Monospaced", 
				Font.PLAIN, 20));
		
		FontMetrics fm = g.getFontMetrics();
		lineHeight = fm.getHeight();
		
		//write out all the code
		for (int i = 0; i < code.length; i++)
		{
			String id = scripter.addText(x, y + dy, code[i], "black", true);
			ids.add(id);
			lineToXaalId.add(id);
			dy += lineHeight;
		}
		
		//write out all the copies
		for (int i = 0; i < copiesToMake.size(); i++)
		{
			copiesOwned.add(new LinkedList<String>());
			LinkedList<String> posCopiesToMake = copiesToMake.get(i);
			
			while (posCopiesToMake.size() > 0)
			{
				String temp = posCopiesToMake.pop();
				String id = scripter.addText(fromPosX[i], y + (lineHeight*callLineNum)
						, temp, "black", true);
				copiesOwned.get(i).add(id);
			} 
		}
		
	}

}

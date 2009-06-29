package viz;


public abstract class Question implements Drawable {
	
	protected int slideId = -1;
	
	protected String questionText;
	
	//For a var, the index we asked about.
	protected int index = -1;
	
	protected Question(String questionText)
	{
		this.questionText = questionText;
		setup();
	}
	
	protected Question(String questionText, int slideId)
	{
		this.questionText = questionText;
		this.slideId = slideId;
		setup();
	}
	
	protected abstract void setup();
	
	//TODO: look at this
	public void setQuestionText(String questionText){
        this.questionText = questionText.trim();
    }
    
    
        public void setIndex(int index)
        {
        	this.index = index;
        }
        
        public int getIndex()
        {
        	return this.index;
        }
	
	public int getSlideId()
	{
		return slideId;
	}
	
	public String getText()
	{
		return this.questionText;
	}
	
	public void setSlideId(int slide)
	{
		slideId = slide;
	}
	
	public abstract void draw(XAALScripter scripter);
}

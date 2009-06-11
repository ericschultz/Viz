import org.jdom.*;
import org.jdom.output.*;
import java.util.*;

public class XAALScripter {
	private Document document = new Document();
	private final Namespace defaultNS = Namespace.getNamespace("http://www.cs.hut.fi/Research/SVG/XAAL");
	
	private final int DEFAULT_FONT_SIZE = 16;
	private final String DEFAULT_FONT_FAMILY = "Lucida Bright";
	
	private int rectNum = 0;
	private int textNum = 0;
	private int lineNum = 0;
	private int triangleNum = 0;
	
	public XAALScripter()
	{
		Element xaalRoot = new Element("xaal", defaultNS);
		 
		xaalRoot.setAttribute("version", 1.0 + "");
		
		Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		xaalRoot.addNamespaceDeclaration(xsi);
		
		Attribute schemaLocation = new Attribute("schemaLocation", 
				"http://www.cs.hut.fi/Research/SVG/XAAL xaal.xsd", xsi);
		xaalRoot.setAttribute(schemaLocation);
		
		Element initial = new Element("initial", defaultNS);
		xaalRoot.addContent(initial);
		
		Element animation = new Element("animation", defaultNS);
		Element sequence = new Element("seq", defaultNS);
		animation.addContent(sequence);
		xaalRoot.addContent(animation);
		
		document.setRootElement(xaalRoot);
	}
	
	/**
	 * Adds a new non-hidden black rectangle to the initial element of a XAAL script.
	 * @param x y coordinate for the top left corner of the rectangle.
	 * @param y y coordinate for the top left corner of the rectangle.
	 * @param width width of the rectangle in pixels.
	 * @param height height of the rectangle in pixels.
	 * @return a String containing the id of the rectangle added.
	 */
	public String addRectangle(int x, int y, int width, int height)
	{
		return addRectangle(x, y, width, height, "black");
	}
	
	/**
	 * Adds a new non-hidden rectangle to the initial element of a XAAL script.
	 * @param x y coordinate for the top left corner of the rectangle.
	 * @param y y coordinate for the top left corner of the rectangle.
	 * @param width width of the rectangle in pixels.
	 * @param height height of the rectangle in pixels.
	 * @param color color of the rectangle's border. Must be a named XAAL color.
	 * @return a String containing the id of the rectangle added.
	 */
	public String addRectangle(int x, int y, int width, int height, String color)
	{
		return addRectangle(x, y, width, height, "black", false);
	}
	
	/**
	 * Adds a new rectangle to the initial element of a XAAL script.
	 * @param x y coordinate for the top left corner of the rectangle.
	 * @param y y coordinate for the top left corner of the rectangle.
	 * @param width width of the rectangle in pixels.
	 * @param height height of the rectangle in pixels.
	 * @param color color of the rectangle's border. Must be a named XAAL color.
	 * @param hidden specifies whether the rectangle should be hidden initially.
	 * @return a String containing the id of the rectangle added.
	 */
	public String addRectangle(int x, int y, int width, int height, String color, boolean hidden)
	{
		Element initial = document.getRootElement().getChild("initial", defaultNS);
		
		Element rect = new Element("polyline");
		
		String idVal = "rectangle" + rectNum;
		rectNum++;
		
		rect.setAttribute("id", idVal );
		
		rect.setAttribute("hidden", hidden + "");
		
		Element x1y1 = new Element("coordinate");
		x1y1.setAttribute("x", x + "");
		x1y1.setAttribute("y", y + "");
		rect.addContent(x1y1);
		
		Element x1y2 = new Element("coordinate");
		x1y2.setAttribute("x", x + "" );
		x1y2.setAttribute("y", (y+height) + "");
		rect.addContent(x1y2);
		
		Element x2y2 = new Element("coordinate");
		x2y2.setAttribute("x", (x+width) + "");
		x2y2.setAttribute("y", (y+height) + "");
		rect.addContent(x2y2);
		
		Element x2y1 = new Element("coordinate");
		x2y1.setAttribute("x", (x+width) + "");
		x2y1.setAttribute("y", y + "");
		rect.addContent(x2y1);
		
		Element x1y1_2 = new Element("coordinate");
		x1y1_2.setAttribute("x", x + "");
		x1y1_2.setAttribute("y", y + "");
		rect.addContent(x1y1_2);
		
		Element closed = new Element("closed");
		closed.setAttribute("value", true + "");
		rect.addContent(closed);
		
		Element style = new Element("style");
		
		Element colorElem = new Element("color");
		colorElem.setAttribute("name", color);
		style.addContent(colorElem);
		
		rect.addContent(style);
		
		initial.addContent(rect);
		
		return idVal;
	}
	
	//TODO: what to call the text?
	/**
	 * Adds a new black, non-hidden, text(?) with default font size
	 * and family to the initial element of the XAAL script.
	 * @param x x coordinate for the top left corner of the text(?)
	 * @param y y coordinate for the top left corner of the text(?)
	 * @param contents the text string to be displayed.
	 * @return a String containing the id of the text(?) added.
	 */
	public String addText(int x, int y, String contents)
	{
		return addText(x, y, contents, "black");
	}
	
	/**
	 * Adds a new non-hidden, text(?) with default font size
	 * and family to the initial element of the XAAL script.
	 * @param x x coordinate for the top left corner of the text(?)
	 * @param y y coordinate for the top left corner of the text(?)
	 * @param contents the text string to be displayed.
	 * @param color the color of the text string. Must be a named XAAL color.
	 * @return a String containing the id of the text(?) added.
	 */
	public String addText(int x, int y, String contents, String color)
	{
		return addText(x, y, contents, color, false);
	}
	
	/**
	 * Adds a new text(?) with default font size
	 * and family to the initial element of the XAAL script.
	 * @param x x coordinate for the top left corner of the text(?)
	 * @param y y coordinate for the top left corner of the text(?)
	 * @param contents the text string to be displayed.
	 * @param color the color of the text string. Must be a named XAAL color.
	 * @param hidden whether the text should be initially hidden.
	 * @return a String containing the id of the text(?) added.
	 */
	public String addText(int x, int y, String contents, String color, boolean hidden)
	{
		return addText(x, y, contents, color, hidden, DEFAULT_FONT_SIZE);
	}

	/**
	 * Adds a new text(?) with default font family to the initial 
	 * element of the XAAL script.
	 * @param x x coordinate for the top left corner of the text(?)
	 * @param y y coordinate for the top left corner of the text(?)
	 * @param contents the text string to be displayed.
	 * @param color the color of the text string. Must be a named XAAL color.
	 * @param hidden whether the text should be initially hidden.
	 * @return a String containing the id of the text(?) added.
	 * @param fontSize the font size of the text, in points.
	 * @return a String containing the id of the text(?) added.
	 */
	public String addText(int x, int y, String contents, String color, boolean hidden, int fontSize)
	{
		return addText(x, y, contents, color, hidden, fontSize, DEFAULT_FONT_FAMILY);
	}
	
	/**
	 * Adds a new text(?) to the initial element of the XAAL script.
	 * @param x x coordinate for the top left corner of the text(?)
	 * @param y y coordinate for the top left corner of the text(?)
	 * @param contents the text string to be displayed.
	 * @param color the color of the text string. Must be a named XAAL color.
	 * @param hidden whether the text should be initially hidden.
	 * @return a String containing the id of the text(?) added.
	 * @param fontSize the font size of the text, in points.
	 * @param fontFamily the name of font family to use when displaying text.
	 * @return a String containing the id of the text(?) added.
	 */
	public String addText(int x, int y, String contents, String color, boolean hidden,
			int fontSize, String fontFamily)
	{
		Element initial = document.getRootElement().getChild("initial", defaultNS);
		
		Element text = new Element("text");
		
		String idVal = "text" + textNum;
		textNum++;
		text.setAttribute("id", idVal);
		
		text.setAttribute("hidden", hidden + "");
		
		Element coordinate = new Element("coordinate");
		coordinate.setAttribute("x", x + "");
		coordinate.setAttribute("y", y + "");
		text.addContent(coordinate);
		
		Element contentsElem = new Element("contents");
		
		Text contentsVal = new Text(contents);
		contentsElem.addContent(contentsVal);
		
		text.addContent(contentsElem);
		
		Element style = new Element("style");
		
		Element colorElem = new Element("color");
		colorElem.setAttribute("name", color);
		style.addContent(colorElem);
		
		Element font = new Element("font");
		font.setAttribute("size", fontSize + "");
		font.setAttribute("family", fontFamily);
		style.addContent(font);
		
		text.addContent(style);
		
		initial.addContent(text);
		
		return idVal;
	}
	
	/**
	 * Adds a new non-hidden black line to the initial element of the XAAL script.
	 * @param x1 x coordinate for first point.
	 * @param y1 y coordinate for first point.
	 * @param x2 x coordinate for second point.
	 * @param y2 y coordinate for second point.
	 * @return a String containing the id of the line added.
	 */
	public String addLine(int x1, int y1, int x2, int y2)
	{
		return addLine(x1, y1, x2, y2, "black");
	}
	
	/**
	 * Adds a new non-hidden black line to the initial element of the XAAL script.
	 * @param x1 x coordinate for first point.
	 * @param y1 y coordinate for first point.
	 * @param x2 x coordinate for second point.
	 * @param y2 y coordinate for second point.
	 * @param color the color of line. Must be a named XAAL color.
	 * @return a String containing the id of the line added.
	 */
	public String addLine(int x1, int y1, int x2, int y2, String color)
	{
		return addLine(x1, y1, x2, y2, color, false);
	}
	
	/**
	 * Adds a new non-hidden black line to the initial element of the XAAL script.
	 * @param x1 x coordinate for first point.
	 * @param y1 y coordinate for first point.
	 * @param x2 x coordinate for second point.
	 * @param y2 y coordinate for second point.
	 * @param color the color of line. Must be a named XAAL color.
	 * @param hidden whether the line should be initially hidden
	 * @return a String containing the id of the line added.
	 */
	public String addLine(int x1, int y1, int x2, int y2, String color, boolean hidden)
	{
		Element initial = document.getRootElement().getChild("initial", defaultNS);
		
		Element line = new Element("line");
		
		String idVal = "line" + lineNum;
		lineNum++;
		line.setAttribute("id", idVal);
		
		line.setAttribute("hidden", hidden + "");
		
		Element coordinate = new Element("coordinate");
		coordinate.setAttribute("x", x1 + "");
		coordinate.setAttribute("y", y1 + "");
		line.addContent(coordinate);
		
		coordinate = new Element("coordinate");
		coordinate.setAttribute("x", x2 + "");
		coordinate.setAttribute("y", y2 + "");
		line.addContent(coordinate);
		
		Element style = new Element("style");
		
		Element colorElem = new Element("color");
		colorElem.setAttribute("name", color);
		style.addContent(colorElem);
		
		line.addContent(style);
		
		initial.addContent(line);
		
		return idVal;
	}
	
	/**
	 * Adds a new non-hidden black equilateral triangle to the initial element of a XAAL script.
	 * @param x y coordinate for the top left corner of the rectangular box containing the triangle.
	 * @param y y coordinate for the top left corner of the rectangular box containing the triangle.
	 * @param width width (and height) of the triangle in pixels.
	 * @return the String containing the id of the triangle added.
	 */
	public String addTriangle(int x, int y, int width)
	{
		return addTriangle(x, y, width, "black");
	}
	
	/**
	 * Adds a new non-hidden equilateral triangle to the initial element of a XAAL script.
	 * @param x y coordinate for the top left corner of the rectangular box containing the triangle.
	 * @param y y coordinate for the top left corner of the rectangular box containing the triangle.
	 * @param width width (and height) of the triangle in pixels.
	 * @param color the border color of the triangle
	 * @return the String containing the id of the triangle added.
	 */
	public String addTriangle(int x, int y, int width, String color)
	{
		return addTriangle(x, y, width, color, false);
	}
	
	/**
	 *  * Adds a new equilateral triangle to the initial element of a XAAL script.
	 * @param x y coordinate for the top left corner of the rectangular box containing the triangle.
	 * @param y y coordinate for the top left corner of the rectangular box containing the triangle.
	 * @param width width (and height) of the triangle in pixels.
	 * @param color the border color of the triangle.
	 * @param hidden whether the triangle is hidden initially.
	 * @return the String containing the id of the triangle added.
	 */
	public String addTriangle(int x, int y, int width, String color, boolean hidden)
	{
		Element initial = document.getRootElement().getChild("initial", defaultNS);
		
		Element triangle = new Element("polyline");
		
		String idVal = "triangle" + lineNum;
		triangleNum++;
		triangle.setAttribute("id", idVal);
		
		triangle.setAttribute("hidden", hidden + "");
		
		Element coordinate = new Element("coordinate");
		coordinate.setAttribute("x", (x +(width/2)) + "");
		coordinate.setAttribute("y", y + "");
		triangle.addContent(coordinate);
		
		
		coordinate = new Element("coordinate");
		coordinate.setAttribute("x", (x + width) + "");
		coordinate.setAttribute("y", (y + width) + "");
		triangle.addContent(coordinate);
		
		coordinate = new Element("coordinate");
		coordinate.setAttribute("x", (x) + "");
		coordinate.setAttribute("y", (y + width) + "");
		triangle.addContent(coordinate);
		
		coordinate = new Element("coordinate");
		coordinate.setAttribute("x", (x +(width/2)) + "");
		coordinate.setAttribute("y", y + "");
		triangle.addContent(coordinate);
		
		Element closed = new Element("closed");
		closed.setAttribute("value", true + "");
		triangle.addContent(closed);
		
		Element style = new Element("style");
		
		Element colorElem = new Element("color");
		colorElem.setAttribute("name", color);
		style.addContent(colorElem);
		
		triangle.addContent(style);
		
		initial.addContent(triangle);
		
		return idVal;
	}
	
	public void addCircle()
	{
	
	}

	public String addArrow(String originName, String destName, boolean isDashed)
	{
		Element initial = document.getRootElement().getChild("initial", defaultNS);
		
		List elements = initial.getChildren();
		Element origin = null;
		Element dest = null;
		for (Object o : elements)
		{
			Element e = (Element) o;
			Attribute a = e.getAttribute("id");
			System.out.println(a);
			if (e.getAttribute("id").getValue().equals(originName))
			{
				origin = e;
			}
			else if (e.getAttribute("id").getValue().equals(destName))
			{
				dest = e;
			}
		}
		//find our starting point

		Element startPos = origin.getChild("coordinate");
		Element endPos = dest.getChild("coordinate");
		int startX = 0;
		int startY = 0;
		int endX = 0;
		int endY= 0;
		try
		{	
			startX = startPos.getAttribute("x").getIntValue();
			startY = startPos.getAttribute("y").getIntValue();
			System.out.println("X: " + startX + " Y: " + startY);
			
			endX = endPos.getAttribute("x").getIntValue();
			endY = endPos.getAttribute("y").getIntValue();
			System.out.println("End X: " + endX + " End Y: " + endY);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		
		addLine(startX, startY, startX - 5, startY, "red");
		addLine(startX - 5, startY, startX -5, endY, "red");
		addLine(startX -5, endY, endX, endY, "red");
		
		return "asdf";
	}	
	
	//TODO: just for testing!
	public String toString()
	{
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		return outputter.outputString(document);
	
	}
}

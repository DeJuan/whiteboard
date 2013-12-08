package canvas;

import java.awt.Color;

public class Brushstroke 
{
	private final int startX;
	private final int startY;
	private final int endX;
	private final int endY;
	private final Color color;
	private final int width;
	
	
	public Brushstroke(int startX, int startY, int endX, int endY, Color color, int width)
	{
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.color = color;
		this.width = width;
	}
	
	public Brushstroke(String information)
	{
		String[] info = information.split(",");
		this.startX = Integer.parseInt(info[0]);
		this.startY = Integer.parseInt(info[1]);
		this.endX = Integer.parseInt(info[2]);
		this.endY = Integer.parseInt(info[3]);
		this.color = new Color(Integer.parseInt(info[4]));
		this.width =  Integer.parseInt(info[5]);
	}
	
	public int getStartX()
	{
		return this.startX;
	}
	
	public int getStartY()
	{
		return this.startY;
	}
	public int getEndX()
	{
		return this.endX;
	}
	
	public int getEndY()
	{
		return this.endY;
	}
	
	public Color getColor()
	{
		return this.color;
	}
	
	public int getWidth()
	{
		return this.width;
	}
	
	@Override
	public String toString()
	{
		return "" + this.startX + " " + this.startY + " " + this.endX + " " + this.endY + " " + this.color.getRGB() + " " + this.width;
	}
}

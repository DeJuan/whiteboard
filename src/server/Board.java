package server;

import java.awt.Color;
import java.net.Socket;
import java.util.ArrayList;

import canvas.Brushstroke;

public class Board 
{
	/*
	 * So in the boards, weÅfll need to either use a dictionary to store boards and their associated strokes, 
	 * or actually set up a field in the board class itself to hold all its associated strokes.  
	 * Every board will also need to maintain a list of users associated with it. 
	 * We should create a Board class which contains an ArrayList of strokes, a list of users, and a canvas. 
	 */
	private int ID;
	private ArrayList<Brushstroke> listOfStrokes = new ArrayList<Brushstroke>();
	private ArrayList<Socket> userList = new ArrayList<Socket>();
	
	public Board(int identification)
	{
		this.ID = identification;
	}
	
	public void registerStroke(Brushstroke stroke)
	{
		this.listOfStrokes.add(stroke);
	}
	
	public void registerStroke(String stroke)
	{
		String[] inp = stroke.split(" "); //"x1 y1 x2 y2 (RRR,GGG,BBB) width
		String[] colorParse = inp[4].split(",");
		Color sColor = new Color(Integer.valueOf(colorParse[0]), Integer.valueOf(colorParse[1]), Integer.valueOf(colorParse[2]));
		Brushstroke currentStroke =  new Brushstroke(Integer.valueOf(inp[0]),Integer.valueOf(inp[1]),Integer.valueOf(inp[2]),Integer.valueOf(inp[3]),sColor,Integer.valueOf(inp[5]));
		this.listOfStrokes.add(currentStroke);
	}
	public void addUser(Socket newUser)
	{
		this.userList.add(newUser);
	}
	
	
	
	public int retrieveID()
	{
		return this.ID;
	}
	
	public ArrayList<Socket> getBoardUsers()
	{
		return this.userList;
	}
}

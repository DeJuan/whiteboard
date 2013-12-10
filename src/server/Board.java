package server;

import java.awt.Color;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	private List<Brushstroke> listOfStrokes =  Collections.synchronizedList(new ArrayList<Brushstroke>());
	private ArrayList<Socket> userList = new ArrayList<Socket>();
	
	public Board(int identification)
	{
		this.ID = identification;
	}
	
	public void registerStroke(Brushstroke stroke)
	{
		this.listOfStrokes.add(stroke);
		updateUsers(stroke);
	}
	
	public String registerStroke(String stroke)
	{
		String[] inp = stroke.split(" "); //"brushstroke x1 y1 x2 y2 ColorData width boardNumber
		//String[] colorParse = inp[4].split(",");
		Color sColor = new Color(Integer.parseInt(inp[5]));
		Brushstroke currentStroke =  new Brushstroke(Integer.valueOf(inp[1]),Integer.valueOf(inp[2]),Integer.valueOf(inp[3]),Integer.valueOf(inp[4]),sColor,Integer.valueOf(inp[6]));
		this.listOfStrokes.add(currentStroke);
		return updateUsers(currentStroke);
	}
	public void addUser(Socket newUser)
	{
		this.userList.add(newUser);
	}
	
	public String updateUsers(Brushstroke stroke)
	{	
		return stroke.toString();
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

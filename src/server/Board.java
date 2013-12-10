package server;

import java.awt.Color;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import canvas.Brushstroke;

/**
 * This class holds the actual data for a given canvas. It has an identification number which is our index number, 
 * a list of all strokes that have been drawn on it, and a list of sockets attached to the board, one socket per user. 
 * 
 * The concurrency issues of two or more strokes being drawn at once to the registerStroke method are addressed by the synchronized aspect of the
 * list used to store them. The add operation is atomic and synchronized, and thus each stroke will be added in the order that it came in. We can't 
 * synchronize the entire method, or else it'll hang and we'll lose all concurrency as every stroke is drawn in sequence. 
 * @author DeJuan
 *
 */
public class Board 
{
	private int ID;
	private List<Brushstroke> listOfStrokes =  Collections.synchronizedList(new ArrayList<Brushstroke>());
	private ArrayList<Socket> userList = new ArrayList<Socket>();
	
	public Board(int identification)
	{
		this.ID = identification;
	}
	/**
	 * This is a method for registering Brushstrokes directly, if passed in. 
	 * 
	 * @param stroke
	 * @param boardNumber
	 * @return Client-readable string representing the stroke
	 */
	public String registerStroke(Brushstroke stroke, int boardNumber)
	{
		this.listOfStrokes.add(stroke);
		return "brushstroke " + stroke.toString() +" " + boardNumber;
	}
	
	/**
	 * This is a more robust method for registering Brushstrokes, indirectly. What this one does is
	 * take in the string representation of a Brushstroke (which is what the client deals with)
	 * and parse it into a new Brushstroke object, record it, then return the client-readable string representation. 
	 * 
	 * @param String form of a Brushstroke
	 * @param int boardNumber
	 * @return Client-readable string representation of the Brushstroke
	 */
	public String registerStroke(String stroke, int boardNumber)
	{
		String[] inp = stroke.split(" "); //"brushstroke x1 y1 x2 y2 ColorData width boardNumber
		//String[] colorParse = inp[4].split(",");
		Color sColor = new Color(Integer.parseInt(inp[5]));
		Brushstroke currentStroke =  new Brushstroke(Integer.valueOf(inp[1]),Integer.valueOf(inp[2]),Integer.valueOf(inp[3]),Integer.valueOf(inp[4]),sColor,Integer.valueOf(inp[6]));
		this.listOfStrokes.add(currentStroke);
		//System.out.println("From the Board Class, the just added stroke was: " + currentStroke.toString());
		//System.out.println("listOfStrokes currently contains: " + listOfStrokes.toString());
		return "brushstroke " + currentStroke.toString() + " " + boardNumber;
	}
	
	/**
	 * This method takes in a socket and registers it as a user to this Board instance. It is used when a new client connects to the board;
	 * we register their socket as their unique identification. 
	 * @param newUser
	 */
	public void addUser(Socket newUser)
	{
		this.userList.add(newUser);
	}
	
	/**
	 * This method takes in a socket and removes it from the users of this board instance. It is used when a client disconnects from the board. 
	 * @param oldUser
	 */
	public void removeUser(Socket oldUser)
	{
		this.userList.remove(oldUser);
	}
	
	/**
	 * This returns the entire list of strokes that the board has received. This is needed for when a new client connects to a board that has
	 * been used; since the client won't have experienced the strokes that already occurred on the board, we need to update them to show
	 * the strokes. This list accomplishes that purpose in combination with some further modifications carried out by the Server. 
	 * @return List<Brushstroke> listOfStrokes
	 */
	public List<Brushstroke> getStrokes()
	{	
		return this.listOfStrokes;
	}
	
	/**
	 * Just in case the ID is needed for further modification or future implementations, this observer method returns the current board ID.
	 * @return int, board identification number
	 */
	public int retrieveID()
	{
		return this.ID;
	}
	
	/**
	 * This method returns the arrayList containing all the users attached to this board. The userList is comprised of all sockets who use this board for their canvas. 
	 * It is used by the server as a base for sending a client-readable list of all users(with some modifications), and as a socket list for updating all users when a
	 * new stroke is drawn. 
	 * @return
	 */
	public ArrayList<Socket> getBoardUsers()
	{
		return this.userList;
	}
}

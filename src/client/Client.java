package client;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import canvas.*;


public class Client {
	
	private String address;
	private int port;
	public newCanvas ourCanvas;
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	
	public Client(String address, int port) throws IOException{
		this.address = address;
		this.port = port;
		this.ourCanvas= new newCanvas(500,800);
		this.socket= new Socket();
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
	}
	
	public void handleResponses(){
		
	}
	
	/*
	 * interpretString takes a string represent one line segment drawn,
	 * and calls the appropriate methods to draw that line.
	 * @param command - Of the form startx,starty,endx,endy,rgbcolor,width
	 */
	public void interpretString(String command){
		String[] tokensStr = command.split(",");
		List<Integer> tokens = new ArrayList<Integer>();
		for (String i: tokensStr){
			tokens.add(Integer.parseInt(i));
		}
		ourCanvas.drawLineSegment(tokens.get(0),tokens.get(1),tokens.get(2),tokens.get(3),tokens.get(4),tokens.get(5));
	}
	
	public void main(){
		Client client = new Client("blah" , 42);
		int rgb = Color.RED.getRGB();
		client.interpretString("407,271,414,278," + rgb + ",5");
	}
	
	
}

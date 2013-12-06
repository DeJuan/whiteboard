package client;
import java.awt.*;
import java.awt.List;
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
		this.socket= new Socket(this.address, this.port);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
	}
	
	public void handleResponses(){
		
	}
	
	/*
	 * interpretString takes a string represent one line segment drawn,
	 * and calls the appropriate methods to draw that line.
	 * @param command - Of the form startx,starty,endx,endy,rgbcolor,width
	 */
	public void interpretString(String command){
		
		Brushstroke ourStroke = new Brushstroke(command);
		ourCanvas.drawLineSegment(ourStroke);
	}
	
	public void main() throws IOException{
		Client client = new Client("blah" , 4444);
		int rgb = Color.RED.getRGB();
		client.interpretString("407,271,414,278," + rgb + ",5");
	}
	
	
}

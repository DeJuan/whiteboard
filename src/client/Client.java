package client;
import java.awt.*;
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
        this.out.print("joinServer");
	}
	
	
	/*
	 * 
	 */
	public void listen() throws IOException{
		for (String line =in.readLine(); line!=null; line=in.readLine()) {
            handleResponses(line);
		}
	}
	/*
	 * handleResponses is called on incoming strings from the server. It tokenizes the given string,
	 * interprets the tokens, and then calls the appropriate methods to do what the string asks.
	 * @param response - A response from the server, properly formatted per the protocol.
	 */
	public void handleResponses(String response){
		String[] tokens = response.split(" ");
		if (tokens[0].equals("brushstroke")){
			Brushstroke newStroke = new Brushstroke(tokens[1]);
			ourCanvas.drawLineSegment(newStroke);
		}
		
		else if (tokens[0].equals("boardList")){
			//Display all the boards to the user.
			String[] boardList = Arrays.copyOfRange(tokens, 1, tokens.length);
			//TODO: do something with boardList so that the GUI can display available boards.
		}
		else{
			throw new RuntimeException("Recieved an improperly formatted string");
		}
	}
	
	/*
	 * send will send the given string to the server.
	 * @param request - a properly formatted request string, as per the protocol.
	 */
	public void send(String request){
		this.out.print(request);
	}
	
	/*
	 * join takes a boardID and sends a properly formatted joinBoard request to the server
	 */
	public void join(String boardID){
		String request = "joinBoard " + boardID;
		this.send(request);
	}
	
	public void exit(){
		this.send("exitBoard");
		//TODO: deal with switching the socket
	}
	public void main() throws IOException{
		//TODO: replace client arguments with the aruguments given by the user.
		Client client = new Client("blah" , 4444);
	}
	
	
	
}

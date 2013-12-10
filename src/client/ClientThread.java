package client;
import canvas.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import javax.swing.*;


public class ClientThread implements Runnable{
	
	public BufferedReader in;
	public Client client;
	
	public ClientThread(BufferedReader in, Client client){
		this.in = in;
		this.client=client;
	}

	@Override
    public void run(){
		try {
			for (String line =in.readLine(); line!=null; line=in.readLine()) {
	            handleResponses(line);
	        }
        } catch (IOException e) {
	        e.printStackTrace();
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
			Brushstroke newStroke = new Brushstroke(
					Integer.parseInt(tokens[1]), 
					Integer.parseInt(tokens[2]), 
					Integer.parseInt(tokens[3]), 
					Integer.parseInt(tokens[4]), 
					new Color(Integer.parseInt(tokens[5])), 
					Integer.parseInt(tokens[6]));
			this.client.ourCanvas.drawLineSegment(newStroke);
		}
		else if (tokens[0].equals("userList")){
			ArrayList<String> newUserList = new ArrayList<String>();
			for (int i =1; i<tokens.length; i++){
				newUserList.add(tokens[i]);
			}
			this.client.users = newUserList;
			
			//TODO: update GUI's user list at this point.
		}
		else if (tokens[0].equals("Welcome")){}
		else{
			throw new RuntimeException("Recieved an improperly formatted string");
		}
	}
}
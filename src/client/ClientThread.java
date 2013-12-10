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
	            this.client.handleResponses(line);
	        }
        } catch (IOException e) {
	        e.printStackTrace();
        }
	    
    }
	
}
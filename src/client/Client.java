package client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import javax.swing.*;

import canvas.*;

/*
 * The CLient class is used to communicate with the server and GUI. It listens to the server,
 * it sends messages to server based on commands from the GUI, and it updates the GUI based on
 * incoming info from the server.
 */

public class Client {
	
	private String address;
	private int port;
	public int boardNumber;
	private String username;
	public newCanvas ourCanvas;
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	public ArrayList<String> users;
	
	
	public Client(String address, int port, String username, int boardNumber) throws IOException{
		this.address = address;
		this.port = port;
		this.username=username;
		this.boardNumber= boardNumber;
		this.ourCanvas= new newCanvas(500,800,this,boardNumber);
		
		this.socket= new Socket(this.address, this.port);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        
        JFrame window = new JFrame("Freehand Canvas");
		System.out.println("Check");
        
        window.setLayout(new BorderLayout());
        window.addWindowListener(new WindowListener(){

			@Override
            public void windowActivated(WindowEvent arg0) {
            }

			@Override
            public void windowClosed(WindowEvent arg0) {
            }

			@Override
            public void windowClosing(WindowEvent arg0) {
	            exit();
	            
            }

			@Override
            public void windowDeactivated(WindowEvent arg0) {
            }

			@Override
            public void windowDeiconified(WindowEvent arg0) {
            }

			@Override
            public void windowIconified(WindowEvent arg0) {
            }

			@Override
            public void windowOpened(WindowEvent arg0){
            }
        });
        window.add(ourCanvas, BorderLayout.CENTER);
        window.pack();
        window.setVisible(true);
        
        System.out.println("About to listen");
        this.listen();
//        try{
//        	Thread.sleep(1000);
//        }catch(Exception e){
//        	e.printStackTrace();
//        }
        this.join(boardNumber);
        this.getAllBrushstrokes();
        
	}
	
	
	

	/*
	 * Listen() is used to start a new ClientThread to listen to the server.
	 */
	public void listen() throws IOException{
		Thread thr = new Thread(new Runnable(){

			@Override
            public void run() {
				try{
		        	Thread.sleep(1000);
		        }catch(Exception e){
		        	e.printStackTrace();
		        }
				try {
					for (String line =in.readLine(); line!=null; line=in.readLine()) {
						System.out.println(line);
			            handleResponses(line);
			        }
		        } catch (IOException e) {
			        e.printStackTrace();
		        }
	            
            }
		}
		);
		thr.start();
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
			ourCanvas.drawLineSegment(newStroke);
		}
		else if (tokens[0].equals("userList")){
			ArrayList<String> newUserList = new ArrayList<String>();
			for (int i =1; i<tokens.length; i++){
				newUserList.add(tokens[i]);
			}
			this.users = newUserList;
			//this.ourCanvas.getBoardUsers();
		}
		else if (tokens[0].equals("Welcome")){}
		else{
			throw new RuntimeException("Recieved an improperly formatted string");
		}
	}
	
	/*
	 * translateBrushstroke() takes a brushstroke and returns a properly formatted string
	 * as per our protocol.
	 */
	public String translateBrushstroke(Brushstroke b){
		return "brushstroke " + b.toString() + " " + this.boardNumber;
	}
	/*
	 * send will send the given string to the server.
	 * @param request - a properly formatted request string, as per the protocol.
	 */
	public void send(String request){
		//System.out.println(request);
		this.out.println(request);
	}
	
	/*
	 * join takes a boardID and sends a properly formatted joinBoard request to the server
	 */
	public void join(int boardID){
		String request = "joinBoard " + username + " "+ boardID;
		this.send(request);
	}
	/*
	 * getAllBrushstrokes is used to send a getAllBrushstrokes request to the server.
	 */
	public void getAllBrushstrokes(){
		String request = "getAllBrushstrokes " + this.boardNumber;
		this.send(request);
	}
	/*
	 * exit is used to send an exitBoard message to the server.
	 */
	public void exit(){
		this.send("exitBoard "+ username + " " + this.boardNumber);
		//TODO: deal with switching the socket
	}
	
	/*
	 * getBoardNumber number is used by the GUI to get the board number.
	 */
	public int getBoardNumber(){
		return this.boardNumber;
	}
	
	/*
	 * setBoardNumber is used by the GUI to set the board number when the user changes it.
	 * @param boardNum - the number of the board you are switching to.
	 */
	public void setBoardNumber(int boardNum){
		if (boardNum<=0 && boardNum > 10){
			this.boardNumber = boardNum;
		}
		else{
			throw new RuntimeException("Out of bounds");
		}
	}
	/*
	 * requestUserList is used to request a new userList from the server
	 */
	public void requestUserList() {
	    send("userList" + boardNumber);
    }
	
	public static void main(String[] args) throws IOException{
		final JFrame box = new JFrame();
		box.setSize(800,80);
		final JPanel addressReq = new JPanel();
		JLabel addressLabel = new JLabel("Server Address?");
		final JTextField address = new JTextField();
		JLabel portLabel = new JLabel("Port Number?");
		final JTextField port = new JTextField();
		JLabel usernameLabel = new JLabel("Username");
		final JTextField username = new JTextField();
		JLabel boardNumLabel = new JLabel("Board #");
		String[] boardsArray = {"0", "1", "2","3","4","5","6","7","8","9"};
		final JComboBox boards = new JComboBox(boardsArray);
		boards.setSelectedIndex(0);
		JButton go = new JButton("Start!");
		
		go.addActionListener(new ActionListener(){

			@Override
            public void actionPerformed(ActionEvent ae) {
	            String adr = address.getText();
	            int p = Integer.parseInt(port.getText());
	            String user = username.getText();
	            String boardNumber = boards.getSelectedItem().toString();
	            System.out.println(adr + " " + p + " " + user + " " + boardNumber);
	            try {
	            	box.dispose();
	                new Client(adr,p, user, Integer.parseInt(boardNumber));
                } catch (IOException e) {
                	box.dispose();
	                e.printStackTrace();
                }
	            
	            
            }
			
		});
		
		GroupLayout layout = new GroupLayout(addressReq);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(
        		layout.createSequentialGroup()
        				.addComponent(addressLabel)
        				.addComponent(address)
        				.addComponent(portLabel)
        				.addComponent(port)
        				.addComponent(usernameLabel)
        				.addComponent(username)
        				.addComponent(boardNumLabel)
        				.addComponent(boards)
        				.addComponent(go)
        		);
        
        		
        layout.setVerticalGroup(
        		layout.createParallelGroup()
        				.addComponent(addressLabel)
        				.addComponent(address)
        				.addComponent(portLabel)
        				.addComponent(port)
        				.addComponent(usernameLabel)
        				.addComponent(username)
        				.addComponent(boardNumLabel)
        				.addComponent(boards)
        				.addComponent(go)
        				
        		);
        
//		addressReq.add(addressLabel);
//		addressReq.add(address);
//		addressReq.add(portLabel);
//		addressReq.add(port);
//		addressReq.add(go);
        
        addressReq.setLayout(layout);
        box.add(addressReq);
		box.setVisible(true);
}
}


	
	

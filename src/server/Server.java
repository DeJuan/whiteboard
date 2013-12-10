package server;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.io.*;
import java.net.*;
import java.util.*;

import canvas.Brushstroke;

/**
 * This class is the actual server that we use to keep track of all the separate whiteboards and clients.
 * It takes in two parameters: The port used and a boardCount. 
 * Currently, the port is adjustable freely, but defaults to 4444. 
 * However, the boardCount is fixed at 10 for our implementation. In the interest of being ready for change,
 * I have left this as a potentially variable amount.  
 * 
 * @param port
 * @param boardCount
 * @throws IOException
 * @author DeJuan
 */
public class Server 
{
	
	private ArrayList<Board> listOfBoards = new ArrayList<Board>();
	private Map<Integer, ArrayList<String>> boardNumberToBoardUsers = new HashMap<Integer, ArrayList<String>>();
	private Map<String, Integer> usernameToBoardNumber = new HashMap<String, Integer>();
	private Map<Socket, String> userSocketToUsername = new HashMap<Socket, String>();
	public BlockingQueue<String> queue;
	private ServerSocket serverSocket;
	private Object lock = new Object(); //Made so that I don't lock on the class if I need to synchro something. 
	
	
	public Server(int port, int boardCount) throws IOException
	{
		for(int i = 0; i < boardCount; i++)
		{
			listOfBoards.add(new Board(i));
			boardNumberToBoardUsers.put(i, new ArrayList<String>());
		}
		try 
		{
			serverSocket = new ServerSocket(port);
		} 
		catch (IOException e) 
		{
			System.out.println("Port was invalid. Should never see this thanks to previous checks, but now defaulting to  port 4444. ");
			serverSocket = new ServerSocket(4444);
		}
	}
	
	/**
	 * This method is called when a new client connects and wishes to access one of our boards. 
	 * We first check whether or not their username is available. If it is not, we throw an exception
	 * indicating that we cannot allow them to connect as they'd overlap usernames.
	 * 
	 * If the name is available, then we record a set of information that will allow us to uniquely identify any given client
	 * from any one parameter. This is done through a series of three hashmaps;
	 * One of them has board numbers as a key, and ArrayList<String> of board usernames as values. This gives easy access to a list of users for any one board.
	 * The next registers the user's socket as its key, and has their username as the value.
	 * Lastly, one takes in the username as key and ouput the board number as value.
	 * 
	 * We record all the data regarding the current user then add them to the board they want.
	 * The update to all previous brushstrokes that occurred on that board is handled by a seperate call.
	 * The reason for this delay is because otherwise, the client receives the update before their c
	 * 
	 * @param userName
	 * @param desiredBoard
	 * @param socket
	 * @throws Exception
	 */
	public void addNewUserToBoard(String userName, int desiredBoard, Socket socket) throws Exception
	{
		//called from a new socket, initializing a user's info to point to the board we need. May need synchronization.
		ArrayList<String> potentialBoardAndUsers = this.boardNumberToBoardUsers.get(desiredBoard);
		if (!potentialBoardAndUsers.contains(userName))
		{
			potentialBoardAndUsers.add(userName);
			listOfBoards.get(desiredBoard).addUser(socket);
			userSocketToUsername.put(socket, userName);
			usernameToBoardNumber.put(userName, desiredBoard);
			/*
			List<Brushstroke> allStrokes = listOfBoards.get(desiredBoard).getStrokes();
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			for(Brushstroke stroke: allStrokes)
			{
				output.println("brushstroke " + stroke.toString() + " " + desiredBoard);
			}*/
			
		}
		else //TODO REMEMBER TO TEST USERNAME OVERLAPS. THIS MAY OR MAY NOT CRASH THE ENTIRE SERVER!!!!!!
		{
			throw new Exception("That user name is already taken. Please choose another.");
		}
	}
		
	/**
	 * This method is called when a user disconnects from the server. It uses their socket to retrieve all of the information we stored
	 * regarding this users, and then removes all of it. 
	 * @param socket
	 * @throws Exception
	 */
	public void removeUserFromBoard(Socket socket) throws Exception
	{
		//Called when a user leaves the board and before they close their socket; want to remove their data.
		try
		{
			String userName = userSocketToUsername.get(socket);
			int boardNum = usernameToBoardNumber.get(userName);
			listOfBoards.get(boardNum).removeUser(socket);
			boardNumberToBoardUsers.get(boardNum).remove(userName);
			usernameToBoardNumber.remove(userName);
			userSocketToUsername.remove(socket);
		}
		catch(Exception e)
		{
			throw new Exception("Failed to remove user from board.");
		}
	}
	
	
	 
	    
	   /**
	     * Run the server, listening for client connections and handling them.
	     * Never returns unless an exception is thrown.
	     * It uses string parsing; client messages are specially structured strings starting with a keyword
	     * that determine the behavior desired, followed by any necessary parameters, all separated by a single whitespace. 
	     * 
	     * @throws IOException if the main server socket is broken
	     *                     (IOExceptions from individual clients do *not* terminate serve())
	     */
	    public void serve() throws IOException {
	        while (true) {
	            // block until a client connects
	            final Socket socket = serverSocket.accept();
	            Thread t = new Thread(new Runnable()
	            {
	            	public void run() 
	            	{
	            		try 
	            		{
	            			
	                        handleConnection(socket); 
	                    } 
	            		catch (Exception e) 
	                    {
	                        e.printStackTrace();  
	                    } 
	            		finally 
	                    {
	                        try 
	                        {
	                        	//removeUserFromBoard(socket);
								socket.close();
							} 
	                        catch (Exception e) 
							{
								e.printStackTrace();
							}
	                    }
	            	}
	            }
	        );
	            t.start();
	           
	            }}
	            
	            // handle the client
	            
	    

	    /**
	     * Handle a single client connection. Stops when client disconnects.
	     * Only takes in the socket of the user. Throws an exception if something goes wrong.   
	     * This actually does print things; however, it does this not to itself but
	     * through an instance of a PrintWriter made from the socket's output stream. 
	     *  
	     * @param socket, the socket where the client is connected
	     * @throws Exception 
	     */
	    private void handleConnection(Socket socket) throws Exception {
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	        out.println("Welcome to the whiteboard command center.");
	        try 
	        {
	            for (String line = in.readLine(); line != null; line = in.readLine())  
	            {
	            	//System.out.println("Succeeded in making in and out, and am about to call handleRequest");
	                String output = handleRequest(line, socket);    
	                
	                if( output == "Disconnect") 
                    {
	                	removeUserFromBoard(socket);
                    	socket.close(); 
                    }
	                
	                if (output != null) 
	                {
	                	if(output.startsWith("brushstroke"))
	                	{
	                		//System.out.println("brushy detected!! It is: " + output);
	                		ArrayList<Socket> users = this.listOfBoards.get(Integer.parseInt(output.split(" ")[7])).getBoardUsers();
	                		for(Socket user: users)
	                		{
	                			//System.out.println("Printing to socket: " + user);
	                			PrintWriter localOut = new PrintWriter(user.getOutputStream(), false);
	                			localOut.println(output);
	                			localOut.flush();
	                		}
	                	}
	                	
	                	else
	                	{
	                		out.println(output);
	                		out.flush();
	                	}
	                	
	                    
	                }
	                
	            }
	        } 	
	        	finally 
	        	{
	        		//removeUserFromBoard(socket);
	            	out.close();
	            	in.close();
	        	}
	    }

	    /**
	     * Handler for client input, performing requested operations and returning an output message.
	     * 
	     * @param input message from client
	     * @return message to client
	     */
	    private String handleRequest(String input, Socket socket) {
	    	
	        String[] tokens = input.split(" ");
	        if (tokens[0].equals("brushstroke")) //"brushstroke x1 x2 y1 y2 ColorData width boardnum 
	        {
	        	int boardNumber = Integer.parseInt(input.split(" ")[7]);
	        	Board board = listOfBoards.get(boardNumber);
	        	return board.registerStroke(input, boardNumber);
	        	
	        }
	        
	        else if (tokens[0].equals("joinBoard")) //"joinBoard username boardNumber
	        {
	        	try {
	        			int boardNum = Integer.parseInt(tokens[2]);
						addNewUserToBoard(tokens[1], boardNum, socket);
						ArrayList<Socket> users = this.listOfBoards.get(boardNum).getBoardUsers();
                		for(Socket user: users)
                		{
                			//System.out.println("Printing to socket: " + user);
                			PrintWriter localOut = new PrintWriter(user.getOutputStream(), true);
                			localOut.println(userListParser(boardNum));
                			localOut.flush();
                		}
                		return null;
					}  
	        	catch (Exception e) 
	        		{
					// TODO Auto-generated catch block with stand-in notification code. May need reworking. 
					System.out.println("Failed to add user to board.");
	        		}
	        }
	         
	        else if (tokens[0].equals("exitBoard")) //"exitBoard username currentBoard
	        {
	        	int boardNum = Integer.parseInt(tokens[2]);
	        	try 
	        	{
	        		removeUserFromBoard(socket);
	        		ArrayList<Socket> users = this.listOfBoards.get(boardNum).getBoardUsers();
            		for(Socket user: users)
            		{
            			//System.out.println("Printing to socket: " + user);
            			PrintWriter localOut = new PrintWriter(user.getOutputStream(), true);
            			localOut.println(userListParser(boardNum));
            			//localOut.flush();
            		}
				} 
	        	catch (Exception e) 
	        	{
					System.out.println("Failed to exit the board.");
				}
	        	return userListParser(boardNum);
	        }
	        
	        else if(tokens[0].equals("getUserList")) //"getUserList boardNumber
	        {
	        	return userListParser(Integer.parseInt(tokens[1]));
	        }
	        
	        else if(tokens[0].equals("getAllBrushstrokes")) //"getAllBrushstrokes boardNumber
	        {
	        	try 
	        	{
	        		int boardNum = Integer.parseInt(tokens[1]);
	        		List<Brushstroke> allStrokes = this.listOfBoards.get(boardNum).getStrokes();
	        		PrintWriter temp = new PrintWriter(socket.getOutputStream(), true);
	        		for(Brushstroke stroke: allStrokes)
	        		{
	        			temp.println("brushstroke " + stroke.toString() + " " + boardNum);
	        		}
	        		return null;
				} 
	        	catch (IOException e) 
	        	{
					e.printStackTrace();
				}
	        }
	        else if(tokens[0].equals("exit") || tokens[0].equals("quit") ||  tokens[0].equals("bye") || tokens[0].equals("dc") ||tokens[0].equals("disconnect"))
	        {
	        	return "Disconnect";
	        }
	        else
	        {
	        	// Should never get here--make sure to return in each of the valid cases above.
		        return null;
	        }
			
	        return "Disconnect";
	    }

	    public String userListParser(int boardNum)
	    {
	    	ArrayList<String> users = boardNumberToBoardUsers.get(boardNum);
        	String usersInString = users.toString();
        	String usersNoBrackets = usersInString.substring(1, usersInString.length()-1);
        	String usersNoCommas = usersNoBrackets.replace(",", "");
        	return ("userList " + usersNoCommas);
	    }
	    
	    
	    public static void main(String[] args) {
	        // Command-line argument parsing is provided. Do not change this method
	        int port = 4444; // default port
	        int numBoards = 10; // default size of board storage

	        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
	        try {
	            while ( ! arguments.isEmpty()) {
	                String flag = arguments.remove();
	                try 
	                {
	                	if (flag.equals("--port")) 
	                	{
	                        port = Integer.parseInt(arguments.remove());
	                        if (port < 0 || port > 65535) 
	                        {
	                            throw new IllegalArgumentException("port " + port + " out of range");
	                        }
	                    } 
	                	else if (flag.equals("--numBoards")) 
	                    {
	                        numBoards = Integer.parseInt(arguments.remove());
	                    } 
	                     else 
	                     {
	                        throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
	                     }
	                } catch (NoSuchElementException nsee) {
	                    throw new IllegalArgumentException("missing argument for " + flag);
	                } catch (NumberFormatException nfe) {
	                    throw new IllegalArgumentException("unable to parse number for " + flag);
	                }
	            }
	        } catch (IllegalArgumentException iae) {
	            System.err.println(iae.getMessage());
	            System.err.println("usage: Server [--port PORT] [--numBoards NUMBER]");
	            return;
	        }

	        try {
	            runServer(numBoards, port);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    /**
	     * Start a Server running on the specified port, with the specified number of boards. Currently set up for 10. 
	     * 
	     * @param numBoards If this argument is not null, start with a Server containing that number of Boards.
	     * @param port The network port on which the server should listen.
	     */
	    public static void runServer(int numBoards, int port) throws IOException {
	        
	        
	        
	        Server server = new Server(port, numBoards);
	        server.serve();
	    }
	}



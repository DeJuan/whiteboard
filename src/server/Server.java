package server;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.io.*;
import java.net.*;
import java.util.*;

import canvas.Brushstroke;

public class Server 
{
	
	private ArrayList<Board> listOfBoards = new ArrayList<Board>();
	private Map<Integer, ArrayList<String>> boardNumberToBoardUsers = new HashMap<Integer, ArrayList<String>>();
	private Map<String, Integer> usernameToBoardNumber = new HashMap<String, Integer>();
	private Map<Socket, String> userSocketToUsername = new HashMap<Socket, String>();
	public BlockingQueue<String> queue;
	private ServerSocket serverSocket;
	private Object lock = new Object(); //Made so that I don't lock on the class if I need to synchro something. 
	
	/**
	 * This class is the actual server that we use to keep track of all the separate whiteboards and clients. 
	 * 
	 * @param port
	 * @param boardCount
	 * @throws IOException
	 */
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
			List<Brushstroke> allStrokes = listOfBoards.get(desiredBoard).getStrokes();
			PrintWriter output = new PrintWriter(socket.getOutputStream(), false);
			for(Brushstroke stroke: allStrokes)
			{
				output.println("brushstroke " + stroke.toString() +" " + desiredBoard);
			}
			
		}
		else //TODO REMEMBER TO TEST USERNAME OVERLAPS. THIS MAY OR MAY NOT CRASH THE ENTIRE SERVER!!!!!!
		{
			throw new Exception("That user name is already taken. Please choose another.");
		}
	}
		
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
	     * Main issue here: How to get desired board information?
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
	            			//addNewUserToBoard(); //TODO need to figure out how to get the index of the desired board in here. If we get that, we're done.
	            			//That's the main problem of this section.
	            			//UPDATE: Don't worry about that here. Do it later in actually handling requests.
	            			System.out.println("Reached serve's try");
	                        handleConnection(socket); //TODO add more info that needs to be passed here
	                    } 
	            		catch (Exception e) 
	                    {
	                        e.printStackTrace(); // Doesn't stop the service. May change once earlier TODO has been taken care of. 
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
	     * Handle a single client connection. Returns when client disconnects.
	     * Information is just a place holder to remind us we need to put things there.
	     * Discuss with group on Sunday.  
	     * 
	     * @param socket socket where the client is connected
	     * @throws Exception 
	     */
	    private void handleConnection(Socket socket) throws Exception {
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	        out.println("Welcome to the whiteboard command center.");
	        try 
	        {
	            for (String line = in.readLine(); line != null; line = in.readLine()) //Need to discuss how these are going to be sent. 
	            {
	            	System.out.println("Succeeded in making in and out, and am about to call handleRequest");
	                String output = handleRequest(line, socket);  //Need to specify ahead of time which board we're adjusting. Just send the int, handle request will get the board out.  
	                
	                if( output == "Disconnect") //write listener code for DCing from a board and send this as output.
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
	     * Start a MinesweeperServer running on the specified port, with either a random new board or a
	     * board loaded from a file. Either the file or the size argument must be null, but not both.
	     * 
	     * @param debug The server should disconnect a client after a BOOM message if and only if this
	     *              argument is false.
	     * @param size If this argument is not null, start with a random board of size size * size.
	     * @param file If this argument is not null, start with a board loaded from the specified file,
	     *             according to the input file format defined in the JavaDoc for main().
	     * @param port The network port on which the server should listen.
	     */
	    public static void runServer(int numBoards, int port) throws IOException {
	        
	        // TODO: Continue your implementation here.
	        
	        Server server = new Server(port, numBoards);
	        server.serve();
	    }
	}



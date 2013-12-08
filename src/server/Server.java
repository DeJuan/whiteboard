package server;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server 
{
	
	private ArrayList<Board> listOfBoards;
	private ArrayList<String> userInfo;
	public BlockingQueue queue;
	private final ServerSocket serverSocket;
	private Object lock = new Object(); //Made so that I don't lock on the class if I need to synchro something. 
	
	public Server(int port, int boardCount)
	{
		for(int i = 0; i < boardCount; i++)
		{
			listOfBoards.add(new Board(i));
		}
		try 
		{
			serverSocket = new ServerSocket(port);
		} 
		catch (IOException e) 
		{
			System.out.println("Port was invalid. Should never see this thanks to previous checks but Java didn't like this. ");
			e.printStackTrace();
		}
	}
	
	public void addNewUserToBoard(String userName, int desiredBoard)
	{
		//called from a new socket, initializing a user's info to point to the board we need. May need synchronization.
		
	}
		
	public void removeUserFromBoard(String userName, int currentBoard)
	{
		//Called when a user leaves the board and before they close their socket; want to remove their data. May not actually be needed. 
	}
	
	private void addMessageToQueue(String message)
	{
		queue.add(message); //TODO Be careful about leaving it like this, depends on what the messages are. 
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
	                        handleConnection(socket, alsoProbablyBoardID); //TODO add more info that needs to be passed here
	                    } 
	            		catch (IOException e) 
	                    {
	                        e.printStackTrace(); // Doesn't stop the service. May change once earlier TODO has been taken care of. 
	                    } 
	            		finally 
	                    {
	                        try 
	                        {
								socket.close();
								removeUserFromBoard();
							} 
	                        catch (IOException e) 
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
	     * @throws IOException if connection has an error or terminates unexpectedly
	     */
	    private void handleConnection(Socket socket, Information info) throws IOException {
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	        out.println("Welcome");
	        try 
	        {
	            for (String line = in.readLine(); line != null; line = in.readLine()) //Need to discuss how these are going to be sent. 
	            {
	                String output = handleRequest(line, boardNumber);  //Need to specify ahead of time which board we're adjusting. Just send the int, handle request will get the board out.  
	                if (output != null) 
	                {
	                    out.println(output);
	                    if( output == "Disconnect") //write listener code for DCing from a board and send this as output.
	                    {
	                    	socket.close(); 
	                    }
	                }
	            }
	        } 	
	        	finally 
	        	{
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
	    private void handleRequest(String input, int boardNumber) {
	    	Board board = listOfBoards.get(boardNumber);
	        String[] tokens = input.split(" ");
	        if (tokens[0].equals("brushstroke")) 
	        {   
	           board.registerStroke(input);
	        }
	         
	        
	        // Should never get here--make sure to return in each of the valid cases above.
	        throw new UnsupportedOperationException();
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


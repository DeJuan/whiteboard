package server;

public class ServerStrategyExplanation {
	/**
	 * This entire class is just an explanation of how server testing was carried out and what means of attack we used. 
	 * First, we initialized the server and ran it. I added command line parsing to the server so that we could directly
	 * manipulate it as if a client were sending requests to it. We opened up two command lines to simulate two seperate clients.
	 * 
	 * 1) Using this command line manipulation, we first tested joining a board by using the "joinBoard" command. Upon joining, all members of the current board are notified of the change. 
	 * The board also stops processing new drawing temporarily as the newcomer is updated, due to synchronization to ensure common state across all clients. 
	 * This was tested by secondly sending the "getAllBrushstrokes" command to the server. All previously executed strokes are then fed in a series of printlns
	 * to the client who needs the information. Upon the termination of this update, if any other strokes were created during this block, all clients execute
	 * simultaneous updates to the newly available strokes. 
	 * 
	 * 2)  We then tested drawing a single stroke with the "brushstroke" command on both clients. Upon success, the brushstroke's parsed form was returned to the client and 
	 *  also to all other clients on the server simultaneously so that all clients could update their state. 
	 *  
	 * 3)  With these having been tested successfully, we disconnected one of the two users. The disconnect caused the userList to update again, which broadcasted
	 *  the update to all remaining users showing that one user had left. This was visible on the other, still-connected command line as it immediately showed the
	 *  updated list containing only its own username as the sole user.
	 *  
	 * 4) We then reconnected the second terminal but on a different board. No updates needed processing, and the client received a user list indicating it was the only user
	 *  on the current board. This was good because it showed the board user lists were indeed functioning seperately as intended.
	 *  
	 * 5) We drew a single brushstroke on the newly reconnected client and as expected received the parsed form of the input. The original client did not receive this message,
	 *  since it was on a different board. We tried sending a new brushstroke from that original client as well, and it too did not reach the new client. 
	 *  This showed that processing strokes for each board was functioning properly and seperately.
	 *  
	 *  6) We then disconnected both terminals without shutting down the server.
	 *  Then we reconnected one to the first board and received both the list of usernames connected to the board (the only active terminal's username was present) and 
	 *  the list of strokes that had been executed before we connected, which numbered three: One from
	 *  each client send from section 2), and one from the test that occurred in 5). This demonstrated persistence across disconnects and reconnects as desired. 
	 *   
	 *   These tests verified the following desired aspects of the server:
	 *   1) Responsiveness to client inputs.
	 *   2) Selective responses: Separate records for each board, separate responses for clients qualified to receive them
	 *   3) Updates in real time: Simultaneous responses to all clients and updates immediately upon disconnect, reconnect, and drawing. 
	 *   4) Persistence in data across disconnects and reconnects; strokeLists are maintained even if no clients are present
	 *   5) Updates to new clients of strokes already present before their connection.
	 *   
	 *   @author DeJuan
	 */
}

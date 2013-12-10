package server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import canvas.Brushstroke;

/**
 * This class is to show all of the methods of the board working as intended. The board is the actual data representation of the state of a canvas to the server
 * which is used to update all of the clients. The server cannot be easily tested with Junit, so for that we executed comprehensive testing through both the '
 * command line and direct examination by testing all the edge cases we could think of in the GUI itself. 
 * 
 * The testing strategy for this though; First, we need to check if brush responses are responded accurately. 
 * For this purpose, I'll use 255 as the color field. This always causes a color field value return of -16776961, so I'm doing that for 
 * consistency. 
 * 
 * We'll first test that brushes are detected and responded to appropriately, then that getting the history of all the strokes works properly. 
 * Unfortunately, the one storage device we cannot properly check here is the socket
 * storage, but due to fail-fast error detection in the server, if that storage does not work, it will quickly become evident in actual testing. 
 * That's the only other storage used here, so as long as these two tests pass, we know the board is functional in terms of brushstrokes.
 * The others are quite simple one line deposits and removals, so it's fairly obvious when they work and don't work. Even so, 
 * I made sure to check that storage separately through other means of investigation, such as command-line parsing of arguments and testing socket storage with printlines
 * as the storage was executed. Can't be too careful!
 * 
 * @author DeJuan
 *
 */
public class BoardTest {

	@Test //Simply test brush response
	public  void testBrushResponse() throws IOException
	{
		Board testBoard = new Board(1);
		Brushstroke testStroke = new Brushstroke("1,1,3,3,255,10");
		assertEquals("brushstroke 1 1 3 3 -16776961 10 1", testBoard.registerStroke(testStroke, 1));
	}
	
	@Test
	/**
	 * This one is hard; it's a pain because the types are different, so I have to make lots of conversions to get into a form where assertArrayEquals can be used. 
	 * I don't change the data, just move it around and put it into other data structures. 
	 */
	public void testGetAllStrokes()
	{
		Board testBoard = new Board(1);
		Brushstroke testStroke = new Brushstroke("1,1,3,3,255,10");
		Brushstroke testStrokeTwo = new Brushstroke("2,2,4,4,255,5");
		testBoard.registerStroke(testStroke, 1);
		testBoard.registerStroke(testStrokeTwo, 1);
		Brushstroke[] allTestStrokes= new Brushstroke[2];
		allTestStrokes[0] = (testStroke);
		allTestStrokes[1] = (testStrokeTwo);
		Brushstroke[] boardResponse = new Brushstroke[2];
		List<Brushstroke> boardTrueResponse = testBoard.getStrokes();
		boardResponse[0] = boardTrueResponse.get(0);
		boardResponse[1] = boardTrueResponse.get(1);
		assertArrayEquals(allTestStrokes, boardResponse);
	}
	
}

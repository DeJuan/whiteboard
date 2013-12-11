package canvas;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Queue;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import client.Client;




/**
 * This is the currently used version of the canvas.
 * It currently has a toolbar containing eraser button, color select, pen size, and reset button. 
 * 
 * Note that as it functions now, the palette popup bypasses the 
 * palette listener, directly returning the color when the user clicks OK.
 * I then change the current pen color to the returned color from the dialog.
 * @author DeJuan 
 */
public class newCanvas extends JPanel implements ActionListener{
    
	private static final long serialVersionUID = 1L;
	// image where the user's drawing is stored
    private Image drawingBuffer;
    private boolean eraserMode = false;
    private JSlider strokeWidth;
    private Color currentPenColor = Color.black;
    private JColorChooser palette = new JColorChooser(Color.black);
    //private JMenu boardMenu;
    private ArrayList<Color> RecentColors = new ArrayList<Color>(); 
    private int currentBoard;
    private JLabel boardNum;
    private int colorButtonSelected = 0;
    private String users;
    private Client client;
    private JInternalFrame usersFrame;
    private JButton openUserList;
    private JTextField userField;
    boolean usersFrameOpen = false;
    
    
    /**
     * Make a canvas.
     * @param width width in pixels
     * @param height height in pixels
     * @param client to connect with canvas
     */
    public newCanvas(int width, int height, Client client, int startingBoardNumber) 
    {
    	this.client = client;
        this.setPreferredSize(new Dimension(width, height));
        addDrawingController();
        addEraserController();
        currentBoard = startingBoardNumber;
        //JLabel boardNum = new JLabel(currentBoard.toString());
        //boardMenu = new JMenu("Change Boards");
        JTextField userField = new JTextField(this.users);
        JToolBar toolbar = new JToolBar();                
        
        JButton openUserList = new JButton("Other Users");
        openUserList.addActionListener(this);
        openUserList.setActionCommand("userList");
        
        JInternalFrame usersFrame = new JInternalFrame("Other Board Users");
        usersFrame.setContentPane(userField);
        //if(usersFrameOpen){
        	usersFrame.setVisible(true);
       // }
        //else{
        //	usersFrame.setVisible(false);
        //}
        usersFrame.setClosable(true);
        //usersFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        
        //toolbar will display the 3 most recent colors. 
        //gray orange and pink display until
        RecentColors.add(Color.magenta);
        RecentColors.add(Color.gray);
        RecentColors.add(Color.orange);
        RecentColors.add(Color.pink);
               
        
       
       
        
        //must still add listener to change to new board when clicked. 
        
        PaletteListener paletteController = new PaletteListener();
        palette.getSelectionModel().addChangeListener(paletteController);
        palette.setPreviewPanel(new JPanel());
        
        JButton paletteButton = new JButton("Show Color Palette");
        paletteButton.addMouseListener(new PalettePopupListener());
        
        JButton resetter = new JButton("Reset All");
        resetter.addMouseListener(new resetListener());
        
        JButton blue = new JButton();
        blue.setBackground(Color.blue);
        blue.setPreferredSize(new Dimension(30, 30));
        blue.setActionCommand("blue");
        blue.addActionListener(this);
        blue.setOpaque(true);
        if(colorButtonSelected == 1){
        	blue.setFocusPainted(true);
        }
        else{
        	blue.setFocusPainted(false);
        }
        
        JButton red = new JButton();
        red.setBackground(Color.red);
        red.setPreferredSize(new Dimension(30, 30));
        red.setActionCommand("red");
        red.addActionListener(this);
        red.setOpaque(true);
        if(colorButtonSelected == 2){
        	red.setFocusPainted(true);
        }
        else{
        	red.setFocusPainted(false);
        }
        
        JButton green = new JButton();
        green.setBackground(Color.green);
        green.setPreferredSize(new Dimension(30,30));
        green.setActionCommand("green");
        green.addActionListener(this);
        green.setOpaque(true);
        if(colorButtonSelected == 3){
        	green.setFocusPainted(true);
        }
        else{
        	green.setFocusPainted(false);
        }
        
        JButton black = new JButton();
        black.setBackground(Color.black);
        black.setPreferredSize(new Dimension(30,30));
        black.setActionCommand("black");
        black.addActionListener(this);
        black.setOpaque(true);
        if(colorButtonSelected == 4){
        	black.setFocusPainted(true);
        	currentPenColor = Color.black;
        }
        else{
        	black.setFocusPainted(false);
        }
        
        JButton yellow = new JButton();
        yellow.setBackground(Color.yellow);
        yellow.setPreferredSize(new Dimension(30,30));
        yellow.setActionCommand("yellow");
        yellow.addActionListener(this);
        yellow.setOpaque(true);
        if(colorButtonSelected == 5){
        	yellow.setFocusPainted(true);
        }
        else{
        	yellow.setFocusPainted(false);
        }
        
        JButton recent1 = new JButton();
        recent1.setBackground(RecentColors.get(1));
        recent1.setPreferredSize(new Dimension(30,30));
        recent1.setActionCommand("recent1");
        recent1.setOpaque(true);
        recent1.addActionListener(this);
        if(colorButtonSelected == 6){
        	
        }
        
        JButton recent2 = new JButton();
        recent2.setBackground(RecentColors.get(2));
        recent2.setPreferredSize(new Dimension(30,30));
        recent2.setActionCommand("recent2");
        recent2.setOpaque(true);
        recent2.addActionListener(this);
        
        JButton recent3 = new JButton();
        recent3.setBackground(RecentColors.get(3));
        recent3.setPreferredSize(new Dimension(30,30));
        recent3.setActionCommand("recent3");
        recent3.setOpaque(true);
        recent3.addActionListener(this);
        
        JLabel widthLabel = new JLabel("Pen Size");
        strokeWidth = new JSlider(JSlider.HORIZONTAL,1,50,1);
        strokeWidth.setPreferredSize(new Dimension(100,20));
        
        JToggleButton DrawOrErase = new JToggleButton("Eraser Mode",false);
        DrawOrErase.addMouseListener(new eraseButtonListener());
        toolbar.add(DrawOrErase);
        toolbar.add(paletteButton);
        toolbar.add(openUserList);
        //toolbar.add(boardNum);
        toolbar.add(widthLabel);
        toolbar.add(strokeWidth);
        toolbar.add(blue);
        toolbar.add(red);
        toolbar.add(green);
        toolbar.add(black);
        toolbar.add(yellow);
        toolbar.add(recent1);
        toolbar.add(recent2);
        toolbar.add(recent3);
        
        //toolbar.add(boardNum);
        add(toolbar);
        //Make the buttons, add the mouse listener to them, and add the buttons to the canvas.
        
        // note: we can't call makeDrawingBuffer here, because it only
        // works *after* this canvas has been added to a window.  Have to
        // wait until paintComponent() is first called.
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(Graphics g) 
    {
        // If this is the first time paintComponent() is being called,
        // make our drawing buffer.
        if (drawingBuffer == null) 
        {
            makeDrawingBuffer();
        }
        
        // Copy the drawing buffer to the screen.
        g.drawImage(drawingBuffer, 0, 0, null);
    }
    
    
    public void updateUsers(String newUsers){
    	users = newUsers;
    }
    
    /*
     * Make the drawing buffer and draw some starting content for it.
     */
    
    private void makeDrawingBuffer() 
    {
        drawingBuffer = createImage(getWidth(), getHeight());
        fillWithWhite();        
    }
    
    /*
     * Make the drawing buffer entirely white.
     */
    private void fillWithWhite() 
    {
        final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0,  0,  getWidth(), getHeight());
        
        // IMPORTANT!  every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
        this.repaint();
    }

    
    /*
     * Draw a line between two points (x1, y1) and (x2, y2), specified in
     * pixels relative to the upper-left corner of the drawing buffer.
     */
    public void drawLineSegment(int x1, int y1, int x2, int y2, int color, int width) 
    {
        Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
        Color strokeColor = new Color(color);
        g.setColor(strokeColor);
        g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        //System.out.println(x1+","+y1+","+x2+","+y2);
        g.drawLine(x1, y1, x2, y2);
        
        // IMPORTANT!  every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
        this.repaint();
    }
    
    public void drawLineSegment(Brushstroke Brushstroke)
    {
    	Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
    	g.setColor(Brushstroke.getColor());
    	g.setStroke(new BasicStroke(Brushstroke.getWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
    	g.drawLine(Brushstroke.getStartX(), Brushstroke.getStartY(), Brushstroke.getEndX(), Brushstroke.getEndY());
    	this.repaint();
    }
    
    /*
     * Add the mouse listener that supports the user's freehand drawing.
     */
    private void addDrawingController() 
    {
        DrawingController controller = new DrawingController();
        addMouseListener(controller);
        addMouseMotionListener(controller);
    }
    /*
     * Add the listener that is used for freehand erasing. 
     */
    private void addEraserController()
    {
    	EraserController eraser = new EraserController();
    	addMouseListener(eraser);
    	addMouseMotionListener(eraser);
    }
    
    /*
     * sendStroke calls the clients send methods to send a stroke to the server.
     */
    private void sendStroke(Brushstroke b){
    	this.client.send(this.client.translateBrushstroke(b));
    }
    
    /*
     * DrawingController handles the user's freehand drawing.
     */
    private class DrawingController implements MouseListener, MouseMotionListener 
    {
        // store the coordinates of the last mouse event, so we can
        // draw a line segment from that last point to the point of the next mouse event.
        private int lastX, lastY; 

        /*
         * When mouse button is pressed down, start drawing.
         */
        public void mousePressed(MouseEvent e) 
        {
        	if(!eraserMode)
        	{
            lastX = e.getX();
            lastY = e.getY();
        	}
        }
//TODO: MAKE THIS USE STROKES, SAME WITH THE ERASER
        /*
         * When mouse moves while a button is pressed down,
         * draw a line segment.
         */
        public void mouseDragged(MouseEvent e) 
        {
        	if(!eraserMode)
        	{
            int x = e.getX();
            int y = e.getY();
            int penSize = strokeWidth.getValue();            
            Brushstroke currentStroke = new Brushstroke(lastX, lastY, x, y, currentPenColor, penSize);
            sendStroke(currentStroke);
            lastX = x;
            lastY = y;
        	}
        }

        // Ignore all these other mouse events.
        public void mouseMoved(MouseEvent e) { }
        public void mouseClicked(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }
        public void mouseExited(MouseEvent e) { }
    }
    
    public static JToggleButton DrawOrErase = new JToggleButton("Eraser",false);
    //DrawOrErase.addMouseListener(new eraseButtonListener());
     
    
   //Toggles the boolean that controls erasure vs drawing.  
    
    private class eraseButtonListener implements MouseListener{

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		eraserMode = !(eraserMode);
	}
	//Handle the others in case people do weird things with the button, like
	//dragging outside while still holding, etc. 
	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e)
	{
		if (e.MOUSE_PRESSED == 1){eraserMode = !(eraserMode);}
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		eraserMode = !(eraserMode);
	}
		
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		eraserMode = !(eraserMode);
	}
	   
   }
    /*
    private void eraserLineSegment(int x1, int y1, int x2, int y2) 
    {
        Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(Integer.parseInt(strokeWidth.getText()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g.drawLine(x1, y1, x2, y2);
        
        // IMPORTANT!  every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
        this.repaint();
    }
    */

    private void eraserLineSegment(Brushstroke eraserStroke)
    {
    	Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
    	g.setColor(eraserStroke.getColor());
    	g.setStroke(new BasicStroke(eraserStroke.getWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
    	g.drawLine(eraserStroke.getStartX(), eraserStroke.getStartY(),eraserStroke.getEndX(), eraserStroke.getEndY());
    	this.repaint();
    }
    
   
    	
    //Similar to the drawing controller, but with opposing checks. Otherwise,
    //aside from variable names, identical. For the Eraser Mode button.
    private class EraserController implements MouseListener, MouseMotionListener
    {
    	//Similar to line segment, but draw in large white strokes.
    	private int startingX, startingY;
    	public void mousePressed(MouseEvent e){
    		if(eraserMode)
    		{
    		startingX = e.getX();
    		startingY = e.getY();
    		}
    	}
    	
    	public void mouseDragged(MouseEvent e){
    		if(eraserMode)
    		{
    		int x = e.getX();
    		int y = e.getY();
    		//try
    		//{
    		//made eraser width be controlled by slider
    		Brushstroke eraser = new Brushstroke(startingX, startingY, x, y, Color.white, (Integer)strokeWidth.getValue());
    		sendStroke(eraser);
    		
    		/*}
    		catch(Exception notaNum)
    		{
    			Brushstroke eraser = new Brushstroke(startingX, startingY, x, y, Color.white, 1);
    			sendStroke(eraser);
    		*/

    		
    		startingX = x;
    		startingY = y;
    		}
    	}
    	
    	public void mouseMoved(MouseEvent e) { }
        public void mouseClicked(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }
        public void mouseExited(MouseEvent e) { }
    }
    
    public class PaletteListener implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent colorChosen) {
			if(isNewColor(palette.getColor())){
				RecentColors.add(0,palette.getColor());
			}
			currentPenColor = palette.getColor();
			
			
			
		}
    	
    }
    
    public void actionPerformed(ActionEvent e){
    	if("blue".equals(e.getActionCommand())){
    		colorButtonSelected = 1;
    		currentPenColor = Color.blue;
    	}
    	else if("red".equals(e.getActionCommand())){
    		colorButtonSelected = 2;
    		currentPenColor = Color.red;
    	}
    	else if("green".equals(e.getActionCommand())){
    		colorButtonSelected = 3;
    		currentPenColor = Color.green;
    	}
    	else if("black".equals(e.getActionCommand())){
    		colorButtonSelected = 4;
    		currentPenColor = Color.black;
    	}
    	else if("yellow".equals(e.getActionCommand())){
    		colorButtonSelected = 5;
    		currentPenColor = Color.yellow;
    	}
    	else if("recent1".equals(e.getActionCommand())){
    		colorButtonSelected = 6;
    		currentPenColor = RecentColors.get(1);
    		Color temp = RecentColors.get(1);
    		RecentColors.remove(1);
    		RecentColors.add(0,temp);
    	}
    	else if("recent2".equals(e.getActionCommand())){
    		colorButtonSelected = 7;
    		currentPenColor = RecentColors.get(2);
    		Color temp0 = RecentColors.get(2);
    		RecentColors.remove(2);
    		RecentColors.add(0, temp0);
    	}
    	else if("recent3".equals(e.getActionCommand())){
    		colorButtonSelected  = 8;
    		currentPenColor = RecentColors.get(3);
    		Color temp0 = RecentColors.get(3);
    		RecentColors.remove(3);
    		RecentColors.add(0,temp0);
    	}
    	else if("userList".equals(e.getActionCommand())){
    		if(!usersFrameOpen){
    			usersFrame.setVisible(true);
    			usersFrameOpen = true;
    		}
    		else{
    			usersFrame.setVisible(false);
    			usersFrameOpen = false;
    		}
    	}
    }
    
    private boolean isNewColor(Color c){
    	if(!c.equals(Color.blue) && !c.equals(Color.red) && !c.equals(Color.green) && !c.equals(Color.black) 
    			&& !c.equals(Color.yellow) && !c.equals(RecentColors.get(0)) && !c.equals(RecentColors.get(1))
    				&& !c.equals(RecentColors.get(2))){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    private class PalettePopupListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) 
		{
			Color intermediate= palette.showDialog(palette, "Choose Your Color!", currentPenColor);
			if (intermediate != null)
			{
				currentPenColor = intermediate;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
    	
    }
    
  //For the reset button.
    private class resetListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) 
		{
			fillWithWhite();	
		}
		//Ignore the other cases for this button. You hit it, you hit it.
		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}    
    	
    }
    /*
     * Main program. Make a window containing a Canvas.
     */
    public static void main(String[] args) {
        // set up the UI (on the event-handling thread)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame window = new JFrame("Freehand Canvas");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setLayout(new BorderLayout());
                newCanvas canvas = new newCanvas(800, 600, null, 0);
                window.add(canvas, BorderLayout.CENTER);
                window.pack();
                window.setVisible(true);
            }
        });
    }
}
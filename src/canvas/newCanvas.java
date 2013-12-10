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
public class newCanvas extends JPanel{
    
	private static final long serialVersionUID = 1L;
	// image where the user's drawing is stored
    private Image drawingBuffer;
    private boolean eraserMode = false;
    private JSlider strokeWidth;
    private Color currentPenColor = Color.black;
    private JColorChooser palette = new JColorChooser(Color.black);
    private JMenu boardMenu;
    private Queue<Color> RecentColors; 
    private int currentBoard;
    private int colorButtonSelected = 0;
    
    private Client client;
    
    
    /**
     * Make a canvas.
     * @param width width in pixels
     * @param height height in pixels
     */
    public newCanvas(int width, int height, Client client) 
    {
    	this.client = client;
        this.setPreferredSize(new Dimension(width, height));
        addDrawingController();
        addEraserController();
        currentBoard = client.getBoard();
        
        JToolBar toolbar = new JToolBar();
       
        JRadioButtonMenuItem board0 = new JRadioButtonMenuItem("Board 0");
        boardMenu.add(board0);
        if(currentBoard == 0){
        	board0.setSelected(true);
        }	
        JRadioButtonMenuItem board1 = new JRadioButtonMenuItem("Board 1");
        boardMenu.add(board1);
        if(currentBoard == 1){
        	board1.setSelected(true);
        }	
        JRadioButtonMenuItem board2 = new JRadioButtonMenuItem("Board 2");
        boardMenu.add(board2);
        if(currentBoard == 2){
        	board2.setSelected(true);
        }	
        JRadioButtonMenuItem board3 = new JRadioButtonMenuItem("Board 3");
        boardMenu.add(board3);
        if(currentBoard == 3){
        	board3.setSelected(true);
        }	
        JRadioButtonMenuItem board4 = new JRadioButtonMenuItem("Board 4");
        boardMenu.add(board4);
        if(currentBoard == 4){
        	board4.setSelected(true);
        }	
        JRadioButtonMenuItem board5 = new JRadioButtonMenuItem("Board 5");
        boardMenu.add(board5);
        if(currentBoard == 5){
        	board5.setSelected(true);
        }	
        JRadioButtonMenuItem board6 = new JRadioButtonMenuItem("Board 6");
        boardMenu.add(board6);
        if(currentBoard == 6){
        	board6.setSelected(true);
        }	
        JRadioButtonMenuItem board7 = new JRadioButtonMenuItem("Board 7");
        boardMenu.add(board7);
        if(currentBoard == 7){
        	board7.setSelected(true);
        }	
        JRadioButtonMenuItem board8 = new JRadioButtonMenuItem("Board 8");
        boardMenu.add(board8);
        if(currentBoard == 8){
        	board8.setSelected(true);
        }	
        JRadioButtonMenuItem board9 = new JRadioButtonMenuItem("Board 9");
        boardMenu.add(board9);
        if(currentBoard == 9){
        	board9.setSelected(true);
        }	
        
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
        blue.setPreferredSize(new Dimension(10, 10));
        blue.setActionCommand("blue");
        blue.addActionListener((ActionListener) this);
        if(colorButtonSelected == 1){
        	blue.setFocusPainted(true);
        }
        else{
        	blue.setFocusPainted(false);
        }
        
        JButton red = new JButton();
        red.setBackground(Color.red);
        red.setPreferredSize(new Dimension(10, 10));
        red.setActionCommand("red");
        red.addActionListener((ActionListener) this);
        if(colorButtonSelected == 2){
        	red.setFocusPainted(true);
        }
        else{
        	red.setFocusPainted(false);
        }
        
        JButton green = new JButton();
        green.setBackground(Color.green);
        green.setPreferredSize(new Dimension(10,10));
        green.setActionCommand("green");
        green.addActionListener((ActionListener) this);
        if(colorButtonSelected == 3){
        	green.setFocusPainted(true);
        }
        else{
        	green.setFocusPainted(false);
        }
        
        JButton black = new JButton();
        black.setBackground(Color.black);
        black.setPreferredSize(new Dimension(10,10));
        black.setActionCommand("black");
        black.addActionListener((ActionListener) this);
        if(colorButtonSelected == 4){
        	black.setFocusPainted(true);
        	currentPenColor = Color.black;
        }
        else{
        	black.setFocusPainted(false);
        }
        
        JButton yellow = new JButton();
        yellow.setBackground(Color.yellow);
        yellow.setPreferredSize(new Dimension(10,10));
        yellow.setActionCommand("yellow");
        yellow.addActionListener((ActionListener) this);
        if(colorButtonSelected == 5){
        	yellow.setFocusPainted(true);
        }
        else{
        	yellow.setFocusPainted(false);
        }
        
        JLabel widthLabel = new JLabel("Pen Size");
        strokeWidth = new JSlider(JSlider.HORIZONTAL,1,50,1);
        strokeWidth.setPreferredSize(new Dimension(100,20));
        
        JToggleButton DrawOrErase = new JToggleButton("Eraser Mode",false);
        DrawOrErase.addMouseListener(new eraseButtonListener());
        toolbar.add(DrawOrErase);
        toolbar.add(paletteButton);
        toolbar.add(widthLabel);
        toolbar.add(strokeWidth);
        toolbar.add(resetter);
        toolbar.add(boardMenu);
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
    
    /*
     * Make the drawing buffer and draw some starting content for it.
     */
    private void makeDrawingBuffer() 
    {
        drawingBuffer = createImage(getWidth(), getHeight());
        fillWithWhite();
        drawSmile();
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
     * Draw a happy smile on the drawing buffer.
     */
    private void drawSmile() 
    {
        final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

        // all positions and sizes below are in pixels
        final Rectangle smileBox = new Rectangle(20, 20, 100, 100); // x, y, width, height
        final Point smileCenter = new Point(smileBox.x + smileBox.width/2, smileBox.y + smileBox.height/2);
        final int smileStrokeWidth = 3;
        final Dimension eyeSize = new Dimension(9, 9);
        final Dimension eyeOffset = new Dimension(smileBox.width/6, smileBox.height/6);
        
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(smileStrokeWidth));
        
        // draw the smile -- an arc inscribed in smileBox, starting at -30 degrees (southeast)
        // and covering 120 degrees
        g.drawArc(smileBox.x, smileBox.y, smileBox.width, smileBox.height, -30, -120);
        
        // draw some eyes to make it look like a smile rather than an arc
        for (int side: new int[] { -1, 1 }) {
            g.fillOval(smileCenter.x + side * eyeOffset.width - eyeSize.width/2,
                       smileCenter.y - eyeOffset.height - eyeSize.width/2,
                       eyeSize.width,
                       eyeSize.height);
        }
        
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
    		
    		//made eraser width be controlled by slider
    		Brushstroke eraser = new Brushstroke(startingX, startingY, x, y, Color.white, (Integer)strokeWidth.getValue());
    		eraserLineSegment(eraser);
    		
    		
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
    
    public class PalettePopupListener implements MouseListener{

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
                newCanvas canvas = new newCanvas(800, 600, null);
                window.add(canvas, BorderLayout.CENTER);
                window.pack();
                window.setVisible(true);
            }
        });
    }
}
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;


/**
 * This is my implementation of the Warmup. I borrowed a lot of code from DeJuan's warmup
 * to implement the eraser, but I also added basic color and pen size support.
 */
public class JoeWarmupCanvas extends JPanel{
    
	private static final long serialVersionUID = 1L;
	// image where the user's drawing is stored
    private Image drawingBuffer;
    public boolean eraserMode = false;
    public JRadioButton blue;
    public JRadioButton green;
    public JRadioButton red;
    public JRadioButton black;
    public JTextField strokeWidth;
    public ButtonGroup colorSelect;
        
    /**
     * Make a canvas.
     * @param width width in pixels
     * @param height height in pixels
     */
    public JoeWarmupCanvas(int width, int height) 
    {
        this.setPreferredSize(new Dimension(width, height));
        addDrawingController();
        addEraserController();
        
        JLabel redLabel = new JLabel("Red");
        red = new JRadioButton();
        JLabel greenLabel = new JLabel("Green");
        green = new JRadioButton();
        JLabel blueLabel = new JLabel("Blue");
        blue = new JRadioButton();
        JLabel blackLabel = new JLabel("Black");
        black = new JRadioButton();
        colorSelect = new ButtonGroup();
        colorSelect.add(black);
        colorSelect.add(red);
        colorSelect.add(blue);
        colorSelect.add(green);
        
        JLabel widthLabel = new JLabel("Pen Size");
        strokeWidth = new JTextField("1");
        strokeWidth.setPreferredSize(new Dimension(40,20));
        JToggleButton DrawOrErase = new JToggleButton("Eraser Mode",false);
        DrawOrErase.addMouseListener(new eraseButtonListener());
        add(DrawOrErase);
        add(blackLabel);
        add(black);
        add(redLabel);
        add(red);
        add(blueLabel);
        add(blue);
        add(greenLabel);
        add(green);
        add(widthLabel);
        add(strokeWidth);
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
    private void drawLineSegment(int x1, int y1, int x2, int y2) 
    {
        Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
        g.setColor(Color.BLACK);
        System.out.println(red.isSelected());
        if (black.isSelected()){
        	g.setColor(Color.BLACK);
        }
        else if (red.isSelected()){
        	g.setColor(Color.RED);
        }
        else if (blue.isSelected()){
        	g.setColor(Color.BLUE);
        }
        else if (green.isSelected()){
        	g.setColor(Color.GREEN);;
        }
        
        String penSizeStr = strokeWidth.getText();
        int penSize;
        try{
        	penSize = Integer.parseInt(penSizeStr);
        }
        catch(Exception e){
        	penSize = 1;
        }
        g.setStroke(new BasicStroke(penSize));
        g.drawLine(x1, y1, x2, y2);
        
        // IMPORTANT!  every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
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
            drawLineSegment(lastX, lastY, x, y);
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
    
    public static JToggleButton DrawOrErase = new JToggleButton("Currently In Eraser Mode?",false);
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
    
    private void eraserLineSegment(int x1, int y1, int x2, int y2) 
    {
        Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
        int eraserWidth = 10;
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(eraserWidth));
        g.drawLine(x1, y1, x2, y2);
        
        // IMPORTANT!  every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
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
    		eraserLineSegment(startingX, startingY, x, y);
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
                JoeWarmupCanvas canvas = new JoeWarmupCanvas(800, 600);
                window.add(canvas, BorderLayout.CENTER);
                window.pack();
                window.setVisible(true);
            }
        });
    }
}
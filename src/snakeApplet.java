

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class snakeApplet extends Frame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3426323838260492550L;
	private static snakeCanvas c;
	
	public static  void main(String[] args) throws InterruptedException
	{
		Frame a = new Frame();
		c = new snakeCanvas();
		c.setPreferredSize(new Dimension(310, 750));
		c.setVisible(true);
		c.setFocusable(true);
		a.add(c);
		a.setVisible(true);
		a.setSize(new Dimension(310, 750));
		a.setLocation(500,0);
		
		
		a.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){

                System.exit(0);
            }

        });
		
	}

	public void paint(Graphics g)
	{
		this.setSize(new Dimension(310, 750));
		
	}

}

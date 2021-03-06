/*
 * Basic window class for all our windows
 */

package orbit.desktop;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;


public class Window extends JFrame{

	private static final long serialVersionUID = -3265727502097532460L;
	private final ImageIcon backgroundImage = new ImageIcon("bin/Button.png");
	private final ImageIcon disabledImage = new ImageIcon("bin/buttondisabled.png");
	protected Orbit orbit; //parent class for sharing stuff to main game
	
	Window(){
		super("Orbit");
	}
	
	Window(Orbit parent){
		super("Orbit");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.orbit = parent;
	}

	
	protected class JOrbitButton extends JButton{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 6L;

		String name;

		JOrbitButton(String name){
			
			this.name = name;
			Dimension d = new Dimension(121, 40);
			setPreferredSize(d);
			repaint();
			
		}
		
		protected void paintComponent(Graphics g){
			Graphics2D gtwod = (Graphics2D) g;
	        FontMetrics fontM = gtwod.getFontMetrics();
	        Rectangle2D r2d = fontM.getStringBounds(name, gtwod);				//centers the string in button
	        int x = (this.getWidth() - (int) r2d.getWidth()) / 2;
	        int y = (this.getHeight() - (int) r2d.getHeight()) / 2 + fontM.getAscent();
	        if(isEnabled()){
				g.drawImage(backgroundImage.getImage(), 0, 0, 121, 40, null);
	        }else{
				g.drawImage(disabledImage.getImage(), 0, 0, 121, 40, null);

	        }
			g.drawString(name, x, y);
			
		}
		
	}
}

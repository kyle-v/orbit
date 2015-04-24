/*
 * Basic window class for all our windows
 */

package com.orbit.game.desktop;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;


public class Window extends JFrame{

	private static final long serialVersionUID = -3265727502097532460L;
	private final ImageIcon backgroundImage = new ImageIcon("assets/Button.png");
	protected Orbit orbit; //parent class for sharing stuff to main game
	
	Window(){
		super("Orbit");
	}
	
	Window(Orbit parent){
		super("Orbit");
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
			Dimension d = new Dimension(100,30);
			setPreferredSize(d);
			repaint();
			
		}
		
		protected void paintComponent(Graphics g){
			g.drawImage(backgroundImage.getImage(), 0, 0, 100, 30, null);
			g.drawString(name, 15, 15);
			
		}
		
	}
}

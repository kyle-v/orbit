/*
 * Basic window class for all our windows
 */

package com.orbit.game.desktop;

import javax.swing.JFrame;


public class Window extends JFrame{

	private static final long serialVersionUID = -3265727502097532460L;
	protected Orbit orbit; //parent class for sharing stuff to main game
	
	Window(){
		super("Orbit");
	}
	
	Window(Orbit parent){
		super("Orbit");
		this.orbit = parent;
	}

}

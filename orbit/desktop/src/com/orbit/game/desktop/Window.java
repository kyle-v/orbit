/*
 * Basic window class for all our windows
 */

package com.orbit.game.desktop;

import javax.swing.JFrame;

import orbit.Orbit;

public class Window extends JFrame{
	private Orbit parent; //parent class for sharing stuff to main game
	
	Window(Orbit parent){
		this.parent = parent;
	}

}

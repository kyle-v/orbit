/*
 * Basic window class for all our windows
 */

package orbit;

import javax.swing.JFrame;

public class Window extends JFrame{
	private Orbit parent; //parent class for sharing stuff to main game
	
	Window(Orbit parent){
		this.parent = parent;
	}

}

package com.germistry.main;

import java.awt.event.KeyEvent;

public class Keyboard {

	public static boolean[] pressed = new boolean[403];
	public static boolean[] previous = new boolean[403];
	
	private Keyboard() {}

	public static void update() {
		for(int i = 0; i < 5; i++) {
			if(i == 0) previous[KeyEvent.VK_LEFT] = pressed[KeyEvent.VK_LEFT];
			if(i == 1) previous[KeyEvent.VK_RIGHT] = pressed[KeyEvent.VK_RIGHT];
			if(i == 2) previous[KeyEvent.VK_UP] = pressed[KeyEvent.VK_UP];
			if(i == 3) previous[KeyEvent.VK_DOWN] = pressed[KeyEvent.VK_DOWN];
			if(i == 4) previous[KeyEvent.VK_ENTER] = pressed[KeyEvent.VK_ENTER];
		}
	}
	
	public static void keyPressed(KeyEvent e) {
		pressed[e.getKeyCode()] = true;
	}
	public static void keyReleased(KeyEvent e) {
		pressed[e.getKeyCode()] = false;
	}
	
	public static boolean typed(int keyEvent) {
		return !pressed[keyEvent] && previous[keyEvent];
	}
	
}

package com.germistry.main;

import java.nio.file.Paths;

import javax.swing.JFrame;

public class Start {

	public static void main(String[] args) {
		System.out.println("CWD is " + Paths.get("").toAbsolutePath().toString());
		
		Game game = new Game();
		
		JFrame window = new JFrame("Java Game Pack");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.add(game);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		game.start();
	}

}

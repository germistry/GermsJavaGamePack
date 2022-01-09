package com.germistry.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.germistry.gui.GuiScreen;
import com.germistry.gui.PanelName;
import com.germistry.gui.leaderboardPanels.MinesweeperLeaderboardPanel;
import com.germistry.gui.leaderboardPanels.PipesLeaderboardPanel;
import com.germistry.gui.leaderboardPanels.SnakeLeaderboardPanel;
import com.germistry.gui.leaderboardPanels.TetrisLeaderboardPanel;
import com.germistry.gui.leaderboardPanels.Twenty48LeaderboardPanel;
import com.germistry.gui.menus.MainMenuPanel;
import com.germistry.gui.menus.MinesweeperMenuPanel;
import com.germistry.gui.menus.PipesMenuPanel;
import com.germistry.gui.menus.SnakeMenuPanel;
import com.germistry.gui.menus.TetrisMenuPanel;
import com.germistry.gui.menus.Twenty48MenuPanel;
import com.germistry.gui.playPanels.MinesweeperPlayPanel;
import com.germistry.gui.playPanels.PipesPlayPanel;
import com.germistry.gui.playPanels.SnakePlayPanel;
import com.germistry.gui.playPanels.TetrisPlayPanel;
import com.germistry.gui.playPanels.Twenty48PlayPanel;

//TODO 8.0 All Score Managers & Leaderboards to be combined into one class so there is one scores file/one leaderboard file
//TODO 8.1 Resource Loading? Do I want a buffer strategy, particularly for raid? & load images as Sprites from one sheet
//TODO 8.20 Rehaul GUI & Components once ALL games implemented

public class Game extends JPanel implements KeyListener, Runnable {

	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 896;  
	public static final int HEIGHT = 504;
	public static final Font mainBold = new Font("Arial", Font.BOLD, 14);
	public static final Font mainReg = new Font("Arial", Font.PLAIN, 16);
	
	private Thread game;
	private boolean running;
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	
	private GuiScreen screen;
	private Mouse mouse;
	
	private static final int NUM_IMAGES = 21;
	public static final BufferedImage uiAssets[] = new BufferedImage[NUM_IMAGES];
	private String path = "/UI/uiButtons/";
	public static final BufferedImage tetrisAssets[] = new BufferedImage[2];
	private String tetrisPath = "/UI/tetris/";
	
	public Game() {
		setFocusable(true);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addKeyListener(this);
		loadAssets();
		loadPanels();
		
		mouse = Mouse.getInstance();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
	}
	
	private void loadPanels() {
		screen = GuiScreen.getInstance();
		screen.add(PanelName.MAIN_MENU, new MainMenuPanel());
		screen.add(PanelName.TWENTY28_MENU, new Twenty48MenuPanel());
		screen.add(PanelName.TWENTY28_LEADERBOARD, new Twenty48LeaderboardPanel());
		screen.add(PanelName.TWENTY28_PLAY, new Twenty48PlayPanel());
		screen.add(PanelName.MINESWEEPER_MENU, new MinesweeperMenuPanel());
		screen.add(PanelName.MINESWEEPER_LEADERBOARD, new MinesweeperLeaderboardPanel());
		screen.add(PanelName.MINESWEEPER_PLAY, new MinesweeperPlayPanel());
		screen.add(PanelName.SNAKE_MENU, new SnakeMenuPanel());
		screen.add(PanelName.SNAKE_LEADERBOARD, new SnakeLeaderboardPanel());
		screen.add(PanelName.SNAKE_PLAY, new SnakePlayPanel());
		screen.add(PanelName.TETRIS_MENU, new TetrisMenuPanel());
		screen.add(PanelName.TETRIS_LEADERBOARD, new TetrisLeaderboardPanel());
		screen.add(PanelName.TETRIS_PLAY, new TetrisPlayPanel());
		screen.add(PanelName.PIPES_MENU, new PipesMenuPanel());
		screen.add(PanelName.PIPES_LEADERBOARD, new PipesLeaderboardPanel());
		screen.add(PanelName.PIPES_PLAY, new PipesPlayPanel());
		//TODO 7.3 BLOCK BREAKER (C64 Krackout Clone) 
		//TODO 7.7 RAID (C64 Raid Clone)
		//TODO 7.2 SPACE INVADERS 
		//TODO 7.5 SUDOKU - Super Hard!
		//TODO 7.5 PIPES
		screen.setCurrentPanel(PanelName.MAIN_MENU);
	}
	
	private void loadAssets() {
		try {
			for (int i = 0; i < NUM_IMAGES; i++) {
	            var fullpath = path + i + ".png";
	            System.out.print("Trying to load: " + fullpath + " ...");
	            uiAssets[i] = ImageIO.read(getClass().getResource(fullpath));
	            System.out.println("succeeded!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("failed!");
		}
		try {
			for (int i = 0; i < 2; i++) {
	            var fullpath = tetrisPath + i + ".png";
	            System.out.print("Trying to load: " + fullpath + " ...");
	            tetrisAssets[i] = ImageIO.read(getClass().getResource(fullpath));
	            System.out.println("succeeded!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("failed!");
		}
	}
	
	private void update() {
		screen.update();
		Keyboard.update();
		
	}
	
	private void render() {
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		screen.render(g);
		g.dispose();
		
		//drawing to actual Jpanel
		Graphics2D g2d = (Graphics2D) getGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
	}
	
	//locking fps at 60fps
	@Override
	public void run() {
		
		int fps = 0, updates = 0;
		long fpsTimer = System.currentTimeMillis();
		double nsPerUpdate = 1000000000.0 / 60;
		
		//last update time in ns
		double diff = System.nanoTime(); 
		double unprocessed = 0;
		
		while(running = true) {
			
			boolean shouldRender = false;
			double now = System.nanoTime();
			unprocessed += (now - diff) / nsPerUpdate;
			diff = now;
			
			//the update loop
			while(unprocessed >= 1) {
				updates++;
				update();
				unprocessed--;
				shouldRender = true;
			}
			//rendering
			if(shouldRender) {
				fps++;
				render();
				shouldRender = false;
			}
			else {
				//TODO 2 better exception handling esp for file IO & audio Manager
				try {
					Thread.sleep(1); 
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		//fps timer
		if(System.currentTimeMillis() - fpsTimer > 1000) {
			System.out.printf("%d fps %d updates", fps, updates);
			System.out.println();
			fps = 0;
			updates = 0;
			fpsTimer += 1000;
		}
	}

	public synchronized void start() {
		if(running) return;
		running = true;
		game = new Thread(this, "game");
		game.start();
	}
	
	public synchronized void stop() {
		if(!running) return;
		running = false;
		System.exit(0);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		Keyboard.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Keyboard.keyReleased(e);
	}
	@Override
	public void keyTyped(KeyEvent e) {}

}

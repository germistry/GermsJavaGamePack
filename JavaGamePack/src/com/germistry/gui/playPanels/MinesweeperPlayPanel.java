package com.germistry.gui.playPanels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;

import com.germistry.gui.GuiPanel;
import com.germistry.gui.GuiScreen;
import com.germistry.gui.PanelName;
import com.germistry.gui.components.ImageButton;
import com.germistry.main.Game;

import com.germistry.minesweeper.GameBoard;
import com.germistry.minesweeper.ScoreManager;
import com.germistry.utils.DrawUtils;

public class MinesweeperPlayPanel extends GuiPanel {

	private GameBoard board;
	private BufferedImage info;
	private ScoreManager scores;
	private Font scoreFont;
	private String timeFormatted;
	
	private ImageButton backButton; // this is the in-game button to return to the menu
	private static final int NUM_IMAGES = 12;
	public static BufferedImage smileyAssets[] = new BufferedImage[NUM_IMAGES];
  	private String path = "/minesweeper/smileyStates/"; 
  	
	private ImageButton smileyHappyButton; 
	private ImageButton smileyDeadButton;
	private ImageButton smileyWonButton;
	
	private ImageButton screenShot;
	
	private int spacing = 20;
	private int smileyY = 70;
	
	private boolean added; //tracks if buttons have already been added to panel
	private int alpha;
	private Font gameOverFont;
	private boolean screenshot;
	
	public MinesweeperPlayPanel() {
		scoreFont = Game.main.deriveFont(24f);
		gameOverFont = Game.main.deriveFont(70f);
		board = new GameBoard(Game.WIDTH / 2 - GameBoard.BOARD_WIDTH / 2, 108);
		scores = board.getScores();
		info = new BufferedImage(GameBoard.BOARD_WIDTH, 50, BufferedImage.TYPE_INT_RGB);
		backButton = new ImageButton(Game.uiAssets[0], Game.uiAssets[1], Game.uiAssets[2], Game.WIDTH / 2 - GameBoard.BOARD_WIDTH / 2, 10);
		try {
			for (int i = 0; i < NUM_IMAGES; i++) {
	            var fullpath = path + i + ".png";
	            System.out.print("Trying to load: " + fullpath + " ...");
	            smileyAssets[i] = ImageIO.read(getClass().getResource(fullpath));
	            System.out.println("succeeded!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("failed!");
		}
		smileyHappyButton = new ImageButton(smileyAssets[0], smileyAssets[1], smileyAssets[2], Game.WIDTH / 2 - 20, smileyY);
		smileyDeadButton = new ImageButton(smileyAssets[6], smileyAssets[7], smileyAssets[8],Game.WIDTH / 2 - 20, smileyY);
		smileyWonButton = new ImageButton(smileyAssets[9], smileyAssets[10], smileyAssets[11],Game.WIDTH / 2 - 20, smileyY);
		screenShot = new ImageButton(Game.uiAssets[3], Game.uiAssets[4], Game.uiAssets[5], backButton.getX() + backButton.getWidth() + spacing, backButton.getY());
	
		backButton.setLabelText("Menu");
		screenShot.setLabelText("Screen Shot");
		
		smileyDeadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.getScores().reset();
				board.reset();
				alpha = 0;
				remove(smileyDeadButton);
				added = false;
				add(smileyHappyButton);
			}
		});
		
		smileyWonButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.getScores().reset();
				board.reset();
				alpha = 0;
				remove(smileyWonButton);
				added = false;
				add(smileyHappyButton);
			}
		});
				
		smileyHappyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.getScores().reset();
				board.reset();
			}
		});
		
		screenShot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				screenshot = true;
			}
		});
		
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.MINESWEEPER_MENU);
				board.setHasStarted(false); 
			}
		});
		add(backButton);
		add(screenShot);
		add(smileyHappyButton);
	}
	@Override
	public void update() {
		if(board.hasStarted()) {
			board.update();
		}
		if(board.hasLost() || board.hasWon()) {
			board.setHasStarted(false);
		}
	}
	
	@Override
	public void render(Graphics2D g) {
		drawGui(g);
		if(board.isSmileyOh() && smileyHappyButton.getReleasedImage() == smileyAssets[0]) {
			smileyHappyButton.setReleasedImage(smileyAssets[3]);
		}
		else if (!board.isSmileyOh() && smileyHappyButton.getReleasedImage() == smileyAssets[3]) 
			smileyHappyButton.setReleasedImage(smileyAssets[0]);
		board.render(g);
		
		//take the screenshot before rendering game over
		if(screenshot) {
			BufferedImage image = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = (Graphics2D)image.getGraphics();
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			drawGui(g2d);
			board.render(g2d);
			if(board.hasLost()) {
				g2d.drawImage(smileyAssets[6], Game.WIDTH / 2 - 20, smileyY, smileyAssets[6].getWidth(), smileyAssets[6].getHeight(), null);
			}
			else if(board.hasWon()) {
				g2d.drawImage(smileyAssets[9], Game.WIDTH / 2 - 20, smileyY, smileyAssets[9].getWidth(), smileyAssets[9].getHeight(), null);
			}
			else {
				g2d.drawImage(smileyAssets[0], Game.WIDTH / 2 - 20, smileyY, smileyAssets[0].getWidth(), smileyAssets[0].getHeight(), null);
			}
			//bizarre way of finding desktop on any system/drive in java!
			try {
				String path = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath().toString();
				ImageIO.write(image, "gif", new File(path, "screenshot" + System.nanoTime() + ".gif"));
			}
			catch (Exception e){
				e.printStackTrace();
			}
			screenshot = false;
		}
		if(board.hasLost()) {
			if(!added) {
				added = true;
				add(smileyDeadButton);
				remove(smileyHappyButton);
				
			}
		}
		if(board.hasWon()) { 
			if(!added) {
				added = true;
				add(smileyWonButton);
				remove(smileyHappyButton);
			}
		}
		super.render(g);
	}
	
	public void drawStartMessage(Graphics2D g) {
		g.setColor(new Color(222, 222, 222, alpha));
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.blue);
		g.setFont(gameOverFont);
		g.drawString("Press enter to start", Game.WIDTH / 2 - DrawUtils.getMessageWidth("Press enter to start", gameOverFont, g) / 2, 250);
	}
	
	private void drawGui(Graphics2D g) {
		//format time variables
		timeFormatted = DrawUtils.formatTime(scores.getTime());
		//drawing
		Graphics2D g2d = (Graphics2D)info.getGraphics();
		g2d.setColor(Color.darkGray);
		g2d.fillRect(0, 0, info.getWidth(), info.getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.white);
		g2d.setFont(scoreFont);
		g2d.drawString("Time: " + timeFormatted, spacing, 
				DrawUtils.getMessageHeight("Time: " + timeFormatted, scoreFont, g2d) + spacing + 10);
		g2d.drawString("Mines To Go: " + scores.getDisplayMines(), 
				info.getWidth() - DrawUtils.getMessageWidth("Mines To Go: " + scores.getDisplayMines(), scoreFont, g2d) - spacing, 
				DrawUtils.getMessageHeight("Mines To Go: " + scores.getDisplayMines(), scoreFont, g2d)+ spacing + 10);
		g2d.dispose();
		g.drawImage(info, Game.WIDTH / 2 - GameBoard.BOARD_WIDTH / 2, 60, null);
	}
}

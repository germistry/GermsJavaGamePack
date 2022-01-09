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
	private BufferedImage buttonPanel;
	private ScoreManager scores;
	private Font scoreFont;
	private Font dummyBtnFont;
	private String timeFormatted;
	
	private ImageButton backButton; // this is the in-game button to return to the menu
	private ImageButton screenShot;
	
	private static final int NUM_IMAGES = 12;
	public static BufferedImage smileyAssets[] = new BufferedImage[NUM_IMAGES];
  	private String path = "/minesweeper/smileyStates/"; 
	private ImageButton smileyHappyButton; 
	private ImageButton smileyDeadButton;
	private ImageButton smileyWonButton;
	
	private int spacing = 20;
	private int smileyY = 70;
	
	private boolean added; //tracks if buttons have already been added to panel
	private boolean screenshot;
	
	public MinesweeperPlayPanel() {
		dummyBtnFont = Game.mainBold.deriveFont(20f);
		scoreFont = Game.mainBold.deriveFont(24f);
		board = new GameBoard(Game.WIDTH / 2 - GameBoard.BOARD_WIDTH / 2, 108);
		scores = board.getScores();
		info = new BufferedImage(GameBoard.BOARD_WIDTH, 50, BufferedImage.TYPE_INT_RGB);
		buttonPanel = new BufferedImage(GameBoard.BOARD_WIDTH, 50, BufferedImage.TYPE_INT_RGB);
		backButton = new ImageButton(Game.uiAssets[0], Game.uiAssets[1], Game.uiAssets[2], Game.WIDTH / 2 - GameBoard.BOARD_WIDTH / 2, 10);
		screenShot = new ImageButton(Game.uiAssets[3], Game.uiAssets[4], Game.uiAssets[5], backButton.getX() + backButton.getWidth() + spacing, backButton.getY());
		
		loadAssets();
				
		smileyHappyButton = new ImageButton(smileyAssets[0], smileyAssets[1], smileyAssets[2], Game.WIDTH / 2 - 20, smileyY);
		smileyDeadButton = new ImageButton(smileyAssets[6], smileyAssets[7], smileyAssets[8],Game.WIDTH / 2 - 20, smileyY);
		smileyWonButton = new ImageButton(smileyAssets[9], smileyAssets[10], smileyAssets[11],Game.WIDTH / 2 - 20, smileyY);
			
		backButton.setLabelText("Menu");
		screenShot.setLabelText("Screen Shot");
		
		smileyDeadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.getScores().reset();
				board.reset();
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
		board.render(g);
		drawButtonPanel(g);
		drawGui(g);
		if(board.isSmileyOh() && smileyHappyButton.getReleasedImage() == smileyAssets[0]) {
			smileyHappyButton.setReleasedImage(smileyAssets[3]);
		}
		else if (!board.isSmileyOh() && smileyHappyButton.getReleasedImage() == smileyAssets[3]) 
			smileyHappyButton.setReleasedImage(smileyAssets[0]);
		
		
		//take the screenshot before rendering game over
		if(screenshot) {
			BufferedImage image = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = (Graphics2D)image.getGraphics();
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			drawButtonPanel(g2d);
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
	
	private void loadAssets() {
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
	}
	private void drawButtonPanel(Graphics2D g) {
		Graphics2D g2d = (Graphics2D)buttonPanel.getGraphics();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, buttonPanel.getWidth(), buttonPanel.getHeight());
		g2d.drawImage(Game.uiAssets[0], 0, 0, null);
		g2d.drawImage(Game.uiAssets[3], Game.uiAssets[0].getWidth() + spacing, 0, null);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.white);
		g2d.setFont(dummyBtnFont);
		g2d.drawString("Menu", Game.uiAssets[0].getWidth() / 2 - DrawUtils.getMessageWidth("Menu", dummyBtnFont, g2d) / 2, 
				Game.uiAssets[0].getHeight() / 2 + DrawUtils.getMessageHeight("Menu", dummyBtnFont, g2d) / 2);
		g2d.drawString("Screen Shot", Game.uiAssets[0].getWidth() + spacing + Game.uiAssets[3].getWidth() / 2 - DrawUtils.getMessageWidth("Screen Shot", dummyBtnFont, g2d) / 2, 
				Game.uiAssets[3].getHeight() / 2 + DrawUtils.getMessageHeight("Screen Shot", dummyBtnFont, g2d) / 2);
		g2d.setColor(Color.darkGray);
		g2d.setFont(scoreFont);
		g2d.drawString("Press smiley button to restart!", 
				buttonPanel.getWidth() - DrawUtils.getMessageWidth("Press smiley button to restart!", scoreFont, g2d)-10, 
				buttonPanel.getHeight() / 2 + DrawUtils.getMessageHeight("Press smiley button to restart!", scoreFont, g2d) / 2);
		
		g2d.dispose();
		g.drawImage(buttonPanel, Game.WIDTH / 2 - GameBoard.BOARD_WIDTH / 2, 10, null);
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
		if(scores.getDisplayMines() >= 0) {
			g2d.drawString("Mines To Go: " + scores.getDisplayMines(), 
					info.getWidth() - DrawUtils.getMessageWidth("Mines To Go: " + scores.getDisplayMines(), scoreFont, g2d) - spacing, 
					DrawUtils.getMessageHeight("Mines To Go: " + scores.getDisplayMines(), scoreFont, g2d)+ spacing + 10);
		}
		else {
			g2d.drawString("Mines To Go: 0", 
					info.getWidth() - DrawUtils.getMessageWidth("Mines To Go: 0", scoreFont, g2d) - spacing, 
					DrawUtils.getMessageHeight("Mines To Go: 0", scoreFont, g2d)+ spacing + 10);
		}
		g2d.dispose();
		g.drawImage(info, Game.WIDTH / 2 - GameBoard.BOARD_WIDTH / 2, 60, null);
	}
}

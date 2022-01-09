package com.germistry.gui.playPanels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;

import com.germistry.gui.GuiPanel;
import com.germistry.gui.GuiScreen;
import com.germistry.gui.PanelName;
import com.germistry.gui.components.GuiButton;
import com.germistry.gui.components.ImageButton;
import com.germistry.main.Game;
import com.germistry.twenty48.GameBoard;
import com.germistry.twenty48.ScoreManager;
import com.germistry.utils.DrawUtils;

public class Twenty48PlayPanel extends GuiPanel {

	private GameBoard board;
	private BufferedImage info;
	private ScoreManager scores;
	private Font scoreFont;
	private Font dummyBtnFont;
	private Font titleFont;
	private Font instructionFont;
	private String timeFormatted;
	private String bestTimeFormatted;
	private Color bestScoreColour = new Color(0x895589);
	private Color titleColour1 = new Color(0x4300FF);
	private Color titleColour2 = new Color(0xC5AFFF);
	
	private ImageButton screenShotBtn;
	private ImageButton returnToMain; // this is the in-game button to return to the menu
	private ImageButton restartGame; // this is the in-game button to restart
	
	//Game Over Variables
	private GuiButton restart;
	private GuiButton mainMenu; //this is the twenty48 menu
	private GuiButton screenShot;
	
	private int smallButtonWidth = 160;
	private int spacing = 20;
	private int largeButtonWidth = smallButtonWidth * 2 + spacing;
	private int buttonHeight = 50;
	
	private boolean added; //tracks if buttons have already been added to panel
	private int alpha;
	private Font gameOverFont;
	private boolean screenshot;
	
	public Twenty48PlayPanel() {
		titleFont = Game.mainBold.deriveFont(75f);
		instructionFont = Game.mainReg;
		dummyBtnFont = Game.mainBold.deriveFont(20f);
		scoreFont = Game.mainBold.deriveFont(24f);
		gameOverFont = Game.mainBold.deriveFont(70f);
		board = new GameBoard(Game.WIDTH - GameBoard.BOARD_WIDTH - 50, Game.HEIGHT / 2 - GameBoard.BOARD_HEIGHT / 2);
		scores = board.getScores();
		info = new BufferedImage(290, Game.HEIGHT - 60, BufferedImage.TYPE_INT_RGB);
		
		screenShotBtn = new ImageButton(Game.uiAssets[3], Game.uiAssets[4], Game.uiAssets[5], 50, Game.HEIGHT - Game.uiAssets[3].getHeight() - 30);
		returnToMain = new ImageButton(Game.uiAssets[0], Game.uiAssets[1], Game.uiAssets[2], 50, 30);
		restartGame = new ImageButton(Game.uiAssets[6], Game.uiAssets[7], Game.uiAssets[8], returnToMain.getX() + returnToMain.getWidth() + spacing, 30);
		
		mainMenu = new GuiButton(Game.WIDTH / 2 - largeButtonWidth / 2, 350, largeButtonWidth, buttonHeight);
		restart = new GuiButton(mainMenu.getX(), mainMenu.getY() - spacing - buttonHeight, smallButtonWidth, buttonHeight);
		screenShot = new GuiButton(restart.getX() + restart.getWidth() + spacing, restart.getY(), smallButtonWidth, buttonHeight);
	
		screenShotBtn.setLabelText("Screen Shot");
		returnToMain.setLabelText("Menu");
		restartGame.setLabelText("Restart");
		mainMenu.setLabelText("Return to Menu");
		restart.setLabelText("Restart");
		screenShot.setLabelText("Screen Shot");
		
		restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.getScores().reset();
				board.reset();
				alpha = 0;
				
				remove(restart);
				remove(screenShot);
				remove(mainMenu);
				added = false;
				add(returnToMain);
				add(restartGame);
				add(screenShotBtn);
			}
		});
		restartGame.addActionListener(new ActionListener() {
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
		screenShotBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				screenshot = true;
			}
		}); 
		mainMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.TWENTY28_MENU); 
			}
		});
		returnToMain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.TWENTY28_MENU);
				board.setHasStarted(false); 
			}
		});
		add(returnToMain);
		add(restartGame);
		add(screenShotBtn);
	}
	@Override
	public void update() {
		board.update();
		if(board.hasLost() || board.hasWon()) {
			alpha++;
			if(alpha > 170) alpha = 170;
		}
	}
	
	@Override
	public void render(Graphics2D g) {
		drawGui(g);
		board.render(g);
		//take the screenshot before rendering game over
		if(screenshot) {
			BufferedImage image = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = (Graphics2D)image.getGraphics();
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			drawGui(g2d);
			board.render(g2d);
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
				add(mainMenu);
				add(restart);
				add(screenShot);
				remove(returnToMain);
				remove(restartGame);
				remove(screenShotBtn);
			}
			drawGameOver(g);
		}
		if(board.hasWon()) {
			if(!added) {
				added = true;
				add(mainMenu);
				add(restart);
				add(screenShot);
				remove(returnToMain);
				remove(restartGame);
				remove(screenShotBtn);
			}
			drawGameWon(g);
		}
		super.render(g);
		
	}
	
	//TODO 8.2 make this fade in effect a bit nicer
	public void drawGameOver(Graphics2D g) {
		g.setColor(new Color(222, 222, 222, alpha));
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.red);
		g.setFont(gameOverFont);
		g.drawString("Game Over!", Game.WIDTH / 2 - DrawUtils.getMessageWidth("Game Over!", gameOverFont, g) / 2, 250);
	}
	//TODO 8.2 make this fade in effect a bit nicer
	public void drawGameWon(Graphics2D g) {
		g.setColor(new Color(222, 222, 222, alpha));
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.green);
		g.setFont(gameOverFont);
		g.drawString("Game WON!", Game.WIDTH / 2 - DrawUtils.getMessageWidth("Game WON!", gameOverFont, g) / 2, 250);
	}
	private void drawGui(Graphics2D g) {
		//format time variables
		timeFormatted = DrawUtils.formatTime(scores.getTime());
		if(scores.getBestTime() == Integer.MAX_VALUE) {
			bestTimeFormatted = "";
		}
		else {
			bestTimeFormatted = DrawUtils.formatTime(scores.getBestTime());
		}
		//drawing
		Graphics2D g2d = (Graphics2D)info.getGraphics();
		//background & dummy buttons
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, info.getWidth(), info.getHeight());
		g2d.drawImage(Game.uiAssets[0], 0, 0, null);
		g2d.drawImage(Game.uiAssets[6], 140, 0, null);
		g2d.drawImage(Game.uiAssets[3], 0, info.getHeight() - Game.uiAssets[3].getHeight(), null);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(dummyBtnFont);
		g2d.drawString("Menu", Game.uiAssets[0].getWidth() / 2 - DrawUtils.getMessageWidth("Menu", dummyBtnFont, g2d) / 2, 
				Game.uiAssets[0].getHeight() / 2 + DrawUtils.getMessageHeight("Menu", dummyBtnFont, g2d) / 2);
		g2d.drawString("Restart", 140 + Game.uiAssets[6].getWidth() / 2 - DrawUtils.getMessageWidth("Restart", dummyBtnFont, g2d) / 2, 
				Game.uiAssets[6].getHeight() / 2 + DrawUtils.getMessageHeight("Restart", dummyBtnFont, g2d) / 2);
		g2d.drawString("Screen Shot", Game.uiAssets[3].getWidth() / 2 - DrawUtils.getMessageWidth("Screen Shot", dummyBtnFont, g2d) / 2, 
				info.getHeight() - Game.uiAssets[3].getHeight() + Game.uiAssets[3].getHeight() / 2 + DrawUtils.getMessageHeight("Menu", dummyBtnFont, g2d) / 2);
		//title
		g2d.setFont(titleFont);
		g2d.setColor(titleColour1);
		g2d.drawString("2", 62, 120);  
		g2d.drawString("4", 146, 120);
		g2d.setColor(titleColour2);
		g2d.drawString("0", 104, 120);
		g2d.drawString("8", 188, 120);
		//instructions
		g2d.setColor(bestScoreColour);
		g2d.setFont(instructionFont);
		g2d.drawString("Use the arrow keys to combine tiles ", 30, 150);
		g2d.drawString("of the same number to reach elusive", 30, 170);
		g2d.drawString("number 2048!", 30, 190);
		//scores
		g2d.setFont(scoreFont);
		g2d.drawString("Best: " + scores.getCurrentTopScore(), 0, 280);
		g2d.drawString("Fastest: " + bestTimeFormatted, 0, 380);
		g2d.setColor(Color.darkGray);
		g2d.drawString("Score: " + scores.getCurrentScore(), 0, 230);
		g2d.drawString("Time: " + timeFormatted, 0, 330);
	
		g2d.dispose();
		g.drawImage(info, 50, 30, null);
	}
}

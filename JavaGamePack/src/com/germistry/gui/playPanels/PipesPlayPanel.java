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
import com.germistry.pipes.GameBoard;
import com.germistry.pipes.ScoreManager;
import com.germistry.utils.DrawUtils;


public class PipesPlayPanel extends GuiPanel {

	private GameBoard board;
	private BufferedImage info;
	private BufferedImage nextPipes;
	private ScoreManager scores;
	private Font scoreFont;
	private Font dummyBtnFont;
	private Font instructionFont;
	private String timeFormatted;
	private Color bestScoreColour = new Color(0x895589);
	
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
	
	public PipesPlayPanel() {
		dummyBtnFont = Game.mainBold.deriveFont(20f);
		scoreFont = Game.mainBold.deriveFont(24f);
		instructionFont = Game.mainReg;
		gameOverFont = Game.mainBold.deriveFont(70f);
		board = new GameBoard(Game.WIDTH - GameBoard.BOARD_WIDTH - 120, Game.HEIGHT / 2 - GameBoard.BOARD_HEIGHT / 2);
		scores = board.getScores();
		info = new BufferedImage(150, GameBoard.BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		nextPipes = new BufferedImage(70, GameBoard.BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		screenShotBtn = new ImageButton(Game.uiAssets[3], Game.uiAssets[4], Game.uiAssets[5], 30, Game.HEIGHT - Game.uiAssets[3].getHeight() - 28);
		returnToMain = new ImageButton(Game.uiAssets[9], Game.uiAssets[10], Game.uiAssets[11], 30, 28);
		restartGame = new ImageButton(Game.uiAssets[6], Game.uiAssets[7], Game.uiAssets[8], 30, 78);
		
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
				GuiScreen.getInstance().setCurrentPanel(PanelName.PIPES_MENU); 
			}
		});
		returnToMain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.PIPES_MENU);
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
		drawNextPipes(g);
		//take the screenshot before rendering game over
		if(screenshot) {
			BufferedImage image = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = (Graphics2D)image.getGraphics();
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
			drawGui(g2d);
			board.render(g2d);
			drawNextPipes(g);
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
		timeFormatted = DrawUtils.formatTimeToSeconds(scores.getTime());
		//drawing
		Graphics2D g2d = (Graphics2D)info.getGraphics();
		//background & dummy buttons
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, info.getWidth(), info.getHeight());
		g2d.drawImage(Game.uiAssets[9], 0, 0, null);
		g2d.drawImage(Game.uiAssets[6], 0, 50, null);
		g2d.drawImage(Game.uiAssets[3], 0, info.getHeight() - Game.uiAssets[3].getHeight(), null);
		
		//text for dummy buttons
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(dummyBtnFont);
		g2d.drawString("Menu", Game.uiAssets[9].getWidth() / 2 - DrawUtils.getMessageWidth("Menu", dummyBtnFont, g2d) / 2, 
				Game.uiAssets[9].getHeight() / 2 + DrawUtils.getMessageHeight("Menu", dummyBtnFont, g2d) / 2);
		g2d.drawString("Restart", Game.uiAssets[6].getWidth() / 2 - DrawUtils.getMessageWidth("Restart", dummyBtnFont, g2d) / 2, 
				50 + Game.uiAssets[6].getHeight() / 2 + DrawUtils.getMessageHeight("Restart", dummyBtnFont, g2d) / 2);
		g2d.drawString("Screen Shot", Game.uiAssets[3].getWidth() / 2 - DrawUtils.getMessageWidth("Screen Shot", dummyBtnFont, g2d) / 2, 
				info.getHeight() - Game.uiAssets[3].getHeight() + Game.uiAssets[3].getHeight() / 2 + DrawUtils.getMessageHeight("Menu", dummyBtnFont, g2d) / 2);
		
		g2d.setColor(bestScoreColour);
		//scores
		g2d.setFont(scoreFont);
		g2d.drawString("Pipes: " + scores.getPipeCount(), 0, 350);
		g2d.setColor(Color.darkGray);
		g2d.drawString("Time: " + timeFormatted, 0, 390);
	
		g2d.dispose();
		g.drawImage(info, 30, Game.HEIGHT / 2 - GameBoard.BOARD_HEIGHT / 2, null);
	}
		
	private void drawNextPipes(Graphics2D g) {
		//next tiles
		Graphics2D g2d = (Graphics2D)nextPipes.getGraphics();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, nextPipes.getWidth(), nextPipes.getHeight());
		g2d.setColor(Color.darkGray);
		//scores
		g2d.setFont(instructionFont);
		g2d.drawString("Current:", 7, 30);
		g2d.fillRect(3, 40, 64, 261);
		if(GameBoard.currentPipe != null) {
			g2d.drawImage(GameBoard.currentPipe.getPipeType().getImage(), 3, 40, null);
		}
		if(GameBoard.option1Pipe != null) {
			g2d.drawImage(GameBoard.option1Pipe.getPipeType().getImage(), 3, 107, null);
		}
		if(GameBoard.option2Pipe != null) {
			g2d.drawImage(GameBoard.option2Pipe.getPipeType().getImage(), 3, 172, null);
		}
		if(GameBoard.option3Pipe != null) {
			g2d.drawImage(GameBoard.option3Pipe.getPipeType().getImage(), 3, 237, null);
		}
		g2d.setColor(Color.green);
		g2d.drawRect(2, 39, 65, 65);
		g2d.drawRect(1, 38, 67, 67);
		g2d.drawRect(0, 37, 69, 69);
		g2d.drawRect(2, 106, 65, 65);
		g2d.drawRect(2, 171, 65, 65);
		g2d.drawRect(2, 236, 65, 65);
		
		g2d.dispose();
		g.drawImage(nextPipes, Game.WIDTH - 100, Game.HEIGHT / 2 - GameBoard.BOARD_HEIGHT / 2, null);
	}
}

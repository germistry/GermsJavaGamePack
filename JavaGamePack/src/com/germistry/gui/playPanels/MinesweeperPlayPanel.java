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
	
	private GuiButton returnToMain; // this is the in-game button to return to the menu
	private GuiButton restartGame; // this is the in-game button to restart
	//Game Over Variables
	private GuiButton restart;
	private GuiButton mainMenu; //this is the minesweeper menu
	private GuiButton screenShot;
	
	private int smallButtonWidth = 180;
	private int spacing = 20;
	private int largeButtonWidth = smallButtonWidth * 2 + spacing;
	private int buttonHeight = 50;
	
	private boolean added; //tracks if buttons have already been added to panel
	private int alpha;
	private Font gameOverFont;
	private boolean screenshot;
	
	public MinesweeperPlayPanel() {
		scoreFont = Game.main.deriveFont(24f);
		gameOverFont = Game.main.deriveFont(70f);
		board = new GameBoard(Game.WIDTH / 2 - GameBoard.BOARD_WIDTH / 2, 20);
		scores = board.getScores();
		info = new BufferedImage(GameBoard.BOARD_WIDTH, 50, BufferedImage.TYPE_INT_RGB);
		returnToMain = new GuiButton(Game.WIDTH / 2 - (smallButtonWidth * 2 + spacing) / 2, Game.HEIGHT - buttonHeight - spacing, smallButtonWidth, buttonHeight);
		restartGame = new GuiButton(returnToMain.getX() + smallButtonWidth + spacing, Game.HEIGHT - buttonHeight - spacing, smallButtonWidth, buttonHeight);
		
		mainMenu = new GuiButton(Game.WIDTH / 2 - largeButtonWidth / 2, 350, largeButtonWidth, buttonHeight);
		restart = new GuiButton(mainMenu.getX(), mainMenu.getY() - spacing - buttonHeight, smallButtonWidth, buttonHeight);
		screenShot = new GuiButton(restart.getX() + restart.getWidth() + spacing, restart.getY(), smallButtonWidth, buttonHeight);
	
		returnToMain.setLabelText("Return to Menu");
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
		
		mainMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.MINESWEEPER_MENU);
				board.setHasStarted(false); 
			}
		});
		returnToMain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiScreen.getInstance().setCurrentPanel(PanelName.MINESWEEPER_MENU);
				board.setHasStarted(false); 
			}
		});
		add(returnToMain);
		add(restartGame);
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
			}
			drawGameWon(g);
		}
		if(!board.hasStarted() && !board.hasWon() || !board.hasStarted() && !board.hasLost()) {
			drawStartMessage(g);
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
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, info.getWidth(), info.getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.darkGray);
		g2d.setFont(scoreFont);
		g2d.drawString("Time: " + timeFormatted, spacing, 
				DrawUtils.getMessageHeight("Time: " + timeFormatted, scoreFont, g2d) + spacing);
		g2d.drawString("Mines To Go: " + scores.getDisplayMines(), 
				info.getWidth() - DrawUtils.getMessageWidth("Mines To Go: " + scores.getDisplayMines(), scoreFont, g2d) - spacing, 
				DrawUtils.getMessageHeight("Mines To Go: " + scores.getDisplayMines(), scoreFont, g2d)+ spacing);
		g2d.dispose();
		g.drawImage(info, Game.WIDTH / 2 - GameBoard.BOARD_WIDTH / 2, GameBoard.BOARD_HEIGHT + spacing, null);
	}
}

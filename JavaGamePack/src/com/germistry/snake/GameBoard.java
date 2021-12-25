package com.germistry.snake;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import com.germistry.main.Keyboard;

//snake logic
public class GameBoard {

	public final static int UNIT_SIZE = 16;
	public final static int ROWS = 28;
	public final static int COLS = 28;
	public final static int BOARD_WIDTH = UNIT_SIZE * COLS;
	public final static int BOARD_HEIGHT = UNIT_SIZE * ROWS;
	public final static int TOTAL_GAME_UNITS = COLS * ROWS * UNIT_SIZE;
	
	private int x, y;
	private BufferedImage gameBoard;
	private BufferedImage finalBoard;
	
	//for timer
	private long elapsedMS;
	private long startTime;
	private boolean hasStarted = false;
	//simple saving, TODO 2 look into encrypting
	private int counter;
		
	private int direction = 2;  // 0 down, 1 up, 2 right, 3 left
	private int[] xDir = {0, 0, 1, -1};  //right - left
	private int[] yDir = {1, -1, 0, 0};  //down - up
	
	private ScoreManager scores;
	private Leaderboard leaderboard;
	private boolean lost;
	private boolean snakeCanMove = false;
	
	private ArrayList<Integer> snakeX = new ArrayList<Integer>(); 
	private ArrayList<Integer> snakeY = new ArrayList<Integer>();

	private int delay = 16;
	private int speed = 10;
	
	private int fruitX, fruitY;
	private int fruitColour; //as a 0xFFFFFF value
	
	public GameBoard(int x, int y) {
		this.x = x;
		this.y = y;
		gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		createBoard();
		leaderboard = Leaderboard.getInstance();
		leaderboard.loadTopScores();
		scores = new ScoreManager(this);
		scores.loadGame();
		scores.setCurrentTopScore(leaderboard.getHighScore());
		if(scores.newGame()) {
			start();
			scores.saveGame();
		}
		else {
			for (int i = 0; i < scores.getSnakeX().size(); i++) {
				snakeX.add(scores.getSnakeX().get(i));
			}
			for (int j = 0; j < scores.getSnakeY().size(); j++) {
				snakeY.add(scores.getSnakeY().get(j));
			}
			direction = scores.getDirection();
			
			if(scores.getFruitX() == 0 && scores.getFruitY() == 0 && scores.getFruitColValue() == 0) {
				spawnRandomFruit();
			}
			else {
				spawnFruit(scores.getFruitX(), scores.getFruitY(), scores.getFruitColValue());
			}
			//to stop cheesing, user must have to hit the restart button!
			lost = checkLost();
		}
	}

	public void reset() {
		if(!snakeX.isEmpty())
			snakeX.clear();
		if(!snakeY.isEmpty())
			snakeY.clear();
		direction = 2;
		start();
		scores.saveGame();
		lost = false;
		hasStarted = false;
		snakeCanMove = false;
		startTime = System.nanoTime();
		elapsedMS = 0;
		counter = 0;
	}
	
	private void start() {
		spawnStartingSnake();
		snakeCanMove = false;
		spawnRandomFruit();
		if(snakeX.get(0) == fruitX && snakeY.get(0) == fruitY || fruitColour == 0x00CC00 || fruitColour == 0x404040) {
			spawnRandomFruit();
		}
	}
		
	private void spawnFruit(int x, int y, int colour) {
		fruitX = x;
		fruitY = y;		
		fruitColour = colour;
	}
		
	private void spawnStartingSnake() {
		snakeX.add(5);
		snakeY.add(7);
	}
	private void spawnRandomFruit() {
		Random random = new Random();	
		fruitX = random.nextInt(COLS);
		fruitY = random.nextInt(ROWS);
		fruitColour = random.nextInt(0xffffff + 1);
	}
	
	public void update() {
		counter++;
		//could change this to save on the same thread but not causing any issues like this as very small data
		moveSnake();
		checkInputs();
		checkCollision();
		if(!lost) {
			if(hasStarted) {
				elapsedMS = (System.nanoTime() - startTime) / 1000000;
				scores.setTime(elapsedMS);
			}
			else {
				startTime = System.nanoTime();
				scores.setTime(elapsedMS);
			}
		}
		if(scores.getCurrentScore() > scores.getCurrentTopScore()) {
			scores.setCurrentTopScore(scores.getCurrentScore());
		}
		scores.saveGame();
	}
	
	public void render(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
		g2d.drawImage(gameBoard, 0, 0, null); 
		
		//drawGrid(g2d);
		drawSnake(g2d);
		drawFruit(g2d);
		
		g.drawImage(finalBoard, x, y, null);
		g2d.dispose();
	}
	
	private void drawFruit(Graphics2D g) {
		if (fruitColour != 0) { 
			int r = (fruitColour & 0xff0000) >> 16;
			int g2 = (fruitColour & 0xff00) >> 8;
			int b = (fruitColour & 0xff);
			Color c = new Color(r, g2, b); 
			g.setColor(c);
		}
			
		else 
			g.setColor(Color.white);
		
			g.fillOval(fruitX * UNIT_SIZE, fruitY * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
		
		
	}
	
	private void drawSnake(Graphics2D g) {
		if(snakeX.isEmpty() || snakeY.isEmpty()) {
			String excMsg = "Snake x or y coordinates do not exist!";
            throw new IllegalStateException(excMsg);
		}
		else {		
			for (int i = 0; i < snakeX.size(); i++) {
				g.setColor(new Color(0x00CC00));
				g.fillRect(snakeX.get(i) * UNIT_SIZE, snakeY.get(i) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
				
			}
		}
	}
	
	private void moveSnake() {
		if(hasStarted && snakeCanMove) {
			if(counter % delay == 0) {
					snakeX.add(0, snakeX.get(0) + xDir[direction]);
					snakeY.add(0, snakeY.get(0) + yDir[direction]);
					snakeX.remove(snakeX.size()-1);
					snakeY.remove(snakeY.size()-1);										
			}
			if(checkFruit()) {
				snakeX.add(0, snakeX.get(0) + xDir[direction]);
				snakeY.add(0, snakeY.get(0) + yDir[direction]);
			}
		}
	}
	
//	private void drawGrid(Graphics2D g) {
//		g.setColor(Color.lightGray);
//		for(int i = 0; i < ROWS; i++) {
//			g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, ROWS * UNIT_SIZE);
//			g.drawLine(0, i * UNIT_SIZE, COLS * UNIT_SIZE, i * UNIT_SIZE);
//		}
//	}
	
	private void createBoard() {
		Graphics2D g = (Graphics2D) gameBoard.getGraphics();
		g.setColor(new Color(0x404040));
		g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
	}
	
	private void checkInputs() {
		if(Keyboard.typed(KeyEvent.VK_LEFT)) {
			if(direction != 2)
				direction = 3;
			if(!hasStarted)  {
				hasStarted = !lost;
				snakeCanMove = hasStarted;
			}
		}
		if(Keyboard.typed(KeyEvent.VK_RIGHT)) {
			if(direction != 3)
				direction = 2;
			if(!hasStarted) {
				hasStarted = !lost;
				snakeCanMove = hasStarted;
			}
		}
		if(Keyboard.typed(KeyEvent.VK_UP)) {
			if(direction != 0)
				direction = 1;
			if(!hasStarted) {
				hasStarted = !lost;
				snakeCanMove = hasStarted;
			}
		}
		if(Keyboard.typed(KeyEvent.VK_DOWN)) {
			if(direction != 1)
				direction = 0;
			if(!hasStarted) {
				hasStarted = !lost;
				snakeCanMove = hasStarted;
			}
		}
	}
	private boolean checkFruit() {
		if(snakeX.get(0) == fruitX && snakeY.get(0) == fruitY) {
			scores.setCurrentScore(scores.getCurrentScore() + 1);
			//TODO 1 Play a Sound when snake eats
			spawnRandomFruit();
			if(snakeX.size() % 2 == 0 && speed >= 2)
				speed--;
			return true;
		}
		return false;
	}
	
	private void checkCollision() {
		//collision with body
		for (int i = 1; i < snakeX.size(); i++) {
			if(snakeX.get(0) == snakeX.get(i) && snakeY.get(0) == snakeY.get(i)) {
				setLost(true);
				
			}
		}
		//collision with walls
		if(snakeX.get(0) < 0 || snakeY.get(0) < 0 || snakeX.get(0) > COLS - 1 || snakeY.get(0) > ROWS - 1) 
			setLost(true);	
	}
	
	private boolean checkLost() {
		if(lost)
			return true;	
		return false;
	}
		
	//Getters
	public ScoreManager getScores() {
		return scores;
	}
	public boolean hasLost() {
		return lost;
	}
	public void setLost(boolean lost) {
		//stop snake animation
		snakeCanMove = false; 
		//ie if not set to lost but you have lost ...
		if(!this.lost && lost) {
			leaderboard.addTopScore(scores.getCurrentScore());
			leaderboard.saveTopScores();
			
		}
		this.lost = lost;
	}
	public boolean hasStarted() {
		return hasStarted;
	}

	public void setHasStarted(boolean hasStarted) {
		snakeCanMove = true;
		this.hasStarted = hasStarted;
	}

	public void setSnakeCanMove(boolean snakeCanMove) {
		this.snakeCanMove = snakeCanMove;
	}

	public ArrayList<Integer> getSnakeX() {
		return snakeX;
	}

	public ArrayList<Integer> getSnakeY() {
		return snakeY;
	}

	public int getFruitX() {
		return fruitX;
	}

	public int getFruitY() {
		return fruitY;
	}

	public int getFruitColour() {
		return fruitColour;
	}

	public int getDirection() {
		return direction;
	}	
}

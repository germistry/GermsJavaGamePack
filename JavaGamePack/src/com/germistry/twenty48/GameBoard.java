package com.germistry.twenty48;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.germistry.main.Keyboard;

import com.germistry.utils.Sound;

//2048 game logic 
public class GameBoard {

	public static final int ROWS = 4;
	public static final int COLS = 4;
	
	private final int startingTiles = 2;
	private Tile[][] board;
	private boolean lost;
	private boolean won;
		
	private BufferedImage gameBoard;
	private BufferedImage finalBoard;
	private int x;
	private int y;
	
	private static int SPACING = 10;
	public static int BOARD_WIDTH = (COLS + 1) * SPACING + COLS * Tile.WIDTH;
	public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + ROWS * Tile.HEIGHT;
	
	//for timer
	private long elapsedMS;
	private long startTime;
	private boolean hasStarted;

	//simple saving, TODO 2 look into encrypting
	private int saveCount;
	
	private ScoreManager scores;
	private Leaderboards leaderboards;
	
	public GameBoard(int x, int y) {
		this.x = x;
		this.y = y;
		board = new Tile[ROWS][COLS];
		gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		createBoardImage();
				
		leaderboards = Leaderboards.getInstance();
		leaderboards.loadTopScores();
		scores = new ScoreManager(this);
		scores.loadGame();
		scores.setBestTime(leaderboards.getFastestTime());
		scores.setCurrentTopScore(leaderboards.getHighScore());
		if(scores.newGame()) {
			start();
			scores.saveGame();
		}
		else {
			for (int i = 0; i < scores.getBoard().length; i++) {
				if(scores.getBoard()[i] == 0) continue;
				spawnTile(i / ROWS, i % COLS, scores.getBoard()[i]);
			}
			lost = checkLost();
			won = checkWon();
		}
	}
	
	public void reset() {
		board = new Tile[ROWS][COLS];
		start();
		scores.saveGame();
		lost = false;
		won = false;
		hasStarted = false;
		startTime = System.nanoTime();
		elapsedMS = 0;
		saveCount = 0;
	}
	
	public void update() {
		saveCount++;
		//could change this to save on the same thread but not causing any issues like this as very small data
		if(saveCount >= 120) {
			saveCount = 0;
			scores.saveGame();
		}
		if(!won && !lost) {
			if(hasStarted) {
				elapsedMS = (System.nanoTime() - startTime) / 1000000;
				scores.setTime(elapsedMS);
			}
			else {
				startTime = System.nanoTime();
				scores.setTime(elapsedMS);
			}
		}

		checkInputs();

		if(scores.getCurrentScore() > scores.getCurrentTopScore()) {
			scores.setCurrentTopScore(scores.getCurrentScore());
		}
		
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if(current == null) continue;
				current.update();
				resetPosition(current, row, col);
				if(current.getValue() == 2048) {
					setWon(true);
				}
			}
		}
	}
	
	public void render(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
		g2d.drawImage(gameBoard, 0, 0, null);
		
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if(current == null) continue;
				current.render(g2d);
			}
		}
		
		g.drawImage(finalBoard, x, y, null);
		g2d.dispose();
	}
	
	private void createBoardImage() {
		Graphics2D g = (Graphics2D) gameBoard.getGraphics();
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		g.setColor(Color.lightGray);
		
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				int x = SPACING + SPACING * col + Tile.WIDTH * col;
				int y = SPACING + SPACING * row + Tile.HEIGHT * row;
				g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH, Tile.ARC_HEIGHT);
			}
		}
	}
	
	private void start() {
		for(int i = 0; i < startingTiles; i++) {
			spawnRandomTiles();
		}
		
//		spawnTile(0, 0, 2);
//		spawnTile(0, 1, 2);
//		spawnTile(0, 2, 2);
//		spawnTile(0, 3, 2);
	}
	
	private void spawnTile(int row, int col, int value) {
		board[row][col] = new Tile(value, getTileX(col), getTileY(row));
	}
	
	private void spawnRandomTiles() {
		Random random = new Random();
		boolean notValid = true;
		
		while(notValid) {
			int location = random.nextInt(ROWS * COLS);
			int row = location / ROWS;
			int col = location % COLS;
			Tile current = board[row][col];
			if(current == null) {
				int value = random.nextInt(10) < 9 ? 2 : 4;
				Tile tile = new Tile(value, getTileX(col), getTileY(row));
				board[row][col] = tile;
				notValid = false;
			}
		}
	}
	
	private void checkInputs() {
		if(Keyboard.typed(KeyEvent.VK_LEFT)) {
			moveTiles(Direction.LEFT);
			if(!hasStarted) hasStarted = !lost;
		}
		if(Keyboard.typed(KeyEvent.VK_RIGHT)) {
			moveTiles(Direction.RIGHT);
			if(!hasStarted) hasStarted = !lost;
		}
		if(Keyboard.typed(KeyEvent.VK_UP)) {
			moveTiles(Direction.UP);
			if(!hasStarted) hasStarted = !lost;
		}
		if(Keyboard.typed(KeyEvent.VK_DOWN)) {
			moveTiles(Direction.DOWN);
			if(!hasStarted) hasStarted = !lost;
		}
	}
	
	private void moveTiles(Direction dir) {
		//canMove is true if ANY tile can move and false if NO tiles can be moved
		boolean canMove = false;
		int horizontalDir = 0;
		int verticalDir = 0;
		
		if(dir == Direction.LEFT) {
			horizontalDir = -1;
			for(int row = 0; row < ROWS; row++) {
				for(int col = 0; col < COLS; col++) {
					if(!canMove) {
						canMove = move(row, col, horizontalDir, verticalDir, dir);
					}
					else move(row, col, horizontalDir, verticalDir, dir);
				}
			}
		}
		else if(dir == Direction.RIGHT) {
			horizontalDir = 1;
			for(int row = 0; row < ROWS; row++) {
				for(int col = COLS - 1; col >= 0; col--) {
					if(!canMove) {
						canMove = move(row, col, horizontalDir, verticalDir, dir);
					}
					else move(row, col, horizontalDir, verticalDir, dir);
				}
			}
		}
		else if(dir == Direction.UP) {
			verticalDir = -1;
			for(int row = 0; row < ROWS; row++) {
				for(int col = 0; col < COLS; col++) {
					if(!canMove) {
						canMove = move(row, col, horizontalDir, verticalDir, dir);
					}
					else move(row, col, horizontalDir, verticalDir, dir);
				}
			}
		}
		else if(dir == Direction.DOWN) {
			verticalDir = 1;
			for(int row = ROWS - 1; row >= 0; row--) {
				for(int col = 0; col < COLS; col++) {
					if(!canMove) {
						canMove = move(row, col, horizontalDir, verticalDir, dir);
					}
					else move(row, col, horizontalDir, verticalDir, dir);
				}
			}
		}
		else {
			System.out.println(dir + " is not a valid direction");
		}
		
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if(current == null) continue;
				current.setCanCombine(true);
			}
		}
		
		if(canMove) {
			Sound.CLICK.play();
			spawnRandomTiles();
			setLost(checkLost());
		}
	}
	
	private boolean move(int row, int col, int horizontalDir, int verticalDir, Direction dir) {
		boolean canMove = false;
		
		Tile current = board[row][col];
		if(current == null) return false;
		boolean move = true;
		int newCol = col;
		int newRow = row;
		while(move) {
			newCol += horizontalDir;
			newRow += verticalDir;
			if(checkOutOfBounds(dir, newRow, newCol)) break;
			if(board[newRow][newCol] == null) {
				board[newRow][newCol] = current;
				board[newRow - verticalDir][newCol - horizontalDir] = null;
				board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
				canMove = true;
			}
			else if(board[newRow][newCol].getValue() == current.getValue() && board[newRow][newCol].canCombine()) {
				board[newRow][newCol].setCanCombine(false);
				board[newRow][newCol].setValue(board[newRow][newCol].getValue() * 2);
				canMove = true;
				board[newRow - verticalDir][newCol - horizontalDir] = null;
				board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
				board[newRow][newCol].setCombineAnimation(true);
				scores.setCurrentScore(scores.getCurrentScore() + board[newRow][newCol].getValue());
			}
			else {
				move = false;
			}
		}
		return canMove;
	}
	
	private boolean checkOutOfBounds(Direction dir, int row, int col) {
		if(dir == Direction.LEFT) {
			return col < 0;
		}
		else if(dir == Direction.RIGHT) {
			return col > COLS - 1;
		}
		else if(dir == Direction.UP) {
			return row < 0;
		}
		else if(dir == Direction.DOWN) {
			return row > ROWS - 1;
		}
		return false;
	}
	
	private boolean checkLost() {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				if(board[row][col] == null) return false;
				boolean canCombine = checkSurroundingTiles(row, col, board[row][col]);
				if(canCombine) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean checkWon() {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				if(board[row][col] == null) continue; 
				if(board[row][col].getValue() >= 2048) return true;
			}
		}
		return false;
	}
	
	private boolean checkSurroundingTiles(int row, int col, Tile current) {
		if (row > 0) {
			Tile check = board[row - 1][col];
			if(check == null) return true;
			if(current.getValue() == check.getValue()) return true;
		}
		if (row < ROWS - 1) {
			Tile check = board[row + 1][col];
			if(check == null) return true;
			if(current.getValue() == check.getValue()) return true;
		}
		if (col > 0) {
			Tile check = board[row][col - 1];
			if(check == null) return true;
			if(current.getValue() == check.getValue()) return true;
		}
		if (col < COLS - 1) {
			Tile check = board[row][col + 1];
			if(check == null) return true;
			if(current.getValue() == check.getValue()) return true;
		}
		return false;
	}
	
	private void resetPosition(Tile current, int row, int col) {
		if(current == null) return;
		int x = getTileX(col);
		int y = getTileY(row);
		int distX = current.getX() - x;
		int distY = current.getY() - y;
		if(Math.abs(distX) < Tile.SLIDE_SPEED) {
			current.setX(current.getX() - distX);
		}
		if(Math.abs(distY) < Tile.SLIDE_SPEED) {
			current.setY(current.getY() - distY);
		}
		//left
		if(distX < 0) {
			current.setX(current.getX() + Tile.SLIDE_SPEED);
		}
		//up
		if(distY < 0) {
			current.setY(current.getY() + Tile.SLIDE_SPEED);
		}
		//right
		if(distX > 0) {
			current.setX(current.getX() - Tile.SLIDE_SPEED);
		}
		//down
		if(distY > 0) {
			current.setY(current.getY() - Tile.SLIDE_SPEED);
		}
	}
	
	public int getHighestTileValue() {
		int value = 2;
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				if(board[row][col] == null) continue;
				if(board[row][col].getValue() > value) 
					value = board[row][col].getValue();
			}
		}
		return value;
	}
		
	//Getters
	public boolean hasLost() {
		return lost;
	}
	public void setLost(boolean lost) {
		//ie if not set to lost but you have lost ...
		if(!this.lost && lost) {
			leaderboards.addTopTile(getHighestTileValue());
			leaderboards.addTopScore(scores.getCurrentScore());
			leaderboards.saveTopScores();
		}
		this.lost = lost;
	}
	
	public boolean hasWon() {
		return won;
	}
	public void setWon(boolean won) {
		//ie if not set to won but you have won ...
		if(!this.won && won) {
			leaderboards.addTopTile(getHighestTileValue());
			leaderboards.addTopScore(scores.getCurrentScore());
			leaderboards.addTopTime(scores.getTime());
			leaderboards.saveTopScores();
		}
		this.won = won;
	}
	
	public ScoreManager getScores() {
		return scores;
	}
	public int getTileX(int col) {
		return SPACING + Tile.WIDTH * col + SPACING * col;
	}
	public int getTileY(int row) {
		return SPACING + Tile.HEIGHT * row + SPACING * row;
	}
	public Tile[][] getBoard() {
		return board;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean hasStarted() {
		return hasStarted;
	}

	public void setHasStarted(boolean hasStarted) {
		this.hasStarted = hasStarted;
	}
}

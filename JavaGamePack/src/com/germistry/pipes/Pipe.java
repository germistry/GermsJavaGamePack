package com.germistry.pipes;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Pipe {

	private int row, col;
	private PipeType pipeType;
	private boolean startFlow = false;
	private boolean pipeFull = false;
	private int id;

	//0 no flow, 1 flowing from bottom to top, 2 flowing top to bottom, 3 flowing right to left, 4 flowing left to right
	//5 flowing bottom to left, 6 flowing bottom to right, 7 flowing top to left, 8 flowing top to right, 9 flowing left to top,
	//10 flowing left to bottom, 11 flowing right to top, 12 flowing right to bottom
	private int flowDirection; 
	private BufferedImage firstImage;
	
	public Pipe(boolean startFlow, int id) {
		this.startFlow = startFlow;
		this.id = id;
	}
	
	public void render(Graphics2D g) {
		g.drawImage(firstImage, col * GameBoard.UNIT_SIZE, row * GameBoard.UNIT_SIZE, null);
	}
	
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public boolean startFlow() {
		return startFlow;
	}

	public void setStartFlow(boolean startFlow) {
		this.startFlow = startFlow;
	}

	public PipeType getPipeType() {
		return pipeType;
	}

	public void setPipeType(PipeType pipeType) {
		this.pipeType = pipeType;
		this.firstImage = pipeType.getImage();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFlowDirection() {
		return flowDirection;
	}

	public void setFlowDirection(int flowDirection) {
		this.flowDirection = flowDirection;
	}

	public boolean isPipeFull() {
		return pipeFull;
	}

	public void setPipeFull(boolean pipeFull) {
		this.pipeFull = pipeFull;
	}

	
	
}

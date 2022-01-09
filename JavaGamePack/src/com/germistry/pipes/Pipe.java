package com.germistry.pipes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Pipe {

	private int row, col;
	private PipeType pipeType;
	private boolean startFlow = false;
	private boolean pipeFull = false;
	private int id;
	private Color waterColour = new Color(0x42FFEC);
	private int counter;
	private int cycles;
	private ArrayList<Integer> waterFlowX = new ArrayList<Integer>(); 
	private ArrayList<Integer> waterFlowY = new ArrayList<Integer>();
	//private int direction;  // 0 down, 1 up, 2 right, 3 left
	private int[] xDir = {0, 0, 1, -1};  //right - left
	private int[] yDir = {1, -1, 0, 0};  //down - up
	private int xSize, ySize;
	private int xOffset, yOffset;
	//0 no flow, 1 flowing from bottom to top, 2 flowing top to bottom, 3 flowing right to left, 4 flowing left to right
	//5 flowing bottom to left, 6 flowing bottom to right, 7 flowing top to left, 8 flowing top to right, 9 flowing left to top,
	//10 flowing left to bottom, 11 flowing right to top, 12 flowing right to bottom
	private int flowDirection; 
	private BufferedImage firstImage;
	
	public Pipe(boolean startFlow, int id) {
		this.startFlow = startFlow;
		this.id = id;
	}
	
	public void update() {
		if(startFlow && !pipeFull) {
			cycles++;
			if(waterFlowX.isEmpty()) {
				if(pipeType == PipeType.TAP_NORTH || pipeType == PipeType.TAP_SOUTH 
						|| pipeType == PipeType.TAP_WEST || pipeType == PipeType.TAP_EAST) {
					setTapWater();
				}	
				else {
					if(flowDirection == 3 || flowDirection == 11 || flowDirection == 12) {
						setLeftFlow();
					}
					else if(flowDirection == 4 || flowDirection == 9 || flowDirection == 10) {
						setRightFlow();
					}
					else if(flowDirection == 1 || flowDirection == 5 || flowDirection == 6) {
						setUpFlow();
					}
					else if(flowDirection == 2 || flowDirection == 7 || flowDirection == 8) {
						setDownFlow();
					}
				}
			}
			if(cycles % 16 == 0) {
				counter++;
				switch(flowDirection) {
				case 1: //straight flowing up
					waterFlowX.add(0, waterFlowX.get(0) + xDir[1]);
					waterFlowY.add(0, waterFlowY.get(0) + yDir[1]);
					break;
				case 2: //straight flowing down
					waterFlowX.add(0, waterFlowX.get(0) + xDir[0]);
					waterFlowY.add(0, waterFlowY.get(0) + yDir[0]);
					break;
				case 3: //straight flowing left
					waterFlowX.add(0, waterFlowX.get(0) + xDir[3]);
					waterFlowY.add(0, waterFlowY.get(0) + yDir[3]);
					break;
				case 4: //straight flowing right 
					waterFlowX.add(0, waterFlowX.get(0) + xDir[2]);
					waterFlowY.add(0, waterFlowY.get(0) + yDir[2]);
					break;
				case 5: //elbow flow up then to left
					if(counter <= 45) {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[1]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[1]);
					}
					else {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[3]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[3]);
					}
					break;
				case 6: //elbow flow up then to right
					if(counter <= 45) {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[1]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[1]);
					}
					else {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[2]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[2]);
					}
					break;
				case 7: //elbow flow down then to left
					if(counter <= 45) {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[0]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[0]);
					}
					else {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[3]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[3]);
					}
					break;
				case 8: //elbow flow down then to right 
					if(counter <= 45) {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[0]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[0]);
					}
					else {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[2]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[2]);
					}
					break;
				case 9: //elbow flow right then up 
					if(counter <= 45) {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[2]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[2]);
					}
					else {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[1]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[1]);
					}
					break;
				case 10: //elbow flow right then down 
					if(counter <= 45) {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[2]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[2]);
					}
					else {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[0]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[0]);
					}
					break;
				case 11: //elbow flow left then up 
					if(counter <= 45) {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[3]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[3]);
					}
					else {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[1]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[1]);
					}
					break;
				case 12: //elbow flow left then down
					if(counter <= 45) {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[3]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[3]);
					}
					else {
						waterFlowX.add(0, waterFlowX.get(0) + xDir[0]);
						waterFlowY.add(0, waterFlowY.get(0) + yDir[0]);
					}
					break;
				}
			}
			if(pipeType == PipeType.TAP_NORTH || pipeType == PipeType.TAP_SOUTH 
						|| pipeType == PipeType.TAP_WEST || pipeType == PipeType.TAP_EAST) {
				if (counter > 11) {
					startFlow = false;
					pipeFull = true;
				}
			}
			else {
				if(counter > 62) {
					startFlow = false;
					pipeFull = true;
				}
			}
		}
	}
	
	public void render(Graphics2D g) {
		//animation
		if(startFlow) {
			if(waterFlowX.isEmpty() || waterFlowY.isEmpty()) {
				String excMsg = "x or y coordinates do not exist!";
				throw new IllegalStateException(excMsg);
		}
		else {		
			for (int i = 0; i < waterFlowX.size(); i++) {
				g.setColor(waterColour);
				g.fillRect(waterFlowX.get(i), waterFlowY.get(i), xSize, ySize);
				}
			}
		}
		g.drawImage(firstImage, col * GameBoard.UNIT_SIZE, row * GameBoard.UNIT_SIZE, null);
	}
	
	private void setTapWater() {
		xOffset = 12; 
		yOffset = 12;
		xSize = 40;
		ySize= 40;
		waterFlowX.add(col * GameBoard.UNIT_SIZE + xOffset);
		waterFlowY.add(row * GameBoard.UNIT_SIZE + yOffset);
	}
	private void setRightFlow() {
		xOffset = 0; 
		yOffset = 12;
		xSize = 1;
		ySize= 40;
		waterFlowX.add(col * GameBoard.UNIT_SIZE + xOffset);
		waterFlowY.add(row * GameBoard.UNIT_SIZE + yOffset);
	}
	private void setLeftFlow() {
		xOffset = 63; 
		yOffset = 12;
		xSize = 1;
		ySize= 40;
		waterFlowX.add(col * GameBoard.UNIT_SIZE + xOffset);
		waterFlowY.add(row * GameBoard.UNIT_SIZE + yOffset);
	}
	private void setUpFlow() {
		xOffset = 12; 
		yOffset = 63;
		xSize = 40;
		ySize= 1;
		waterFlowX.add(col * GameBoard.UNIT_SIZE + xOffset);
		waterFlowY.add(row * GameBoard.UNIT_SIZE + yOffset);
	}
	private void setDownFlow() {
		xOffset = 12; 
		yOffset = 0;
		xSize = 40;
		ySize= 1;
		waterFlowX.add(col * GameBoard.UNIT_SIZE + xOffset);
		waterFlowY.add(row * GameBoard.UNIT_SIZE + yOffset);
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

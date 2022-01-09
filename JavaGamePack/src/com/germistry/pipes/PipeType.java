package com.germistry.pipes;

import java.awt.image.BufferedImage;

public enum PipeType {

	VERTICAL(GameBoard.gameAssets[8], true, false, true, false),
	HORIZONTAL(GameBoard.gameAssets[9], false, true, false, true),
	CROSS(GameBoard.gameAssets[10], true, true, true, true),
	TOP_LEFT_CORNER(GameBoard.gameAssets[14], true, false, false, true),
	TOP_RIGHT_CORNER(GameBoard.gameAssets[13], true, true, false, false),
	BOTTOM_LEFT_CORNER(GameBoard.gameAssets[12], false, false, true, true),
	BOTTOM_RIGHT_CORNER(GameBoard.gameAssets[11], false, true, true, false),
	TAP_NORTH(GameBoard.gameAssets[0], true, false, false, false),
	TAP_SOUTH(GameBoard.gameAssets[3], false, false, true, false),
	TAP_EAST(GameBoard.gameAssets[1], false, false, false, true),
	TAP_WEST(GameBoard.gameAssets[2], false, true, false, false),
	DRAIN_NORTH(GameBoard.gameAssets[4], true, false, false, false),
	DRAIN_SOUTH(GameBoard.gameAssets[7], false, false, true, false),
	DRAIN_EAST(GameBoard.gameAssets[5], false, false, false, true),
	DRAIN_WEST(GameBoard.gameAssets[6], false, true, false, false);
	
	BufferedImage image;
	boolean pipehasTop; // Top outlet flag
	boolean pipehasRight; // Right outlet flag
	boolean pipehasBottom; // Bottom outlet flag
	boolean pipehasLeft; // Left outlet flag
	int outletCount; // No. of outlets (I pipe has 2, cross pipe has 4, etc)
	//boolean connectedCount; // No. of outlets connected to adjacent grid's outlet
	
	PipeType(BufferedImage image, boolean pipeHasTop, boolean pipeHasRight, boolean pipeHasBottom, boolean pipeHasLeft){
		this.image = image;
		this.pipehasTop = pipeHasTop;
		this.pipehasRight = pipeHasRight;
		this.pipehasBottom = pipeHasBottom;
		this.pipehasLeft = pipeHasLeft;
	}
	
	public boolean pipehasTop() {
		return pipehasTop;
	}

	public boolean pipehasRight() {
		return pipehasRight;
	}

	public boolean pipehasBottom() {
		return pipehasBottom;
	}

	public boolean pipehasLeft() {
		return pipehasLeft;
	}
	public BufferedImage getImage() {
		return image;
	}
 }

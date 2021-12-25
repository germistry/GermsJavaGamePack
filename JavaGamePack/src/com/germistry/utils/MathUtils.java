package com.germistry.utils;

public class MathUtils {

	private MathUtils() {}
	
	public static int[] convertToSingleIntArray(int[][] twoDArray)
	{
		int[] oneDArray = new int[twoDArray.length * twoDArray.length];
		for(int i = 0; i < twoDArray.length; i++)
		{
			for(int j = 0; j < twoDArray.length; j++)
			{
			
				oneDArray[(i * twoDArray.length) + j] = twoDArray[i][j];
			}
		}
		return oneDArray;
	}
	
}

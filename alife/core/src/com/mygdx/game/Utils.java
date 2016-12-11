package com.mygdx.game;

public class Utils {
	
	public static void printArray(float[] array) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < array.length; i++) {
			sb.append(array[i]).append(' ');
		}
		System.out.println(sb);
	}
	
}

package com.mygdx.game;

public class IntProperty extends Property {
	public int val;
	
	public IntProperty(int val) {
		this.val = val;
	}

	public void set(float value) {
		this.val = (int) value;
	}
	
	public void set(int value) {
		this.val = value;
	}

	public float get() {
		// TODO Auto-generated method stub
		return val;
	}
}

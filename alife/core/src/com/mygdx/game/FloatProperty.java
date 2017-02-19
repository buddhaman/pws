package com.mygdx.game;

public class FloatProperty extends Property {
	public float val;
	
	public FloatProperty() {
		
	}
	
	public FloatProperty(float val) {
		this.val = val;
	}

	@Override
	public void set(float value) {
		this.val = value;
	}

	@Override
	public float get() {
		return val;
	}

	@Override
	public void set(int value) {
		this.val = value;
	}
}

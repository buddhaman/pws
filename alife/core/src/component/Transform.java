package component;

import com.badlogic.ashley.core.Component;

public class Transform implements Component {
	public float x;
	public float y;
	public float rotation;
	
	public Transform() {
		
	}
	
	public Transform(float x, float y) {
		this.x = x;
		this.y = y;
	}
}

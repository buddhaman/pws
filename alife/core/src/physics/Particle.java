package physics;

import com.badlogic.gdx.math.Vector2;

public class Particle {
	public Vector2 pos;
	public Vector2 vel;
	public float invMass = 1;
	
	public boolean fixed;
	
	public Particle(float x, float y) {
		pos = new Vector2(x, y);
		vel = new Vector2();
	}
	
	public Particle() {
		
	}
	
	/**
	 * @param pos doesn't make a deep copy of pos
	 */
	public Particle(Vector2 pos) {
		this.pos = pos;
		vel = new Vector2();
	}
	
	public void update() {
		if(!fixed)
			pos.add(vel);
	}
	
	public void move(Vector2 amount) {
		pos.add(amount);
	}
	
	public void addImpulse(float x, float y) {
		if(!fixed)
			vel.add(x, y);
	}

	public void addImpulse(Vector2 vel) {
		if(!fixed)
			this.vel.add(vel);
	}
}

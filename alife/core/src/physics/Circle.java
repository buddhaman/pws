package physics;

import com.badlogic.ashley.core.Entity;

public class Circle {
	
	public Particle particle;
	public float r;
	public boolean hasPhysics;
	
	public Group group;
	
	public Circle(float x, float y, float r, boolean hasPhysics) {
		particle = new Particle(x, y);
		this.r = r;
		this.hasPhysics = hasPhysics;
	}
	
	public Circle() {
		particle = new Particle();
	}
	
	public float getX() {
		return particle.pos.x;
	}
	
	public float getY() {
		return particle.pos.y;
	}

	public float getMinX() {
		return particle.pos.x-r;
	}
	
	public float getMinY() {
		return particle.pos.y-r;
	}
	
	public float getMaxX() {
		return particle.pos.x+r;
	}
	
	public float getMaxY() {
		return particle.pos.y+r;
	}
	
	public Entity getEntity() {
		return group.entity;
	}

	public boolean sameGroup(Circle c) {
		return group==c.group;
	}

	public void setPosition(float x, float y) {
		particle.pos.set(x, y);
	}

	public boolean containsPoint(float x, float y) {
		float dx = x-particle.pos.x;
		float dy = y-particle.pos.y;
		return (dx*dx+dy*dy < r*r);
	}
}

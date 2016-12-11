package physics;

import com.badlogic.gdx.math.Vector2;

public class Constraint {
	public Particle p;
	public Particle q;
	
	public float length;
	public boolean removed;
	
	public float k = .1f;
	public float b = .4f;
	
	private static final Vector2 tmp0 = new Vector2();
	private static final Vector2 tmp1 = new Vector2();
	private static final Vector2 tmp2 = new Vector2();
	private static final Vector2 tmp3 = new Vector2();
	private static final Vector2 tmp4 = new Vector2();
	
	public Constraint(Particle p, Particle q) {
		this.p = p;
		this.q = q;
		length = p.pos.dst(q.pos);
	}
	
	public boolean contains(Particle particle) {
		if(p==particle) return true;
		if(q==particle) return true;
		return false;
	}
	
	public void solve() {
		float l = p.pos.dst(q.pos);
		
		Vector2 pVel = tmp3.set(0,0);
		Vector2 qVel = tmp4.set(0,0);
		
		float dx = q.pos.x-p.pos.x;
		float dy = q.pos.y-p.pos.y;
		Vector2 axis = tmp0.set(dx, dy);
		axis.scl(1f/l);
		
		float dif = (l-length)*k;
		pVel.add(axis.x*dif, axis.y*dif);
		qVel.add(-axis.x*dif, -axis.y*dif);
		
		Vector2 relVel = tmp1.set(q.vel.x-p.vel.x, q.vel.y-p.vel.y);
		float dp = relVel.dot(axis);
		Vector2 damper = tmp2.set(axis.x*dp, axis.y*dp);
		damper.scl(b*.5f);
		pVel.add(damper);
		qVel.sub(damper);
		p.addImpulse(pVel);
		q.addImpulse(qVel);
	}
	
	public void remove() {
		removed = true;
	}

	public float getCurrentLength() {
		return p.pos.dst(q.pos);
	}
}

package physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class CollisionInfo implements Poolable {
	
	public Vector2 normal = new Vector2();
	public float depth;
	
	public Circle c1;	//the circle contained in the group this instance is send to
	public Circle c2;
	
	public CollisionInfo() {
	
	}

	@Override
	public void reset() {
		normal.set(0,0);
		c1 = null;
		c2 = null;
		depth = 0;
	}
}

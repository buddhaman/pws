package physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TileCollisionInfo implements Poolable {
	public Vector2 normal = new Vector2();
	public float depth;
	
	public Tile tile;
	public Circle circle;
	
	public TileCollisionInfo() {
		
	}

	@Override
	public void reset() {
		normal.set(0,0);
		depth = 0;
		tile = null;
		circle = null;
	}
}

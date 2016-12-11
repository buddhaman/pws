package component;

import physics.CollisionInfo;
import physics.Group;
import physics.TileCollisionInfo;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;


public class Physics implements Component {
	public Group group;
	public Array<CollisionInfo> collisions = new Array<CollisionInfo>();
	public Array<TileCollisionInfo> tileCollisions = new Array<TileCollisionInfo>();
}

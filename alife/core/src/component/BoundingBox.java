package component;

import com.badlogic.ashley.core.Component;

import physics.AABB;

public class BoundingBox implements Component {
	public AABB aabb = new AABB();
}

package physics;

import system.Mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import component.Physics;

public class Group {
	
	private static final ComponentMapper<Physics> physM = Mappers.physicsMapper;
	
	public Array<Constraint> constraintList = new Array<Constraint>();
	public Array<Circle> circleList = new Array<Circle>();
	public Array<Connection> connectionList = new Array<Connection>();
	public Array<Particle> particleList = new Array<Particle>();
	
	public World world;
	
	public Entity entity;
	public float rotation;
	public float groundFriction;
	
	public Material material;
	
	public Group() {
		material = Material.materials[Material.WHITE];
	}
	
	public void update() {
		for(int i = constraintList.size-1; i>=0; i--) {
			Constraint c = constraintList.get(i);
			if(c.removed) constraintList.removeIndex(i);
		}
		for(int i = 0; i < particleList.size; i++) {
			Particle p = particleList.get(i);
			p.update();
			p.vel.scl(groundFriction);
		}
		if(rotation > MathUtils.PI)
			rotation-=MathUtils.PI2;
		if(rotation < -MathUtils.PI)
			rotation+=MathUtils.PI2;
		updateConnections(connectionList, rotation);
	}
	
	public void updateConnections(Array<Connection> connections, float angle) {
		for(int i = 0; i < connections.size; i++) {
			Connection connection = connections.get(i);
			float ang = connection.angle+angle;
			float sin = MathUtils.sin(ang);
			float cos = MathUtils.cos(ang);
			connection.b.pos.set(connection.a.pos.x+connection.length*cos, 
					connection.a.pos.y+connection.length*sin);
			updateConnections(connection.connections, ang);
		}
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public void addCircle(Circle circle) {
		circle.group = this;
		circleList.add(circle);
		particleList.add(circle.particle);
	}
	
	public void addConnection(Connection connection) {
		connectionList.add(connection);
	}
	
	public void addConstraint(Constraint constraint) {
		constraintList.add(constraint);
	}
	
	public void removeConstraint(Constraint constraint) {
		constraintList.removeValue(constraint, true);
	}
	
	public void addCollisionInfo(CollisionInfo info) {
		physM.get(entity).collisions.add(info);
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}

	public float getX() {
		return circleList.get(0).particle.pos.x;
	}
	
	public float getY() {
		return circleList.get(0).particle.pos.y;
	}

	public void addTileCollisionInfo(TileCollisionInfo info) {
		physM.get(entity).tileCollisions.add(info);
	}

	public void setPosition(float x, float y) {
		circleList.get(0).particle.pos.set(x, y);
	}

	public void setVelocity(float x, float y) {
		circleList.get(0).particle.vel.set(x, y);
	}
}

package system;

import physics.CollisionInfo;
import physics.TileCollisionInfo;
import physics.World;
import simulation.Simulation;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import component.Physics;

public class PhysicsSystem extends EntitySystem {

	private Simulation simulation;
	
	private ImmutableArray<Entity> physicsEntities;
	private Family family = Family.all(Physics.class).get();
	
	private ComponentMapper<Physics> physM = Mappers.physicsMapper;
	
	public PhysicsSystem(Simulation simulation) {
		this.simulation = simulation;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		physicsEntities = engine.getEntitiesFor(family);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		World world = simulation.world;
		
		for(int i = 0; i < physicsEntities.size(); i++) {
			Entity entity = physicsEntities.get(i);
			Array<CollisionInfo> collisions = physM.get(entity).collisions;
			world.FreeAll(collisions);
			collisions.clear();
			
			Array<TileCollisionInfo> tileCollisions = physM.get(entity).tileCollisions;
			world.FreeAllTileCollisions(tileCollisions);
			tileCollisions.clear();
		}
		world.update();
	}
	
}

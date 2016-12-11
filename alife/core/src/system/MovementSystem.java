package system;

import simulation.Simulation;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;

import component.Transform;
import component.Velocity;

public class MovementSystem extends EntitySystem {

	private Family family = Family.all(Velocity.class, Transform.class).get();
	
	private ComponentMapper<Transform> transM = Mappers.transformMapper;
	private ComponentMapper<Velocity> velM = Mappers.velocityMapper;
	
	ImmutableArray<Entity> movingEntityArray;
	public Simulation simulation;
	
	public MovementSystem(Simulation simulation) {
		this.simulation = simulation;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		movingEntityArray = engine.getEntitiesFor(family);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		for(int i = 0; i < movingEntityArray.size(); i++) {
			Entity e = movingEntityArray.get(i);
			Velocity v = velM.get(e);
			Transform t = transM.get(e);
			t.x+=v.x;
			t.y+=v.y;
			t.rotation+=v.angular;
			if(t.rotation > MathUtils.PI)
				t.rotation = -MathUtils.PI;
			if(t.rotation < -MathUtils.PI) 
				t.rotation = MathUtils.PI;
		}
	}
	
	
}

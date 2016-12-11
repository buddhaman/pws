package system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import component.Corpse;
import component.Physics;
import physics.Tile;
import simulation.Simulation;

public class CorpseSystem extends EntitySystem {
	
	private Family family = Family.all(Corpse.class).get();
	private ImmutableArray<Entity> corpseArray;
	private ComponentMapper<Corpse> corpseM = Mappers.corpseMapper;
	private ComponentMapper<Physics> physM = Mappers.physicsMapper;
	
	private Simulation simulation;
	
	public CorpseSystem(Simulation simulation) {
		corpseArray = simulation.engine.getEntitiesFor(family);
		this.simulation = simulation;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		for(int i = corpseArray.size()-1; i >= 0; i--) {
			Entity entity = corpseArray.get(i);
			Corpse corpse =  corpseM.get(entity);
			Vector2 pos = physM.get(entity).group.circleList.get(0).particle.pos;
			
			Tile t = simulation.world.getTileAt(pos.x, pos.y);
			corpse.energy-=Corpse.ENERGY_LEAK;
			t.energy+=Corpse.ENERGY_LEAK;
			
			if(corpse.energy <= 0) {
				simulation.removeEntity(entity);
			}
		}
	}
	
}

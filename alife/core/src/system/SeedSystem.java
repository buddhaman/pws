package system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Settings;

import component.Physics;
import component.Plant;
import component.Seed;
import entity.Factory;
import physics.Tile;
import simulation.Simulation;

public class SeedSystem extends EntitySystem {

	private Family family = Family.all(Seed.class).get();

	private ComponentMapper<Seed> seedM = Mappers.seedMapper;
	private ComponentMapper<Physics> physM = Mappers.physicsMapper;

	private Simulation simulation;
	private ImmutableArray<Entity> seeds;

	public SeedSystem(Simulation simulation) {
		this.simulation = simulation;
		seeds = simulation.engine.getEntitiesFor(family);
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		for (int i = seeds.size() - 1; i >= 0; i--) {
			Entity seed = seeds.get(i);
			Seed seedComponent = seedM.get(seed);
			seedComponent.ticksAlive++;
			

			if(seedComponent.eaten) {
				simulation.removeEntity(seed);
				return;
			}
			
			if(seedComponent.ticksAlive > Seed.MAX_TICKS_ALIVE) {
				Vector2 pos = physM.get(seed).group.circleList.get(0).particle.pos;
				Tile t = simulation.world.getTileAt(pos.x, pos.y);
				t.energy+=seedComponent.energy;
				simulation.removeEntity(seed);
				return;
			}
			
			if (seedComponent.ticksAlive % Seed.TICKS_PER_ENERGY_UNIT == 0) {
				Vector2 pos = physM.get(seed).group.circleList.get(0).particle.pos;
				Tile t = simulation.world.getTileAt(pos.x, pos.y);
				if (t.energy > 0) {
					t.energy--;
					seedComponent.energy++;
				}
				if (seedComponent.energy >= Seed.ENERGY_THRESHOLD) {
					simulation.addEntity(Factory.createPlant(seedComponent.plantGenome, pos.x, pos.y));
					simulation.removeEntity(seed);
					t.energy+=(seedComponent.energy-Plant.MIN_ENERGY);
				}
			}
		}

	}
}

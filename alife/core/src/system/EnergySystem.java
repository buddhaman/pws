package system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import component.Bot;
import component.Corpse;
import component.Plant;
import component.Seed;
import physics.World;
import simulation.Simulation;

public class EnergySystem extends EntitySystem {
	Simulation simulation;
	
	/**
	 * count all energy in the entire simulation. Should be constant. This is a test system.
	 */
	
	//list of all components with energy
	private ComponentMapper<Bot> botM = Mappers.botMapper;
	private ComponentMapper<Plant> plantM = Mappers.plantMapper;
	private ComponentMapper<Seed> seedM = Mappers.seedMapper;
	private ComponentMapper<Corpse> corpseM = Mappers.corpseMapper;
	
	//list of all entities with energy
	private ImmutableArray<Entity> botList;
	private ImmutableArray<Entity> plantList;
	private ImmutableArray<Entity> seedList;
	private ImmutableArray<Entity> corpseList;
	
	public EnergySystem(Simulation simulation) {
		this.simulation = simulation;
		botList = simulation.engine.getEntitiesFor(Family.all(Bot.class).get());
		plantList = simulation.engine.getEntitiesFor(Family.all(Plant.class).get());
		seedList = simulation.engine.getEntitiesFor(Family.all(Seed.class).get());
		corpseList = simulation.engine.getEntitiesFor(Family.all(Corpse.class).get());
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		if(!Gdx.input.isKeyJustPressed(Keys.E))
			return;
		//int is big enough I hope
		int totalEnergy = 0;
		
		World world = simulation.world;
		int groundEnergy = 0;
		for(int i = 0; i < world.tiles.length; i++) {
			groundEnergy+=world.tiles[i].energy;
		}
		
		int livingBotEnergy = 0;
		for(Entity e : botList) {
			Bot bot = botM.get(e);
			livingBotEnergy+=bot.body.energy;
		}
		
		int plantEnergy = 0;
		for(Entity e : plantList) {
			Plant plant = plantM.get(e);
			plantEnergy+=plant.energy;
		}
		
		int deadBotEnergy = 0;
		for(Entity e : corpseList) {
			Corpse corpse = corpseM.get(e);
			deadBotEnergy+=corpse.energy;
		}
		
		int seedEnergy=0;
		for(Entity e : seedList) {
			Seed seed = seedM.get(e);
			seedEnergy+=seed.energy;
		}
		
		totalEnergy = groundEnergy+livingBotEnergy+plantEnergy+deadBotEnergy+seedEnergy;
		System.out.println("Total energy : " + totalEnergy );
	}
}

package simulation;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Settings;

import component.Bot;
import component.CameraComponent;
import component.Physics;
import component.Plant;
import creature.CreatureBody;
import entity.Factory;
import genome.GenePool;
import genome.PlantNode;
import physics.Circle;
import physics.Tile;
import physics.World;
import renderer.Resources;
import system.BotSystem;
import system.CameraSystem;
import system.CorpseSystem;
import system.EnergySystem;
import system.EvolutionSystem;
import system.ExperimentSystem;
import system.KeyInputSystem;
import system.Mappers;
import system.MovementSystem;
import system.PhysicsSystem;
import system.PlantSystem;
import system.RenderSystem;
import system.SeedSystem;

public class Simulation implements EntityListener {
	public Engine engine;
	
	public float timeStep = 1f/60f;
	public int screenWidth;
	public int screenHeight;
	
	public int iterations = 1;
	
	public float worldWidth;
	public float worldHeight;
	
	public World world;
	public GenePool genePool;
	private ComponentMapper<Physics> physM = Mappers.physicsMapper;
	private ComponentMapper<Bot> botM = Mappers.botMapper;
	private Family botFamily = Family.all(Bot.class).get();

	public int minBots;
	public int maxBots;
	
	public int ticks;
	public int seconds;
	public int minutes;
	public int hours;
	public int days;
	
	public int stopAtDays = Integer.MAX_VALUE;
	
	//camera handling
	public Entity camera;

	public boolean onlineEvolution;

	private boolean running;
	public boolean hasSimulationTime = false;
	public boolean experimentRunning = false;
	
	public Simulation(Settings settings, boolean experiment) {
		updateSettings(settings);
		this.experimentRunning = experiment;
		//setup engine
		engine = new Engine();

		worldWidth = settings.tWidth.val*settings.tileSize.val;
		worldHeight = settings.tHeight.val*settings.tileSize.val;
		
		world = new World(settings.tWidth.val, settings.tHeight.val, settings.tileSize.val);
		engine.addEntityListener(this);
		
		engine.addSystem(new KeyInputSystem(this));
		engine.addSystem(new ExperimentSystem(this));
		engine.addSystem(new BotSystem(this));
		engine.addSystem(new EvolutionSystem(this));
		engine.addSystem(new PlantSystem(this));
		engine.addSystem(new SeedSystem(this));
		engine.addSystem(new CorpseSystem(this));
		engine.addSystem(new MovementSystem(this));
		engine.addSystem(new PhysicsSystem(this));
		engine.addSystem(new CameraSystem(this));
		engine.addSystem(new EnergySystem(this));
		
		
		camera = Factory.createCamera(worldWidth/2, worldHeight/2, .5f);
		addEntity(camera);
		
		for(int i = 0; i < 20; i++) {
			addPlant();
		}
		
		genePool = new GenePool();
		for(int i = 0; i < settings.botPopulation.val; i++) {
			addRandomBot();
		}
		running = true;
	}
	
	public void addPlant() {
		PlantNode pg = new PlantNode(2,3,null);
		Vector2 pos = getFreePosition();
		Tile t = (world.getTileAt(pos.x, pos.y));
		if(t.energy>=Plant.MIN_ENERGY) {
			t.energy-=Plant.MIN_ENERGY;
			addEntity(Factory.createPlant(pg, pos.x, pos.y));
		}
	}
	
	public void updateSettings(Settings settings) {		
		minBots = settings.minBots.val;
		maxBots = settings.botPopulation.val;
		
		onlineEvolution = settings.onlineEvolution;
		
		//set parameters to the right settings
		GenePool.bodyMaxNodes = settings.bodyMaxNodes.val;
		GenePool.bodyRecursionLimit = settings.bodyRecursionLimit.val;
		GenePool.nodeMaxConnections = settings.nodeMaxConnections.val;
		GenePool.nodeMutationRate = settings.nodeMutationRate.val;
		
		GenePool.nodeMutationProb = settings.nodeMutationProb.val;
		GenePool.brainMutationProb = settings.brainMutationProb.val;
		GenePool.brainMutationRate = settings.brainMutationRate.val;
		GenePool.brainActiveMutationProb = settings.brainActiveMutationProb.val;
		
		GenePool.eatPlantFactor = settings.eatPlantFactor.val;
		GenePool.eatCorpseFactor = settings.eatCorpseFactor.val;
	}
	
	public Entity createBot(float x, float y) {
		Entity bot = Factory.createBot(genePool.getRandomGenome(this), x, y);
		return bot;
	}
	
	public void setResources(Resources res) {
		engine.addSystem(new RenderSystem(this, res));
	}
	
	/**
	 * @param delta actual real world delta. Entities are updated with a fixed timestep
	 */
	public void update(float delta) {
		float d = delta/(float)iterations;
		setRendering(false);
		for(int i = 0; i < iterations-1; i++) {
			timeStep(d);
		}
		setRendering(true);
		timeStep(d);
	}
	
	public void timeStep(float d) {
		engine.update(d);
		if(!running)
			return;
		ticks++;
		if(ticks >= 60) {
			ticks-=60;
			seconds++;
			if(seconds >= 60) {
				seconds-=60;
				minutes++;
				if(minutes >= 60) {
					minutes-=60;
					hours++;
					hourPassed();
					if(hours >= 24) {
						hours-=24;
						days++;
						if(hasSimulationTime && days==stopAtDays) 
							this.setRunning(false);
					}
				}
			}
		}
	}
	
	public void hourPassed() {
		
		System.out.println(getTime() + " ");
	}
	
	public String getTime() {
		return String.format("D:%d H:%d M:%d S:%d T:%d", days, hours, minutes, seconds, ticks);
	}
	
	public void addEntity(Entity entity) {
		engine.addEntity(entity);
	}
	
	public void removeEntity(Entity entity) {
		engine.removeEntity(entity);
	}
	
	public void resize(int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
		ImmutableArray<EntitySystem> systems = engine.getSystems();
		int sysNum = systems.size();
		for(int i = 0; i < sysNum; i++) {
			EntitySystem system = systems.get(i);
			if(system.getClass()==RenderSystem.class 
					|| system.getClass()==CameraSystem.class 
					|| system.getClass()==KeyInputSystem.class) 
				continue;
			system.setProcessing(running);
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void setRendering(boolean rendering) {
		engine.getSystem(RenderSystem.class).setProcessing(rendering);
	}
	
	public float getRandX() {
		return MathUtils.random()*(worldWidth-world.tileSize*2)+world.tileSize;
	}
	
	public float getRandY() {
		return MathUtils.random()*(worldHeight-world.tileSize*2)+world.tileSize;
	}
	
	public Vector2 getFreePosition() {
		for(int i = 0; i < 5; i++) {		//max tries
			float x = getRandX();
			float y = getRandY();
			if(world.isFree(x, y)) {
				return new Vector2(x, y);
			}
		}
		return new Vector2(getRandX(), getRandY());
	}
	
	public Vector2 getWorldPosition(float screenX, float screenY) {
		CameraComponent camComp = Mappers.cameraComponentMapper.get(camera);
		float w = camComp.cam.viewportWidth;
		float h = camComp.cam.viewportHeight;
		float x = (screenX/(float)screenWidth)*w+camComp.cam.position.x-w/2;
		float y = (screenY/(float)screenHeight)*h+camComp.cam.position.y-h/2;
		return new Vector2(x, y);
	}
	
	public void entityAdded(Entity entity) {
		Physics p = physM.get(entity);
		if(p!=null) {
			world.addGroup(p.group);
		}
	}
	
	public void entityRemoved(Entity entity) {
		Physics p = physM.get(entity);
		if(p!=null) {
			world.removeGroup(p.group);
		}
	}
	
	/**
	 * @param x world position
	 * @param y
	 * @return an entity with a physics component at position (x, y)
	 */
	public Entity getEntityAt(float x, float y) {
		Circle circle = world.getCircleAt(x, y);
		if(circle!=null) {
			return circle.getEntity();
		}
		return null;
	}
	
	public void addRandomBot() {
		Vector2 pos = getFreePosition();
		Tile t = world.getTileAt(pos.x, pos.y);
		if(t.energy >= CreatureBody.REPRODUCTION_COST) {
			t.energy-=CreatureBody.REPRODUCTION_COST;
			addEntity(Factory.createBot(genePool.getRandomGenome(this), pos.x, pos.y));
		}
	}
	
	public void botDead(Bot bot) {
		ExperimentSystem exp = engine.getSystem(ExperimentSystem.class);
		if(exp!=null) {
			exp.addDeadBot(bot);
		}
	}
	
	public float getCameraX() {
		return Mappers.transformMapper.get(camera).x;
	}
	
	public float getCameraY() {
		return Mappers.transformMapper.get(camera).y;
	}
	
	public void setCamCanMove(boolean canMove) {
		Mappers.cameraComponentMapper.get(camera).canMove = canMove;
	}

	public void setExperimentListener(ExperimentListener listener) {
		ExperimentSystem exp = engine.getSystem(ExperimentSystem.class);
		if(exp!=null) {
			exp.addExperimentListener(listener);
		}
	}
}

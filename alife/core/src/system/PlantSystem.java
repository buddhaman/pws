package system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Settings;

import component.Physics;
import component.Plant;
import component.Stem;
import entity.Factory;
import genome.GenePool;
import genome.PlantNode;
import physics.Circle;
import physics.Constraint;
import physics.Particle;
import physics.Tile;
import simulation.Simulation;

public class PlantSystem extends EntitySystem {

	private Family family = Family.all(Plant.class).get();
	private ComponentMapper<Plant> plantM = Mappers.plantMapper;
	private ComponentMapper<Physics> physM = Mappers.physicsMapper;

	private Simulation simulation;

	private ImmutableArray<Entity> PlantArray;

	public PlantSystem(Simulation simulation) {
		this.simulation = simulation;
		PlantArray = simulation.engine.getEntitiesFor(family);
	}

	@Override
	public void update(float deltaTime) {
		Settings settings = Settings.getCurrent();
		
		if(PlantArray.size() < settings.minPlants.val) {
			simulation.addPlant();
		}
		for (int m = PlantArray.size()-1; m >= 0; m--) {
			Entity plant = PlantArray.get(m);
			Plant plantComponent = plantM.get(plant);
			Physics body = physM.get(plant);
			Vector2 pos = body.group.circleList.get(0).particle.pos;
			
			Tile t = simulation.world.getTileAt(pos.x, pos.y);
			//Check if plant should die and create seed ends update!!!
			plantComponent.ticksAlive++;
			if (plantComponent.energy <= 0 || plantComponent.ticksAlive > Plant.MAX_TICKS_ALIVE) {
				simulation.removeEntity(plant);
				t.energy+=plantComponent.energy;
				if(plantComponent.genome.hasSeed && MathUtils.random() < settings.spawnSeedProb.val) {
					PlantNode base = plantComponent.genome.base==null? plantComponent.genome : plantComponent.genome.base;
					PlantNode nBase = new PlantNode(base, GenePool.brainActiveMutationProb);
					simulation.addEntity(Factory.createSeed(nBase, pos.x-0.01f, pos.y+.01f));
				}
				return;
			}
			
			//update energy
			if(t.energy>=Plant.ENERGY_INCREASE) {
				Plant pc = needsEnergy(plantComponent);
				if(pc!=null) {
					t.energy-=Plant.ENERGY_INCREASE;
					pc.energy+=Plant.ENERGY_INCREASE;
				}
			}
			
			//update size
			Circle circle = body.group.circleList.get(0);
			float growth =((float)plantComponent.energy)/(float)Plant.MAX_ENERGY;
			circle.r = (Plant.MAX_SIZE-Plant.MIN_SIZE)*growth+Plant.MIN_SIZE;
			for(Constraint constr : body.group.constraintList) {
				constr.length=Plant.STEM_LENGTH+Plant.MAX_SIZE;
			}
			
			//see if constraints are broken
			for(int i = plantComponent.stemArray.size-1; i>=0; i--) {
				Stem stem = plantComponent.stemArray.get(i);
				if(stem.constraint.removed) {
					setBranchReproduction(plantComponent, false);
					plantComponent.stemArray.removeIndex(i);
					plantComponent.leafs.removeValue(stem.b, true);
				}	
			}
			
			//grow
			PlantNode node = plantComponent.genome;
			if(canReproduce(plantComponent)) {
				Array<PlantNode> leafGenes = node.leafs;
				if(node.leafs.size>plantComponent.leafs.size) {
					PlantNode next = nextNode(leafGenes, plantComponent.leafs);
					Entity nPlant = Factory.createPlant(next, pos.x+MathUtils.random(-.1f,.1f), pos.y+MathUtils.random(-.1f, .1f));
					plantComponent.energy-=Plant.MIN_ENERGY;
					simulation.addEntity(nPlant);
					Physics nBody = physM.get(nPlant);
					Constraint constraint = simulation.world.makeConstraint(body.group.circleList.get(0), 
							nBody.group.circleList.get(0));
					plantComponent.stemArray.add(new Stem(plantComponent, plantM.get(nPlant), constraint));
					plantComponent.leafs.add(plantM.get(nPlant));
				}
			}	
		}

		super.update(deltaTime);
	}
	
	public void setBranchReproduction(Plant pc, boolean canReproduce) {
		for(Plant leaf : pc.leafs) {
			leaf.canReproduce = canReproduce;
			setBranchReproduction(leaf, canReproduce);
		}
	}
	
	public boolean canReproduce(Plant plantComponent) {
		if(plantComponent.energy < Plant.MAX_ENERGY)
			return false;
		return plantComponent.canReproduce;
	}
	
	/**
	 * @return missing node in genome
	 */
	public PlantNode nextNode(Array<PlantNode> leafGenes, Array<Plant> leafs) {
		for(int i = 0; i < leafGenes.size; i++) {
			PlantNode leafGene = leafGenes.get(i);
			boolean leafBuilt = false;
			for(int j = 0; j < leafs.size; j++) {
				Plant leaf = leafs.get(j);
				if(leafGene==leaf.genome) {
					leafBuilt = true;
					break;
				}
			}
			if(!leafBuilt)
				return leafGene;
		}
		return null;
	}

	public Plant needsEnergy(Plant pc) {
		if (pc.energy < Plant.MAX_ENERGY)
			return pc;
		for (int i = 0; i < pc.leafs.size; i++) {
			Plant leaf = pc.leafs.get(i);
			if (leaf.energy < Plant.MAX_ENERGY)
				return leaf;
		}
		for (int i = 0; i < pc.leafs.size; i++) {
			Plant leaf = needsEnergy(pc.leafs.get(i));
			if (leaf != null)
				return leaf;
		}
		return null;
	}

	/**
	 * random position in circle around (x, y)
	 * 
	 * @param x
	 * @param y
	 * @param rMin
	 * @param rMax
	 * @return
	 */
	public Vector2 randPos(float x, float y, float rMin, float rMax) {
		float r = MathUtils.random(rMin, rMax);
		float ang = MathUtils.random(-3.1415f, 3.1415f);
		return new Vector2(MathUtils.cos(ang) * r + x, MathUtils.sin(ang) * r + y);
	}

	public void fixToGround(Circle c) {
		Vector2 pos = randPos(c.particle.pos.x, c.particle.pos.y, 2, 3);
		Particle fixed = new Particle(pos);
		fixed.fixed = true;
		Constraint constraint = new Constraint(fixed, c.particle);
		c.group.addConstraint(constraint);
	}
	
	

}

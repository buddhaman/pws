package system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Settings;

import component.Bot;
import component.Corpse;
import component.Physics;
import component.Plant;
import component.Seed;
import creature.Actuator;
import creature.BodyPart;
import creature.CreatureBody;
import creature.Sensor;
import creature.VisionSensor;
import entity.Factory;
import genome.Genome;
import genome.PlantNode;
import physics.Circle;
import physics.CollisionInfo;
import physics.Group;
import physics.Material;
import physics.TileCollisionInfo;
import physics.World;
import simulation.Simulation;

public class BotSystem extends IteratingSystem {

	private Simulation simulation;
	private static final Family family = Family.all(Bot.class, Physics.class).get();

	private ComponentMapper<Bot> botM = Mappers.botMapper;
	private ComponentMapper<Physics> physM = Mappers.physicsMapper;
	private ComponentMapper<Plant> plantM = Mappers.plantMapper;
	private ComponentMapper<Corpse> corpseM = Mappers.corpseMapper;
	private ComponentMapper<Seed> seedM = Mappers.seedMapper;

	public BotSystem(Simulation simulation) {
		super(family);
		this.simulation = simulation;
	}

	protected void processEntity(Entity entity, float deltaTime) {
		Physics physics = physM.get(entity);

		Bot bot = botM.get(entity);

		CreatureBody creature = bot.body;

		updateSensors(creature, physics);
		updateBrain(creature);
		updateMetabolism(creature);
		updateActuators(creature);

		Vector2 pos = creature.group.circleList.get(0).particle.pos;
		checkBounds(pos);

		if (creature.isDead()) {
			simulation.removeEntity(entity);
			simulation.botDead(creature);
			dropSeed(creature, pos.x, pos.y);
			return;
		}
		// drop seeds
		// randomly drop seeds once every 16 seconds
		if (creature.ticksAlive % (60 * 16) == 0) {
			dropSeed(creature, pos.x + .1f, pos.y + .1f);
		}
	}
	
	private void updateMetabolism(CreatureBody creature) {
		creature.work(creature.metabolism);
		if (creature.isDead()) {
			creature.die();
		} else
			creature.ticksAlive++;
	}

	private void updateSensors(CreatureBody creature, Physics physics) {
		World world = simulation.world;

		// update health
		Sensor healthSensor = creature.getSensors(Sensor.HEALTH).get(0);
		healthSensor.input[0] = creature.getHealth() / creature.startHealth;

		// update random
		Sensor randomSensor = creature.getSensors(Sensor.RANDOM).get(0);
		randomSensor.input[0] = MathUtils.random();

		// update mate sensor
		Sensor mateSensor = creature.getSensors(Sensor.MATE).get(0);
		mateSensor.input[0] = creature.isTarget ? 1 : 0;

		// update vision sensors
		int visionSensorNum = creature.getSensorNum(Sensor.VISION);
		float rayLength = Settings.getCurrent().rayLength.val;
		int rayNum = Settings.getCurrent().rayNum.val;
		for (int i = 0; i < visionSensorNum; i++) {
			VisionSensor visionSensor = (VisionSensor) creature.getSensors(Sensor.VISION).get(i);

			float dAngle = visionSensor.fov / (float) (rayNum > 1 ? rayNum - 1 : 1);
			float angle = visionSensor.getAngle();
			for (int j = 0; j < rayNum; j++) {
				float rayAngle = angle - visionSensor.fov / 2 + dAngle * j;
				float sin = MathUtils.sin(rayAngle);
				float cos = MathUtils.cos(rayAngle);
				Material mat = world.castRayMaterial(physics.group, visionSensor.getX(), visionSensor.getY(), cos, sin,
						rayLength);
				visionSensor.materialHitSensor[j] = mat;
				visionSensor.rayAngle[j] = rayAngle;
			}
		}

		// update touch sensors for collisions with other circles
		for (Sensor ts : creature.getSensors(Sensor.TOUCH)) {
			ts.input[0] = 0;
		}
		int numCollisions = physics.collisions.size;
		for (int i = 0; i < numCollisions; i++) {
			CollisionInfo info = physics.collisions.get(i);
			BodyPart touched = creature.bodyMap.get(info.c1);
			if (touched.touchSensor != null)
				touched.touchSensor.input[0] = 1;

			// if the collided entity is a plant & this bodypart has an
			// eatActuator
			if (touched.eatActuator != null) {
				Plant plantC = plantM.get(info.c2.getEntity());
				if (plantC != null) {
					eat(creature, plantC, touched.eatActuator.output[0]);
				} else {
					Corpse corpseC = corpseM.get(info.c2.getEntity());
					if (corpseC != null) {
						eat(creature, corpseC, touched.eatActuator.output[0]);
					} else {
						Seed seed = seedM.get(info.c2.getEntity());
						if (seed != null)
							eat(creature, seed, touched.eatActuator.output[0]);
					}
				}
			} else {
				Bot botC = botM.get(info.c2.getEntity());
				if (botC != null) {
					if (touched.spikeActuator != null)
						attack(creature, touched.spikeActuator.output[0], botC.body);
					else if (creature.mating) {
						if (botC.body == creature.matingTarget) {
							mate(creature, botC.body);
						}
					}
				}
			}

		}

		numCollisions = physics.tileCollisions.size;
		for (int i = 0; i < numCollisions; i++) {
			TileCollisionInfo info = physics.tileCollisions.get(i);
			BodyPart touched = creature.bodyMap.get(info.circle);
			if (touched.touchSensor != null)
				touched.touchSensor.input[0] = 1;
		}

	};

	private void dropSeed(CreatureBody creature, float x, float y) {
		PlantNode gene = creature.dropSeed();
		if(gene==null)
			return;
		Entity seed = Factory.createSeed(gene, x+.1f, y);
		simulation.addEntity(seed);
	}

	private void mate(CreatureBody p1, CreatureBody p2) {
		p1.mating = false;
		p2.mating = false;
		p1.mate();
		p2.mate();
		Genome gene = new Genome(p1.genome, p2.genome);
		gene.mutate();
		Entity newBot = Factory.createBot(gene, p1.group.getX() + .1f, p1.group.getY() + .1f);
		simulation.addEntity(newBot);
	}

	private void attack(CreatureBody attacker, float activity, CreatureBody creature) {
		if (activity > .5f) {
			int hurt = Settings.getCurrent().attackStrength.val;
			creature.hurt(hurt);
		}
	}

	private void eat(CreatureBody creature, Plant pc, float activity) {
		if (activity > .5f) {
			int energy = Math.min((int) creature.eatPlantFactor, pc.energy);
			pc.energy -= energy;
			creature.eat(energy);
		}
	}

	private void eat(CreatureBody creature, Corpse cc, float activity) {
		if (activity > .5f) {
			int energy = Math.min(cc.energy, (int) creature.eatCorpseFactor);
			cc.energy -= energy;
			creature.eat(energy);
		}
	}

	private void eat(CreatureBody creature, Seed seed, float activity) {
		if (activity > .5f) {
			int energy = Math.min(seed.energy, (int) creature.eatPlantFactor);
			seed.energy -= energy;
			if (seed.energy <= 0) {
				creature.seeds.add(seed.plantGenome);
				seed.eaten = true;
			}
			creature.eat(energy);
		}
	}

	private void updateBrain(CreatureBody creature) {
		float[] input = new float[creature.brain.inputSize];
		float[] output;

		// call update on all sensors
		int sensorNum = creature.sensors.size;
		for (int i = 0; i < sensorNum; i++) {
			Sensor s = creature.sensors.get(i);
			s.update();
			for (int j = s.startIdx; j < s.startIdx + s.numNeurons; j++) {
				input[j] = s.input[j - s.startIdx];
			}
		}

		output = creature.brain.update(input);

		// notify actuators
		for (Actuator actuator : creature.actuators) {
			actuator.update(output);
		}
	}

	private void updateActuators(CreatureBody creature) {
		float turningSpeed = Settings.getCurrent().turningSpeed.val;
		float movingSpeed = Settings.getCurrent().movingSpeed.val;
		// get actuators and move
		Actuator moveActuator = creature.getActuators(Actuator.MOVE).get(0);
		Group group = creature.group;

		float moveLeft;
		float moveRight;

		float speed;
		float movementThreshold = .2f;

		if (creature.mating && !creature.matingTarget.isDead()) {
			float dx = creature.matingTarget.group.getX() - creature.group.getX();
			float dy = creature.matingTarget.group.getY() - creature.group.getY();
			float angDif = MathUtils.atan2(dy, dx) - creature.group.rotation;
			if (angDif > MathUtils.PI)
				angDif -= MathUtils.PI * 2;
			if (angDif < -MathUtils.PI)
				angDif += MathUtils.PI * 2;
			moveRight = moveLeft = 0;
			if (angDif < 0)
				moveRight = turningSpeed * .3f;
			else
				moveLeft = turningSpeed * .3f;
			speed = .15f;
		} else {
			moveLeft = moveActuator.output[0] * turningSpeed;
			moveRight = moveActuator.output[1] * turningSpeed;
			speed = (moveActuator.output[2] < movementThreshold ? 0 : (moveActuator.output[2] - movementThreshold));
			speed *= (1 / (1 - movementThreshold)) * movingSpeed;
		}

		group.rotation += (moveLeft - moveRight);

		float sin = MathUtils.sin(group.rotation);
		float cos = MathUtils.cos(group.rotation);

		Circle body = group.circleList.get(0);
		body.particle.addImpulse(cos * speed, sin * speed);

		for (Actuator eatActuator : creature.getActuators(Actuator.EAT)) {
			float activation = eatActuator.output[0];
			if (activation > .5f) {
				creature.work(1);
			}
		}
	}

	private void checkBounds(Vector2 pos) {
		if (pos.x > simulation.worldWidth)
			pos.x -= simulation.worldWidth;
		if (pos.y > simulation.worldHeight)
			pos.y -= simulation.worldHeight;
		if (pos.x < 0)
			pos.x += simulation.worldWidth;
		if (pos.y < 0)
			pos.y += simulation.worldHeight;
	}

}

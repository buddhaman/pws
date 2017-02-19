package entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.Settings;

import component.Bot;
import component.CameraComponent;
import component.Corpse;
import component.InputMovement;
import component.Physics;
import component.Plant;
import component.Seed;
import component.Transform;
import creature.CreatureBody;
import genome.Genome;
import genome.PlantNode;
import physics.Circle;
import physics.Constraint;
import physics.Group;
import physics.Material;
import physics.Particle;

public class Factory {
	
	public static Entity createCamera(float x, float y, float zoom) {
		Entity camera = new Entity();
		Transform t = new Transform();
		t.x=x;
		t.y=y;
		camera.add(t);
		CameraComponent camComponent = new CameraComponent();
		camComponent.zoom = zoom;
		camComponent.cam = new OrthographicCamera();
		camera.add(camComponent);
		camera.add(new InputMovement());
		camComponent.canMove = true;
		
		return camera;
	}
	
	public static Entity createBot(Genome genome, float x, float y) {
		Entity entity = new Entity();
		
		Bot bot = new Bot();
		entity.add(bot);
	
		CreatureBody body = new CreatureBody(genome);
		bot.body = body;
		body.energy = CreatureBody.REPRODUCTION_COST;
		
		Physics physics = new Physics();
		Group group = body.group;
		group.material = new Material(genome.r, genome.g, genome.b);
		group.setEntity(entity);
		group.groundFriction = Settings.getCurrent().friction.val;
		
		physics.group = group;
		group.circleList.get(0).setPosition(x, y);
		entity.add(physics);
		return entity;
	}
	
	public static Entity createSeed(PlantNode base, float x, float y) {
		Entity entity = new Entity();
		
		Seed seed = new Seed();
		seed.plantGenome = base;
		entity.add(seed);
		
		Physics physics = new Physics();
		Group group = new Group();
		group.material = new Material(base.r, base.g, base.b);
		physics.group = group;
		group.groundFriction = .96f;
		
		Circle circle = new Circle(x, y, Seed.SIZE, true);
		group.addCircle(circle);
		group.setEntity(entity);
		entity.add(physics);
		
		return entity;
	}
	
	public static Entity createPlant(PlantNode node, float x, float y) {
		Entity entity = new Entity();
		
		Plant plant = new Plant();
		entity.add(plant);
		plant.genome = node;
		plant.energy = Plant.MIN_ENERGY;
		plant.canReproduce = !node.hasSeed;
		
		Physics physics = new Physics();
		Group group = new Group();
		group.material = new Material(node.r, node.g, node.b);
		physics.group = group;
		group.groundFriction = .96f;
		
		Circle circle = new Circle(x, y, Plant.MIN_SIZE, true);
		group.addCircle(circle);
		
		if(node.base==null) {
			Particle fixed = new Particle(x+2, y+2);
			fixed.fixed = true;
			group.addConstraint(new Constraint(fixed, circle.particle));
		}
		group.setEntity(entity);
		
		entity.add(physics);
		
		return entity;
	}

	public static Entity createCorpse(CreatureBody creature) {
		Entity entity = new Entity();
		
		Corpse corpse = new Corpse();
		
		corpse.energy = creature.energy;
		entity.add(corpse);
		
		Physics physics = new Physics();
		Group group = new Group();
		group.material = Material.materials[Material.CORPSE];
		physics.group = group;
		
		Circle circle = new Circle(creature.group.getX(), creature.group.getY(), Corpse.MAX_SIZE, false);
		group.addCircle(circle);
		entity.add(physics);
		group.setEntity(entity);
		
		return entity;
	}
}

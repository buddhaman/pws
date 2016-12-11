package system;

import com.badlogic.ashley.core.ComponentMapper;

import component.Bot;
import component.BoundingBox;
import component.CameraComponent;
import component.Corpse;
import component.InputMovement;
import component.Physics;
import component.Plant;
import component.Seed;
import component.Transform;
import component.Velocity;

public class Mappers {
	public static final ComponentMapper<Transform> transformMapper = ComponentMapper.getFor(Transform.class);
	public static final ComponentMapper<Velocity> velocityMapper = ComponentMapper.getFor(Velocity.class);
	public static final ComponentMapper<CameraComponent> cameraComponentMapper = ComponentMapper.getFor(CameraComponent.class);
	public static final ComponentMapper<InputMovement> inputMovementMapper = ComponentMapper.getFor(InputMovement.class);
	public static final ComponentMapper<BoundingBox> boundingBoxMapper = ComponentMapper.getFor(BoundingBox.class);
	public static final ComponentMapper<Physics> physicsMapper = ComponentMapper.getFor(Physics.class);
	public static final ComponentMapper<Bot> botMapper = ComponentMapper.getFor(Bot.class);
	public static final ComponentMapper<Plant> plantMapper = ComponentMapper.getFor(Plant.class);
	public static final ComponentMapper<Corpse> corpseMapper = ComponentMapper.getFor(Corpse.class);
	public static final ComponentMapper<Seed> seedMapper = ComponentMapper.getFor(Seed.class);
}

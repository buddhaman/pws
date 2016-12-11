package system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.math.MathUtils;

import component.CameraComponent;
import component.InputMovement;
import component.Transform;
import simulation.Simulation;

public class CameraSystem extends IntervalIteratingSystem {
	
	public ComponentMapper<Transform> transM = Mappers.transformMapper;
	public ComponentMapper<CameraComponent> camM = Mappers.cameraComponentMapper;
	public ComponentMapper<InputMovement> inputM = Mappers.inputMovementMapper;
	
	public Simulation simulation;
	
	private static final Family family = Family.all(CameraComponent.class, Transform.class).get();
	
	public CameraSystem(Simulation simulation) {
		super(family, simulation.timeStep);
		this.simulation = simulation;
	}

	@Override
	protected void processEntity(Entity entity) {
		CameraComponent camComponent = camM.get(entity);
		camComponent.width = simulation.screenWidth;
		camComponent.height = simulation.screenHeight;
		float w = camComponent.width*camComponent.zoom;
		float h = camComponent.height*camComponent.zoom;
		camComponent.cam.setToOrtho(false, w, h);
		Transform camTrans = transM.get(entity);
		
		camComponent.cam.translate(camTrans.x-w/2, camTrans.y-h/2);
		camComponent.cam.rotate(-camTrans.rotation*MathUtils.radDeg);
		camComponent.cam.update();
		
		boolean canMove = camComponent.canMove;
		
		InputMovement input = inputM.get(entity);
		if(input!=null && canMove) {
			float speed = 1f;
			if(input.left) camTrans.x-=speed;
			if(input.right) camTrans.x+=speed;
			if(input.down) camTrans.y-=speed;
			if(input.up) camTrans.y+=speed;
			if(input.action1) camComponent.zoom*=1.01f;
			if(input.action2) camComponent.zoom/=1.01f;
		}
	}
}

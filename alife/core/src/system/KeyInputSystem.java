package system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import component.InputMovement;
import simulation.Simulation;

public class KeyInputSystem extends IntervalSystem{
	
	Simulation simulation;
	
	private static Family family = Family.all(InputMovement.class).get();
	private ComponentMapper<InputMovement> inputM = Mappers.inputMovementMapper;
	
	private ImmutableArray<Entity> inputMovementArray;
	
	public boolean up;
	public boolean down;
	public boolean left;
	public boolean right;
	public boolean action1;
	public boolean action2;
	public boolean action3;
	public boolean isMouseDown;
	
	public KeyInputSystem(Simulation simulation) {
		super(simulation.timeStep);
		this.simulation = simulation;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		inputMovementArray = engine.getEntitiesFor(family);
	}

	@Override
	protected void updateInterval() {
		up = Gdx.input.isKeyPressed(Keys.W);
		down = Gdx.input.isKeyPressed(Keys.S);
		left = Gdx.input.isKeyPressed(Keys.A);
		right = Gdx.input.isKeyPressed(Keys.D);
		
		action1 = Gdx.input.isKeyPressed(Keys.X);
		action2 = Gdx.input.isKeyPressed(Keys.Z);
		action3 = Gdx.input.isKeyJustPressed(Keys.T);
		if(action3) System.out.println(simulation.getTime());
		
		for(int i = 0; i < inputMovementArray.size(); i++) {
			InputMovement input = inputM.get(inputMovementArray.get(i));
			input.down = down;
			input.left = left;
			input.up = up;
			input.right = right;
			input.action1 = action1;
			input.action2 = action2;
		}
	}
	
}

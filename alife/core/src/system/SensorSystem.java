package system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;

import component.Bot;

public class SensorSystem extends EntitySystem {
	
	private Family botFamily = Family.all(Bot.class).get();
	
	public SensorSystem() {
		
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
	}	
}

package component;

import com.badlogic.ashley.core.Component;

public class Corpse implements Component {
	
	public static final float MIN_SIZE = 0.8f;
	public static final float MAX_SIZE = 2.2f;
	
	public static final int ENERGY_LEAK = 1;
	
	public int energy;
}

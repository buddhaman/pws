package component;

import com.badlogic.ashley.core.Component;

import genome.PlantNode;

public class Seed implements Component{
	
	public static final float SIZE = .5f;
	public static final int ENERGY_THRESHOLD = 300;
	public static final int TICKS_PER_ENERGY_UNIT = 5;	//5*300 = 1500 is 25 seconds
	public static final int MAX_TICKS_ALIVE = 60*60*3;	//3 minuten
	
	public PlantNode plantGenome;	//must be base node
	public int energy;
	public int ticksAlive;
	public boolean eaten;
	
}

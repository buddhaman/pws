package component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import genome.PlantNode;

public class Plant implements Component {
	
	public static final int ENERGY_INCREASE = 2;
	public static final int MAX_ENERGY = 600;
	public static final int MIN_ENERGY = 5;
	public static final int MAX_TICKS_ALIVE = 60*45*2;	//n minute
	public static final float MIN_SIZE = .8f;
	public static final float MAX_SIZE = 3;
	public static final float STEM_LENGTH = 6;
	
	public int energy;
	public int ticksAlive;
	public PlantNode genome; 	//node in genome that refers to this leaf. Also contains base node
	public Array<Plant> leafs = new Array<Plant>();
	public Array<Stem> stemArray = new Array<Stem>();
	public boolean canReproduce;
}

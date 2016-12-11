package physics;

import com.badlogic.gdx.utils.Array;


public class Tile {
	public int type;
	
	public static final int TYPE_EMPTY = 0;
	public static final int TYPE_STONE = 1;
	
	public Material[] materials = new Material[]{null, Material.getMaterial(Material.STONE)};
	
	public Array<Circle> circleList = new Array<Circle>();
	
	public boolean[] empty = new boolean[]{true, false};
	public int energy;

	public Tile() {
		
	}
	
	public Tile(int type) {
		this.type = type;
	}
	
	public Tile(int type, int energy) {
		this.type = type;
		this.energy = energy;
	}
	
	
	public boolean isEmpty() {
		return empty[type];
	}

	public Material getMaterial() {
		return materials[type];
	}

	public void setType(int type) {
		this.type = type;
	}
}

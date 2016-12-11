package physics;

import com.badlogic.gdx.graphics.Color;

public class Material {
	/**
	 * Material contains material properties like color and later density, mass, restitution, diffusion, specular etc
	 */
	
	public static final Material[] materials = new Material[]{new Material(Color.WHITE), 
		new Material(Color.GREEN), new Material(Color.RED), new Material(Color.BLUE),
		new Material(Color.RED)};
	
	public static final int WHITE = 0;
	public static final int PLANT = 1;
	public static final int CREATURE_TEST = 2;
	public static final int STONE = 3;
	public static final int CORPSE = 4;
	
	private Color color;
	
	public Material(Color color) {
		this.color = color;
	}
	
	public Material(float r, float g, float b) {
		this(new Color(r, g, b, 1));
	}

	public Color getColor() {
		return color;
	}
	
	public static Material getMaterial(int type) {
		return materials[type];
	}
}

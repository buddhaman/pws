package renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Renderer {
	
	public SpriteBatch batch;
	public RenderUtils utils;
	public Resources resources;
	
	public Renderer(Resources resources) {
		batch = resources.getSpriteBatch();
		utils = resources.getRenderUtils();
		this.resources = resources;
	}
}

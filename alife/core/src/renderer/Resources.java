package renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public interface Resources {
	public AtlasRegion findRegion(String name);
	public SpriteBatch getSpriteBatch();
	public RenderUtils getRenderUtils();
}

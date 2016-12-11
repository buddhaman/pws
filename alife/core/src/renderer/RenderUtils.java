package renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class RenderUtils {
	
	/**
	 * handles all shared renderfunctions
	 */
	
	public AtlasRegion region;
	public SpriteBatch batch;
	
	private Resources resources;
	
	public RenderUtils(Resources resources) {
		this.region = resources.findRegion("blankRegion");
		this.batch = resources.getSpriteBatch();
		this.resources = resources;
	}
	
	public void drawLine(float x1, float y1, float x2, float y2, float lineWidth) {
		drawLine(batch, x1, y1, x2, y2, lineWidth, region);
	}
	
	public void drawLineRect(float x, float y, float width, float height, float lineWidth) {
		drawLineRect(batch, x, y, width, height, lineWidth, region);
	}
	
	public static void drawLine(SpriteBatch batch, float x1, float y1, float x2, float y2, float lineWidth, AtlasRegion lineTexture) {
		float xdif = x2-x1;
		float ydif = y2-y1;
		float l2 = xdif*xdif+ydif*ydif;
		float invl = (float)(1/Math.sqrt(l2));
		xdif*=invl*lineWidth;
		ydif*=invl*lineWidth;
		
		float floatBits = batch.getColor().toFloatBits();
		float[] verts = new float[]{x1+ydif, y1-xdif, floatBits, lineTexture.getU(), lineTexture.getV(),
									x1-ydif, y1+xdif, floatBits, lineTexture.getU2(), lineTexture.getV(),
									x2-ydif, y2+xdif, floatBits, lineTexture.getU2(), lineTexture.getV2(),
									x2+ydif, y2-xdif, floatBits, lineTexture.getU(), lineTexture.getV2()};
		batch.draw(lineTexture.getTexture(), verts, 0, 20);
	}
	
	public static void drawLineRect(SpriteBatch batch, float x, float y, float width, float height, float lineWidth, AtlasRegion lineTexture) {
		drawLine(batch, x, y, x+width, y, lineWidth, lineTexture);
		drawLine(batch, x+width, y, x+width, y+height, lineWidth, lineTexture);
		drawLine(batch, x+width, y+height, x, y+height, lineWidth, lineTexture);
		drawLine(batch, x, y+height, x, y, lineWidth, lineTexture);
	}
}

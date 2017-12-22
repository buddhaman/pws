package renderer;

import physics.Tile;
import physics.World;
import simulation.Simulation;
import system.Mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import component.CameraComponent;

public class WorldRenderer extends Renderer {
	
	private AtlasRegion blankRegion;
	private ComponentMapper<CameraComponent> camM = Mappers.cameraComponentMapper;
	
	public static final Color bgColor = new Color(.94f,.94f,.94f,1);
	
	public WorldRenderer(Resources res) {
		super(res);
		blankRegion = res.findRegion("blankRegion");
	}

	public void render(Simulation simulation, Entity camera) {
		World world = simulation.world;
		
		batch.setColor(Color.WHITE);
		utils.drawLineRect(0, 0, world.width, world.height, .1f);
		
		CameraComponent cam = camM.get(camera);
		
		Vector3 camPos = cam.cam.position;
		float camWidth = cam.cam.viewportWidth;
		float camHeight = cam.cam.viewportHeight;
		
		int xStart = (int)((camPos.x-camWidth/2)/world.tileSize);
		int yStart = (int)((camPos.y-camHeight/2)/world.tileSize);
		xStart = MathUtils.clamp(xStart, 0, world.tWidth-1);
		yStart = MathUtils.clamp(yStart, 0, world.tHeight-1);
		
		int xEnd = (int)((camPos.x+camWidth/2)/world.tileSize);
		int yEnd = (int)((camPos.y+camHeight/2)/world.tileSize);
		xEnd = MathUtils.clamp(xEnd+1, 0, world.tWidth-1);
		yEnd = MathUtils.clamp(yEnd+1, 0, world.tHeight-1);
		
		for(int i = xStart; i <= xEnd; i++) {
			for(int j = yStart; j <= yEnd; j++) {
				Tile t = world.tiles[i+j*world.tWidth];
				if(t.type==Tile.TYPE_STONE) {
					batch.setColor(Color.BLUE);
				} else {
					float tint = .3f+MathUtils.clamp(t.energy/(10*world.tileSize*world.tileSize), 0, .7f);
					batch.setColor(tint*bgColor.r,tint*bgColor.g,tint*bgColor.b,1);
				}
				batch.draw(blankRegion, i*world.tileSize, j*world.tileSize, world.tileSize, world.tileSize);
			}
		}
	}
}

package renderer;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import component.Physics;
import component.Seed;
import physics.Circle;
import system.Mappers;

public class SeedRenderer extends Renderer {

	private AtlasRegion circle;
	private ComponentMapper<Physics> physM = Mappers.physicsMapper;
	private ComponentMapper<Seed> seedM = Mappers.seedMapper;
	
	public SeedRenderer(Resources resources) {
		super(resources);
		circle = resources.findRegion("circle");
	}
	
	public void render(ImmutableArray<Entity> seeds) {
		for(int i = 0; i < seeds.size(); i++) {
			Entity entity = seeds.get(i);
			Physics physics = physM.get(entity);
			Seed seed = seedM.get(entity);
			Circle c = physics.group.circleList.get(0);
			batch.setColor(seed.plantGenome.r, seed.plantGenome.g, seed.plantGenome.b, 1);
			batch.draw(circle, c.getMinX(), c.getMinY(), c.r*2, c.r*2);
		}
	}
	
}

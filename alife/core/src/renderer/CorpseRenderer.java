package renderer;

import physics.Circle;
import physics.Group;
import system.Mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;

import component.Corpse;
import component.Physics;

public class CorpseRenderer extends Renderer {
	
	private ComponentMapper<Physics> physM = Mappers.physicsMapper;
	private ComponentMapper<Corpse> corpseM = Mappers.corpseMapper;

	private AtlasRegion corpseRegion;
	
	public CorpseRenderer(Resources resources) {
		super(resources);
		corpseRegion = resources.findRegion("circle");
	}
	
	public void render(ImmutableArray<Entity> corpseArray) {
		for(int i = 0; i < corpseArray.size(); i++) {
			Entity entity = corpseArray.get(i);
			Physics physics = physM.get(entity);
			Group group = physics.group;
			Corpse corpse = corpseM.get(entity);
			for(Circle circle : group.circleList) {
				float tint = MathUtils.clamp(((float)corpse.energy)/1000f, .2f, 1);
				batch.setColor(tint, 0, 0, 1);
				batch.draw(corpseRegion, circle.getMinX(), circle.getMinY(), circle.r*2, circle.r*2);
			}
		}
	}
	
}

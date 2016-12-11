package renderer;

import physics.Circle;
import physics.Constraint;
import physics.Group;
import system.Mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import component.Physics;
import component.Plant;
import component.Seed;

public class PlantRenderer extends Renderer {
	
	private AtlasRegion plantRegion;
	
	private ComponentMapper<Plant> plantM = Mappers.plantMapper;
	private ComponentMapper<Physics> physM = Mappers.physicsMapper;
	
	public PlantRenderer(Resources resources) {
		super(resources);
		plantRegion = resources.findRegion("plant");
	}
	
	public void render(ImmutableArray<Entity> plantArray) {
		
		for(int i = 0; i < plantArray.size(); i++) {
			Entity entity = plantArray.get(i);
			Physics physics = physM.get(entity);
			Group group = physics.group;
			for(Constraint constraint : group.constraintList) {
				batch.setColor(group.material.getColor());
				utils.drawLine(constraint.p.pos.x, constraint.p.pos.y,
						constraint.q.pos.x, constraint.q.pos.y, .333f);
			}
		}
		
		for(int i = 0; i < plantArray.size(); i++) {
			Entity entity = plantArray.get(i);
			Physics physics = physM.get(entity);
			Group group = physics.group;
			Plant pc = plantM.get(entity);
			boolean hasSeed = pc.genome.hasSeed;
			for(Circle circle : group.circleList) {
				batch.setColor(group.material.getColor());
				batch.draw(plantRegion, circle.getMinX(), circle.getMinY(), circle.r*2, circle.r*2);
				if(hasSeed) {
					float r = Seed.SIZE;
					batch.setColor(Color.BLACK);
					batch.draw(plantRegion, circle.getX()-r, circle.getY()-r, r*2, r*2);
				}
			}
		}
		
		
		
	}
	
}

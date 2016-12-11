package renderer;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Settings;

import component.Bot;
import component.Physics;
import creature.Actuator;
import creature.BodyPart;
import creature.CreatureBody;
import creature.Sensor;
import creature.VisionSensor;
import physics.Circle;
import physics.Material;
import system.Mappers;

public class BotRenderer extends Renderer {
	private ComponentMapper<Bot> botM = Mappers.botMapper;
	private ComponentMapper<Physics> physM = Mappers.physicsMapper;

	private AtlasRegion circleRegion;
	private AtlasRegion eyeRegion;
	private AtlasRegion spikeRegion;
	private AtlasRegion mouthLeftRegion;
	private AtlasRegion mouthRightRegion;
	private AtlasRegion arrow;
	private AtlasRegion heart;
	
	private Color eyeTestColor = new Color(1, 1, 1, 1);
	private Color spikeTestColor = new Color(.7f, 1, 1, 1);
	private Color mouthTestColor = new Color(1, 1, .7f, 1);
	
	private int timer;
	
	private CreatureBody selected;
	
	public BotRenderer(Resources resources) {
		super(resources);
		circleRegion = resources.findRegion("circle");
		eyeRegion = resources.findRegion("oog");
		spikeRegion = resources.findRegion("spike");
		mouthLeftRegion = resources.findRegion("jawLeft");
		mouthRightRegion = resources.findRegion("jawRight");
		heart = resources.findRegion("heart");
		arrow = resources.findRegion("pijl");
	}

	public void render(ImmutableArray<Entity> botArray) {
		float edge = .2f;
		timer++;
		if(timer >= 60) {
			timer=0;
		}
		float cycle = MathUtils.sin(timer*MathUtils.PI2/60)*.5f+.5f;
		
		if(selected!=null) {
			float selectedRadius = 5;
			batch.setColor(1,.9f-cycle*.05f,.9f-cycle*.05f,.2f+cycle*.12f);
			Vector2 selectedPos= selected.group.circleList.get(0).particle.pos;
			for(int i = 0; i < 2; i++) {
			batch.draw(circleRegion, selectedPos.x-selectedRadius, selectedPos.y-selectedRadius, 
					selectedRadius*2, selectedRadius*2);
			selectedRadius-=.7f;
			}
		}
		selected = null;
		for (int i = 0; i < botArray.size(); i++) {
			Entity entity = botArray.get(i);
			Physics physics = physM.get(entity);
			Bot bot = botM.get(entity);
			
			if(bot.renderState) {
				selected = bot.body;
			}
			
			//update mouth timers
			for(Actuator act : bot.body.getActuators(Actuator.EAT)) {
				float activation = act.output[0];
				if(activation > .5f)
					act.bodyPart.timer+=(activation-.5f)*.2f;
				else
					act.bodyPart.timer*=.8f;
				act.bodyPart.timer%=1.0f;
			}
			
			Color bodyColor = physics.group.material.getColor();
			
			Array<BodyPart> parts = bot.body.bodyParts;
			for (int j = 0; j < parts.size; j++) {
				BodyPart bp = parts.get(j);
				Circle circle = bp.circle;
				
				float x = circle.particle.pos.x;
				float y = circle.particle.pos.y;

				batch.setColor(Color.BLACK);
				if(bp.type==BodyPart.MOUTH || bp.type == BodyPart.SPIKE) {
					//do nothing... yet
				} else {
					drawBodyPart(circleRegion, bp, edge);
				}
			}
			batch.setColor(Color.BLACK);
			for(Actuator actuator : bot.body.getActuators(Actuator.EAT)) {
				drawMouth(actuator.bodyPart, edge);
			}
			
			for(Actuator actuator : bot.body.getActuators(Actuator.SPIKE)) {
				drawSpike(actuator, edge);
			}

			for (int j = 0; j < bot.body.bodyParts.size; j++) {
				BodyPart bp = bot.body.bodyParts.get(j);
	
				if(bp.type==BodyPart.LIMB) {
					batch.setColor(bodyColor);
					drawBodyPart(circleRegion, bp, 0);
				}
				
			}
			
			for(Actuator actuator : bot.body.getActuators(Actuator.SPIKE)) {
				batch.setColor(spikeTestColor);
				this.drawSpike(actuator, 0);
			}
			
			for(Actuator actuator : bot.body.getActuators(Actuator.EAT)) {
				BodyPart bp = actuator.bodyPart;
				batch.setColor(mouthTestColor);
				drawMouth(bp, 0);
			}
			
			//draw eyes
			for(Sensor sensor : bot.body.getSensors(Sensor.VISION)) {
				BodyPart bp = sensor.bodyPart;
				batch.setColor(eyeTestColor);
				drawBodyPart(eyeRegion, bp);
			}

			if(bot.renderState) {
				int rayNum = Settings.getCurrent().rayNum.val;
				for (Sensor s : bot.body.getSensors(Sensor.VISION)) {
					VisionSensor sensor = (VisionSensor)s;
					for (int j = 0; j < rayNum; j++) {
						float x = sensor.bodyPart.circle.particle.pos.x;
						float y = sensor.bodyPart.circle.particle.pos.y;
						Material m = sensor.materialHitSensor[j];
						if (m != null) {
							batch.setColor(m.getColor());
						} else
							batch.setColor(Color.WHITE);
						float sin = MathUtils.sin(sensor.rayAngle[j]);
						float cos = MathUtils.cos(sensor.rayAngle[j]);
						utils.drawLine(x, y, x + cos * 40, y + sin * 40, .1f);
				}
			}
			}
		}
		
		for(int i = 0; i < botArray.size(); i++) {
			Bot bot = botM.get(botArray.get(i));
			CreatureBody cb = bot.body;
			if(cb.mating) {
				float hw = 4;
				float hh = 4;
				batch.setColor(1f,.3f+cycle*.4f,.3f+(1-cycle)*.4f,1);
				batch.draw(heart, cb.group.getX()-hw/2, cb.group.getY()-hh/2+4+cycle, hw, hh);
			}
		}
		
		if(selected!=null) {
			if(selected.mating) {
				CreatureBody target = selected.matingTarget;
				float aw = 3;
				float ah = 4;
				batch.setColor(1f, .2f, .2f, 1);
				batch.draw(arrow, target.group.getX()-aw/2, target.group.getY()-ah/2+cycle+4, aw, ah);
			}
		}
	}
	
	public void drawSpike(Actuator actuator, float edge) {
		float ang = actuator.bodyPart.getAngle();
		float activation = actuator.output[0];
		
		Circle circle = actuator.bodyPart.circle;
		float length = (activation-.5f)*1.2f;
		float r = circle.r+edge;
		float x = circle.particle.pos.x+MathUtils.cos(ang)*length;
		float y = circle.particle.pos.y+MathUtils.sin(ang)*length;
		batch.draw(spikeRegion, x-r, y-r, r, r, r*2, r*2, 1, 1, ang*MathUtils.radiansToDegrees-90);
	}
	
	public void drawMouth(BodyPart bp, float edge) {
		float ang = bp.getAngle();
		Vector2 base = bp.connection.a.pos;
		float length = bp.connection.length;
		float r = bp.circle.r+edge;
		float mouthAngle = bp.timer < .5f ? bp.timer*2 : (1-bp.timer)*2;
		mouthAngle*=.7f;
		
		float y = length*MathUtils.sin(ang-mouthAngle);
		float x = length*MathUtils.cos(ang-mouthAngle);
		batch.draw(mouthLeftRegion, base.x+x-r, base.y+y-r, r, r, r*2, r*2, 1, 1, 90+(ang-mouthAngle)*MathUtils.radiansToDegrees);
		y = length*MathUtils.sin(ang+mouthAngle);
		x = length*MathUtils.cos(ang+mouthAngle);
		batch.draw(mouthRightRegion, base.x+x-r, base.y+y-r, r, r, r*2, r*2, 1, 1, 90+(ang+mouthAngle)*MathUtils.radiansToDegrees);
	}
	
	public void drawBodyPart(TextureRegion region, BodyPart part) {
		Vector2 pos = part.circle.particle.pos;
		float r = part.circle.r;
		batch.draw(region, pos.x-r, pos.y-r, r, r, r*2, r*2, 1, 1, part.getAngle()*MathUtils.radiansToDegrees-90);
	}
	
	public void drawBodyPart(TextureRegion region, BodyPart part, float edge) {
		float r = part.circle.r;
		Vector2 pos = part.circle.particle.pos;
		batch.draw(circleRegion, pos.x-r-edge, pos.y-r-edge, (r+edge)*2, (r+edge)*2);
	}
}

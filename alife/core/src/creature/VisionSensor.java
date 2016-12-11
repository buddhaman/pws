package creature;

import com.mygdx.game.Settings;

import physics.Material;

public class VisionSensor extends Sensor {

	//angle between rays
	public float[] rayAngle;
	public Material[] materialHitSensor;
	public float fov = .4f;
	
	public VisionSensor() {
		super(Sensor.VISION);
		int rayNum = Settings.getCurrent().rayNum.val;
		materialHitSensor = new Material[rayNum];
		rayAngle = new float[rayNum];
	}

	@Override
	public void update() {
		super.update();
		for(int i = 0; i < numNeurons/3; i++) {
			if(materialHitSensor[i]==null) {
				input[i*3] = 0;
				input[i*3+1] = 0;
				input[i*3+2] = 0;
			} else {
				input[i*3] = materialHitSensor[i].getColor().r;
				input[i*3+1] = materialHitSensor[i].getColor().g;
				input[i*3+2] = materialHitSensor[i].getColor().b;
			}
		}
	}
	
	public float getAngle() {
		return bodyPart.getAngle();
	}
	
	public float getX() {
		return bodyPart.circle.particle.pos.x;
	}
	
	public float getY() {
		return bodyPart.circle.particle.pos.y;
	}

}

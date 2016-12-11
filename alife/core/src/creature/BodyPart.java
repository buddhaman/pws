package creature;

import physics.Circle;
import physics.Connection;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class BodyPart {
	
	//pointer to physics circle;
	public Circle circle;
	
	public float angle;
	
	//connection that contains this circle, use later for movement if necesarry
	public Connection connection;	
	
	public int type;
	public static final int LIMB = 0;
	public static final int EYE = 1;
	public static final int MOUTH = 2;
	public static final int SPIKE = 3;
	
	public static final boolean[] extendable = new boolean[]{true, false, false, false};
	public Sensor touchSensor;
	public Actuator eatActuator;
	public Actuator spikeActuator;
	
	public float timer;
	
	public BodyPart(int type, Circle circle) {
		this.type = type;
		this.circle = circle;
	}
	
	public BodyPart(int type, Circle circle, Connection connection) {
		this(type, circle);
		this.connection = connection;
		this.angle = connection.angle;
	}
	
	public void sensorAdded(Sensor sensor) {
		if(sensor.type == Sensor.TOUCH)
			touchSensor = sensor;
	}
	
	public void actuatorAdded(Actuator actuator) {
		if(actuator.type==Actuator.EAT) {
			eatActuator = actuator;
		} else if(actuator.type==Actuator.SPIKE) {
			spikeActuator = actuator;
		}
	}
	
	public static int getRandomType(float limbProbabilty) {
		if(MathUtils.random() < limbProbabilty) {
			return LIMB;
		} else {
			return MathUtils.random(EYE, SPIKE);
		}
	}

	public float getAngle() {
		float dx = connection.b.pos.x-connection.a.pos.x;
		float dy = connection.b.pos.y-connection.a.pos.y;
		return MathUtils.atan2(dy, dx);
	}
	
	public static boolean isExtendable(int type) {
		return extendable[type];
	}
}

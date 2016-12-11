package physics;

import com.badlogic.gdx.utils.Array;

public class Connection {
	public Particle a;
	public Particle b;
	
	public float length;
	public float angle;
	
	public Array<Connection> connections = new Array<Connection>();
	
	public Connection(Particle a, Particle b, float length, float angle) {
		this.length = length;
		this.angle = angle;
		this.a = a;
		this.b = b;
	}
}

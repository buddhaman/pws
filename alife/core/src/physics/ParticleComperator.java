package physics;

import java.util.Comparator;

public class ParticleComperator implements Comparator<Particle> {

	public Particle to;
	
	private static final ParticleComperator particleComperator = new ParticleComperator(null);
	
	public static ParticleComperator getComperator(Particle to) {
		particleComperator.to = to;
		return particleComperator;
	}
	
	public ParticleComperator(Particle to) {
		this.to = to;
	}
	
	@Override
	public int compare(Particle p1, Particle p2) {
		return (int) Math.signum(p2.pos.dst2(to.pos)-p1.pos.dst2(to.pos));
	}

}

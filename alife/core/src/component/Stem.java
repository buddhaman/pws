package component;

import physics.Constraint;

public class Stem {
	public Plant a;
	public Plant b;
	public Constraint constraint;
	
	public Stem(Plant a, Plant b, Constraint constraint) {
		this.a = a;
		this.b = b;
		this.constraint = constraint;
	}
}

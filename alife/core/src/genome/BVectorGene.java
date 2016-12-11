package genome;

import com.badlogic.gdx.math.MathUtils;

public class BVectorGene {
	
	public boolean[] vector;
	
	public int size;
	
	public BVectorGene() {
		
	}
	
	public BVectorGene(int size) {
		this.size = size;
		vector = new boolean[size];
	}
	
	public BVectorGene(BVectorGene gene) {
		this.size = gene.size;
		vector = new boolean[size];
		for(int i = 0; i < size; i++) vector[i] = gene.vector[i];
	}
	
	public BVectorGene(BVectorGene g1, BVectorGene g2, float switchProb) {
		if(g1.size!=g2.size)
			System.err.println("boolean vector genes must have same size");
		
		this.size = g1.size;
		vector = new boolean[size];
		
		boolean inParent1 = true;
		for(int i = 0; i < size; i++) {
			if(MathUtils.random() < switchProb) {
				inParent1=!inParent1;
			}
			vector[i] = inParent1? g1.vector[i] : g2.vector[i];
		}
	}
	
	/**
	 * @param distr probability that any element in this vector is true
	 */
	public void setRandomValues(float distr) {
		for(int i = 0; i < size; i++) {
			vector[i] = MathUtils.random() < distr;
		}
	}
	
	public void mutate(float mutationProb) {
		for(int i = 0; i < size; i++) {
			if(MathUtils.random() < mutationProb) {
				vector[i]=!vector[i];
			}
		}
	}
}

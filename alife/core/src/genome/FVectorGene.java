package genome;

import com.badlogic.gdx.math.MathUtils;

public class FVectorGene {
	public float[] vector;
	int size;
	
	float min;
	float max;
	
	//TODO: wraparound when value is out of range
	
	public FVectorGene() {
		
	}
	
	public FVectorGene(int size, float min, float max) {
		this.min = min;
		this.max = max;
		this.size = size;
		this.vector = new float[size];
	}
	
	/**
	 * set random value for every element around avg with triangular distribution * distr
	 * @param avg
	 * @param distr
	 */
	public void setRandomValues(float avg, float distr) {
		for(int i = 0; i < size; i++)
			vector[i] = avg+MathUtils.randomTriangular()*distr;
	}
	
	public FVectorGene(FVectorGene gene) {
		this.size = gene.size;
		this.vector = new float[size];
		for(int i = 0; i < size; i++) vector[i] = gene.vector[i];
		this.min = gene.min;
		this.max = gene.max;
	}
	
	public FVectorGene(FVectorGene g1, FVectorGene g2, float switchProb) {
		if(g1.size!=g2.size)
			System.err.println("gene vectors must have same size");
		this.size = g1.size;
		this.vector = new float[size];
		boolean inParent1 = true;
		for(int i = 0; i < size; i++) {
			if(MathUtils.random() < switchProb) inParent1=!inParent1;
			vector[i] = inParent1 ? g1.vector[i] : g2.vector[i];
		}
		this.min = g1.min;
		this.max = g2.max;
	}

	public void mutate(float mutationProb, float mutationAmount) {
		for(int i =0; i < size; i++) {
			if(MathUtils.random() < mutationProb) {
				vector[i]=clamp(vector[i]+mutationAmount*MathUtils.randomTriangular());
			}
		}
	}
	
	public float clamp(float value) {
		if(value < min) value = min;
		if(value > max) value = max;
		return value;
	}
}

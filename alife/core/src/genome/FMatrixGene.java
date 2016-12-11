package genome;

import com.badlogic.gdx.math.MathUtils;

public class FMatrixGene {
	
	public int w;
	public int h;
	
	public float[][] matrix;
	
	public float min, max;
	
	public FMatrixGene() {
		
	}
	
	public FMatrixGene(int w, int h, float min, float max) {
		this.min = min;
		this.max = max;
		this.w = w;
		this.h = h;
		this.matrix = new float[w][h];
	}
	
	public void setRandomValues(float avg, float distr) {
		for(int i = 0; i < w; i++) {
			float[] vec = matrix[i];
			for(int j = 0; j < h; j++) {
				vec[j] = MathUtils.randomTriangular()*distr+avg;
			}
		}
	}
	
	/**
	 * sets all elements to random values equally distributed on [min, max]
	 */
	public void setRandomValues() {
		for(int i = 0; i < w; i++) {
			float[] vec = matrix[i];
			for(int j = 0; j < h; j++) {
				vec[j] = MathUtils.random(min, max);
			}
		}
	}
	
	public FMatrixGene(FMatrixGene gene) {
		this.min = gene.min;
		this.max = gene.max;
		this.w = gene.w;
		this.h = gene.h;
		this.matrix = new float[w][h];
		for(int i = 0; i < matrix.length; i++) {
			float[] collumnThis = matrix[i];
			float[] collumnOther = gene.matrix[i];
			for(int j = 0; j < collumnThis.length; j++) {
				collumnThis[j] = collumnOther[j];
			}
		}
	}
	
	public FMatrixGene(FMatrixGene g1, FMatrixGene g2, float switchProb) {
		if(g1.w!=g2.w||g1.h!=g2.h)
			System.err.println("matrix genes must have same dimensions");
		this.min = g1.min;
		this.max = g1.max;
		this.w = g1.w;
		this.h = g1.h;
		this.matrix = new float[w][h];
		
		boolean inParent1 = true;
		for(int i = 0; i < matrix.length; i++) {
			float[] collumnThis = matrix[i];
			if(MathUtils.random() < switchProb) 
				inParent1=!inParent1;
			float[] collumnOther = inParent1? g1.matrix[i] : g2.matrix[i];
			for(int j = 0; j < collumnThis.length; j++) {
				collumnThis[j] = collumnOther[j];
			}
		};
	}

	/**
	 * probability that an element will mutate is mutationProb 
	 * will mutate with mutationAmount * (triangular distributed random number on [-1, 1])
	 * @param mutationProb mutation probability
	 * @param mutationAmount 
	 */
	public void mutate(float mutationProb, float mutationAmount) {
		for(int i = 0; i < matrix.length; i++) {
			float[] collumn = matrix[i];
			for(int j = 0; j < collumn.length; j++) {
				if(MathUtils.random() < mutationProb)
					collumn[j]=clamp(mutationAmount*MathUtils.randomTriangular()+collumn[j]);
			}
		}
	}
	
	/**
	 * @param value
	 * @return egh clamp with member (min, max)
	 */
	public float clamp(float value) {
		if(value > max) value = 2*max-value;
		if(value < min) value = 2*min-value;
		return value;
	}
}

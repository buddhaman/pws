package genome;

import com.badlogic.gdx.math.MathUtils;

public class BMatrixGene {
	
	public int w;
	public int h;
	
	public boolean[][] matrix;
	
	public BMatrixGene() {
		
	}
	
	public BMatrixGene(int w, int h) {
		this.w = w;
		this.h = h;
		matrix = new boolean[w][h];
	}
	
	
	/**
	 * @param distr probabiltiy that any element in this matrix is true
	 */
	public void setRandomValues(float distr) {
		for(int i = 0; i < w; i ++) {
			boolean[] vec = matrix[i];
			for(int j = 0; j < h; j++) {
				vec[j] = MathUtils.random() < distr;
			}
		}
	}
	
	/**
	 * Makes a deep copy of this boolean matrix gene
	 * @param gene
	 */
	public BMatrixGene(BMatrixGene gene) {
		this.w = gene.w;
		this.h = gene.h;
		this.matrix = new boolean[w][h];
		for(int i = 0; i < matrix.length; i++) {
			boolean[] collumnThis = matrix[i];
			boolean[] collumnOther = gene.matrix[i];
			for(int j = 0; j < collumnThis.length; j++) {
				collumnThis[j] = collumnOther[j];
			}
		}
	}
	
	public BMatrixGene(BMatrixGene parent1, BMatrixGene parent2, float crossoverProb) {
		this.w = parent1.w;
		this.h = parent1.h;
		this.matrix = new boolean[w][h];
		boolean inParent1 = MathUtils.randomBoolean();
		for(int i = 0; i < w; i++) {
			if(MathUtils.random() < crossoverProb)  {
				inParent1 = !inParent1;
			}
			boolean[] nVec = inParent1 ? parent1.matrix[i] : parent2.matrix[i];
			for(int j = 0; j < h; j++) {
				matrix[i][j] = nVec[j];
			}
		}
	}


	/**
	 * probability that an element will mutate is mutationProb 
	 * will flip the element
	 * @param mutationProb mutation probability
	 */
	public void mutate(float mutationProb) {
		for(int i = 0; i < matrix.length; i++) {
			boolean[] collumn = matrix[i];
			for(int j = 0; j < collumn.length; j++) {
				if(MathUtils.random() < mutationProb) {
					collumn[j]=!collumn[j];
				}
			}
		}
	}
}

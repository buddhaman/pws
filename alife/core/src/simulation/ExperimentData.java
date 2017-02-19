package simulation;

import genome.Genome;
import genome.PlantNode;

public class ExperimentData {
	public Genome gene;
	public PlantNode plantGene;
	
	/**
	 * @param gene		genome of creature
	 * @param plantGene	genome of most eaten plant
	 */
	public ExperimentData(Genome gene, PlantNode plantGene) {
		this.gene = gene;
		plantGene.removeRecursion();
		this.plantGene = plantGene;
	}
	
	public ExperimentData() {
		
	}
}

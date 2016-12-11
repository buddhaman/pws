package genome;

import com.mygdx.game.Settings;

import creature.Actuator;
import creature.Sensor;

public class RNNInteractionGene {
	
	/**
	 * sensor or actuator data for building brain
	 */
	
	//borowed from Sensor or actuator
	public int type;
	public int hiddenSize;
	public int nNeurons;
	
	public FMatrixGene weights;
	public BMatrixGene active;
	public FMatrixGene resetWeights;
	public FMatrixGene updateWeights;
	
	public int interactionType;
	public static final int SENSOR = 0;
	public static final int ACTUATOR = 1;
	
	/**
	 * @param type
	 * @param hiddenSize
	 * inits a random sensorgene
	 */
	public RNNInteractionGene(int interactionType, int type, int hiddenSize) {
		this.type = type;
		this.hiddenSize = hiddenSize;
		nNeurons = getNumNeurons(interactionType, type);
		float max =  Settings.getCurrent().efficiacyRange.val;
		float dev = Settings.getCurrent().weightDeviation.val;
		
		weights = new FMatrixGene(nNeurons, hiddenSize, -max, max);
		weights.setRandomValues(0, dev);
		
		updateWeights = new FMatrixGene(nNeurons, hiddenSize, -max, max);
		updateWeights.setRandomValues(0, dev);
		
		resetWeights = new FMatrixGene(nNeurons, hiddenSize, -max, max);
		resetWeights.setRandomValues(0, dev);

		active = new BMatrixGene(nNeurons, hiddenSize);
		active.setRandomValues(Settings.getCurrent().outputConnectivity.val);
	}
	
	public RNNInteractionGene(RNNInteractionGene gene) {
		this.type = gene.type;
		this.hiddenSize = gene.hiddenSize;
		this.nNeurons = gene.nNeurons;
		weights = new FMatrixGene(gene.weights);
		updateWeights = new FMatrixGene(gene.updateWeights);
		resetWeights = new FMatrixGene(gene.resetWeights);
		active = new BMatrixGene(gene.active);
		this.interactionType = gene.interactionType;
	}
	
	public RNNInteractionGene() {
		
	}
	
	public void mutate() {
		float mutationAmount = Settings.getCurrent().brainMutationRate.val;
		float mutationProb = Settings.getCurrent().brainMutationProb.val;
		weights.mutate(mutationProb, mutationAmount);
		updateWeights.mutate(mutationProb, mutationAmount);
		resetWeights.mutate(mutationProb, mutationAmount);
		active.mutate(Settings.getCurrent().brainActiveMutationProb.val);
	}
	
	public RNNInteractionGene(RNNInteractionGene parent1, RNNInteractionGene parent2) {
		this.interactionType = parent1.interactionType;
		this.hiddenSize = parent1.hiddenSize;
		this.nNeurons = parent1.nNeurons;
		this.type = parent1.type;
		this.active = new BMatrixGene(parent1.active, parent2.active, 2f/(float)parent1.active.h);
		this.weights = new FMatrixGene(parent1.weights, parent2.weights, 2f/(float)parent1.weights.h);
		this.updateWeights = new FMatrixGene(parent1.updateWeights, parent2.updateWeights, 2f/(float)parent1.updateWeights.h);
		this.resetWeights = new FMatrixGene(parent1.resetWeights, parent2.resetWeights, 2f/(float)parent1.resetWeights.h);
	}
	
	public static int getNumNeurons(int interactionType, int type) {
		return interactionType == SENSOR ? Sensor.getNumNeurons(type) : Actuator.getNumNeurons(type);
	}
	
	public static int getMaxNumber(int interactionType, int type) {
		return interactionType == SENSOR ? Sensor.getMaxNumber(type) : Actuator.getMaxNumber(type);
	}

	public static int getNumberOfTypes(int interactionType) {
		return interactionType == SENSOR ? Sensor.NUMBER_OF_TYPES : Actuator.NUMBER_OF_TYPES;
	}
}

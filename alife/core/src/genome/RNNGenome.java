package genome;

import com.mygdx.game.Settings;

public class RNNGenome {
	
	//connections from hidden to (hidden)
	public BMatrixGene connectionsHidden;
	
	//activity vector for hidden neurons
	public BVectorGene active;
	public FVectorGene bias;
	
	//synaptic efficacy for connections from hidden to (hidden)         	(weight matrix)
	public FMatrixGene weights;
	public FMatrixGene updateWeights;			
	public FMatrixGene resetWeights;
	
	public static float MIN_EFFICACY = -10;
	public static float MAX_EFFICACY = 10;
	public static float DEVIATION = 6;
	
	public static float MIN_BIAS = -2;
	public static float MAX_BIAS = 2;
	public static float BIAS_DISTR = 1;
	
	//use for initial weight matrices
	public static float AVERAGE_EFFICACY = .1f;
	
	public RNNGenome() {
		
	}
	
	public RNNGenome(boolean init) {
		if(!init)
			return;
		int maxHiddenSize = Settings.getCurrent().maxHiddenSize.val;
		
		connectionsHidden = new BMatrixGene(maxHiddenSize, maxHiddenSize);
		
		active = new BVectorGene(maxHiddenSize);
		
		weights = new FMatrixGene(maxHiddenSize, maxHiddenSize, MIN_EFFICACY, MAX_EFFICACY);
		updateWeights = new FMatrixGene(maxHiddenSize, maxHiddenSize, MIN_EFFICACY, MAX_EFFICACY);
		resetWeights = new FMatrixGene(maxHiddenSize, maxHiddenSize, MIN_EFFICACY, MAX_EFFICACY);
		bias = new FVectorGene(maxHiddenSize, MIN_BIAS, MAX_BIAS);
		
		//initialize with random values
		connectionsHidden.setRandomValues(Settings.getCurrent().hiddenConnectivity.val);
		bias.setRandomValues(0, BIAS_DISTR);
		active.setRandomValues(Settings.getCurrent().brainSize.val);
		
		weights.setRandomValues(AVERAGE_EFFICACY, DEVIATION);
		updateWeights.setRandomValues(AVERAGE_EFFICACY, DEVIATION);
		resetWeights.setRandomValues(AVERAGE_EFFICACY, DEVIATION);
	}
	
	public RNNGenome(RNNGenome brainGenome) {
		connectionsHidden = new BMatrixGene(brainGenome.connectionsHidden);
		
		active = new BVectorGene(brainGenome.active);
		
		weights = new FMatrixGene(brainGenome.weights);
		updateWeights = new FMatrixGene(brainGenome.updateWeights);
		resetWeights = new FMatrixGene(brainGenome.resetWeights);
		bias = new FVectorGene(brainGenome.bias);
	}

	public RNNGenome(RNNGenome parent1, RNNGenome parent2) {
		connectionsHidden = new BMatrixGene(parent1.connectionsHidden, parent2.connectionsHidden, 1f/parent1.connectionsHidden.w);
		
		active = new BVectorGene(parent1.active, parent2.active, 1f/parent1.active.size);
		bias = new FVectorGene(parent1.bias, parent2.bias, 1f/parent1.bias.size);
		
		weights = new FMatrixGene(parent1.weights, parent2.weights, 1f/parent1.weights.w);
		updateWeights = new FMatrixGene(parent1.updateWeights, parent2.updateWeights, 1f/parent1.updateWeights.w);
		resetWeights = new FMatrixGene(parent1.resetWeights, parent2.resetWeights, 1f/parent1.resetWeights.w);
	}

	public void mutate(float mutationProb, float mutationRate) {
		float activationMutationProb = GenePool.brainActiveMutationProb;
		connectionsHidden.mutate(activationMutationProb);
		active.mutate(activationMutationProb);
		bias.mutate(mutationProb, mutationRate);
		weights.mutate(mutationProb, mutationRate);
		updateWeights.mutate(mutationProb, mutationRate);
		resetWeights.mutate(mutationProb, mutationRate);
		connectionsHidden.mutate(activationMutationProb);
	}

	public FVectorGene getBias() {
		if(bias == null) {
			bias = new FVectorGene(Settings.getCurrent().maxHiddenSize.val, MIN_BIAS, MAX_BIAS);
			System.out.println("bias was null, creating new bias");
		}
		return bias;
	}
}

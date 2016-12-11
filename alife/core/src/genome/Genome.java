package genome;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.Settings;

import creature.BodyPart;

public class Genome {
	
	//all nodes, nodes are linked to eachother using indices of this array
	public Node[] nodes;
	public int numNodes;
	
	public int maxRecursion;
	
	public RNNGenome brainGenome;		//just the RNN type for now. TODO: Extend with brain groups when evolution is in place
	
	public int maxAlive = 60*60;
	public int generation;
	public String timeOriginated;
	
	public float r;
	public float g;
	public float b;
	
	public float foodType;		//0 = pure herbivore, 1 = pure carnivore
	
	public float mateProb = .55f;
	
	public RNNInteractionGene[][] sensors;
	public RNNInteractionGene[][] actuators;
	
	public long version = 1;
	
	public Genome() {
		
	}
	
	/**
	 * generate random genome
	 * @param time
	 */
	public Genome(String time) {
		generation = 0;
		timeOriginated = time;
		brainGenome = new RNNGenome(true);
		//generate body
		loadValuesFromGenePool();
		
		int nodeMaxConnections = GenePool.nodeMaxConnections;
		 
		float sizeRange = .5f;
		float activeProb = .3f;
		float mirrorProb = .5f;
		
		for(int i = 0; i < numNodes; i++) {
			Node n = new Node(nodeMaxConnections);
			//node properties
			n.size = 1-MathUtils.random()*sizeRange;		//size is the radius
			n.length = MathUtils.random();
			n.type = i!=0 ? BodyPart.getRandomType(.7f) : BodyPart.LIMB;
			n.hasTouchSensor = true;
			
			for(int j = 0; j < nodeMaxConnections; j++) {
				n.active[j] = MathUtils.random() < activeProb;
				n.connections[j] = 1+MathUtils.random(numNodes-2);
				n.angles[j] = MathUtils.randomTriangular()*MathUtils.PI;
				n.isMirrored[j] = MathUtils.random() < mirrorProb;
			}
			nodes[i] = n;
		}
		nodes[0].isRootNode = true;
		nodes[0].size = 1;
		
		//color
		r = MathUtils.random();
		g = MathUtils.random();
		b = MathUtils.random();
		
		foodType = 0;
		
		//make list with genes for sensor and actuators  
		//sensors
		sensors = generateRandom(RNNInteractionGene.SENSOR);
		actuators = generateRandom(RNNInteractionGene.ACTUATOR);
	}
	
	public RNNInteractionGene[][] generateRandom(int interactionType) {
		int numberOfTypes = RNNInteractionGene.getNumberOfTypes(interactionType);
		RNNInteractionGene[][] interactors = new RNNInteractionGene[numberOfTypes][];
		for(int i = 0; i < numberOfTypes; i++) {
			int maxNumber = RNNInteractionGene.getMaxNumber(interactionType, i);
			RNNInteractionGene[] interactorOfType = new RNNInteractionGene[maxNumber];
			for(int j = 0; j < maxNumber; j++) {
				interactorOfType[j] = new RNNInteractionGene(interactionType, i, Settings.getCurrent().maxHiddenSize.val);
			}
			interactors[i] = interactorOfType;
		}
		return interactors;
	}

	public Genome(Genome genome) {
		this.timeOriginated = genome.timeOriginated;
		this.generation = genome.generation+1;
		this.brainGenome = new RNNGenome(genome.brainGenome);
		this.sensors = copy(genome.sensors);
		this.actuators = copy(genome.actuators);
		loadValuesFromGenePool();
		this.mateProb = genome.mateProb;
		
		for(int i = 0; i < genome.nodes.length; i++) {
			nodes[i] = new Node(genome.nodes[i]);
		}
		this.numNodes = genome.numNodes;
		this.maxRecursion = genome.maxRecursion;
		this.r = genome.r;
		this.g= genome.g;
		this.b = genome.b;
		this.foodType = genome.foodType;
	}
	
	public RNNInteractionGene[][] copy(RNNInteractionGene[][] gene) {
		RNNInteractionGene[][] copy = new RNNInteractionGene[gene.length][];
		for(int i = 0; i < gene.length; i++) {
			copy[i] = new RNNInteractionGene[gene[i].length];
			for(int j = 0; j < gene[i].length; j++) {
				copy[i][j] = gene[i][j];
			}
		}
		return copy;
	}
	
	public Genome(Genome parent1, Genome parent2) {
		this.brainGenome = new RNNGenome(parent1.brainGenome, parent2.brainGenome);
		this.foodType = (parent1.foodType+parent2.foodType)/2f;
		this.r = (parent1.r+parent2.r)/2f;
		this.g = (parent1.g+parent2.g)/2f;
		this.b = (parent1.b+parent2.b)/2f;
		this.mateProb = (parent1.mateProb+parent2.mateProb)/2;
		this.timeOriginated = "confusing";
		
		this.maxRecursion = parent1.maxRecursion;
		this.numNodes = parent1.numNodes;
		this.generation = Math.max(parent1.generation, parent2.generation)+1;
		loadValuesFromGenePool();
	
		float switchProb = 1f/(float)parent1.nodes.length;
		boolean inParent1 = true;
		for(int i = 0; i < parent1.nodes.length; i++) {
			if(MathUtils.random() < switchProb) 
				inParent1=!inParent1;
			nodes[i] = new Node(inParent1? parent1.nodes[i] : parent2.nodes[i]);
		}
		this.sensors = crossover(parent1.sensors, parent2.sensors);
		this.actuators = crossover(parent1.actuators, parent2.actuators);
	}
	
	public RNNInteractionGene[][] crossover(RNNInteractionGene[][] parent1, RNNInteractionGene[][] parent2) {
		RNNInteractionGene[][] gene = new RNNInteractionGene[parent1.length][];
		for(int i = 0; i < parent1.length; i++) {
			gene[i] = new RNNInteractionGene[parent1[i].length];
			for(int j = 0; j < parent1[i].length; j++) {
				gene[i][j] = new RNNInteractionGene(parent1[i][j], parent2[i][j]);
			}
		}
		return gene;
	}
	
	public void mutate() {
		brainGenome.mutate(GenePool.brainMutationProb, GenePool.brainMutationRate);
		for(int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			node.mutate();
		}
		
		//mutate color
		r = mutateFloat(r, GenePool.nodeMutationProb*5, 0, 1);
		g = mutateFloat(g, GenePool.nodeMutationProb*5, 0, 1);
		b = mutateFloat(b, GenePool.nodeMutationProb*5, 0, 1);
		
		foodType = mutateFloat(foodType, .025f, 0, 1);
		mateProb = mutateFloat(mateProb, .07f, 0, 1);
		mutate(sensors);
		mutate(actuators);
	}
	
	public void mutate(RNNInteractionGene[][] gene) {
		for(int i = 0; i < gene.length; i++) {
			RNNInteractionGene[] s = gene[i];
			for(int j = 0; j < s.length; j++) {
				s[j].mutate();
			}
		}
	}
	
	public float mutateFloat(float val, float rate, float min, float max) {
		float prob = .1f;
		
		if(MathUtils.random() < prob) {
			float nv = val+MathUtils.random(-rate, rate);
			if(nv > max) 
				nv=2*max-nv;
			if(nv < min) 
				nv=2*min-nv;
			return nv;
		} else {
			return val;
		}
	}
	
	public void loadValuesFromGenePool() {
		maxRecursion = GenePool.bodyRecursionLimit;
		numNodes = GenePool.bodyMaxNodes;
		nodes = new Node[numNodes];
	}
}

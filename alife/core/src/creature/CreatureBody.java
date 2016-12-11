package creature;

import java.util.HashMap;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Settings;

import brain.RNN;
import brain.SpikeRateNeuron;
import brain.Synapse;
import genome.FVectorGene;
import genome.GenePool;
import genome.Genome;
import genome.Node;
import genome.PlantNode;
import genome.RNNGenome;
import genome.RNNInteractionGene;
import physics.Circle;
import physics.Connection;
import physics.Group;

public class CreatureBody {
	
	public static final int REPRODUCTION_COST = 800;
	public Genome genome;
	public HashMap<Circle, BodyPart> bodyMap = new HashMap<Circle, BodyPart>();
	public Array<BodyPart> bodyParts = new Array<BodyPart>();
	
	//sensors
	public Array<Sensor> sensors = new Array<Sensor>();
	public HashMap<Integer, Array<Sensor>> sensorTypes = new HashMap<Integer, Array<Sensor>>();
	
	//actuators
	public Array<Actuator> actuators = new Array<Actuator>();
	public HashMap<Integer, Array<Actuator>> actuatorTypes = new HashMap<Integer, Array<Actuator>>();
	
	//physics group
	public Group group;
	
	//brain
	public RNN brain;
	
	private int health;
	public int metabolism;
	public int ticksAlive;
	public boolean dead;
	
	public int numSynapses;
	public int numNeurons;
	public int numInputNeurons;
	public int numHiddenNeurons;
	public int numOutputNeurons;
	
	public int energy;
	public int startHealth = 4000;
	public int totalEnergyCollected;
	
	public float fitness;
	public float eatPlantFactor;
	public float eatCorpseFactor;
	
	public boolean mating;
	public CreatureBody matingTarget;
	public boolean isTarget;
	public Array<PlantNode> seeds = new Array<PlantNode>();
		
	public CreatureBody(Genome genome) {
		this.genome = genome;
		
		this.group = new Group();
		
		//build body
		Node rootNode = genome.nodes[0];
		
		int maxRecursion = genome.maxRecursion;
		
		//default sensors/actuators
		for(int i = 0; i < Sensor.NUMBER_OF_TYPES; i++) {
			sensorTypes.put(i, new Array<Sensor>());
			if(Sensor.shouldAlwaysAdd(i)) {
				addSensor(i, null);
			}
		}
		for(int i = 0; i < Actuator.NUMBER_OF_TYPES; i++) {
			actuatorTypes.put(i, new Array<Actuator>());
			if(Actuator.shouldAlwaysAdd(i)) {
				addActuator(i, null);
			}
		}
		
		buildBody(rootNode, null, 0, false, maxRecursion);
		buildBrain();
		numInputNeurons = brain.inputSize;
		numHiddenNeurons = brain.size-brain.outputSize-brain.inputSize;
		numOutputNeurons = brain.outputSize;
		numNeurons = brain.size;
		numSynapses = brain.getSynapseNum();
		
		//metabolism = .0005f*(numNeurons*5+numSynapses) + visionSensors.size*.005f + touchSensors.size*.005f + .2f;
		float visionSensors = sensorTypes.get(Sensor.VISION).size;
		float touchSensors = sensorTypes.get(Sensor.TOUCH).size;
		metabolism = 1 + (int)(visionSensors*.04f +touchSensors*.02f + numNeurons*.003f + numSynapses*.001f);
		health = startHealth;
		
		eatPlantFactor = (1-genome.foodType)*GenePool.eatPlantFactor;
		eatCorpseFactor = genome.foodType*GenePool.eatCorpseFactor;
	}
	
	//CLEANED UPD!@J!!!!!
	private void buildBody(Node node, BodyPart connectedTo, float angle, boolean isMirrored, int recursionLevel) {
		if(recursionLevel==0) return;
		
		float size = connectedTo==null ? node.size : node.size*connectedTo.circle.r;
		size = Math.max(size, .2f);
		
		//set to false to end recursion
		//for instance: spike eye or mouth, cannot be extended
		boolean extendable = true;
		
		Circle circle = new Circle(0, 0, size, node.isRootNode);
		BodyPart part;
		if(node.isRootNode) {
			part = new BodyPart(node.type, circle);
		} else {
			Connection connection = new Connection(connectedTo.circle.particle, circle.particle, 
					node.length*size+connectedTo.circle.r, angle);
			part = new BodyPart(node.type, circle, connection); 
			if(connectedTo.connection!=null) 
				connectedTo.connection.connections.add(connection);
			else
				group.addConnection(connection);
		}
		//add touch sensor to every bodypart
		addSensor(Sensor.TOUCH, part);
		if(node.type==BodyPart.EYE) {
			if(!addSensor(Sensor.VISION, part)) 
				part.type = BodyPart.LIMB;
		} else if(node.type==BodyPart.MOUTH) {
			if(!addActuator(Actuator.EAT, part)) 
				part.type = BodyPart.LIMB;
		} else if(node.type==BodyPart.SPIKE) {
			if(!addActuator(Actuator.SPIKE, part))
				part.type = BodyPart.LIMB;
		}
		extendable = BodyPart.isExtendable(part.type);
		
		addBodyPart(part);
		
		if(!extendable)
			return;
		int connectionNum = node.connections.length;
		for(int i = 0; i < connectionNum; i++) {
			if(node.active[i]) {
				Node next = genome.nodes[node.connections[i]];
				boolean m = node.isMirrored[i] ? !isMirrored : isMirrored;
				buildBody(next, part, node.angles[i]*(isMirrored ? -1 : 1), m, recursionLevel-1);
			}
		}
	}
	
	private void buildBrain() {
		
		//calculate inputSize
		int inputSize = 0;
		for(Sensor sensor : sensors) {
			//give sensor index from which to write input into brain
			sensor.startIdx = inputSize;
			inputSize+=sensor.numNeurons;
		}
		
		//make array with all possible neurons
		int maxHiddenSize = Settings.getCurrent().maxHiddenSize.val;
		SpikeRateNeuron[] hiddenNeurons = new SpikeRateNeuron[maxHiddenSize];
		RNNGenome brainGenome = this.genome.brainGenome;
		
		//first make hidden layer
		int idxCounter = inputSize;
		FVectorGene bias = brainGenome.getBias();
		
		for(int i = 0; i < maxHiddenSize; i++) {
			if(brainGenome.active.vector[i]) {
				hiddenNeurons[i] = new SpikeRateNeuron(idxCounter, bias.vector[i]);
				idxCounter++;
			}
		}
		//connect neurons according to genome from i to j read as from->to
		for(int i = 0; i < maxHiddenSize; i++) {
			
			if(hiddenNeurons[i]!=null)
			for(int j = 0; j < maxHiddenSize; j++) {
				if(j==i)
					continue;
				if(hiddenNeurons[j]==null)
					continue;
				if(brainGenome.connectionsHidden.matrix[i][j]) {
					//make synapse
					Synapse syn = new Synapse();
					syn.weight = brainGenome.weights.matrix[i][j];
					syn.reset = brainGenome.resetWeights.matrix[i][j];
					syn.update = brainGenome.updateWeights.matrix[i][j];
					syn.from = hiddenNeurons[i].index;
					hiddenNeurons[j].synapses.add(syn);
				}
			}
		}
		
		//connect all sensors with information from genome
		Array<SpikeRateNeuron> inputNeurons = new Array<SpikeRateNeuron>();
		Array<SpikeRateNeuron> outputNeurons = new Array<SpikeRateNeuron>();
		//input
		int inputIdxCounter = 0;
		for(int i = 0; i < sensors.size; i++) {
			
			Sensor sensor = sensors.get(i);
			//get corresponding sensor gene data
			RNNInteractionGene gene = genome.sensors[sensor.type][sensor.id];
			for(int j = 0; j < gene.nNeurons; j++) {
				
				SpikeRateNeuron neuron = new SpikeRateNeuron(inputIdxCounter++);
				for (int k = 0; k < maxHiddenSize; k++) {
					
					if(gene.active.matrix[j][k] && hiddenNeurons[k]!=null) {
						Synapse syn = new Synapse();
						syn.weight = gene.weights.matrix[j][k];
						syn.update = gene.updateWeights.matrix[j][k];
						syn.reset = gene.resetWeights.matrix[j][k];
						syn.from = neuron.index;
						hiddenNeurons[k].synapses.add(syn);
					}
				}
				inputNeurons.add(neuron);
			}
		}
		int outputStart = idxCounter;
		int outputIdxCounter = 0;
		for(int i = 0; i < actuators.size; i++) {
			
			Actuator actuator = actuators.get(i);
			actuator.startIdx = outputIdxCounter;
			RNNInteractionGene gene = genome.actuators[actuator.type][actuator.id];
			for(int j = 0; j < gene.nNeurons; j++) {
				SpikeRateNeuron neuron = new SpikeRateNeuron(outputIdxCounter+outputStart);
				outputIdxCounter++;
				for(int k = 0; k < maxHiddenSize; k++) {
					//other way around this time (to <- from)
					if(gene.active.matrix[j][k] && hiddenNeurons[k]!=null) {
						Synapse syn = new Synapse();
						syn.weight = gene.weights.matrix[j][k]; //so from k to j
						syn.reset = gene.resetWeights.matrix[j][k];
						syn.update = gene.updateWeights.matrix[j][k];
						syn.from = hiddenNeurons[k].index;
						neuron.synapses.add(syn);
					}
				}
				outputNeurons.add(neuron);
			}
		}
		Array<SpikeRateNeuron> neurons = new Array<SpikeRateNeuron>();
		neurons.addAll(inputNeurons);
		for(int i = 0; i < maxHiddenSize; i++) {
			if(hiddenNeurons[i]!=null)
				neurons.add(hiddenNeurons[i]);
		}
		neurons.addAll(outputNeurons);
		//make brain!
		brain = new RNN(inputSize, outputIdxCounter, neurons);
	}
	
	
	public boolean addSensor(int type, BodyPart part) {
		int maxNum = Sensor.getMaxNumber(type);
		if(sensorTypes.get(type).size < maxNum) {
			Sensor sensor;
			if(type==Sensor.VISION) 
				sensor = new VisionSensor();
			 else
				sensor = new Sensor(type);
			sensor.bodyPart = part;
			sensor.id = getSensorNum(type);
			
			sensors.add(sensor);
			if(part!=null)
				part.sensorAdded(sensor);
			sensorTypes.get(type).add(sensor);
			return true;
		} else
			return false;
	}
	
	public boolean addActuator(int type, BodyPart part) {
		int maxNum = Actuator.getMaxNumber(type);
		if(actuatorTypes.get(type).size < maxNum) {
			Actuator actuator = new Actuator(type);
			actuator.bodyPart = part;
			actuators.add(actuator);
			actuator.id = getActuatorNum(type);
			
			actuatorTypes.get(type).add(actuator);
			if(part!=null)
				part.actuatorAdded(actuator);
			return true;
		} else
			return false;
	}
	
	public float getFitness() {
		float brain = Settings.getCurrent().brainSizeAttraction.val;
		float spikes = Settings.getCurrent().spikesAttraction.val;
		float bodySize = Settings.getCurrent().bodySizeAttraction.val;
		float eyes = Settings.getCurrent().eyesAttraction.val;
		float energy = Settings.getCurrent().energyCollectedAttraction.val;
		return brain*this.numHiddenNeurons
				+spikes*this.getActuatorNum(Actuator.SPIKE)
				+bodySize*this.getSensorNum(Sensor.TOUCH)
				+eyes*this.getSensorNum(Sensor.VISION)
				+energy*this.totalEnergyCollected;
	}
	
	public PlantNode dropSeed() {
		if(seeds.size>0)
			return seeds.pop();
		else
			return null;
	}
	
	public int getSensorNum(int type) {
		return sensorTypes.get(type).size;
	}
	
	public int getActuatorNum(int type) {
		return actuatorTypes.get(type).size;
	}
	
	public Array<Actuator> getActuators(int type) {
		return actuatorTypes.get(type);
	}
	
	public Array<Sensor> getSensors(int type) {
		return sensorTypes.get(type);
	}
	
	public void die() {
		dead = true;
	}
	
	public boolean isDead() {
		return health < 0 || ticksAlive > genome.maxAlive ||dead==true;
	}
	
	public float getHealth() {
		return health;
	}
	
	public void addSensor(Sensor sensor) {
		sensors.add(sensor);
	}
	
	public void addActuator(Actuator actuator) {
		actuators.add(actuator);
	}
	
	public void addBodyPart(BodyPart bodyPart) {
		group.addCircle(bodyPart.circle);
		bodyParts.add(bodyPart);
		bodyMap.put(bodyPart.circle, bodyPart);
	}
	
	public BodyPart getBodyPartWith(Circle circle) {
		return bodyMap.get(circle);
	}
	
	public void addEnergy(int energy) {
		this.energy+=energy;
		totalEnergyCollected+=energy;
		health+=energy;
		fitness+=energy;
	}
	
	public void hurt(int hurt) {
		health-=hurt;
		fitness-=hurt;
	}
	
	public void eat(int energy) {
		health+=energy;
		this.energy+=energy;
	}
	
	public String getInfo() {
		return "creature";
	}

	public boolean canReproduce() {
		return energy >= REPRODUCTION_COST*2;
	}

	public void work(int work) {
		this.health-=work;
	}

	public void mate() {
		this.energy-=REPRODUCTION_COST/2;
	}
}

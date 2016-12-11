package creature;

public class Sensor {
	
	public float[] input;		//input neuron firing probabilities 
	public int numNeurons;
	public int startIdx;		//starting index for input neuron to brain
	
	public int type;
	public int maxNum;
	public int id;
	public BodyPart bodyPart;
	
	public static final int HEALTH = 0;
	public static final int TOUCH = 1;
	public static final int VISION = 2;
	public static final int MATE = 3;
	public static final int RANDOM = 4;
	public static final int NUMBER_OF_TYPES = 5;
	
	public static final int[] nNeurons = new int[]{1,1,9,1,1};
	public static final int[] maxNumber = new int[]{1,27,4,1,1};
	public static boolean[] alwaysAdd = new boolean[]{true, false, false, true, true};
	
	/**
	 * @param inputSize inputsize for only this sensor
	 */
	public Sensor(int type) {
		this.type = type;
		this.numNeurons = getNumNeurons(type);
		this.input = new float[numNeurons];
	}
	
	/**
	 * set the input vector to the correct values
	 */
	public void update() {
		
	}

	public static int getNumNeurons(int type) {
		return nNeurons[type];
	}
	
	public static int getMaxNumber(int type) {
		return maxNumber[type];
	}
	
	public static boolean shouldAlwaysAdd(int type) {
		return alwaysAdd[type];
	}
}

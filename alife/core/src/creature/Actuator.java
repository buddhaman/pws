package creature;

public class Actuator {
	
	public int numNeurons;
	public int startIdx;
	public float[] output;
	
	public int type;
	public int maxNum;
	public BodyPart bodyPart;
	
	//how manieth sensor of this type it is
	public int id;
	
	public static final int MOVE = 0;
	public static final int EAT = 1;
	public static final int SPIKE = 2;
	public static final int NUMBER_OF_TYPES = 3;
	
	public static final int[] nNeurons = new int[]{3,1,1};
	public static final int[] maxNumber = new int[]{1,4,4};
	public static final boolean[] alwaysAdd = new boolean[]{true, false, false};
	
	public Actuator(int type) {
		this.type = type;
		this.numNeurons = getNumNeurons(type);
		output = new float[numNeurons];
	}

	public void update(float[] output) {
		for(int i = startIdx; i < startIdx+numNeurons; i++) {
			this.output[i-startIdx] = output[i];
		}
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

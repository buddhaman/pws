package brain;

import com.badlogic.gdx.utils.Array;

public class SpikeRateNeuron {
	public float state;
	public float prevState;
	public float reset;
	public float bias;
	
	public Array<Synapse> synapses = new Array<Synapse>();
	
	public int index;
	
	public SpikeRateNeuron(int index) {
		this.index = index;
	}
	
	public SpikeRateNeuron() {
		
	}

	public SpikeRateNeuron(int index, float bias) {
		this.index = index;
		this.bias = bias;
	}

	public static float sigmoid(float z) {
		return (float)(1/(1+Math.exp(-z)));
	}
}
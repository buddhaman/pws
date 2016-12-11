package brain;

import genome.FVectorGene;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class RNN {
	
	public int size;		//total brain size in neurons
	public int inputSize;
	public int outputSize;
	
	public Array<SpikeRateNeuron> neurons = new Array<SpikeRateNeuron>();
	
	
	/**
	 * this brain is explicitly constructed 
	 * @param spikeRateNeurons list of all brain neurons
	 */
	public RNN(int inputSize, int outputSize, Array<SpikeRateNeuron> spikeRateNeurons) {
		this.neurons = spikeRateNeurons;
		this.size = spikeRateNeurons.size;
		this.inputSize = inputSize;
		this.outputSize = outputSize;
	}
	
	public float[] update(float[] input) {
		//set input neurons to input value
		float[] newReset = new float[size];
		
		for(int i = 0; i < inputSize; i++) {
			neurons.get(i).prevState = MathUtils.clamp(input[i], 0, 1);
			newReset[i]=1;
		}
		
		for(int i = inputSize; i < size; i++) {
			SpikeRateNeuron n = neurons.get(i);
			n.prevState = n.state;
		}
		
		//update neurons
		
		for(int i = inputSize; i < size; i++) {
			SpikeRateNeuron neuron = neurons.get(i);
			
			float z = 0;
			float r = 0;
			float h = 0;
			
			for(Synapse syn : neuron.synapses) {
				float smin = getPrevState(syn.from);
				z+=syn.update*smin;
				r+=syn.reset*smin;
			}
			z = SpikeRateNeuron.sigmoid(z);
			r = SpikeRateNeuron.sigmoid(r);
			for(Synapse syn : neuron.synapses) {
				SpikeRateNeuron n = neurons.get(syn.from);
				h+=n.prevState*n.reset*syn.weight;
			}
			h = (float)Math.tanh(h+neuron.bias);
			
			neuron.state = (1-z)*h+z*neuron.prevState;
			newReset[i]=r;
		}
		
		for(int i = 0; i < size; i++) {
			neurons.get(i).reset = newReset[i];
		}
		
		float[] output = new float[outputSize];
		
		for(int i = size-outputSize; i < size; i++) {
			output[i-(size-outputSize)] = .5f+.5f*neurons.get(i).state;
		}
		
		return output;
	}
	
	public float getPrevState(int idx) {
		return neurons.get(idx).prevState;
	}
	
	public int getNeuronNum() {
		return neurons.size;
	}
	
	public static void setValues(float[] from, float[] to) {
		int length = from.length;
		for(int i = 0; i < length; i++) 
			to[i] = from[i];
	}
	
	public static void shiftCopy(float[][] array) {
		for(int i = array.length-1; i > 0; i--) {
			setValues(array[i-1], array[i]);
		}
	}
	
	public void printArray(float[] array) {
		StringBuilder sb = new StringBuilder();
		for(int i =0; i < array.length; i++) {
			sb.append(array[i]).append(" ");
		}
		System.out.println(sb);
	}

	public int getSynapseNum() {
		int synNum = 0;
		for(SpikeRateNeuron neuron : neurons) {
			synNum+=neuron.synapses.size;
		}
		return synNum;
	}
}

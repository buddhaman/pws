package renderer;

import genome.RNNGenome;
import brain.RNN;
import brain.SpikeRateNeuron;
import brain.Synapse;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import creature.CreatureBody;


public class BrainRenderer extends Renderer {
	
	private AtlasRegion circleRegion;
	private AtlasRegion blankRegion;
	
	public float width = 400;
	public float height = 400;
	private float screenX;
	private float screenY;
	
	public float inputXOffset = 20;
	public float inputYOffset = 30;
	public float r;
	
	private RNN brain;
	
	public BrainRenderer(Resources res) {
		super(res);
		circleRegion = res.findRegion("circle");
		blankRegion = res.findRegion("blankRegion");
	}
	
	public void render(CreatureBody creature, int screenWidth, int screenHeight) {
		float w = width;
		float h = height;
		screenX = screenWidth-w;
		screenY = screenHeight-h;
		
		batch.setColor(Color.WHITE);
		brain = creature.brain;
		
		//hidden neurons
		int hiddenSize = brain.size-brain.inputSize-brain.outputSize;
		float dAngle = MathUtils.PI2/(float)hiddenSize;
		float neuronR = 10;
		r = w/2-neuronR-25;
		float cx = screenX+w/2f;
		float cy = screenY+h/2f-10;
		
		int outputStart = brain.size-brain.outputSize;
		
		for(int i = brain.inputSize; i < brain.size; i++) {
			SpikeRateNeuron neuron = brain.neurons.get(i);
			float nx1 = i < outputStart ? cx+MathUtils.cos((i-brain.inputSize)*dAngle)*r : getOutputX(i-outputStart);
			float ny1 = i < outputStart ? cy+MathUtils.sin((i-brain.inputSize)*dAngle)*r : getOutputY(i-outputStart);
			for(Synapse syn : neuron.synapses) {
				float nx2 = 0;
				float ny2 = 0;
				if(syn.from < brain.inputSize) {
					int j = syn.from;
					nx2 = getInputX(j); 
					ny2 = getInputY(j);
				} else if(syn.from < brain.size-brain.outputSize) {
					int j = syn.from-brain.inputSize;
					nx2 = cx+MathUtils.cos(j*dAngle)*r;
					ny2 = cy+MathUtils.sin(j*dAngle)*r;
				} else {
					System.out.println(syn.from);
				}
				float absEfficacy = Math.abs(syn.weight)/RNNGenome.MAX_EFFICACY;
				float lw = absEfficacy+.5f;
				batch.setColor(syn.weight < 0 ? Color.RED : Color.GREEN);
				utils.drawLine(nx1, ny1, nx2, ny2, lw);
			}
		}
		
		for(int i = 0; i < hiddenSize; i++) {
			SpikeRateNeuron neuron = brain.neurons.get(brain.inputSize+i);
			float nx = cx+MathUtils.cos(i*dAngle)*r;
			float ny = cy+MathUtils.sin(i*dAngle)*r;
			float v = neuron.state;
			if(v>=0)
				batch.setColor(v, v, v, 1);
			else {
				batch.setColor(-v, 0, 0, 1);
			}
			batch.draw(circleRegion, nx-neuronR, ny-neuronR, neuronR*2, neuronR*2);
		}
		
		//draw input neurons
		float subWidth = w/(brain.inputSize-1);
		for(int i = 0; i < brain.inputSize; i++) {
			float v = brain.neurons.get(i).prevState;
			batch.setColor(v,v,v,1);
			batch.draw(circleRegion, screenX+subWidth*i-neuronR-inputXOffset, screenY-neuronR-inputYOffset, neuronR*2, neuronR*2);
		}
		

		for(int i = 0; i < brain.outputSize; i++) {
			float v = brain.neurons.get(i+brain.size-brain.outputSize).state;
			batch.setColor(v, v, v, 1);
			batch.draw(circleRegion, getOutputX(i)-neuronR, getOutputY(i)-neuronR, neuronR*2, neuronR*2);
		}
	}
	
	private float getInputX(int neuron) {
		return screenX+(width/(brain.inputSize-1))*neuron-inputXOffset;
	}
	
	private float getInputY(int neuron) {
		return screenY-inputYOffset;
	}
	
	private float getOutputX(int neuron) {
		return screenX+(width/(brain.outputSize-1))*neuron-inputXOffset;
	}
	
	private float getOutputY(int neuron) {
		return screenY+r*2+49;
	}

	public boolean containsPoint(float x, float y) {
		return x >= screenX && y>=screenY && x <= screenX+width && y <= screenY+height;
	}
}

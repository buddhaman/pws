package genome;

import com.badlogic.gdx.math.MathUtils;

import creature.BodyPart;

public class Node {
	/**
	 * Nodes are used to describe the creature morphology. 
	 */
	
	public int[] connections;
	public float[] angles;
	public boolean[] active;
	public boolean[] isMirrored;
	
	//size is relative to previous node
	public float size;
	
	//distance of the center to the edge of the last circle
	public float length;
	
	public boolean isRootNode;
	public int type;
	
	//data if is eye	TODO:implement evolving eye data
	public int rays;
	public float fov;
	
	//sensor information
	public boolean hasTouchSensor;
	public float sensitivity;		//determines number of neurons dedicated to this nodes touch sensor
	
	
	public Node() {
		
	}
	
	/**
	 * generates random node
	 */
	public Node(int maxConnections) {
		connections = new int[maxConnections];
		angles = new float[maxConnections];
		active = new boolean[maxConnections];
		isMirrored = new boolean[maxConnections];
	}

	public Node(Node node) {
		connections = node.connections.clone();
		angles = node.angles.clone();
		active = node.active.clone();
		isMirrored = node.isMirrored.clone();
		
		size = node.size;
		length = node.length;
		isRootNode = node.isRootNode;
		type = node.type;
		rays = node.rays;
		fov = node.fov;
		hasTouchSensor = node.hasTouchSensor;
		sensitivity = node.sensitivity;
	}
	
	/**
	 *  sexual reproduction node
	 * @param n1
	 * @param n2
	 */

	public void mutate() {
		float mutationProb = .15f;
		float activeMutationProb = GenePool.nodeMutationProb;
		float mutationRate = GenePool.nodeMutationRate;
		//mutate connections
		float connectionMutationProb = GenePool.nodeMutationProb*.15f;
		for(int i = 0; i < connections.length; i++) {
			if(MathUtils.random() < connectionMutationProb) {
				connections[i] = MathUtils.random(GenePool.bodyMaxNodes-1);
			}
		}
		mutateFloatArray(angles, -3.1415f, 3.1415f, mutationRate, mutationProb);
		mutateBooleanArray(active, activeMutationProb);
		mutateBooleanArray(isMirrored, activeMutationProb);
		
		if(!isRootNode) {
			size = mutateFloat(size, mutationProb, mutationRate, .5f, 1f);
			length = mutateFloat(length, mutationProb, mutationRate, .5f, 1f);
			//mutate type
			if(MathUtils.random() < connectionMutationProb) {
				type = MathUtils.random(BodyPart.SPIKE);
			}
		}
	}
	
	public float mutateFloat(float g, float mutationProb, float mutationRate, float min, float max) {
		if(MathUtils.random() < mutationProb) {
			float val = g + MathUtils.random(-mutationRate, mutationRate);
			if(val > max) val = 2*max-val;		//wraparound
			if(val < min) val = 2*min-val;
			return val;
		} else
			return g;
	}
	
	public void mutateFloatArray(float[] gene, float min, float max, float mutationRate, float mutationProb) {
		for(int i = 0; i < gene.length; i++) {
			if(MathUtils.random() < mutationProb) {
				float val = gene[i] + MathUtils.random(-mutationRate, mutationRate);
				//wraparound
				if(val < min) {
					val+=(max-min);
				}
				if(val > max) {
					val-=(max-min);
				}
				gene[i] = val;
			}
		}
	}
	
	public void mutateBooleanArray(boolean[] gene, float mutationProb) {
		for(int i = 0; i < gene.length; i++) {
			if(MathUtils.random() < mutationProb)
				gene[i] = !gene[i];
		}
	}
}

package genome;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class PlantNode {
	public float r, g, b;
	public boolean hasSeed;			//cannot have leafs if it has a seed
	public float edible;	
	public Array<PlantNode> leafs = new Array<PlantNode>();
	public PlantNode base;
	
	public PlantNode(int layerFromLast, int nodesPerLayer, PlantNode base) {
		r = MathUtils.random();
		g = MathUtils.random();
		b = MathUtils.random();
		hasSeed = MathUtils.random() < 1f/7f;
		this.base = base;
		if(layerFromLast>0)
		for(int i = 0; i < nodesPerLayer; i++) {
			leafs.add(new PlantNode(layerFromLast-1, nodesPerLayer, base==null ? this : base));
		}
	}
	
	/**
	 * copy tree
	 * @param node
	 */
	public PlantNode(PlantNode node, float mutationRate) {
		this.r = node.r;
		this.g= node.g;
		this.b = node.b;
		this.edible = node.edible;
		this.hasSeed = node.hasSeed;
		this.base = node.base;
		for(int i = 0; i < node.leafs.size; i++) {
			PlantNode leaf = new PlantNode(node.leafs.get(i), mutationRate);
			if(this.base==null)
				leaf.base=this;
			this.leafs.add(leaf);
		}
		mutate(mutationRate);
	}
	
	public PlantNode() {
		
	}
	
	public void mutate(float rate) {
		r = MathUtils.clamp(r+MathUtils.random(-rate, rate), 0, 1);
		g = MathUtils.clamp(g+MathUtils.random(-rate, rate), 0, 1);
		b = MathUtils.clamp(b+MathUtils.random(-rate, rate), 0, 1);
		hasSeed = MathUtils.random()<rate ? !hasSeed : hasSeed;
		edible = MathUtils.clamp(edible+MathUtils.random(-rate,rate), 0, 1);
	}
}

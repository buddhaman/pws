package genome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import simulation.Simulation;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import creature.CreatureBody;


public class GenePool {
	/**
	 * holds gene parameters and handles a list of fittest genes
	 */
	
	public static int bodyRecursionLimit;
	public static int bodyMaxNodes;			//excluding root node
	public static int nodeMaxConnections;
	public static float nodeMutationProb;
	
	public static float brainActiveMutationProb;
	public static float brainMutationProb;
	public static float brainMutationRate;
	public static int eatPlantFactor;
	public static int eatCorpseFactor;
	public static float nodeMutationRate;
	
	//list of n best creatures
	public int amount;
	public List<CreatureBody> best = new ArrayList<CreatureBody>();
	public float worstInList;
	public float totalFitness;
	
	private cCompare creatureComparator = new cCompare();
	
	public int creatureNum;
	public int maxListSize = 80;
	
	public GenePool() {
		amount = 10;
	}
	
	public Genome getRandomGenome(Simulation simulation) {
		return new Genome(simulation.getTime());
	}
	
	public Genome getRandomBestGenome(Simulation simulation) {
		//do roulette selection
		float select = MathUtils.random()*totalFitness;
		float counter = 0;
		for(CreatureBody cb : best) {
			counter+=cb.fitness;
			if(counter > select) {
				return new Genome(cb.genome);
			}
		}
		return new Genome(simulation.getTime());
	}

	public void creatureDead(CreatureBody creature) {
		if(creature.fitness < worstInList && best.size()==amount)
			return;
		else {
			best.add(creature);
			Collections.sort(best, this.creatureComparator);
			while(best.size() > amount) best.remove(amount);
			worstInList = best.get(best.size()-1).fitness;
			totalFitness = 0;
			for(CreatureBody cb : best) {
				totalFitness+=cb.fitness;
			}
		}
	}

	public class cCompare implements Comparator<CreatureBody> {

		@Override
		public int compare(CreatureBody c1, CreatureBody c2) {
			return c1.fitness < c2.fitness ? 1 : -1;
		}
		
	}

	public void clearList() {
		worstInList = 0;
		best.clear();
	}
	
}

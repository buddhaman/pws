package system;

import java.util.Map;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;

import component.Bot;
import genome.PlantNode;
import simulation.ExperimentData;
import simulation.ExperimentListener;
import simulation.Simulation;

public class ExperimentSystem extends EntitySystem {

	private Simulation simulation;
	private ComponentMapper<Bot> botM = Mappers.botMapper;

	private Family family = Family.all(Bot.class).get();
	private Array<Bot> deadBots = new Array<Bot>();
	private Array<Bot> candidates = new Array<Bot>();
	
	public int nGeneration;
	public int nCandidates;
	
	//if set to true, experiment is done
	public boolean hasResults = false;
	
	private Array<ExperimentListener> experimentListenerList = new Array<ExperimentListener>();
	
	/*
	 * setup single experiment
	 */
	public ExperimentSystem(Simulation simulation) {
		this.simulation = simulation;
		
		//TODO: set in settings
		nGeneration = 200;
		nCandidates = 20;
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if(hasResults)
			return;
		
		//check all dead bots. Save if generation is high enough and stop simulation. else remove from list
		for(int i = deadBots.size-1; i>=0; i--) {
			Bot bot = deadBots.get(i);
			if(bot.body.genome.generation>=nGeneration) {
				candidates.add(bot);
			}
			deadBots.removeIndex(i);
		}
		
		//stop simulation and start new one or pause if this is the last one
		if(candidates.size >= nCandidates) {
			//get bot with highest fitness
			Bot best = null;
			float maxFitness = -1;
			for(int i = 0; i < candidates.size; i++) {
				Bot bot = candidates.get(i);
				float fitness = bot.body.getFitness();
				if(fitness > maxFitness) {
					maxFitness = fitness;
					best = bot;
				}
			}
			if(best!=null)
				experimentDone(best);
			else
				System.out.println("Experiment failed somehow, best bot==null");
		}
	}
	
	public void experimentDone(Bot best) {
		//get most eaten plant
		float max = -1;
		PlantNode mostEatenPlant = null;
		for(Map.Entry<PlantNode, Float> entry : best.body.plantsEaten.entrySet()) {
			if(entry.getValue() > max) {
				max = entry.getValue();
				mostEatenPlant = entry.getKey();
			}
		}
		
		hasResults = true;
		for(ExperimentListener listener : experimentListenerList) {
			listener.experimentDone(new ExperimentData(best.body.genome, mostEatenPlant));
		}
	}

	public void addExperimentListener(ExperimentListener listener) {
		this.experimentListenerList.add(listener);
	}

	public void addDeadBot(Bot bot) {
		deadBots.add(bot);
	}

}

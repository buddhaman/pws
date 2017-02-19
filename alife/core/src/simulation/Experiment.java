package simulation;

import com.mygdx.game.Settings;

public class Experiment implements ExperimentListener {
	
	public SimulationScreen screen;
	
	public int experimentsDone = 0;

	/*
	 * Experiment starts as soon as this object is created
	 */
	public Experiment(SimulationScreen screen) {
		this.screen = screen;
		startNew();
	}
	
	public void startNew() {
		screen.startSimulation(true);
		screen.simulation.setExperimentListener(this);
	}
	
	public void update(float delta) {
		//not called at the moment
	}
	
	@Override
	public void experimentDone(ExperimentData data) {
		int nExperiments = Settings.getCurrent().nExperiments.val;
		//save creature and most eaten plant genome then restart until all experiments are done
		experimentsDone++;
		System.out.println("experiment " + experimentsDone + " of " + nExperiments + " is finished");
		
		//save creature
		screen.creatureLibrary.saveExperimentData(data, String.format("test%d", experimentsDone));
		
		if(experimentsDone < nExperiments)
			startNew();
		else
			screen.simulation.experimentRunning = false;
	}
	
}

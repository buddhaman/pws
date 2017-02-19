package simulation;

import com.mygdx.game.Settings;

public class Experiment implements ExperimentListener {
	
	public SimulationScreen screen;
	
	public int experimentsDone = 0;
	public int nExperiments;
	public int nGenerations;
	public String name;

	/*
	 * Experiment starts as soon as this object is created
	 */
	public Experiment(SimulationScreen screen, String name) {
		this.screen = screen;
		
		this.name = name;
		System.out.println(String.format("starting experiment with %d runs and %d generations per run",
				Settings.getCurrent().nExperiments.val,
				Settings.getCurrent().nGenerations.val));
		//save current settings
		screen.creatureLibrary.saveSettings(Settings.getCurrent(), name+"Settings");
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
		
		//save data
		screen.creatureLibrary.saveExperimentData(data, String.format("%s_%d", name, experimentsDone));
		
		if(experimentsDone < nExperiments)
			startNew();
		else {
			screen.simulation.experimentRunning = false;
			System.out.println("experiment done");
		}
	}
	
}

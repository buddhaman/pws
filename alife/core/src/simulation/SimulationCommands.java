package simulation;

import java.util.List;

import com.badlogic.gdx.files.FileHandle;
import com.mygdx.game.Settings;

import console.CommandListener;
import console.Console;
import console.Console.Type;

public class SimulationCommands {
	
	public final SimulationScreen screen;
	
	public SimulationCommands(SimulationScreen screen) {
		this.screen = screen;
		addCommands();
	}
	
	public void addCommands() {
		
		Console.createCommand("simRestart", Type.NONE, new CommandListener(){
			public void executed() {
				System.out.println("starting new simulation ");
				screen.startSimulation(false);
			}
		});
		
		Console.createCommand("setNumExperiments", Type.INT, new CommandListener(){
			public void executed(int arg) {
				System.out.println("nExperiments is now " + arg);
				Settings.getCurrent().nExperiments.val = arg;
			}
		});
		Console.createCommand("setNumGenerations", Type.INT, new CommandListener(){
			public void executed(int arg) {
				System.out.println("nGenerations is now " + arg);
				Settings.getCurrent().nGenerations.val = arg;
			}
		});
		Console.createCommand("startExperiment", Type.STRING, new CommandListener(){
			public void executed(String arg) {
				if(arg.contains("_")) {
					System.out.println("illegal character '_'");
					return;
				}
				System.out.println(String.format("starting new experiment \"%s\"", arg));
				screen.setupExperiment(arg);
			}
		});
		Console.createCommand("loadSettings", Type.STRING, new CommandListener(){
			public void executed(String arg) {
				Settings settings = screen.creatureLibrary.loadSettings(arg);
				if(settings!=null) {
					Settings.setCurrent(settings);
					System.out.println("settings loaded");
				}
			}
		});
		Console.createCommand("loadExperiment", Type.STRING, new CommandListener(){
			public void executed(String arg) {
				ExperimentData data = screen.creatureLibrary.loadExperimentData(arg);
				if(data!=null) {
					data.plantGene.addRecursion(data.plantGene);
					screen.startSimulation(false);
					screen.simulation.addPlant(data.plantGene, 400);
					screen.simulation.addBot(data.gene, 40);
				} else {
					System.out.println("failed : data==null");
				}
			}
		});
		Console.createCommand("listExperiments", Type.NONE, new CommandListener(){
			public void executed() {
				List<String> names = screen.creatureLibrary.getExperimentNameList();
				for(int i = 0; i < names.size(); i++) {
					System.out.println(names.get(i));
				}
			}
		});
		
		Console.createCommand("writeStatistics", Type.STRING, new CommandListener(){
			public void executed(String arg) {
				screen.creatureLibrary.writeStatistics(arg);
			}
		});
	}
}

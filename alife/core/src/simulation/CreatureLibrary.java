package simulation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Settings;

import console.CSVWriter;
import creature.CreatureBody;
import genome.Genome;

public class CreatureLibrary {

	Json json;
	public static final String CREATURE_FOLDER = "savedCreatures";
	public static final String EXPERIMENT_FOLDER = "evoExperiments";
	FileHandle creatureFolder;
	FileHandle experimentFolder;
	
	private List<String> creatureNameList = new ArrayList<String>();
	private List<String> experimentNameList = new ArrayList<String>();
	private List<Genome> genomeList = new ArrayList<Genome>();
	
	public String[] interestingValues = new String[]{"numEyes", "numSpikes", "numTouchSensors", 
			"numNeurons", "numSynapses", "numInputNeurons",
			"numHiddenNeurons", "numOutputNeurons"};
	private CSVWriter csvWriter;
	
	public static boolean saved = false;
	
	public CreatureLibrary() {
		json = new Json();
		csvWriter = new CSVWriter();
		findFolder();
	}
	
	public void findFolder() {
		creatureFolder = Gdx.files.local(CREATURE_FOLDER);
		if(!creatureFolder.exists()) {
			creatureFolder.mkdirs();
		} else {
			loadCreatures();
		}
		
		experimentFolder = Gdx.files.local(EXPERIMENT_FOLDER);
		if(!experimentFolder.exists())
			experimentFolder.mkdirs();
		else {
			loadExperimentNames();
		}
	}
	
	public void loadAll() {
		loadCreatures();
		loadExperimentNames();
	}
	
	public void loadCreatures() {
		creatureNameList.clear();
		genomeList.clear();
		for(FileHandle handle : creatureFolder.list()) {
			if(!handle.name().endsWith(".json")) continue;
			creatureNameList.add(handle.name().replace(".json", ""));
			genomeList.add(loadGenome(handle));
		}
		
		
	}
	
	public void loadExperimentNames() {
		//load experimentnames
		experimentNameList = new ArrayList<String>();
		for(FileHandle handle : experimentFolder.list()) {
			if(!handle.name().endsWith(".json")) continue;
			experimentNameList.add(handle.name().replace(".json", ""));
		}
	}
	
	public Genome getGenome(int idx) {
		return genomeList.get(idx);
	}
	
	public Genome loadGenome(FileHandle handle) {
		return json.fromJson(Genome.class, handle);
	}
	
	public Settings loadSettings(String name) {
		String path = "/"+EXPERIMENT_FOLDER+"/"+name+".json";
		FileHandle handle = getHandle(path);
		if(handle!=null) {
			return json.fromJson(Settings.class, handle);
		} else {
			return null;
		}
	}
	
	public FileHandle getHandle(String path) {
		FileHandle file = null;
		if(Gdx.files.local(path).exists()) {
			file = Gdx.files.local(path);
		} else
			System.out.println(path + " not found");
		return file;
	}
	
	public void saveGenome(Genome genome, String name) {
		FileHandle file = Gdx.files.local("/"+CREATURE_FOLDER+"/"+name+".json");
		file.writeString(json.toJson(genome), false);
	}
	
	public void saveExperimentData(ExperimentData data, String name) {
		FileHandle file = Gdx.files.local("/"+EXPERIMENT_FOLDER+"/"+name+".json");
		file.writeString(json.toJson(data), false);
	}
	
	public void saveSettings(Settings settings, String name) {
		FileHandle file = Gdx.files.local("/"+EXPERIMENT_FOLDER+"/"+name+".json");
		file.writeString(json.toJson(settings), false);
	}
	
	public synchronized ExperimentData loadExperimentData(String name) {
		String path = "/" + EXPERIMENT_FOLDER + "/"+name+".json";
		FileHandle handle = getHandle(path);
		return loadExperimentData(path);
	}
	
	public synchronized ExperimentData loadExperimentData(FileHandle handle) {
		ExperimentData data = null;
		if(handle!=null)
			data = json.fromJson(ExperimentData.class, handle);
		return data;
	}

	public List<String> getNames() {
		return creatureNameList;
	}
	
	public List<String> getExperimentNameList() {
		return this.experimentNameList;
	}

	public void removeIdx(int idx) {
		String name = creatureNameList.get(idx);
		FileHandle file = Gdx.files.local("/"+CREATURE_FOLDER+"/"+name+".json");
		System.out.println("delete succesful = " + file.delete());
	}
	
	public void writeStatistics(String experiment) {
		
		ArrayList<ExperimentData>  experiments = new ArrayList<ExperimentData>();
		for(FileHandle file : experimentFolder.list()) {
			if(file.name().startsWith(experiment+"_")) {
				experiments.add(loadExperimentData(file));
			}
		}
		
		ArrayList<CreatureBody> creatures = new ArrayList<CreatureBody>();
		for(ExperimentData data : experiments) {
			CreatureBody creature = new CreatureBody(data.gene);
			creatures.add(creature);
		}
		
		//beginWriting
		csvWriter.beginWriting();
		
		for(String var : this.interestingValues) {
			try {
				Field field = CreatureBody.class.getField(var);
				ArrayList<Float> values = new ArrayList<Float>(creatures.size());
				for(int i = 0; i < creatures.size(); i++) {
					Number value = (Number)field.get(creatures.get(i));
					values.add(value.floatValue());
				}
				
				//write csv line
				csvWriter.writeLine(var, values);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		//endWriting
		csvWriter.endWriting(experiment);
		System.out.println("done writing statistics csv");
	}
}

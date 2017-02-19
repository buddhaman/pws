package simulation;

import genome.Genome;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class CreatureLibrary {

	Json json;
	public static final String CREATURE_FOLDER = "savedCreatures";
	public static final String EXPERIMENT_FOLDER = "evoExperiments";
	FileHandle creatureFolder;
	
	private List<String> creatureNameList = new ArrayList<String>();
	private List<Genome> genomeList = new ArrayList<Genome>();
	
	public static boolean saved = false;
	
	public CreatureLibrary() {
		json = new Json();
		findFolder();
	}
	
	public void findFolder() {
		creatureFolder = Gdx.files.local(CREATURE_FOLDER);
		if(!creatureFolder.exists()) {
			creatureFolder.mkdirs();
		} else {
			loadAll();
		}
	}
	
	public void loadAll() {
		creatureNameList.clear();
		genomeList.clear();
		for(FileHandle handle : creatureFolder.list()) {
			if(!handle.name().endsWith(".json")) continue;
			creatureNameList.add(handle.name().replace(".json", ""));
			genomeList.add(loadGenome(handle));
		}
	}
	
	public Genome getGenome(int idx) {
		return genomeList.get(idx);
	}
	
	public Genome loadGenome(FileHandle handle) {
		return json.fromJson(Genome.class, handle);
	}
	
	public void saveGenome(Genome genome, String name) {
		FileHandle file = Gdx.files.local("/"+CREATURE_FOLDER+"/"+name+".json");
		file.writeString(json.toJson(genome), false);
	}
	
	public void saveExperimentData(ExperimentData data, String name) {
		FileHandle file = Gdx.files.local("/"+EXPERIMENT_FOLDER+"/"+name+".json");
		file.writeString(json.toJson(data), false);
	}
	
	public ExperimentData loadExperimentData(FileHandle handle) {
		return json.fromJson(ExperimentData.class, handle);
	}

	public List<String> getNames() {
		return creatureNameList;
	}

	public void removeIdx(int idx) {
		String name = creatureNameList.get(idx);
		FileHandle file = Gdx.files.local("/"+CREATURE_FOLDER+"/"+name+".json");
		System.out.println("delete succesful = " + file.delete());
	}
	
}

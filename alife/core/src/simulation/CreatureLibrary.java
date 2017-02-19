package simulation;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Settings;

import console.CommandListener;
import console.Console;
import console.Console.Type;
import genome.Genome;

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
		ExperimentData data = null;
		FileHandle handle = getHandle(path);
		if(handle!=null)
			data = json.fromJson(ExperimentData.class, handle);
		return data;
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

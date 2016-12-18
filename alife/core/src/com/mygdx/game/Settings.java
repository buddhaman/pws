package com.mygdx.game;

public class Settings {
	
	private static Settings currentSettings;
	
	//world data
	public FloatProperty tileSize;
	public IntProperty tWidth;
	public IntProperty tHeight;
	public boolean dayNightCycles;
	public boolean onlineEvolution;
	
	public FloatProperty newPlantProb;
	public FloatProperty plantGrowProb;
	
	public IntProperty bodyRecursionLimit;
	public IntProperty bodyMaxNodes;
	public IntProperty nodeMaxConnections;
	
	//creature fitness and attraction function
	public FloatProperty eyesAttraction;
	public FloatProperty bodySizeAttraction;
	public FloatProperty spikesAttraction;
	public FloatProperty brainSizeAttraction;
	public FloatProperty energyCollectedAttraction;
	public FloatProperty matingProb;
	
	//all actuators
	public IntProperty maxMouths;
	public IntProperty maxSpikes;
	
	public IntProperty maxHiddenSize;
	public FloatProperty brainSize; 			//probability of neuron being active;
	public FloatProperty hiddenConnectivity;
	public FloatProperty outputConnectivity; 	//probability of synapse from a->b existing
	public FloatProperty efficiacyRange;
	public FloatProperty weightDeviation;
	
	public IntProperty botPopulation;
	public IntProperty minBots;

	public FloatProperty nodeMutationProb;
	public FloatProperty nodeMutationRate;
	public FloatProperty brainMutationProb;
	public FloatProperty brainMutationRate;
	
	public FloatProperty brainActiveMutationProb;
	
	public IntProperty maxPlants;
	
	public IntProperty eatPlantFactor;
	public IntProperty eatCorpseFactor;
	public IntProperty attackStrength;
	public FloatProperty friction;
	public FloatProperty turningSpeed;

	public FloatProperty movingSpeed;

	public IntProperty rayNum;

	public FloatProperty rayLength;

	
	public static Settings getCurrent() {
		return currentSettings;
	}
	
	public static void setCurrent(Settings settings) {
		currentSettings = settings;
	}
	
	public static void setDefault(Settings settings) {
		settings.tileSize = new FloatProperty(14f);
		settings.tWidth = new IntProperty(60);
		settings.tHeight = new IntProperty(40);
		settings.bodyRecursionLimit = new IntProperty(5);
		settings.bodyMaxNodes = new IntProperty(16);
		settings.nodeMaxConnections = new IntProperty(3);
		settings.nodeMutationProb = new FloatProperty(.01f);
		settings.nodeMutationRate = new FloatProperty(.1f);
		settings.brainMutationProb = new FloatProperty(.008f);
		settings.brainMutationRate = new FloatProperty(.1f);
		settings.brainActiveMutationProb = new FloatProperty(.00003f);
		settings.efficiacyRange = new FloatProperty(12);
		settings.weightDeviation = new FloatProperty(12f);
		
		settings.brainSize = new FloatProperty(.3f);
		settings.hiddenConnectivity = new FloatProperty(.2f);
		settings.outputConnectivity = new FloatProperty(.4f);
		
		settings.botPopulation = new IntProperty(100);
		settings.minBots = new IntProperty(20);
		
		settings.maxMouths = new IntProperty(3);
		settings.maxSpikes = new IntProperty(4);
		settings.rayNum = new IntProperty(3);
		
		settings.maxHiddenSize = new IntProperty(35);
		settings.maxPlants = new IntProperty(90);
		settings.newPlantProb = new FloatProperty(1f);
		settings.plantGrowProb = new FloatProperty(.04f);
		
		settings.dayNightCycles = false;
		settings.onlineEvolution = false;
		settings.eatPlantFactor = new IntProperty(15);
		settings.eatCorpseFactor = new IntProperty(10);
		settings.attackStrength = new IntProperty(14);
		settings.turningSpeed = new FloatProperty(.2f);
		settings.movingSpeed = new FloatProperty(.15f);
		settings.friction = new FloatProperty(.64f);
		
		settings.matingProb = new FloatProperty(0f);
		settings.spikesAttraction = new FloatProperty(.4f);
		settings.eyesAttraction = new FloatProperty(1);
		settings.bodySizeAttraction = new FloatProperty(.2f);
		settings.brainSizeAttraction = new FloatProperty(.1f);
		settings.energyCollectedAttraction = new FloatProperty(.004f);
		
		settings.rayLength = new FloatProperty(50);
	}
	
	public static Settings getDefault() {
		Settings settings = new Settings();
		setDefault(settings);
		return settings;
	}
}

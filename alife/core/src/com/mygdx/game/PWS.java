package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

import console.CommandListener;
import console.Console;
import console.Console.Type;
import simulation.SimulationScreen;

public class PWS extends Game implements PWSContainer {
	SpriteBatch batch;
	
	public TextureAtlas atlas;
	public TextureAtlas simulationAtlas;
	public Skin skin;
	public int width;
	public int height;
	
	public Settings settings;
	
	@Override
	public void create () {

		//start the console
		new Thread(new Console()).start();
		
		batch = new SpriteBatch();
		
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();

		/*		USE THIS IF ON YOHFDH
		 * atlas = new TextureAtlas(Gdx.files.internal("/Users/<NAME>/Desktop/evolutie/pws/alife/core/assets/uiskin.atlas"));
		simulationAtlas = new TextureAtlas(Gdx.files.internal("/Users/<NAME>/Desktop/evolutie/pws/alife/core/assets/spritesheet.pack"));
		
		//define fonts
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("/Users/timtrussner/Desktop/evolutie/pws/alife/core/assets/Arial Unicode.ttf"));
		
		 */
		
		atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
		simulationAtlas = new TextureAtlas(Gdx.files.internal("spritesheet.pack"));
		
		//define fonts
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Arial Unicode.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 16;
		parameter.minFilter = TextureFilter.Linear;
		parameter.magFilter = TextureFilter.Linear;
		BitmapFont font16 = generator.generateFont(parameter);
		generator.dispose();
		
		skin = new Skin();
		skin.addRegions(atlas);

		skin.add("font16", font16);
		skin.load(Gdx.files.internal("uiskin.json"));
		
		TextButtonStyle noCheck = new TextButtonStyle(skin.get("default", TextButtonStyle.class));
		noCheck.checked = noCheck.up;
		skin.add("noCheck", noCheck);
		
		settings = Settings.getDefault();
		this.setScreen(new SimulationScreen(this));
		addCommands();
	}
	
	/**
	 * add some commands for setting settings
	 */
	public void addCommands() {
		Console.createCommand("setWorldWidth", Type.INT, new CommandListener(){
			public void executed(int arg) {
				System.out.println("world width is now " + arg);
				Settings.getCurrent().tWidth.val = arg;
			}
		});
		Console.createCommand("setWorldHeight", Type.INT, new CommandListener(){
			public void executed(int arg) {
				System.out.println("world height is now " + arg);
				Settings.getCurrent().tHeight.val = arg;
			}
		});
		Console.createCommand("setTileSize", Type.FLOAT, new CommandListener(){
			public void executed(float arg) {
				System.out.println("tile size is now " + arg);
				Settings.getCurrent().tileSize.val = arg;
			}
		});
		Console.createCommand("help", Type.NONE, new CommandListener(){
			@Override
			public void executed() {
				Console.printCommands();
				super.executed();
			}
		});
	}
	
	public void startSimulation() {
		this.setScreen(new SimulationScreen(this));
	}

	@Override
	public void render () {
		super.render();
		if(Gdx.input.isKeyJustPressed(Keys.F)) System.out.println(Gdx.app.getJavaHeap()/(1024*1024));
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		skin.dispose();
		batch.dispose();
		atlas.dispose();
		simulationAtlas.dispose();
	}

	@Override
	public Skin getSkin() {
		return skin;
	}

	@Override
	public Settings getSettings() {
		return settings;
	}

	@Override
	public SpriteBatch getSpriteBatch() {
		return batch;
	}
	
	@Override
	public TextureAtlas getSimulationAtlas() {
		return simulationAtlas;
	}
}

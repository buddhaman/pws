package simulation;

import java.util.List;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.PWSContainer;
import com.mygdx.game.Property;
import com.mygdx.game.Settings;

import component.Bot;
import component.Physics;
import creature.Actuator;
import creature.CreatureBody;
import creature.Sensor;
import genome.Genome;
import physics.Constraint;
import physics.Group;
import physics.Tile;
import renderer.BrainRenderer;
import renderer.RenderUtils;
import renderer.Resources;
import system.EvolutionSystem;
import system.Mappers;

public class SimulationScreen implements Screen, Resources {

	public Simulation simulation;
	private boolean simulationRunning;
	
	//properties
	public int maxIterationsPerFrame = 250;

	// ui stuff
	public Table mainTable;
	public Stage stage;
	public Popup popup;
	public Skin skin;
	public Label upsLabel;
	public Label timeLabel;
	public Slider slider;
	public Table worldSettings;
	public Table brainSettings;
	public Table creatureSettings;
	public Table fitnessSettings;
	public Window settingsPane;
	private Table settingsContainer;
	public ButtonGroup<TextButton> settingsSelection;
	public ButtonGroup<TextButton> creaturesGroup;
	public Array<Table> settingsTables;
	public int settingsTabIdx = 0;
	float settingsPaneWidth = 420;

	public Table leftTable;

	public PWSContainer container;
	private BrainRenderer brainRenderer;
	private RenderUtils utils;
	private SpriteBatch batch;

	public int screenWidth;
	public int screenHeight;

	public static final int TOOL_SELECT = 0;
	public static final int TOOL_DRAG = 1;
	public static final int TOOL_CHANGE_TILE = 2;
	public int toolType;
	public ButtonGroup<TextButton> toolSelection;
	public Entity selected;
	public int selectedTileType;
	private ComponentMapper<Physics> physM = Mappers.physicsMapper;
	private ComponentMapper<Bot> botM = Mappers.botMapper;

	// mouse
	public Vector2 worldMouse;
	public boolean mouseDown;
	public boolean prevMouseDown;
	public boolean isDragging;
	public boolean canSelect = true;

	public OrthographicCamera uiCamera;
	public Array<SliderHandler> sliderHandlerArray = new Array<SliderHandler>();
	
	public CreatureLibrary creatureLibrary;
	public Table loadTable;
	public TextButton loadButton;
	public TextButton showSettingsButton;
	protected TextField nameField;

	public SimulationScreen(PWSContainer container) {
		this.container = container;
		batch = container.getSpriteBatch();
		utils = new RenderUtils(this);
		skin = container.getSkin();
		this.container = container;
		brainRenderer = new BrainRenderer(this);
		
		creatureLibrary = new CreatureLibrary();
		
		Settings.setCurrent(container.getSettings());
		simulation = new Simulation(container.getSettings());
		simulation.setResources(this);
		simulationRunning = true;
		initUI();
	}

	public void initUI() {
		uiCamera = new OrthographicCamera();
		stage = new Stage(new ScreenViewport(uiCamera));
		mainTable = new Table();
		mainTable.setFillParent(true);
		stage.addActor(mainTable);
		popup = new Popup(skin, uiCamera);

		// pause button
		TextButton pause = new TextButton("||", skin);
		mainTable.add(pause).pad(5);
		pause.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				simulation.setRunning(!simulation.isRunning());
			}
		});

		upsLabel = new Label("", skin);
		slider = new Slider(1, maxIterationsPerFrame, 1, false, skin);
		mainTable.top().add(upsLabel).right();
		mainTable.add(slider).width(300).center();

		leftTable = new Table();
		stage.addActor(leftTable);
		leftTable.left().top().row();
		leftTable.setFillParent(true);
		// create button group for current tool
		toolSelection = new ButtonGroup<TextButton>();

		timeLabel = new Label("", skin);
		leftTable.add(timeLabel).colspan(3).space(0).left();
		leftTable.row();
		Table tools = new Table();
		TextButton selection = new TextButton("select", skin);
		TextButton drag = new TextButton("drag", skin);
		TextButton changeTile = new TextButton("tile", skin);
		toolSelection.add(selection);
		toolSelection.add(drag);
		toolSelection.add(changeTile);

		float buttonWidth = 50;
		tools.add(selection).width(buttonWidth).left().space(2);
		tools.add(drag).width(buttonWidth).left().space(2);
		tools.add(changeTile).width(buttonWidth).left().space(2);
		leftTable.add(tools).left();

		Table settingsTable = new Table();
		leftTable.row();
		leftTable.add(settingsTable).colspan(3).space(30, 0, 0, 0);

		buttonWidth = 80;
		settingsSelection = new ButtonGroup<TextButton>();
		TextButton worldButton = new TextButton("world", skin);
		TextButton brainButton = new TextButton("brain", skin);
		TextButton evolutionButton = new TextButton("creature", skin);
		TextButton fitnessButton = new TextButton("fitness", skin);
		settingsSelection.add(worldButton);
		settingsSelection.add(brainButton);
		settingsSelection.add(evolutionButton);
		settingsSelection.add(fitnessButton);
		settingsTable.add(worldButton).width(buttonWidth);
		settingsTable.add(brainButton).width(buttonWidth);
		settingsTable.add(evolutionButton).width(buttonWidth);
		settingsTable.add(fitnessButton).width(buttonWidth);
		settingsContainer = new Table();

		// settings blablBAO;HFAG
		worldSettings = new Table();
		creatureSettings = new Table();
		brainSettings = new Table();
		fitnessSettings = new Table();
		settingsTables = new Array<Table>();
		settingsTables.add(worldSettings);
		settingsTables.add(brainSettings);
		settingsTables.add(creatureSettings);
		settingsTables.add(fitnessSettings);

		Settings settings = Settings.getCurrent();
		addSlider(brainSettings, "brain mutation rate", 0, 1f, 0.005f,
				settings.brainMutationRate);
		addSlider(brainSettings, "large mutation probability", 0, .05f,
				0.0003f, settings.brainActiveMutationProb);
		addSlider(brainSettings, "weight deviation", 0, 12, 0.1f,
				settings.weightDeviation);
		addSlider(brainSettings, "brain size", .2f, 1, 0.01f,
				settings.brainSize);
		addSlider(brainSettings, "hidden connectivity", .1f, 1, 0.01f,
				settings.hiddenConnectivity);
		addSlider(brainSettings, "output connectivity", .1f, 1, 0.01f,
				settings.outputConnectivity);

		addSlider(worldSettings, "max plants", 0, 200, 2,
				settings.maxPlants);
		addSlider(worldSettings, "plant growth", 0, 1, 0.01f,
				settings.plantGrowProb);
		addSlider(worldSettings, "minimum creatures", 0,
				settings.minBots.val, 1,
				settings.minBots);

		addSlider(creatureSettings, "body mutation rate", 0, 1f, 0.005f,
				settings.nodeMutationRate);
		addSlider(creatureSettings, "node mutation probability", 0, 0.2f,
				0.001f, settings.nodeMutationProb);
		addSlider(creatureSettings, "speed", 0, .8f, 0.01f,
				settings.movingSpeed);
		addSlider(creatureSettings, "turning speed", 0, .8f, 0.01f,
				settings.turningSpeed);

		addSlider(fitnessSettings, "eyes", 0, 1f, 0.01f,
				settings.eyesAttraction);
		addSlider(fitnessSettings, "spikes", 0, 1f, 0.01f,
				settings.spikesAttraction);
		addSlider(fitnessSettings, "body size", 0, 1f, 0.01f,
				settings.bodySizeAttraction);
		addSlider(fitnessSettings, "brain size", 0, 1f, 0.01f,
				settings.brainSizeAttraction);
		addSlider(fitnessSettings, "energy collected", 0, .02f, 0.0005f,
				settings.energyCollectedAttraction);
		addSlider(fitnessSettings, "mating probability", 0, 1, 0.01f,
				settings.matingProb);

		TextButton applyButton = new TextButton("apply", skin, "noCheck");
		leftTable.row();

		settingsPane = new Window("settings", skin);
		settingsPane.add(settingsTable).row();
		settingsPane.add(settingsContainer);
		settingsPane.row();
		settingsPane.add(applyButton);

		// add button for hiding/showing settings and loading
		showSettingsButton = new TextButton("x", skin);
		showSettingsButton.addListener(new ChangeListener() {
			private boolean settingsVisible = true;

			public void changed(ChangeEvent event, Actor actor) {
				if (settingsVisible) {
					settingsPane.setVisible(false);
					settingsVisible = !settingsVisible;
				} else {
					settingsPane.setVisible(true);
					settingsVisible = !settingsVisible;
				}
			}
		});

		leftTable.add(showSettingsButton).top().left().width(20).height(20).row();
		leftTable.add(settingsPane).colspan(3).width(settingsPaneWidth);
		applyButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				for (SliderHandler sh : sliderHandlerArray) {
					sh.update();
				}
				simulation.updateSettings(Settings.getCurrent());
			}
		});

		this.setSettingsPane(worldSettings);
		Gdx.input.setInputProcessor(stage);
		
		Label loadSaveLabel = new Label("load/save creatures", skin);
		settingsPane.row();
		settingsPane.add(loadSaveLabel).space(10).row();
		
		//add button for saving creatures
		TextButton saveButton = new TextButton("save", skin, "noCheck");
		saveButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				saveCurrentCreature();
			}
		});
		Table saveRemoveTable = new Table();
		saveRemoveTable.add(saveButton).width(60).space(5);
		//delete creatures
		TextButton deleteButton = new TextButton("delete", skin, "noCheck");
		deleteButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				showDeleteWarning();
			}
		});
		saveRemoveTable.add(deleteButton).width(60);
		settingsPane.add(saveRemoveTable).row();;
		
		//add the loading table
		loadTable = new Table();
		ScrollPane loadTableScroll = new ScrollPane(loadTable, skin);
		settingsPane.add(loadTableScroll).fill(true, false).space(5).row();
		updateLoadTable();
		TextButton addCreatureButton = new TextButton("add", skin, "noCheck");
		addCreatureButton.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor) {
				if(creatureLibrary.getNames().size()==0)
					return;
				Genome selectedGenome = creatureLibrary.getGenome(creaturesGroup.getCheckedIndex());
				if(selectedGenome!=null)
				simulation.engine.getSystem(EvolutionSystem.class).spawn(selectedGenome, 
						simulation.getCameraX(), simulation.getCameraY());
				else {
					System.err.println("error: genome is not available");
				}
			}
		});
		settingsPane.add(addCreatureButton);
	}
	
	public void updateLoadTable() {
		loadTable.clear();
		creatureLibrary.loadAll();
		creaturesGroup = new ButtonGroup<TextButton>();
		List<String> names = creatureLibrary.getNames();
		for(String name : names) {
			TextButton button = new TextButton(name, skin);
			creaturesGroup.add(button);
			loadTable.add(button).width(360).height(22);
			loadTable.row();
		}
	}
	
	public void setSettingsPane(Table table) {
		settingsContainer.clear();
		settingsContainer.add(table);
	}

	public void updateUI(float delta) {
		upsLabel.setText(Gdx.graphics.getFramesPerSecond() + " at "
				+ simulation.iterations + " iterations per tick");
		simulation.iterations = (int) slider.getValue();
		String time = simulation.getTime();
		if(time.length()<20) {
			time=(" "+time);
		}
		timeLabel.setText(simulation.getTime());
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(.5f, .5f, .5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		updateUI(delta);
		stage.act(delta);
		simulation.update(delta);
		stage.draw();
		setBatchToScreenSize(batch);
		
		batch.begin();
		renderBrain();
		drawBotInfo();
		batch.end();

		toolType = toolSelection.getCheckedIndex();
		if(settingsSelection.getCheckedIndex()!=settingsTabIdx) {
			settingsTabIdx = settingsSelection.getCheckedIndex();
			this.setSettingsPane(settingsTables.get(settingsTabIdx));
		}

		// handle input in simulation world
		if(popup.active) {
			popup.act();
		} else {
		float mx = Gdx.input.getX();
		float my = Gdx.graphics.getHeight() - Gdx.input.getY();
		mouseDown = Gdx.input.isButtonPressed(Buttons.LEFT);
		worldMouse = simulation.getWorldPosition(mx, my);
		boolean inHud = inHud(mx, my);
		
		if(!inHud)
			handleSelect();
		if (toolType == TOOL_SELECT) {

		} else if (toolType == TOOL_DRAG) {
			handleDrag(inHud);
		} else if (toolType == TOOL_CHANGE_TILE) {
			handleTileChange(inHud);
		}
		prevMouseDown = mouseDown;
		}
	}
	
	
	private void renderBrain() {
		if(selected!=null) {
			Bot bot = botM.get(selected);
			if(bot!=null)
				brainRenderer.render(bot.body, screenWidth, screenHeight);
		}
	}
	
	public void setBatchToScreenSize(SpriteBatch batch) {
		batch.setProjectionMatrix(uiCamera.combined);
	}

	/**
	 * returns true if screenpos is a hud item
	 * 
	 * @param screenPos
	 * @return is in hud ?
	 */
	public boolean inHud(float x, float y) {
		Actor actor = stage.hit(x, y, true);
		if(actor!=null)
			return true;
		return false;
	}

	public void handleSelect() {
		if (mouseDown && canSelect) {
			if(selected!=null) {
				Bot bot = botM.get(selected);
				if(bot!=null) 
					bot.renderState = false;
			}
			selected = simulation.getEntityAt(worldMouse.x, worldMouse.y);
			if (selected != null) {
				canSelect = false;
				isDragging = true;
				Bot bot = botM.get(selected);
				if(bot!=null) {
					bot.renderState = true;
				}
			}
		} 
		if(!mouseDown) {
			isDragging = false;
		}
		if (!canSelect && !mouseDown) {
			canSelect = true;
		}
	}

	public void handleDrag(boolean inHud) {
		if (selected != null && mouseDown && isDragging) {
			Physics physics = physM.get(selected);
			Group group = physics.group;
			group.setPosition(worldMouse.x, worldMouse.y);
			group.setVelocity(0, 0);
			for (Constraint constraint : group.constraintList) {
				if (constraint.getCurrentLength() > constraint.length * 8)
					constraint.remove();
			}
		}
	}

	public void handleTileChange(boolean inHud) {
		if (mouseDown && !prevMouseDown) {
			Tile t = simulation.world.getTileAt(worldMouse.x, worldMouse.y);
			this.selectedTileType = t.type == Tile.TYPE_EMPTY ? Tile.TYPE_STONE
					: Tile.TYPE_EMPTY;
		}
		if (mouseDown && !inHud) {
			Tile t = simulation.world.getTileAt(worldMouse.x, worldMouse.y);
			t.setType(selectedTileType);
		}
	}
	
	public void showDeleteWarning() {
		if(creatureLibrary.getNames().size()==0)
			return;
		Window deleteWindow = new Window("delete creature", skin);
		Label text = new Label("Are you sure you want to delete "+
		creatureLibrary.getNames().get(creaturesGroup.getCheckedIndex())+"?", skin);
		TextButton yes = new TextButton("yes", skin);
		TextButton no = new TextButton("no", skin);
		
		deleteWindow.add(text).space(5).colspan(2).row();
		deleteWindow.add(yes).width(60);
		deleteWindow.add(no).width(60);
		
		popup.set(deleteWindow);
		
		yes.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor) {
				creatureLibrary.removeIdx(creaturesGroup.getCheckedIndex());
				popup.hide();
				Gdx.input.setInputProcessor(stage);
				updateLoadTable();
			}
		});
		no.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor) {
				popup.hide();
				Gdx.input.setInputProcessor(stage);
			}
		});
	}
	
	public void saveCurrentCreature() {
		if(selected==null)
			return;
		final Bot bot = botM.get(selected);
		if(bot==null)
			return;
		simulation.setCamCanMove(false);
		Window saveWindow = new Window("save creature", skin);
		Label text = new Label("name : ", skin);
		nameField = new TextField("", skin);
		TextButton saveButton = new TextButton("save", skin);
		TextButton cancelButton = new TextButton("cancel", skin);
		saveButton.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor) {
				String name = nameField.getText();
				if(name.length()==0)
					return;
				creatureLibrary.saveGenome(botM.get(selected).body.genome, name);
				updateLoadTable();
				popup.hide();
				Gdx.input.setInputProcessor(stage);
				simulation.setCamCanMove(true);
			}
		});
		cancelButton.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor) {
				popup.hide();
				Gdx.input.setInputProcessor(stage);
				simulation.setCamCanMove(true);
			}
		});
		
		//build window
		saveWindow.add(text).space(5);
		saveWindow.add(nameField);
		saveWindow.row();
		saveWindow.add(saveButton);
		saveWindow.add(cancelButton);
		popup.set(saveWindow);
	}
	
	public void drawBotInfo() {
		if(selected!=null) {
			Bot b = botM.get(selected);
			if(b!=null) {
				CreatureBody creature = b.body;
				BitmapFont font = skin.get("font16", BitmapFont.class);
				
				String stats = String.format("%d bodyparts, %d spikes, %d eyes, %d energy\n", creature.getSensorNum(Sensor.TOUCH), creature.getActuatorNum(Actuator.SPIKE),
						creature.getSensorNum(Sensor.VISION), creature.totalEnergyCollected);
				String fitness = String.format("fitness = %.2f x bodyparts + %.2f x spikes + %.2f x eyes + %.2f x brainsize + %.3f x energy = %.2f "
						,Settings.getCurrent().bodySizeAttraction.val, Settings.getCurrent().spikesAttraction.val, Settings.getCurrent().eyesAttraction.val
						,Settings.getCurrent().brainSizeAttraction.val, Settings.getCurrent().energyCollectedAttraction.val, creature.getFitness());
				
				font.draw(batch, String.format(
						  "# of neurons   : %d\n"
						+ "# of inputs    : %d\n"
						+ "# of hidden    : %d\n"
						+ "# of output    : %d\n"
						+ "# of synapses  : %d\n"
						+ "generation : %d\n"
						+ "originated : %s\n"
						+ "carnivore : %.1f%%\n"
						+ stats + fitness,
						creature.numNeurons, creature.numInputNeurons, creature.numHiddenNeurons, 
						creature.numOutputNeurons, creature.numSynapses, creature.genome.generation, 
						creature.genome.timeOriginated, creature.genome.foodType*100), 10, 250);
			}
		}
		
	}
	
	private Slider addSlider(Table table, String text, float min, float max, float stepSize, Property property) {
		Label label = new Label(text, skin);
		Slider slider = new Slider(min, max, stepSize, false, skin);
		table.row();
		table.add(label).space(2);
		table.add(slider).width(220).colspan(2);
		slider.setValue(property.get());
		sliderHandlerArray.add(new SliderHandler(slider, property));
		return slider;
	}

	@Override
	public void resize(int width, int height) {
		this.screenHeight = height;
		this.screenWidth = width;
		stage.getViewport().update(width, height, true);
		popup.resize(width, height);
		simulation.resize(width, height);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		stage.dispose();
		popup.dispose();
	}

	@Override
	public AtlasRegion findRegion(String name) {
		return container.getSimulationAtlas().findRegion(name);
	}

	@Override
	public SpriteBatch getSpriteBatch() {
		return batch;
	}

	@Override
	public RenderUtils getRenderUtils() {
		return utils;
	}
	
	public class SliderHandler {
		public Slider s;
		public Property property;
		
		public SliderHandler(Slider s, Property property) {
			this.s = s;
			this.property = property;
		}
		
		public void update() {
			property.set(s.getValue());
		}
	}
}

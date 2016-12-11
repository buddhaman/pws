package system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import component.Bot;
import component.CameraComponent;
import component.Corpse;
import component.Plant;
import component.Seed;
import component.Transform;
import renderer.BotRenderer;
import renderer.CorpseRenderer;
import renderer.PlantRenderer;
import renderer.RenderUtils;
import renderer.Resources;
import renderer.SeedRenderer;
import renderer.WorldRenderer;
import simulation.Simulation;
	
public class RenderSystem extends EntitySystem{
	public TextureAtlas atlas;
	public Engine engine;
	
	private Resources res;
	
	public ComponentMapper<CameraComponent> camM = Mappers.cameraComponentMapper;
	public ComponentMapper<Transform> transM = Mappers.transformMapper;
	
	private Family camFamily = Family.all(CameraComponent.class).get();
	private Family botFamily = Family.all(Bot.class).get();
	private Family plantFamily = Family.all(Plant.class).get();
	private Family corpseFamily = Family.all(Corpse.class).get();
	private Family seedFamily = Family.all(Seed.class).get();
	
	public SpriteBatch batch;
	public Simulation simulation;
	
	private ImmutableArray<Entity> cameraArray; 
	private ImmutableArray<Entity> botArray;
	private ImmutableArray<Entity> plantArray;
	private ImmutableArray<Entity> corpseArray;
	private ImmutableArray<Entity> seedArray;
	
	private WorldRenderer worldRenderer;
	private BotRenderer botRenderer;
	private PlantRenderer plantRenderer;
	private CorpseRenderer corpseRenderer;
	private SeedRenderer seedRenderer;
	
	//renderers
	private RenderUtils utils;
	
	public RenderSystem(Simulation simulation, Resources res) {
		this.simulation = simulation;
		this.batch = res.getSpriteBatch();
		this.res = res;
		
		//renderers
		utils = new RenderUtils(res);
		worldRenderer = new WorldRenderer(res);
		botRenderer = new BotRenderer(res);
		plantRenderer = new PlantRenderer(res);
		corpseRenderer = new CorpseRenderer(res);
		seedRenderer = new SeedRenderer(res);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
		cameraArray = engine.getEntitiesFor(camFamily);
		botArray = engine.getEntitiesFor(botFamily);
		plantArray = engine.getEntitiesFor(plantFamily);
		corpseArray = engine.getEntitiesFor(corpseFamily);
		seedArray = engine.getEntitiesFor(seedFamily);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		//update camera;
		Entity camera = cameraArray.get(0);
		CameraComponent camComponent = camM.get(camera);
		batch.setProjectionMatrix(camComponent.cam.combined);
			
		batch.begin();
		
		worldRenderer.render(simulation, camera);
		corpseRenderer.render(corpseArray);
		plantRenderer.render(plantArray);
		botRenderer.render(botArray);
		seedRenderer.render(seedArray);
		
		batch.end();
	}
}

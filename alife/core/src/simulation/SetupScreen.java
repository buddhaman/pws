package simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.PWSContainer;

public class SetupScreen implements Screen {

	public PWSContainer container;
	private Skin skin;
	private SpriteBatch batch;
	private TextureAtlas atlas;
	private Stage stage;
	
	public Table mainTable;
	public OrthographicCamera uiCamera;
	
	private float screenWidth, screenHeight;
	
	public SetupScreen(PWSContainer container) {
		this.skin = container.getSkin();
		this.batch = container.getSpriteBatch();
		atlas = container.getSimulationAtlas();
		
		this.container = container;
		
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		
		initUI();
	}
	
	public void initUI() {
		uiCamera = new OrthographicCamera();
		stage = new Stage(new ScreenViewport(uiCamera));
		Gdx.input.setInputProcessor(stage);
		
		mainTable = new Table();
		mainTable.setFillParent(true);
		stage.addActor(mainTable);
		
		Table tab = new Table(skin);
		tab.setBackground("default-pane");
		
		Label setupLabel = new Label("setup", skin);
		tab.add(setupLabel);
		tab.row();
		
		TextButton startButton = new TextButton("start", skin);
		startButton.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor) {
				container.startSimulation();
			}
		});
		tab.add(startButton);
		
		mainTable.add(tab).center();
	}

	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(.5f, .5f, .5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		screenWidth = width;
		screenHeight = height;
		uiCamera.setToOrtho(false, width, height);
		uiCamera.update();
		stage.getViewport().update(width, height);;
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}

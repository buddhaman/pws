package simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Popup {
	
	public Stage stage;
	
	public Window popup;
	public Skin skin;
	
	public boolean active;
	public OrthographicCamera cam;
	
	private int width, height;
	
	public Popup(Skin skin, OrthographicCamera cam) {
		this.skin = skin;
		stage = new Stage();
		stage.setViewport(new ScreenViewport(cam));
		this.cam = cam;
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
	}
	
	public void set(Window popup) {
		this.popup = popup;
		stage.clear();
		Table cTable = new Table();
		cTable.add(popup).center();
		cTable.setFillParent(true);
		stage.addActor(cTable);
		Gdx.input.setInputProcessor(stage);
		active = true;
	}
	
	public void hide() {
		active = false;
	}
	
	public void act() {
		stage.getViewport().update(width, height, true);
		stage.act();
		stage.draw();
	}
	
	public void dispose() {
		stage.dispose();
	}

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		stage.getViewport().update(width, height, true);
	}
}

package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public interface PWSContainer {
	public Skin getSkin();
	public Settings getSettings();
	public SpriteBatch getSpriteBatch();
	public TextureAtlas getSimulationAtlas();
	public void startSimulation();
}

package com.dragonBones.java;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class DragonBonesJava extends Game {
	private Stage demoStage;

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		demoStage.act();
		demoStage.draw();
	}

	@Override
	public void create() {
		demoStage = new DemoDragonBoyStage();
		Gdx.input.setInputProcessor(demoStage);
	}

	@Override
	public void resize(int width, int height) {

	}


	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}
}

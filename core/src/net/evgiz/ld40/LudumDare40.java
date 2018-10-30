package net.evgiz.ld40;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import net.evgiz.ld40.game.Game;
import net.evgiz.ld40.game.Menu;


public class LudumDare40 extends ApplicationAdapter {

	Game game;
	Menu menu;

	boolean gameOverScreen = false;
	float playTime = 0f;
	float gameOverTime = 0f;

	Texture white;

	String formatPlayTime;

	int loot = 0;

	Texture background;

	boolean paused = false;

	@Override
	public void create () {

		white = new Texture(Gdx.files.internal("white.png"));
		background = new Texture(Gdx.files.internal("background.png"));

		menu = new Menu();

	}

	@Override
	public void render () {

		if(gameOverScreen){
			renderUpdateGameOver();
			return;
		}

		if(game!=null && game.intro.alphaOut<=0 && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
			paused = true;
			menu.begin = false;

			if(game==null)
				paused=false;

			Gdx.input.setCursorCatched(false);
			Game.audio.stopMusic();
			return;
		}


		if(game!=null && !paused) {
			game.update();
			game.render();

			playTime += Gdx.graphics.getDeltaTime();

			if(game.game_over){
				gameOverScreen = true;

				Game.audio.stopMusic();

				int sec = (int)playTime;
				int min = sec/60;
				sec %= 60;

				min = Math.min(min, 99);

				formatPlayTime = ((min<10)?("0"+min):min)+":"+((sec<10)?("0"+sec):sec);

				loot = game.inventory.totalLoot;

			}
		}else{

			Gdx.gl.glViewport(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			Gdx.gl.glClearColor(0,0,0,1);

			menu.batch.begin();
			drawBackground();
			menu.batch.end();

			menu.paused = paused;
			menu.update();
			menu.render();

			if(menu.begin){
				if(!paused)
					game = new Game(menu.retro, menu.difficulty, menu.lookSensitivity);
				else
					game.setSettings(menu.retro, menu.difficulty, menu.lookSensitivity);
				paused = false;
				Game.audio.startMusic();
			}
		}
		Game.audio.update();

	}

	public void drawBackground(){
		int size = 96;
		for (int x = 0; x < Gdx.graphics.getWidth(); x+=size) {
			for (int y = Gdx.graphics.getHeight(); y > Gdx.graphics.getHeight()-size*3; y-=size) {
				menu.batch.draw(background, x,y,size,size);
			}
		}

	}

	public void renderUpdateGameOver(){

		gameOverTime += Gdx.graphics.getDeltaTime();

		if(gameOverTime>3f && (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) || Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			gameOverScreen = false;
			gameOverTime = 0f;
			playTime = 0f;

			game.destroy();
			game = null;
			menu.begin = false;

			Game.gameComplete = false;

			Gdx.input.setCursorCatched(false);
		}

		//Dark background
		menu.batch.begin();

		drawBackground();

		menu.batch.setColor(0f,0f,0f, 1);
		menu.batch.draw(white, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		menu.batch.setColor(1,1,1, 1);

		//Text
		menu.font.getData().setScale(2f);
		menu.font.setColor(1,0,0,1);
		menu.font.draw(menu.batch, Game.gameComplete ? "Game Won!" : "Game Over", Gdx.graphics.getWidth()/2 - 16*9, Gdx.graphics.getHeight()-100 + (float)Math.max(0,Math.cos(gameOverTime*4f))*16);
		menu.font.getData().setScale(1f);

		menu.font.setColor(1,1,1,1);

		menu.font.draw(menu.batch, "Time: ", Gdx.graphics.getWidth()/2 - 8*15 - 5, Gdx.graphics.getHeight()-250);
		menu.font.draw(menu.batch, formatPlayTime, Gdx.graphics.getWidth()/2 + 8*8, Gdx.graphics.getHeight()-250);

		menu.font.draw(menu.batch, "Loot: ", Gdx.graphics.getWidth()/2 - 8*15, Gdx.graphics.getHeight()-290);
		menu.font.draw(menu.batch, ""+loot, Gdx.graphics.getWidth()/2 + 8*8, Gdx.graphics.getHeight()-290);


		menu.font.setColor(1,1,1, 1);

		menu.batch.flush();
		menu.batch.end();

	}

	@Override
	public void resize(int width, int height) {
		if(game!=null)
			game.resize(width,height);
	}

	@Override
	public void dispose () {
		if(game!=null)
		game.destroy();
	}
}

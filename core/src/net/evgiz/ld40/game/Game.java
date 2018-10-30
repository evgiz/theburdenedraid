package net.evgiz.ld40.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import net.evgiz.ld40.game.decals.DecalManager;
import net.evgiz.ld40.game.entity.EntityManager;
import net.evgiz.ld40.game.player.CameraController;
import net.evgiz.ld40.game.player.Inventory;
import net.evgiz.ld40.game.renderable.GameShaderProvider;
import net.evgiz.ld40.game.player.Player;
import net.evgiz.ld40.game.world.World;

import java.util.Random;

public class Game {

    public static final float UNIT = 16f;
    public static final Random random = new Random();
    public static final CollisionDetector collision = new CollisionDetector();
    public static final Art art = new Art();
    public static final Audio audio = new Audio();

    public Intro intro;

    SpriteBatch batch2d;
    BitmapFont font;

    ModelBatch batch;
    ShaderProvider shaderProvider;

    PerspectiveCamera camera;
    FrameBuffer frameBuffer = null;

    Player player;
    World world;
    public Inventory inventory;
    EntityManager entityManager;

    DecalManager decalManager;


    int downscale = 2;

    int[] scales = new int[]{1,2,6,8};
    int[] health = new int[]{8,5,3,1};

    private static boolean transition = false;
    private static float transitionProgress = 0f;
    private static String targetLevel = "level";
    private static boolean hasLoaded = false;

    public boolean game_over = false;
    public static boolean gameComplete = false;


    public Game(int retro, int diff, int lookSens) {

        downscale = scales[retro];

        batch2d = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("font.fnt"));


        shaderProvider = new GameShaderProvider();
        batch = new ModelBatch(shaderProvider);

        camera = new PerspectiveCamera(65, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 0, 10f);
        camera.lookAt(11f, 0, 10f);
        camera.near = .5f;
        camera.far = 30f * Game.UNIT;
        camera.update();

        decalManager = new DecalManager(camera);


        entityManager = new EntityManager(decalManager);
        world = new World(decalManager, entityManager);

        inventory = new Inventory();
        player = new Player(camera, world, entityManager, inventory, lookSens);

        player.health = health[diff];

        frameBuffer = FrameBuffer.createFrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth()/downscale, Gdx.graphics.getHeight()/downscale, true);
        frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        intro = new Intro();
    }

    public void setSettings(int retro, int difficulty, int lookSensitivity){
        downscale = scales[retro];
        player.cameraController = new CameraController(camera, lookSensitivity);

        frameBuffer = FrameBuffer.createFrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth()/downscale, Gdx.graphics.getHeight()/downscale, true);
        frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

    }

    public void resize(int w, int h){

    }

    public static void changeLevel(String level){
        Game.transition = true;
        Game.transitionProgress = 0f;
        Game.targetLevel = level;
        Game.hasLoaded = false;
    }

    public void update(){
        if(Game.gameComplete && !intro.isOutro) {
            intro = new Intro();
            intro.setOutro();
        }

        if(!intro.finished) {
            intro.update();

            if(!intro.finishing && !Game.gameComplete)
                return;

            if(!Game.gameComplete) {
                player.getPosition().set(world.spawnx * Game.UNIT, 0, world.spawny * Game.UNIT);
                player.cameraController.bobbing = 0;
            }

        }else {
            if(Game.gameComplete){
                Game.audio.stopMusic();
                game_over = true;
            }else {
                Game.audio.startMusic();
            }
        }

        if(Game.gameComplete && intro.alphaOut>=1f)
            return;

        if(Game.transition){
            Game.transitionProgress += Gdx.graphics.getDeltaTime();

            if(Game.transitionProgress>=0.5f && !Game.hasLoaded){
                Game.hasLoaded = true;
                world.load(Game.targetLevel);

                player.getPosition().set(world.spawnx*Game.UNIT, 0, world.spawny*Game.UNIT);
                entityManager.update(world,player);
                camera.rotate(Vector3.Y, (float)Math.toDegrees(Math.atan2(camera.direction.z, camera.direction.x)));
                player.update(entityManager);
                camera.update();

            }
            if(Game.transitionProgress>1f){
                Game.transitionProgress = 0;
                Game.transition = false;
            }else
                return;

        }
        player.update(entityManager);
        camera.update();
        entityManager.update(world, player);

        if(player.health<=0 && !Game.gameComplete) {
            game_over = true;
            Game.audio.play("gameover");
        }
    }

    public void render(){

        Gdx.gl.glViewport(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0,0,0,1);

        frameBuffer.begin();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0,0,0,1);

        batch.begin(camera);

        for (int i = 0; i < world.modelInstances.size(); i++) {
            batch.render(world.modelInstances.get(i));
        }
        batch.end();

        decalManager.render();

        batch2d.begin();
        inventory.render(batch2d, player);
        player.render(batch2d);
        batch2d.end();

        frameBuffer.end();


        //Draw buffer and FPS
        batch2d.begin();

        float c = 1.0f;
        if(Game.transition){
            c = 1.0f - transitionProgress*4;
            if(transitionProgress>=.75f)
                c = (transitionProgress-0.75f)*4;
            c = MathUtils.clamp(c, 0, 1);
        }

        batch2d.setColor(c,c,c,1);
        batch2d.draw(frameBuffer.getColorBufferTexture(), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), - Gdx.graphics.getHeight());

        if(!Game.transition)
            player.renderUI(batch2d, font, downscale);

        if(!intro.finished)
            intro.render(batch2d, font);
        //font.draw(batch2d, "FPS: "+Gdx.graphics.getFramesPerSecond(), 10, 20);

        batch2d.end();

    }

    public void destroy(){

    }

}






/*

    TODO:

    * Expand small levels
    * More puzzles
    * More loot
    * More enemy types
    * Game-Over screen on death

 */



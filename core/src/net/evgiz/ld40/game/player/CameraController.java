package net.evgiz.ld40.game.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import net.evgiz.ld40.game.Game;

public class CameraController {

    private Camera camera;
    private Vector3 tmp;

    private float rotSpeed = 220f;
    private float rotSpeedY = 140f;

    private float cursorSpeed = .15f;

    public float bobbing = 0f;
    private float bobHeight = .03f;
    public float bobSpeed = 20f;

    float[] sensitivity = new float[]{
      0.5f, 1f, 1.75f
    };
    float[] sensitivity2 = new float[]{
            0.25f, 1f, 1.75f
    };

    final float maxLookY = 80f;
    float lookY = 0f;

    public CameraController(Camera cam, int sens){
        camera = cam;
        tmp = new Vector3();

        rotSpeed *= sensitivity[sens];
        rotSpeedY *= sensitivity[sens];
        cursorSpeed *= sensitivity2[sens];
    }

    public void update(){

        float dt = Gdx.graphics.getDeltaTime();

        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            Gdx.input.setCursorCatched(true);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            Gdx.input.setCursorCatched(false);
        }

         //Rotation
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {


                if(camera.direction.y < 0.95) {
                    tmp.set(camera.direction).crs(camera.up).nor();
                    camera.rotate(tmp, rotSpeedY * dt);
                }

            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if(camera.direction.y>-0.95) {
                    tmp.set(camera.direction).crs(camera.up).nor();
                    camera.rotate(tmp, -rotSpeedY * dt);
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                camera.rotate(Vector3.Y, rotSpeed * dt);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                camera.rotate(Vector3.Y, -rotSpeed * dt);
            }

        if(Gdx.input.isCursorCatched()){
            float rx = Gdx.input.getDeltaX();
            float ry = Gdx.input.getDeltaY();

            tmp.set(camera.direction).crs(camera.up).nor();
            if((ry>0 && camera.direction.y>-0.95) || (ry<0 && camera.direction.y < 0.95))
                camera.rotate(tmp, -cursorSpeed * ry);
            camera.rotate(Vector3.Y, -cursorSpeed  * rx);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.D)){
            bobbing += dt;
        }

        tmp.set(0f, (float) (Math.sin(bobbing * bobSpeed) * bobHeight * Game.UNIT), 0f);
        camera.position.add(tmp);

    }

}

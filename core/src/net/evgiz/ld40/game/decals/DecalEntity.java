package net.evgiz.ld40.game.decals;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;
import net.evgiz.ld40.game.Game;

import java.util.Random;

public class DecalEntity {

    public Decal decal;
    public float scale;

    public Vector3 position;

    public boolean faceCamera = true;
    public boolean faceCameraTilted = false;

    float rotation = 0f;

    public DecalEntity(TextureRegion reg) {

        decal = Decal.newDecal(reg, true);

        scale = 16f / Game.UNIT;

        position = new Vector3(0,0,0);

        decal.setScale(scale);
        decal.setPosition(position);

    }

    public void setPosition(float x, float y){
        position.set(x, position.y, y);
    }
    public void setPosition(float x, float y, float z){
        position.set(x, y, z);
    }


    public void setRotation(float f){
        rotation = f;
    }

    public void setRotationRandom(){
        rotation = new Random().nextFloat()*360f;
    }

    private Vector3 tmp = new Vector3();


    public void updateTransform(Camera cam){

        if(faceCamera){
            tmp.set(cam.direction).scl(-1);
            if(!faceCameraTilted)
               tmp.y = 0;
            decal.setRotation(tmp, Vector3.Y);
            decal.rotateY(rotation);
        }else{
            decal.setRotationY(rotation);
        }


        decal.setPosition(position);
        decal.translateY(.5f * Game.UNIT);

    }



}

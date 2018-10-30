package net.evgiz.ld40.game.decals;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import net.evgiz.ld40.game.Game;

import java.util.ArrayList;

public class DecalManager {

    private Camera camera;
    private ArrayList<DecalEntity> decals;

    private DecalBatch batch;
    private ShadedGroupStrategy groupStrategy;

    private Decal weaponDecal;
    private Vector3 weaponRotation;

    public DecalManager(Camera cam) {

        camera = cam;
        groupStrategy = new ShadedGroupStrategy(camera);

        batch = new DecalBatch(groupStrategy);

        decals = new ArrayList<DecalEntity>();

    }

    public void add(DecalEntity ent){
        decals.add(ent);
    }

    public void remove(DecalEntity ent){
        decals.remove(ent);
    }

    public DecalBatch getBatch(){
        return batch;
    }

    public void clear(){
        decals.clear();
    }

    public int getCount(){
        return decals.size();
    }

    public void render(){

        for(DecalEntity ent : decals){
            ent.updateTransform(camera);

            if(!camera.frustum.sphereInFrustum(ent.position, Game.UNIT))
                continue;

            batch.add(ent.decal);

        }

        batch.flush();

    }


}

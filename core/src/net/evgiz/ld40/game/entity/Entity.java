package net.evgiz.ld40.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import net.evgiz.ld40.game.Game;
import net.evgiz.ld40.game.decals.DecalEntity;
import net.evgiz.ld40.game.player.Player;
import net.evgiz.ld40.game.world.World;

public class Entity {

    Vector3 position;
    public boolean remove = false;

    DecalEntity decalEntity;

    TextureRegion texture[][];

    World world = null;
    int world_x, world_y;

    public boolean interactable = false;
    public boolean attackable = false;

    public Entity(){

    }

    public Entity(TextureRegion tex[][], int x, int y) {
        texture = tex;

        decalEntity = new DecalEntity(tex[0][0]);
        position = new Vector3(Game.UNIT*x, 0,Game.UNIT*y);

    }

    public Entity(TextureRegion tex[][], int x, int y, int tx, int ty) {
        texture = tex;

        decalEntity = new DecalEntity(tex[tx][ty]);
        position = new Vector3(Game.UNIT*x, 0,Game.UNIT*y);

    }

    public void bindWorldTile(World wrld, int tx, int ty){
        world = wrld;
        world_x = tx;
        world_y = ty;
        wrld.world[tx + ty*world.width] = 2;
    }

    public void interact(Player player){

    }

    public String getInteractText(Player player){
        return "Interact";
    }

    public Vector3 getPosition(){
        return position;
    }

    public void update(World world, Player player){

    }

    public void render(DecalBatch batch){

    }

    public void damage(Player player) {

    }
}

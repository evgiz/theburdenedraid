package net.evgiz.ld40.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import net.evgiz.ld40.game.Game;
import net.evgiz.ld40.game.player.Player;
import net.evgiz.ld40.game.world.World;

public class Door extends Entity{

    boolean open = false;
    float openProcess = 0.1f;

    Decal openDecal;

    public boolean locked = true;

    public Door(TextureRegion[][] tex, int x, int y) {
        super(tex, x, y, 0,1);

        interactable = true;
        openDecal = Decal.newDecal(tex[1][1], true);

        decalEntity.faceCamera = false;
        decalEntity.setRotation(-90f);
    }

    public void interact(Player player){
        if(locked && player.inventory.keys>0) {
            open = true;
            world.world[world_x + world_y * world.width] = 0;
            decalEntity.decal = openDecal;
            interactable = false;
            player.inventory.keys--;
            Game.audio.play("door");
        }
    }

    @Override
    public String getInteractText(Player player) {
        return (locked && player.inventory.keys==0) ? "Locked" : "Open (E)";
    }

    public void update(World world, Player player){

    }

}

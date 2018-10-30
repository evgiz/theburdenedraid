package net.evgiz.ld40.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.evgiz.ld40.game.Game;
import net.evgiz.ld40.game.player.Player;
import net.evgiz.ld40.game.world.World;

public class Enemy extends Entity {

    Vector3 direction = new Vector3();

    float damageTimer = 0f;

    Vector3 push;
    float pushScale = 0f;

    public int health = 3;

    public Enemy(TextureRegion[][] tex, int x, int y) {
        super(tex, x, y);

        attackable = true;

    }

    public Enemy(TextureRegion[][] tex, int x, int y, int tx, int ty) {
        super(tex, x, y, tx, ty);

        attackable = true;

    }

    public void damage(Player player){
        if(damageTimer>0)
            return;

        damageTimer = .5f;
        push = player.camera.direction;
        pushScale = 5f;
        health--;

        if(health<=0) {
            death(player);
        }else{
            Game.audio.play("hurt");
        }

    }

    public void death(Player player){
        Game.audio.play("death");
        remove = true;
    }

    @Override
    public void update(World world, Player player) {
        float dt  = Gdx.graphics.getDeltaTime();

        Vector2 moveVec = new Vector2();

        if(pushScale>0){
            moveVec.x += push.x * pushScale * dt * Game.UNIT;
            moveVec.y += push.z * pushScale * dt * Game.UNIT;
            pushScale -= dt * 5f;
        }

        if(damageTimer>0f){
            if(Math.floor(damageTimer*8)%2==1)
                decalEntity.decal.setColor(Color.RED);
            else
                decalEntity.decal.setColor(Color.WHITE);
            damageTimer -= dt;
        }else{
            decalEntity.decal.setColor(Color.WHITE);
        }


        tryMove(world, moveVec, false);

    }

    public void tryMove(World world, Vector2 moveVec, boolean doFine){
        //Move if free
        if(world.rectangleFree(position.x+moveVec.x, position.z, .75f, .75f))
            position.x += moveVec.x;
        else if(doFine){
            for (int i = 0; i < 10; i++) {
                if(world.rectangleFree(position.x+moveVec.x/10f, position.z, .75f, .75f))
                    position.x += moveVec.x/10f;
                else break;
            }
        }

        if(world.rectangleFree(position.x, position.z+moveVec.y, .75f, .75f))
            position.z += moveVec.y;
        else if(doFine){
            for (int i = 0; i < 10; i++) {
                if(world.rectangleFree(position.x, position.z+moveVec.y/10f, .75f, .75f))
                    position.z += moveVec.y/10f;
                else break;
            }
        }

    }

}

package net.evgiz.ld40.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.evgiz.ld40.game.Game;
import net.evgiz.ld40.game.player.Player;
import net.evgiz.ld40.game.world.World;

public class FlyingSkull extends Enemy {

    Vector3 direction = new Vector3();

    float speed = 2f;

    Decal decal1;
    Decal decal2;

    float animTimer = 0f;

    public FlyingSkull(TextureRegion[][] tex, int x, int y) {
        super(tex, x, y, 0, 2);

        decal1 = decalEntity.decal;
        decal2 = Decal.newDecal(tex[1][2], true);

        attackable = true;

        health = 2;

    }

    @Override
    public void death(Player player){

        super.death(player);

        player.entityManager.add(new LootEntity(Game.art.items, position.x, position.z));

    }

    @Override
    public void update(World world, Player player) {
        super.update(world, player);

        if (health <= 0) {
            decalEntity.decal.setColor(Color.DARK_GRAY);
            return;
        }

        float dt = Gdx.graphics.getDeltaTime();

        Vector2 moveVec = new Vector2();


        if (player.getPosition().dst2(position) < (Game.UNIT * 5) * (Game.UNIT * 5) && world.lineOfSightCheap(position, player.getPosition())) {
            animTimer += dt;
            if(animTimer>.3f){
                animTimer-=.3f;
                decalEntity.decal = (decalEntity.decal==decal1) ? decal2 : decal1;
            }
            direction.set(player.getPosition()).sub(position).nor();
            direction.scl(Gdx.graphics.getDeltaTime() * speed * Game.UNIT);
            direction.y = 0f;

            moveVec.x += direction.x;
            moveVec.y += direction.z;

            tryMove(world, moveVec, true);
        }


    }

}

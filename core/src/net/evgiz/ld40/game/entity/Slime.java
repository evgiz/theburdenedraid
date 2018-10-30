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

public class Slime extends Enemy {

    Vector3 direction = new Vector3();

    float speed = 3f;

    Decal idleDecal;
    Decal jumpDecal;
    Decal airDecal;

    float jumpTimer = 0f;
    float dy = 0f;
    float gravity = 10f * Game.UNIT;


    public Slime(TextureRegion[][] tex, int x, int y) {
        super(tex, x, y);

        idleDecal = decalEntity.decal;
        jumpDecal = Decal.newDecal(tex[1][0], true);
        airDecal = Decal.newDecal(tex[2][0], true);

        jumpTimer = Game.random.nextFloat() + .5f;

        attackable = true;

    }

    @Override
    public void death(Player player){

        Game.audio.play("death");
        position.y = 0;
        decalEntity.decal = idleDecal;
        idleDecal.setColor(Color.DARK_GRAY);
        attackable = false;
        remove = false;

        player.entityManager.add(new LootEntity(Game.art.items, position.x, position.z));


    }

    @Override
    public void update(World world, Player player) {
        super.update(world, player);

        if(health<=0) {
            decalEntity.decal.setColor(Color.DARK_GRAY);
            return;
        }

        float dt = Gdx.graphics.getDeltaTime();

        Vector2 moveVec = new Vector2();


        if (player.getPosition().dst2(position) < (Game.UNIT * 6) * (Game.UNIT * 6))
            jumpTimer -= dt;
        else
            return;

        if (jumpTimer <= 0f) {
            if (decalEntity.decal != airDecal) {
                decalEntity.decal = airDecal;
                dy = 2.5f * Game.UNIT;

                direction.set(player.getPosition()).sub(position).nor();
                direction.y = 0f;

                direction.rotate(Vector3.Y, Game.random.nextFloat() * 20f - 10f);
                direction.scl(Gdx.graphics.getDeltaTime() * speed * Game.UNIT);

            }

            moveVec.x += direction.x;
            moveVec.y += direction.z;

            position.y += dy * Gdx.graphics.getDeltaTime();
            dy -= Gdx.graphics.getDeltaTime() * gravity;

            if (position.y <= 0 && dy <= 0) {
                position.y = 0;
                dy = 0;
                jumpTimer = Game.random.nextFloat() * 1.25f + .5f;
            }
        } else if (jumpTimer < .2f) {
            decalEntity.decal = jumpDecal;
        } else {
            decalEntity.decal = idleDecal;
        }

        tryMove(world, moveVec, true);

    }

}

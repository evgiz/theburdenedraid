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

public class Demon extends Enemy {

    Vector3 direction = new Vector3();



    Decal decal1;
    Decal decal2;

    float animTimer = 0f;

    float attackTimer = 0f;
    float speed = 0f;

    public Demon(TextureRegion[][] tex, int x, int y) {
        super(tex, x, y, 4, 0);

        decal1 = decalEntity.decal;
        decal2 = Decal.newDecal(tex[5][0], true);

        attackable = true;

        health = 4;

    }

    @Override
    public void damage(Player player) {
        super.damage(player);

        direction.set(player.getPosition()).sub(position).nor();
        direction.scl(Game.UNIT);
        direction.y = 0f;

        speed /=2;

    }

    @Override
    public void death(Player player){

        super.death(player);

        player.entityManager.add(new LootEntity(Game.art.items, position.x, position.z));
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


        if (player.getPosition().dst2(position) < (Game.UNIT * 6) * (Game.UNIT * 6) && world.lineOfSightCheap(position, player.getPosition())) {
            attackTimer += dt;

            if(attackTimer>1f){
                if(speed==0){
                    direction.set(player.getPosition()).sub(position).nor();
                    direction.scl(Game.UNIT);
                    direction.y = 0f;
                }

                speed += dt*8f;
                if(speed>5)speed=5;

                decalEntity.decal = decal2;

                moveVec.x += direction.x * Gdx.graphics.getDeltaTime() * speed;
                moveVec.y += direction.z * Gdx.graphics.getDeltaTime() * speed;

                tryMove(world, moveVec, true);
            }

            if(attackTimer>2f){
                speed = 0;
                attackTimer = -Game.random.nextFloat();
                decalEntity.decal = decal1;
            }

        }else{
            speed = 0;
            attackTimer = 0;
            decalEntity.decal = decal1;
        }


    }

}

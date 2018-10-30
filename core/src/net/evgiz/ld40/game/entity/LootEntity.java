package net.evgiz.ld40.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import net.evgiz.ld40.game.Game;
import net.evgiz.ld40.game.decals.DecalEntity;
import net.evgiz.ld40.game.player.Player;
import net.evgiz.ld40.game.world.World;

public class LootEntity extends Entity {

    public int tx, ty;

    boolean isAttracted = false;
    float attractTime = 0f;

    float waitTime = .3f;

    final int LOOT_TYPES = 15;

    public LootEntity(TextureRegion[][] tex, int x, int y) {

        texture = tex;

        int numb = Game.random.nextInt(LOOT_TYPES)+1;
        tx = numb%4;
        ty = numb/4;

        decalEntity = new DecalEntity(tex[tx][ty]);
        position = new Vector3(Game.UNIT*x, 0,Game.UNIT*y);

        decalEntity.faceCameraTilted = true;
        decalEntity.decal.setScale(decalEntity.decal.getScaleX()/2f);
        position.y = -Game.UNIT/3f;

    }

    public LootEntity(TextureRegion[][] tex, float x, float y) {

        texture = tex;

        int numb = Game.random.nextInt(LOOT_TYPES)+1;
        tx = numb%4;
        ty = numb/4;

        decalEntity = new DecalEntity(tex[tx][ty]);
        position = new Vector3(x+Game.random.nextFloat()*Game.UNIT/2-Game.UNIT/4, 0, y+Game.random.nextFloat()*Game.UNIT/2-Game.UNIT/4);

        decalEntity.faceCameraTilted = true;
        decalEntity.decal.setScale(decalEntity.decal.getScaleX()/2f);
        position.y = -Game.UNIT/3f;

    }


    public void update(World wrld, Player player){
        if(waitTime>0){
            waitTime -= Gdx.graphics.getDeltaTime();
            return;
        }

        if(isAttracted){
            position.set(
                    MathUtils.lerp(position.x, player.getPosition().x, Gdx.graphics.getDeltaTime()*15f),
                    MathUtils.lerp(position.y, -Game.UNIT/4f, Gdx.graphics.getDeltaTime()*15f),
                    MathUtils.lerp(position.z, player.getPosition().z, Gdx.graphics.getDeltaTime()*15f)
            );

            attractTime += Gdx.graphics.getDeltaTime();

            float d = Game.UNIT/3f;
            if(attractTime>2f || player.getPosition().dst2(position) < d*d){
                remove = true;
                player.inventory.addLoot(tx,ty);
                Game.audio.play("loot");
            }



            return;
        }
        if(player.getPosition().dst2(position)<Game.UNIT*Game.UNIT){
            isAttracted = true;
        }
    }


}

package net.evgiz.ld40.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.evgiz.ld40.game.Game;
import net.evgiz.ld40.game.player.Player;
import net.evgiz.ld40.game.world.World;

public class Goal extends Entity {

    public Goal(TextureRegion[][] tex, int x, int y) {
        super(tex, x, y);
    }

    public void update(World world, Player player){
        decalEntity.decal.setPosition(0,10,10);

        if(position.dst2(player.getPosition())< (Game.UNIT*2)*(Game.UNIT*2)){

            player.inventory.gameComplete = true;

            for (Entity ent : player.entityManager.getEntities()) {
                if(ent instanceof LootEntity){
                    if(ent.position.dst2(player.getPosition())<(Game.UNIT*5)*(Game.UNIT*5)){
                        ((LootEntity)ent).isAttracted = true;
                        ((LootEntity)ent).waitTime = Game.random.nextFloat();
                    }
                }
            }

            remove = true;

            Game.gameComplete = true;

        }


    }

}

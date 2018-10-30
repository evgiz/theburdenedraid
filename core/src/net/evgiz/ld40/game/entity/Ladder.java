package net.evgiz.ld40.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.evgiz.ld40.game.Game;
import net.evgiz.ld40.game.player.Player;
import net.evgiz.ld40.game.world.World;

public class Ladder extends Entity{

    private String targetLevel = "nil";

    public boolean upLadder = false;

    public Ladder(TextureRegion[][] tex, int x, int y, String level) {
        super(tex, x, y, 3,1);

        interactable = true;

        targetLevel = level;
    }

    public Ladder(TextureRegion[][] tex, int x, int y, String level, boolean upLadder) {
        super(tex, x, y, upLadder ? 2 : 3,1);

        interactable = true;
        targetLevel = level;

        this.upLadder = upLadder;

    }

    public void interact(Player player){
        if(upLadder || targetLevel==null)return;

        player.getPosition().x = player.world.spawnx*Game.UNIT;
        player.getPosition().z = player.world.spawny*Game.UNIT;
        Game.audio.play("ladder");
        Game.changeLevel(targetLevel);
    }

    @Override
    public String getInteractText(Player player) {
        return (targetLevel==null || upLadder) ? "" : "Enter "+targetLevel+" (E)";
    }

    public void update(World world, Player player){
        if(upLadder){
            if(player.getPosition().dst2(position)>Game.UNIT*Game.UNIT*.5f*.5f)
                upLadder = false;
        }
    }

}


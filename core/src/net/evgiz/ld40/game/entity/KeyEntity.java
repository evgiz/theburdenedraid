package net.evgiz.ld40.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.evgiz.ld40.game.Game;
import net.evgiz.ld40.game.player.Player;

public class KeyEntity extends Entity {

    public KeyEntity(TextureRegion[][] tex, int x, int y, int tx, int ty) {
        super(tex, x, y, tx, ty);

        interactable = true;

        decalEntity.faceCameraTilted = true;

        decalEntity.decal.setScale(decalEntity.decal.getScaleX()/2f);

        position.y = -Game.UNIT/3f;

    }

    @Override
    public String getInteractText(Player player) {
        return "Pick Up (E)";
    }

    @Override
    public void interact(Player player) {

        remove = true;
        player.inventory.keys++;

        Game.audio.play("pickup");

    }
}

package net.evgiz.ld40.game.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Item {

    private final int tx, ty;

    public float rotation;
    public Sprite sprite;

    public Vector2 position;

    public Item(Texture src, int tx, int ty){
        this.tx = tx;
        this.ty = ty;

        int unit = 16;

        sprite = new Sprite(src, tx*unit, ty*unit, unit, unit);

        rotation = new Random().nextFloat()*40 - 20;
    }

}

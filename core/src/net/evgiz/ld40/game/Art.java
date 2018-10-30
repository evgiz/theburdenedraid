package net.evgiz.ld40.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Art {

    public TextureRegion[][] entities;
    public TextureRegion[][] items;

    public Art(){
        entities = createSheet("entities.png",16,16);
        items = createSheet("items.png",16,16);
    }

    private TextureRegion[][] createSheet(String src, int ww, int hh){

        Texture tex = new Texture(Gdx.files.internal(src));
        int w = tex.getWidth()/ww, h = tex.getHeight()/hh;

        TextureRegion reg[][]  = new TextureRegion[w][h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                reg[x][y] = new TextureRegion(tex, x*16, y*16, 16, 16);
            }
        }

        return reg;

    }

}

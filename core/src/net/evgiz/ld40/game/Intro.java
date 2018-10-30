package net.evgiz.ld40.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Intro {

    String introTxt[] = new String[]{
      "On his way down to raid the infamous dungeon,",
      "our brave hero realized something important.",
            "",
      "He forgot to bring a bag.",
            "",
      "\"Oh well,\" he said.",
            "",
      "\"Guess I'll just have to carry it.\""
    };

    String outroTxt[] = new String[]{
            "\"That went well,\" the hero thought to himself",
            "as he bathed in all the treasure he had found.",
            "",
            "\"Now I just have to get back!\"",
            "",
            "He was never seen again.",
            "",
            "",
            "The End"
    };

    String intro[] = introTxt;


    private float introTimer = 0f;
    private int introProgress = 0;

    boolean finishing = false;
    boolean finished = false;

    public float alphaOut = 3f;

    public boolean isOutro = false;

    Texture tex = new Texture(Gdx.files.internal("white.png"));

    public void update(){

        float dt = Gdx.graphics.getDeltaTime();

        if((Gdx.input.isKeyPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) && !Game.gameComplete)
           dt *= 3;

        if(isOutro && alphaOut<2f && !finishing) {
            alphaOut += dt;
            return;
        }

        if(!finishing) {
            introTimer += dt;
        }

        if(introTimer>1f){
            introTimer -= 1.75f;
            introProgress++;

            if(introProgress>intro.length) {
                introProgress--;
                finishing = true;
                introTimer = 1f;
            }

        }

        if(finishing){
            if(!Game.gameComplete)
                alphaOut -= Gdx.graphics.getDeltaTime();
            else
                alphaOut -= Gdx.graphics.getDeltaTime()/2f;

            if(alphaOut<=0){
                finished = true;
            }

        }

    }

    public void setOutro(){
        intro = outroTxt;
        alphaOut = -3f;
        isOutro = true;
    }

    public void render(SpriteBatch batch, BitmapFont font){

        float alpha = Math.max(0,Math.min(alphaOut, 1));

        batch.setColor(0,0,0, (Game.gameComplete && finishing) ? 1 : alpha);
        batch.draw(tex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        int sy = Gdx.graphics.getHeight()/2 + 8*16;

        for (int i = 0; i < introProgress; i++) {
            String s = intro[i];

            if(i == introProgress-1)
                font.setColor(.75f,.75f,.75f, introTimer*alpha);
            else
                font.setColor(.75f,.75f,.75f,1f*alpha);

            GlyphLayout layout = new GlyphLayout(font, s);
            font.setUseIntegerPositions(true);
            font.draw(batch, s, Gdx.graphics.getWidth()/2 - layout.width/2, sy - i*32);

        }

        batch.setColor(1,1,1,1);



    }


}

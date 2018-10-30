package net.evgiz.ld40.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Menu {

    public boolean begin = false;

    String opts[] = new String[]{
      "Play",
      "Difficulty: ",
      "Display Mode: ",
      "Look Sensitivity: "
    };

    String difficultyLevels[] = new String[]{
            "Easy", "Normal", "Hard", "Insta-Death"
    };

    public int difficulty = 1;

    String retroModes[] = new String[]{
            "Standard", "Retro", "Old School", "IT HURTS MY EYES"
    };

    public int retro = 1;

    String lookModes[] = new String[]{
            "Low", "Normal", "High"
    };

    public int lookSensitivity = 1;

    public SpriteBatch batch;
    public BitmapFont font;

    int selected = 0;

    private boolean releasedMouse = true;

    public boolean paused = false;

    float startDelay = 1f;

    public Menu() {

        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("font.fnt"));

        Texture cursor = new Texture(Gdx.files.internal("cursor.png"));
        cursor.getTextureData().prepare();

        Cursor curs = Gdx.graphics.newCursor(cursor.getTextureData().consumePixmap(), 0, 0);
        Gdx.graphics.setCursor(curs);
    }

    public void update(){

        if(startDelay>0)
            startDelay -= Gdx.graphics.getDeltaTime();

        if(Gdx.app.getType() != Application.ApplicationType.WebGL) {
            if (Math.abs(Gdx.input.getDeltaX()) > 0 || Math.abs(Gdx.input.getDeltaY()) > 0) {
                int sy = Gdx.graphics.getHeight() / 4 + 5 * 32;

                if (Gdx.input.getX() < Gdx.graphics.getWidth() / 2) {
                    int s = selected;
                    int y = Gdx.input.getY();

                    for (int i = 0; i < opts.length; i++) {
                        if (y > sy + i * 35 + 20)
                            selected = i;
                    }

                    if (y < sy || y > sy + 10 + opts.length * 35)
                        selected = -1;
                    else if (selected != s)
                        Game.audio.play("select");

                } else {
                    selected = -1;
                }
            }

            if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
                if(releasedMouse)
                    action(1);
                releasedMouse = false;
            }else
                releasedMouse = true;

        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            selected++;
            if(selected>opts.length-1)selected=0;
            Game.audio.play("select");
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            selected--;
            if(selected<0)selected= opts.length-1;
            Game.audio.play("select");
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||  Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            action(1);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.A) ||  Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
            action(-1);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.D) ||  Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
            action(1);
        }


    }

    public void action(int val){
        switch (selected){
            case 0:
                if(startDelay<0) {
                    begin = true;
                    Game.audio.play("play");
                }
                break;
            case 1:
                if(!paused) {
                    difficulty += val;
                    if (difficulty > difficultyLevels.length - 1)
                        difficulty = 0;
                    if (difficulty < 0)
                        difficulty = difficultyLevels.length - 1;
                    Game.audio.play("menu_action");
                }else
                    Game.audio.play("hurt");
                break;
            case 2:
                retro+=val;
                if(retro>retroModes.length-1)
                    retro=0;
                if(retro<0)
                    retro=retroModes.length-1;
                Game.audio.play("menu_action");
                break;
            case 3:
                lookSensitivity+=val;
                if(lookSensitivity>lookModes.length-1)
                    lookSensitivity=0;
                if(lookSensitivity<0)
                    lookSensitivity=lookModes.length-1;
                Game.audio.play("menu_action");
                break;
        }
    }

    int[] health = new int[]{8,5,3,1};

    public void render(){

        batch.begin();

        font.setColor(1,0,0,1);
        font.getData().setScale(1.5f);
        String str = "The Burdened Raid";

        int w = str.length()*24;

        font.draw(batch, str, 50, Gdx.graphics.getHeight()-60);

        font.getData().setScale(1f);

        font.setColor(.5f,.5f,.5f,1);

        str = "a game by Evgiz";

        font.draw(batch, str, 80, Gdx.graphics.getHeight()-110);


        font.setColor(1f,.5f, 0f, 1f);
        str = "Ludum Dare 40";
        font.draw(batch, str, Gdx.graphics.getWidth()-300, Gdx.graphics.getHeight()-70);

        font.getData().setScale(.5f);
        font.setColor(.5f,.5f, .5f, 1f);
        str = "\"The more you have, the worse it is\"";
        font.draw(batch, str, Gdx.graphics.getWidth()-325, Gdx.graphics.getHeight()-100);

        font.setColor(1,1,1, 1f);
        font.getData().setScale(1f);

        int sy = Gdx.graphics.getHeight()/4 + 64;
        for (int i = 0; i < opts.length; i++) {
            int addx = selected==i ? 20 : 0;

            if(selected != i)
                font.setColor(.3f,.3f,.3f,1f);
            else
                font.setColor(1f,1f,1f,1f);

            String txt = opts[i];

            switch (i){
                case 0:
                    if(paused)
                        txt = "Resume";
                    break;
                case 1:
                    txt = txt+difficultyLevels[difficulty];
                    break;
                case 2:
                    txt = txt+retroModes[retro];
                    break;
                case 3:
                    txt = txt+lookModes[lookSensitivity];
                    break;
            }

            if(i==1 && paused)
                if(i==selected)
                    font.setColor(.25f,.25f,.25f,1f);
                else
                    font.setColor(.1f,.1f,.1f,1f);

            font.draw(batch, txt, 60+addx, sy - i*35);
        }

        font.setColor(1,1,1,1);

        batch.end();
    }

}

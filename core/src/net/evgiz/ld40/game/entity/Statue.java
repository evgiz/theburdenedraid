package net.evgiz.ld40.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import net.evgiz.ld40.game.Game;
import net.evgiz.ld40.game.player.Player;
import net.evgiz.ld40.game.world.World;

public class Statue extends Entity {

    private boolean reading = false;

    int readProgress = 0;
    float readTimer = 0f;

    float soundTimer = 0f;

    Vector3 position;

    String readText[] = new String[]{
           "Stars Above Us All"," ",
            "Sun Shining In The East"," ",
           "And A Silent Moon To The West"," "
    };

    public Statue(TextureRegion[][] tex, int x, int y) {
        super(tex, x, y, 3, 4);

        interactable = true;
        position = new Vector3();

    }

    @Override
    public String getInteractText(Player player) {

        String txt = " ";

        if(readProgress < readText.length)
            txt = readText[readProgress];

        return reading ? txt.substring(0, Math.min(txt.length(), (int)(readTimer*txt.length()))) : "Read (E)";
    }

    @Override
    public void interact(Player player) {

        position.set(player.getPosition());
        reading = true;
        readProgress = 0;
        readTimer = 0;

    }

    @Override
    public void update(World world, Player player) {

        if(reading){
            player.getPosition().set(position);
            player.interactTarget = this;

            if(readTimer<1f && (readText[readProgress].equals(" ") == false))
                soundTimer += Gdx.graphics.getDeltaTime();

            if(soundTimer>0.1f){
                soundTimer-=0.1f;
                Game.audio.play("read");
            }

            readTimer += Gdx.graphics.getDeltaTime();

            if(readTimer>2f || (readText[readProgress].equals(" ") && readTimer>.5f)){
                readTimer=0f;
                readProgress++;
                if(readProgress>readText.length-1)
                    reading = false;
            }


        }

    }
}

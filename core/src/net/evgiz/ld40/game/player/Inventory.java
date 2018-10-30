package net.evgiz.ld40.game.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import net.evgiz.ld40.game.Game;

import java.util.ArrayList;

public class Inventory {

    Texture itemTexture;
    ArrayList<Item> items;

    public boolean gameComplete = false;

    public int totalLoot = 0;

    private class ItemPos {

        public Vector2 position;
        int priority = 0;

        public ItemPos(Vector2 vec, int p){
            position = vec;
            priority = p;
        }
    }
    ArrayList<ItemPos> itemPositions;


    public int keys = 0;

    public Inventory() {

        itemTexture = new Texture(Gdx.files.internal("items.png"));
        items = new ArrayList<Item>();

        itemPositions = new ArrayList<ItemPos>();

        Texture itemPos = new Texture(Gdx.files.internal("item_positions.png"));
        itemPos.getTextureData().prepare();

        Pixmap pixmap = itemPos.getTextureData().consumePixmap();


        for (int x = 0; x < pixmap.getWidth(); x++) {
            for (int y = 0; y < pixmap.getHeight(); y++) {
                int px = pixmap.getPixel(x,y);

                float xx = (float)x / (float)pixmap.getWidth();
                float yy = (float)y / (float)pixmap.getHeight();

                yy = 1.0f-yy;

                xx *= Gdx.graphics.getWidth();
                yy *= Gdx.graphics.getHeight();

                switch(px){
                    case 0xff:
                        itemPositions.add(new ItemPos(new Vector2(xx,yy), 0));
                        break;
                    case -16776961:
                        itemPositions.add(new ItemPos(new Vector2(xx,yy), 1));
                        break;
                    case 9764863:
                        itemPositions.add(new ItemPos(new Vector2(xx,yy), 2));
                        break;
                    case 16720383:
                        itemPositions.add(new ItemPos(new Vector2(xx,yy), 3));
                        break;
                    case -16748801:
                        itemPositions.add(new ItemPos(new Vector2(xx,yy), 4));
                        break;
                    case -2621185:
                        itemPositions.add(new ItemPos(new Vector2(xx,yy), 5));
                        break;

                    default:
                        //if(px != -256)
                        //System.out.println(px);
                        break;
                }

            }
        }


    }

    public void addLoot(int tx, int ty){
        totalLoot++;

        if(gameComplete){

            Item item = new Item(itemTexture, tx, ty);
            item.position = new Vector2(Game.random.nextFloat(), Game.random.nextFloat());
            items.add(item);

            item = new Item(itemTexture, tx, ty);
            item.position = new Vector2(Game.random.nextFloat(), Game.random.nextFloat());
            items.add(item);

            return;

        }else if(itemPositions.size() == 0){

            System.out.println("Out of item positions!");

            return;
        }
        int lowestPriority = 1000;

        ArrayList<ItemPos> options = new ArrayList<ItemPos>();

        for(ItemPos p : itemPositions){
            if(p.priority < lowestPriority){
                options.clear();
                lowestPriority = p.priority;
                options.add(p);
            }else if(p.priority == lowestPriority){
                options.add(p);
            }
        }


        Item item = new Item(itemTexture, tx, ty);
        ItemPos ip = options.get(Game.random.nextInt(options.size()));
        item.position = ip.position;

        items.add(item);
        itemPositions.remove(ip);


    }

    public void render(SpriteBatch batch, Player player){

        Sprite spr = null;
        Item item;

        float bobs[] = new float[]{
                (float)Math.cos(player.cameraController.bobbing*15f + .25),
                (float)Math.cos(player.cameraController.bobbing*15f + .5),
                (float)Math.cos(player.cameraController.bobbing*15f + .75)
        };


        for(int i=0; i<items.size();i++){
            item = items.get(i);
            spr = item.sprite;

            float bob = bobs[i%3] * 10f;

            spr.setScale(10f);
            spr.setRotation(item.rotation);
            spr.setPosition(item.position.x, item.position.y + bob);
            spr.draw(batch);
        }

    }

}

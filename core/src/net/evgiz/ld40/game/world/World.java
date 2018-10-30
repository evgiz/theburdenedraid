package net.evgiz.ld40.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import net.evgiz.ld40.game.Game;
import net.evgiz.ld40.game.decals.DecalEntity;
import net.evgiz.ld40.game.decals.DecalManager;
import net.evgiz.ld40.game.entity.EntityManager;
import net.evgiz.ld40.game.renderable.RenderData;

import java.util.ArrayList;
import java.util.Random;

public class World {

    Pixmap pixmap;

    public int world[];
    public int width;
    public int height;

    public int spawnx, spawny;

    public ArrayList<ModelInstance> modelInstances;

    private Texture tileTexture;
    private TextureRegion detailTexture[];

    private DecalManager decalManager;
    private EntityManager entityManager;

    public String previousLevel;
    public String currentLevel;

    public String levelOrder[] = new String[]{
            "Dungeons","Crypt","Ice Caves","Demon Lair"
    };

    public ArrayList<ModelInstance> specialBlocks;

    public World(DecalManager decalMan, EntityManager entityMan){

        entityManager = entityMan;
        decalManager = decalMan;

        tileTexture = new Texture(Gdx.files.internal("tiles.png"));

        Texture detailTex = new Texture(Gdx.files.internal("detail.png"));

        int w = detailTex.getWidth()/16;
        int h = detailTex.getHeight()/16;

        detailTexture = new TextureRegion[w*h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                detailTexture[x + y*w] = new TextureRegion(detailTex, x*16, y*16, 16, 16);
            }
        }

        load("Dungeons");

    }

    public int getTileType(String level){
        for (int i = 0; i < levelOrder.length; i++) {
            if(levelOrder[i].equals(level))
                return i;
        }

        return 0;
    }

    public String getNextLevel(){
        for (int i = 0; i < levelOrder.length-1; i++) {
            if(levelOrder[i].equals(currentLevel))
                return levelOrder[i+1];
        }

        return null;
    }

    public void load(String level){

        previousLevel = currentLevel;
        currentLevel = level;

        entityManager.getEntities().clear();
        decalManager.clear();

        Texture texture = new Texture(Gdx.files.internal("level/"+level+".png"));
        texture.getTextureData().prepare();

        pixmap = texture.getTextureData().consumePixmap();

        width = pixmap.getWidth();
        height = pixmap.getHeight();

        spawnx = 1;
        spawny = 1;

        world = new int[width * height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                world[x + y*width] = 0;

                int pix = pixmap.getPixel(x,y);
                int result = 0;

                boolean spawn = entityManager.spawn(this, pix, x, y);

                switch (pix) {
                    //Red
                    case -16776961:
                        spawnx = x;
                        spawny = y;
                        break;
                    //Special wall 1 pink-ish
                    case 1799323647:
                        result = 3;
                        break;
                    //Special wall 2 pink-ish
                    case 2134858751:
                        result = 4;
                        break;
                    //Special wall 3 pink-ish
                    case -696254465:
                        result = 5;
                        break;
                    //Sign, prevent details from spawning here
                    case 2140340223:
                        result = 2;
                        break;
                    //Black
                    case 0xff:
                        result = 1;
                        break;
                    default:
                        //Not working on HTML5, remove for that build
                        if(pix!=-1 && !spawn)
                            System.out.println(pix);
                        break;
                }

                if(result > 0)
                    world[x + y*width] = result;


            }
        }

        createModels();

        if(currentLevel.equals("Demon Lair"))
            return;

        addDetail();
    }

    private void addDetail(){
        int count = width*height / 3;

        Random r = new Random();
        DecalEntity ent;

        int x,y;

        for (int i = 0; i < count; i++) {
            x = r.nextInt(width);
            y = r.nextInt(height);

            if(getCollision(x,y) == 0) {
                ent = new DecalEntity(detailTexture[r.nextInt(2)]);
                ent.position.set(x * Game.UNIT, 0, y * Game.UNIT);
                decalManager.add(ent);
            }else{
                count++;
            }
        }


    }

    private void createModels(){

        int tileType = getTileType(currentLevel);

        ModelBuilder modelBuilder = new ModelBuilder();

        Texture tiles = new Texture(Gdx.files.internal("tiles.png"));
        Material material = new Material(TextureAttribute.createDiffuse(tiles));

        Model model = modelBuilder.createBox(Game.UNIT,Game.UNIT,Game.UNIT, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates);

        ModelInstance instance;

        modelInstances = new ArrayList<ModelInstance>();
        specialBlocks = new ArrayList<ModelInstance>();

        int block;

        Random random = new Random();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                block = world[x + y*width];

                if (block == 1){
                    instance = new ModelInstance(model);
                    instance.transform.translate(x* Game.UNIT,Game.UNIT/2f, y*Game.UNIT);
                    instance.transform.rotate(Vector3.Z, 90);
                    instance.userData = new RenderData(RenderData.ShaderType.FOG_TEXTURE, 0, tileType, 6, 6);
                    modelInstances.add(instance);
                }else if(block>2 && block<=5){
                    instance = new ModelInstance(model);
                    instance.transform.translate(x* Game.UNIT,Game.UNIT/2f, y*Game.UNIT);
                    instance.transform.rotate(Vector3.Z, 90);
                    instance.userData = new RenderData(RenderData.ShaderType.FOG_TEXTURE, 3+block-3, tileType, 6, 6);
                    modelInstances.add(instance);
                    specialBlocks.add(instance);
                }

            }
        }


        //Create floor

        Model floor = modelBuilder.createRect(
                0f,0f, (float) height*Game.UNIT,
                (float)width*Game.UNIT,0f, (float)height*Game.UNIT,
                (float)width*Game.UNIT, 0f, 0f,
                0f,0f,0f,
                1f,1f,1f,
                material,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates
        );


        instance = new ModelInstance(floor);
        instance.userData = new RenderData(RenderData.ShaderType.FOG_COLOR, 1, tileType, 6, 6, width, height);
        modelInstances.add(instance);


        instance = new ModelInstance(floor);
        instance.userData = new RenderData(RenderData.ShaderType.FOG_COLOR, 2, tileType, 6, 6, width, height);
        instance.transform.translate(0, Game.UNIT,0);
        instance.transform.rotate(Vector3.X, 180);
        instance.transform.translate(0,0,-(float)width* Game.UNIT);
        modelInstances.add(instance);


    }

    public int getCollision(int x, int y){
        int t = x + y*width;
        if(t < world.length)
            return world[t];
        else
            return 1;
    }

    public boolean rectangleFree(float centerx, float centery, float w, float h){
        //Upper left
        float x = centerx/Game.UNIT-w/2 + 0.5f;
        float y = centery/Game.UNIT-h/2 + 0.5f;

        if(getCollision((int)(x), (int)(y))!=0)
            return false;
        //Down left
        x = centerx/Game.UNIT-w/2 + 0.5f;
        y = centery/Game.UNIT+h/2 + 0.5f;

        if(getCollision((int)(x), (int)(y))!=0)
            return false;
        //Upper right
        x = centerx/Game.UNIT+w/2 + 0.5f;
        y = centery/Game.UNIT-h/2 + 0.5f;

        if(getCollision((int)(x), (int)(y))!=0)
            return false;
        //Down right
        x = centerx/Game.UNIT+w/2 + 0.5f;
        y = centery/Game.UNIT+h/2 + 0.5f;

        if(getCollision((int)(x), (int)(y))!=0)
            return false;

        return true;
    }

    public boolean lineOfSightCheap(Vector3 pos1, Vector3 pos2){

        Vector3 tmp = new Vector3();
        tmp.set(pos1);

        Vector3 dir = new Vector3();
        dir.set(tmp).sub(pos2);
        dir.y = 0;
        dir.nor();

        while(tmp.dst2(pos2) > (Game.UNIT/2f) * (Game.UNIT/2f)){
            tmp.mulAdd(dir, -Game.UNIT/4f);

            if(getCollision(tmp.x, tmp.z)!=0)
                return false;

        }


        return true;
    }

    public int getCollision(float x, float y){
        return getCollision((int)(x/Game.UNIT+0.5f), (int)(y/Game.UNIT+0.5f));
    }

    public int getCollision(Vector3 vec){
        return getCollision((int)(vec.x+0.5f), (int)(vec.z+0.5f));
    }



}

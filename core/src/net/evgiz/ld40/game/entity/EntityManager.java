package net.evgiz.ld40.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import net.evgiz.ld40.game.Game;
import net.evgiz.ld40.game.decals.DecalManager;
import net.evgiz.ld40.game.player.Player;
import net.evgiz.ld40.game.world.World;

import java.util.ArrayList;

public class EntityManager {


    DecalManager decalManager;

    private ArrayList<Entity> entities;

    private ArrayList<Lever> levers;
    private boolean solvedLevers = false;
    private boolean leverComplete = false;

    public EntityManager(DecalManager decal){

        decalManager = decal;

        levers = new ArrayList<Lever>();
        entities = new ArrayList<Entity>();

    }


    public ArrayList<Entity> getEntities(){
        return entities;
    }

    public boolean spawn(World world, int col, int x, int y){

        solvedLevers = false;

        if(x==0 && y==0)
            levers.clear();

        boolean result = true;


        switch (col){
            //Red spawn
            case -16776961:
                Entity ent = new Ladder(Game.art.entities, x, y, null, true);

                add(ent);
                break;
            //Blue finish
            case 2555903:
                Ladder lad = new Ladder(Game.art.entities, x, y, world.getNextLevel());
                add(lad);
                break;
            //Green slime
            case 16720383:
                add(new Slime(Game.art.entities, x , y));
                break;
            //Beige flying skull
            case -5079041:
                add(new FlyingSkull(Game.art.entities, x , y));
                break;
            //Gray bat
            case 1077952767:
                add(new Bat(Game.art.entities, x , y));
                break;
            //Red-beige demon
            case 2083598847:
                add(new Demon(Game.art.entities, x , y));
                break;
            //Cyan snowblock
            case 16777215:
               SnowWall snowWall = new SnowWall(Game.art.entities, x , y);
               snowWall.bindWorldTile(world, x, y);
               add(snowWall);
               break;
            //Orange door
            case -9830145:
                Door d = new Door(Game.art.entities, x, y);
                d.bindWorldTile(world, x, y);
                add(d);
                break;
            //Brown-yellow lever
            case 2137653503:
                Lever lever = new Lever(Game.art.entities, x, y);
                levers.add(lever);
                add(lever);
                break;
            case 2140340223:
                add(new Statue(Game.art.entities, x, y));
                break;
            //Yellow key
            case -2621185:
                KeyEntity item = new KeyEntity(Game.art.items, x, y, 0, 0);
                add(item);
                break;
            //Details, barrel/pot etc
            case -1600085761:
                Entity det = new Entity(Game.art.entities, x, y, world.getTileType(world.currentLevel), 5);
                add(det);
                break;

            //Lightblue final goal
            case 9764863:
                Goal goal = new Goal(Game.art.entities, x, y);
                add(goal);
            //Pink loot
            case -16720641:
                for (int i = 0; i < 5; i++) {
                    LootEntity le = new LootEntity(Game.art.items, x, y);
                    le.position.x += Game.random.nextFloat()*Game.UNIT*1.5f - Game.UNIT*.75f;
                    le.position.z += Game.random.nextFloat()*Game.UNIT*1.5f - Game.UNIT*.75f;
                    le.position.y += Game.random.nextFloat()*Game.UNIT*.15;
                    add(le);
                }
                break;
            default:
                result = false;
                break;

        }


        return result;

    }


    public void add(Entity ent){
        entities.add(ent);
        decalManager.add(ent.decalEntity);
    }

    public void remove(Entity ent){
        entities.remove(ent);
        decalManager.remove(ent.decalEntity);
    }

    public int getTargetLeverState(String level, int lever){
        if(level.equals("Dungeons")){
            switch(lever){
                case 0:
                    return -1;
                case 1:
                    return 1;
                case 2:
                    return 0;
            }
        }
        //No target
        return -2;
    }

    public void update(World world, Player player){

        Entity ent;
        for (int i = 0; i < entities.size(); i++) {
            ent = entities.get(i);
            ent.update(world, player);
            ent.decalEntity.setPosition(ent.position.x, ent.position.y, ent.position.z);

            if(ent.remove){
                remove(ent);
                i--;
            }
        }

        if(!solvedLevers && levers.size() > 0){
            boolean isCorrect = true;
            for (int i = 0; i < levers.size(); i++) {
                if(levers.get(i).getState() != getTargetLeverState(world.currentLevel, i)){
                    isCorrect = false;
                    break;
                }
            }

            if(isCorrect){
                solvedLevers = true;
                System.out.println("SOLVED LEVERS!");
                Game.audio.play("wall_open");
                Game.audio.play("success");
            }

        }

        if(solvedLevers && !leverComplete){
            Vector3 tmp = new Vector3();
            for (ModelInstance inst : world.specialBlocks) {
                inst.transform.translate(-Gdx.graphics.getDeltaTime()*Game.UNIT, 0, 0);
                inst.transform.getTranslation(tmp);
            }
            if(tmp.y < -Game.UNIT/1.9f){
                for (int x = 0; x < world.width*world.height; x++) {
                    if(world.world[x]>=3 && world.world[x]<=5)
                        world.world[x] = 0;
                }
                leverComplete = true;
            }

        }

    }


}


/*

    Great progress today! Next up is making Demon Lair (the last stage),
    and more content. After that, music, menus, end-game and polish!


    ..who needs sleep anyway
 */

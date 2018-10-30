package net.evgiz.ld40.game.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.evgiz.ld40.game.Game;
import net.evgiz.ld40.game.entity.Enemy;
import net.evgiz.ld40.game.entity.Entity;
import net.evgiz.ld40.game.entity.EntityManager;
import net.evgiz.ld40.game.world.World;

public class Player {

    public Camera camera;
    public World world;
    public Inventory inventory;
    public EntityManager entityManager;

    public CameraController cameraController;

    Vector3 position;

    float moveSpeed = 2f * Game.UNIT;

    private Vector3 moveVector;
    private Vector3 tmpVector;

    public float playerHeight = Game.UNIT * .4f;
    float colliderSize = .2f * Game.UNIT;

    boolean onGround = false;

    float gravityScale = 25 * Game.UNIT;
    float gravity = 0f;
    float jumpScale = 4f * Game.UNIT;

    Sprite weaponSprite;
    float weaponRotation;
    Vector2 weaponPosition;
    float attackAnimation;
    float weaponScaleY = 1f;
    boolean didAttack = true;
    boolean didPlayAudio = false;
    boolean mouseReleased = false;

    float footstepTimer;

    public int health = 5;
    float hurtTimer = 0f;
    Texture hurtTexture;
    Texture heart;

    private final float defaultWeaponRotation = 30f;
    private final float chargeWeaponRotation = -20f;
    private final float attackWeaponRotation = 120f;

    public Entity interactTarget;

    public Player(Camera cam, World wrld, EntityManager ents, Inventory inv, int lookSens) {

        inventory = inv;
        camera = cam;
        world = wrld;
        entityManager = ents;

        cameraController = new CameraController(camera, lookSens);

        position = new Vector3(world.spawnx * Game.UNIT,0f,world.spawny * Game.UNIT);
        moveVector = new Vector3();
        tmpVector = new Vector3();

        Texture weaponTex = new Texture(Gdx.files.internal("sword.png"));
        weaponSprite = new Sprite(weaponTex);

        weaponSprite.setOrigin(32, 0);
        weaponSprite.setScale(7.5f, 5f);


        weaponPosition = new Vector2(0,0);
        weaponRotation = defaultWeaponRotation;
        weaponSprite.setRotation(defaultWeaponRotation);

        hurtTexture = new Texture(Gdx.files.internal("red.png"));
        heart = new Texture(Gdx.files.internal("heart.png"));


    }

    public Vector3 getPosition() {
        return position;
    }

    public void update(EntityManager entityManager){

        move();
        gravity();
        attack(entityManager);
        interact(entityManager);

        cameraController.update();

        float weaponBob = (float)Math.cos(cameraController.bobbing * 15f + .15f) * 20f;
        weaponSprite.setPosition(Gdx.graphics.getWidth()-300+weaponPosition.x, -20+weaponBob+weaponPosition.y);
        weaponSprite.setScale(6f, Math.min(5f*weaponScaleY,8f));
        weaponSprite.setRotation(weaponRotation + (float)Math.cos(cameraController.bobbing*7.5f)*5f - 2.5f);


        if(hurtTimer>0){
            hurtTimer-=Gdx.graphics.getDeltaTime();

        }else {
            float hurtDistance = Game.UNIT * .5f;
            for (Entity ent : entityManager.getEntities()) {
                if (!(ent instanceof Enemy) || ((Enemy)ent).health<=0) continue;

                if (ent.getPosition().dst2(position) < hurtDistance * hurtDistance) {
                    health--;
                    hurtTimer = 1.5f;
                    Game.audio.play("player_hurt");
                }

            }
        }

    }

    private void interact(EntityManager entityManager) {

        interactTarget = null;

        float dist = 0f;
        float d = 0;

        Vector3 hitPos = new Vector3().set(position).mulAdd(camera.direction, Game.UNIT/2f);

        for(Entity ent : entityManager.getEntities()){
            d = ent.getPosition().dst2(position);
            if(ent.interactable && Game.collision.hitCircle(hitPos, ent.getPosition(), Game.UNIT/2f) && (dist==0 || d<dist)){


                interactTarget = ent;
                dist = d;


            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.E) && interactTarget!=null){
            interactTarget.interact(this);
        }

    }


    private void attack(EntityManager entityManager) {

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && attackAnimation<=0){
            attackAnimation = 1.0f;
            didAttack = false;
            didPlayAudio = false;
        }
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            if(mouseReleased && Gdx.input.isCursorCatched() && attackAnimation<=0){
                attackAnimation = 1.0f;
                didAttack = false;
                didPlayAudio = false;
            }
            mouseReleased = false;
        }else mouseReleased = true;

        if(attackAnimation>0f){
            attackAnimation -= Gdx.graphics.getDeltaTime()*4;

            if(attackAnimation>.3f) {
                weaponRotation = MathUtils.lerp(weaponRotation, chargeWeaponRotation, Gdx.graphics.getDeltaTime() * 8f);
                weaponPosition.set(
                        MathUtils.lerp(weaponPosition.x, 30, Gdx.graphics.getDeltaTime()*20f),
                        MathUtils.lerp(weaponPosition.y, -80, Gdx.graphics.getDeltaTime()*20f)
                );

                if(!didPlayAudio && attackAnimation<.8f){
                    didPlayAudio = true;
                    Game.audio.play("weapon");
                }

            }else {
                weaponRotation = MathUtils.lerp(weaponRotation, attackWeaponRotation, Gdx.graphics.getDeltaTime() * 15f);
                weaponPosition.set(
                        MathUtils.lerp(weaponPosition.x, -150, Gdx.graphics.getDeltaTime()*20f),
                        MathUtils.lerp(weaponPosition.y, 150, Gdx.graphics.getDeltaTime()*20f)
                );
                weaponScaleY = MathUtils.lerp(weaponScaleY, 2f, Gdx.graphics.getDeltaTime()*3);

                //In case of low framerate skip, unlikely
                if(!didPlayAudio){
                    didPlayAudio = true;
                    Game.audio.play("weapon");
                }
            }
        }else{
            weaponRotation = MathUtils.lerp(weaponRotation, defaultWeaponRotation, Gdx.graphics.getDeltaTime()*5f);
            weaponPosition.set(
                    MathUtils.lerp(weaponPosition.x, 0, Gdx.graphics.getDeltaTime()*10f),
                    MathUtils.lerp(weaponPosition.y, 0, Gdx.graphics.getDeltaTime()*10f)
            );
            weaponScaleY = MathUtils.lerp(weaponScaleY, 1f, Gdx.graphics.getDeltaTime()*10);
        }

        if(attackAnimation<.3f && !didAttack){
            didAttack = true;
            checkAttackHit(entityManager);
        }


    }


    private void checkAttackHit(EntityManager entityManager) {

        Entity closest = null;
        float dist = 0f;

        Vector3 tmp = new Vector3();

        for (Entity ent : entityManager.getEntities()) {
            if(!ent.attackable)continue;

            tmp.set(position).mulAdd(camera.direction, Game.UNIT*.75f);

            if(Game.collision.hitCircle(ent.getPosition(), tmp, Game.UNIT*.75f)){
                float d = position.dst2(ent.getPosition());
                if(closest == null || d<dist){
                    dist = d;
                    closest = ent;
                }
            }

        }

        if(closest != null){
            closest.damage(this);
        }

    }

    private void gravity(){


        gravity -= gravityScale*Gdx.graphics.getDeltaTime();
        position.y += gravity*Gdx.graphics.getDeltaTime();

        position.y = Math.max(0, position.y);
        position.y = Math.min(0.8f*Game.UNIT-playerHeight, position.y);

        onGround = (position.y == 0);

        if(onGround){

            if( Gdx.input.isKeyPressed(Input.Keys.SPACE)){
                //gravity = jumpScale;
            }else{
                gravity = 0f;

            }

        }



    }

    private void move(){

        float dt = Gdx.graphics.getDeltaTime();

        //if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
          //  dt*=3;

        moveVector.setZero();

        //Movement
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            tmpVector.set(camera.direction);
            tmpVector.y = 0;
            moveVector.add(tmpVector.nor().scl(dt * moveSpeed));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            tmpVector.set(camera.direction);
            tmpVector.y = 0;
            moveVector.add(tmpVector.nor().scl(dt * -moveSpeed));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            tmpVector.set(camera.direction).crs(camera.up);
            tmpVector.y = 0;
            moveVector.add(tmpVector.nor().scl(dt * -moveSpeed));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            tmpVector.set(camera.direction).crs(camera.up);
            tmpVector.y = 0;
            moveVector.add(tmpVector.nor().scl(dt * moveSpeed));
        }

        float colX = moveVector.x==0 ? 0 : (moveVector.x>0 ? 1 : -1);
        float colZ = moveVector.z==0 ? 0 : (moveVector.z>0 ? 1 : -1);

        if (world.getCollision(position.x + moveVector.x + colX * colliderSize, position.z) == 0)
            position.add(moveVector.x, 0, 0);
        if (world.getCollision(position.x, position.z + moveVector.z + colZ * colliderSize) == 0)
            position.add(0, 0, moveVector.z);


        camera.position.set(position.x, position.y + playerHeight, position.z);

        if(moveVector.len2() > 0){
            footstepTimer += Gdx.graphics.getDeltaTime();

            if(footstepTimer>0.45f){
                footstepTimer -= 0.45f;
                Game.audio.play("step");
            }

        }

    }

    public void render(SpriteBatch batch){
        weaponSprite.draw(batch);
    }

    public void renderUI(SpriteBatch batch, BitmapFont font, int downscale){

        if(interactTarget!=null) {
            String str = interactTarget.getInteractText(this);
            int w2 = str.length() * 8;
            font.setColor(1,1,1,1);
            font.draw(batch, str, Gdx.graphics.getWidth() / 2 - w2, Gdx.graphics.getHeight() / 2 + 50/downscale + 8);
        }

        int sx = Gdx.graphics.getWidth()/2 - health*18;
        for (int i = 0; i < inventory.keys; i++) {
            batch.draw(Game.art.items[0][0], 10 + i*50, Gdx.graphics.getHeight()-40, 48, 48);
        }


        for (int i = 0; i < health; i++) {
            batch.draw(heart, sx + i*36, Gdx.graphics.getHeight()-40, 32, 32);
        }

        if(hurtTimer>0 && (int)(hurtTimer*5)%2==0){
            batch.setColor(1,1,1,.25f);
            batch.draw(hurtTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(1,1,1,1);
        }

    }

}

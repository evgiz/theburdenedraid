package net.evgiz.ld40.game;

import com.badlogic.gdx.math.Vector3;

public class CollisionDetector {

    public boolean hitCircle(Vector3 pos1, Vector3 pos2, float hitRadius){
        return pos1.dst2(pos2)<=(hitRadius*hitRadius);
    }

}

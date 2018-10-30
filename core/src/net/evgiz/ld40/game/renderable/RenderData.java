package net.evgiz.ld40.game.renderable;

import com.badlogic.gdx.math.Vector2;

public class RenderData {

    public enum ShaderType{
        FOG_TEXTURE,
        FOG_COLOR
    }

    public ShaderType shaderType;
    public Vector2 tilemapOffset;
    public Vector2 tilemapSize = new Vector2(1.0f,1.0f);
    public Vector2 textureRepeat = new Vector2(1.0f,1.0f);

    public boolean boxFrustrumCulling = false;

    public RenderData(ShaderType type, float tx, float ty) {
        shaderType = type;
        tilemapOffset = new Vector2(tx,ty);
    }

    public RenderData(ShaderType type, float tx, float ty, float mx, float my) {
        shaderType = type;

        tilemapOffset = new Vector2(tx,ty);
        tilemapSize = new Vector2(mx,my);
    }

    public RenderData(ShaderType type, float tx, float ty, float mx, float my, float rx, float ry) {
        shaderType = type;

        tilemapOffset = new Vector2(tx,ty);
        tilemapSize = new Vector2(mx,my);
        textureRepeat = new Vector2(rx,ry);
    }

}

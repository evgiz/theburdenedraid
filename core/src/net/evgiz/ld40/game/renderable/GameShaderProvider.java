package net.evgiz.ld40.game.renderable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

public class GameShaderProvider extends DefaultShaderProvider {


    DefaultShader.Config fog_texture;
    DefaultShader.Config fog_diffuse;

    public GameShaderProvider() {

        fog_texture = new DefaultShader.Config(
                Gdx.files.internal("shaders/default_vertex.glsl").readString(),
                Gdx.files.internal("shaders/fog_texture.glsl").readString()
        );

        fog_diffuse = new DefaultShader.Config(
                Gdx.files.internal("shaders/default_vertex.glsl").readString(),
                Gdx.files.internal("shaders/fog_diffuse.glsl").readString()
        );

    }


    @Override
    public Shader createShader(Renderable renderable) {
        System.out.println(renderable.meshPart.size);
        if(renderable.userData instanceof RenderData) {

            RenderData data = (RenderData) renderable.userData;
            CustomShader shader = new CustomShader(renderable, getConfig(data.shaderType));

            return shader;

        }
        return super.createShader(renderable);
    }

    public DefaultShader.Config getConfig(RenderData.ShaderType type){
        switch(type){
            case FOG_TEXTURE:
                return fog_texture;
            case FOG_COLOR:
                return fog_diffuse;
            default:
                return fog_diffuse;
        }
    }

    private class CustomShader extends DefaultShader {

        public final int u_tilemapOffset = register("u_tilemapOffset");
        public final int u_tilemapSize = register("u_tilemapSize");
        public final int u_textureRepeat = register("u_textureRepeat");

        public CustomShader(Renderable renderable, Config config) {
            super(renderable, config);
        }


        @Override
        public void render(Renderable renderable, Attributes combinedAttributes) {
            RenderData renderData = (RenderData)renderable.userData;
            set(u_tilemapOffset, renderData.tilemapOffset);
            set(u_tilemapSize, renderData.tilemapSize);
            set(u_textureRepeat, renderData.textureRepeat);
            super.render(renderable, combinedAttributes);
        }
    }


}

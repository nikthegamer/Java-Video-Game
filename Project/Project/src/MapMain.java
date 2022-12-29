import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class MapMain {

    private Mesh background;

    private PlayerMain Player;
    public MapMain(){
        float[] vertices = new float[]{
//                points of index 0-4
//                x , y , z
                -10.0f,-10.0f*9.0f / 16.0f,0, //top left ( 0, 0 )
                -10.0f, 10.0f*9.0f / 16.0f,0,
                 0.0f,  10.0f*9.0f / 16.0f,0,
                 0.0f, -10.0f*9.0f / 16.0f,0
        };

        byte[] indices = new byte[]{
          0,1,2, //vertices triangle points of index 0, 1 and 2
          2,3,0
        };

        float[] tcs = new float[]{
                //x,y?
                0,1, //top right
                0,0, //top left
                1,0, //bottom right
                1,1 // bottom left
        };

        background = new Mesh(vertices,indices,tcs);
        Player = new PlayerMain();
    }

    public void update(){
        Player.update();
    }

    public void render(){
        Player.render();

        Shader.BG.enable();
        background.render();
        Shader.BG.disable();
    }
}

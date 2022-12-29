import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
public class PlayerMain
{
    private float SIZE = 1.0f;
    private Mesh player;
    private Controls Input;
    private Vector3f position = new Vector3f();

    public PlayerMain(){
        float[] vertices = new float[]{ //SIZE IS 1.0f
                //points of index 0-4
                //x , y , z
                -SIZE / 2.0f, -SIZE / 2.0f, 0,
                -SIZE / 2.0f, SIZE / 2.0f, 0,
                SIZE / 2.0f, SIZE / 2.0f, 0,
                SIZE / 2.0f, -SIZE / 2.0f, 0
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

        player = new Mesh(vertices,indices,tcs);
    }

    public void update(){
        if (Input.isKeyDown(GLFW.GLFW_KEY_UP))
            position.y+= 0.1f;
        if (Input.isKeyDown(GLFW.GLFW_KEY_DOWN))
            position.y-= 0.1f;
        if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT))
            position.x-= 0.1f;
        if (Input.isKeyDown(GLFW.GLFW_KEY_RIGHT))
            position.x+= 0.1f;
    }

    public void render(){
        Shader.Player.enable();
        Shader.Player.setUniformMat4f("ml_matrix", Matrix4f.translate(position));
        player.render();
        Shader.Player.disable();
    }

}

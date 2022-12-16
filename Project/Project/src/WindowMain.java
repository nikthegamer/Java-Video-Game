import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.lang.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
public class WindowMain {

    // The window handle
    private long window;
    private float PlayerX = 11.5f*2/1024-1.0f;
    private float PlayerY = 11.5f*2/1024-1.0f;
    private int winWidth = 1024;
    private int winHeight = 512;


    public void RunWindow() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);


        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //Draw / Color the window and the player

        window = glfwCreateWindow(winWidth, winHeight, "Game | RayCaster", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        //Key press handler

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) ->
        {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
        });

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos
                    (
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwSetWindowSize(window, winWidth,winHeight);
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
    }

    private void loop()
    {
        GL.createCapabilities();
        glClear(GL_COLOR_BUFFER_BIT);

        glViewport(0, 0, winWidth, winHeight);
        glClearColor(0.3f, 0.3f, 0.3f, 0.0f);

        PlayerMain playerPosDraw = new PlayerMain();
        playerPosDraw.setPlayerX(PlayerX);
        playerPosDraw.setPlayerY(PlayerY);

        MapMain MM = new MapMain();

        Mesh testMesh = new Mesh();
        testMesh.create(new float[]{
                -1,-1,0,
                0,1,0,
                1,-1,0
        });

        Shader shader = new Shader();
        shader.create("basic");
        shader.useShader();

        while ( !glfwWindowShouldClose(window) )
        {

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            MM.drawMap2D();
            playerPosDraw.drawPlayer();

            testMesh.draw();

            glfwSetKeyCallback(window, (window, key, scancode, action, mods) ->
            {
                if ( key ==  GLFW_KEY_A && action == GLFW_REPEAT)
                    playerPosDraw.setPlayerX(playerPosDraw.getPlayerX()-0.01f);
                if ( key ==  GLFW_KEY_A && action == GLFW_PRESS)
                    playerPosDraw.setPlayerX(playerPosDraw.getPlayerX()-0.01f);

                if ( key ==  GLFW_KEY_D && action == GLFW_PRESS)
                    playerPosDraw.setPlayerX(playerPosDraw.getPlayerX()+0.01f);
                if ( key ==  GLFW_KEY_D && action == GLFW_REPEAT)
                    playerPosDraw.setPlayerX(playerPosDraw.getPlayerX()+0.01f);

                if ( key ==  GLFW_KEY_W && action == GLFW_PRESS)
                    playerPosDraw.setPlayerY(playerPosDraw.getPlayerY()+0.01f);
                if ( key ==  GLFW_KEY_W && action == GLFW_REPEAT)
                    playerPosDraw.setPlayerY(playerPosDraw.getPlayerY()+0.01f);

                if ( key ==  GLFW_KEY_S && action == GLFW_PRESS)
                    playerPosDraw.setPlayerY(playerPosDraw.getPlayerY()-0.01f);
                if ( key ==  GLFW_KEY_S && action == GLFW_REPEAT)
                    playerPosDraw.setPlayerY(playerPosDraw.getPlayerY()-0.01f);

//                if ((key == GLFW_KEY_A && action == GLFW_PRESS) || (key == GLFW_KEY_W && action == GLFW_PRESS)){
//                    playerPosDraw.setPlayerX(playerPosDraw.getPlayerX()-0.01f);
//                    playerPosDraw.setPlayerY(playerPosDraw.getPlayerY()+0.01f);
//                }
//
//                if ((key == GLFW_KEY_A && action == GLFW_REPEAT) || (key == GLFW_KEY_W && action == GLFW_REPEAT)){
//                    playerPosDraw.setPlayerX(playerPosDraw.getPlayerX()-0.01f);
//                    playerPosDraw.setPlayerY(playerPosDraw.getPlayerY()+0.01f);
//                }
            });
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        testMesh.destroy();
        shader.destroy();
    }
}
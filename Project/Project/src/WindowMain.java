import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.lang.*;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
public class WindowMain implements Runnable{

    // The window handle
    private long window;
    private final int width = 1024;
    private final int height = 512;
    private Thread thread;
    private boolean running = true;
    private MapMain map;
    public Controls Input;

    public void start(){
        running = true;
        thread = new Thread(this, "Game");
        thread.start();
    }

    private void init(){
        if(!glfwInit()){
            return;
        }

        Input = new Controls();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        window = glfwCreateWindow(width, height,"Game | RayCaster", NULL, NULL);

        if (window == NULL){
            return;
        }


        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSetKeyCallback(window, Input.getKeyboardCallback());
        glfwShowWindow(window);

        GL.createCapabilities();

        glClearColor(0.3f,0.3f,0.3f, 1);
        glEnable(GL_DEPTH_TEST);
        System.out.println("OpenGL" + glGetString(GL_VERSION));
        Shader.loadAll();

        //---Projection Matrix---
        //left, right == x axis; bottom; top == y axis; near, far == z axis;
        //Matrix4f pr_matrix = Matrix4f.orthographic(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f  /16.0f, -1.0f, 1.0f);
        Matrix4f pr_matrix = Matrix4f.orthographic(-width, width, -height, height, -1.0f, 1.0f); //width is 1024 height is 512
        Shader.BG.setUniformMat4f("pr_matrix", pr_matrix);
        Shader.Player.setUniformMat4f("pr_matrix", pr_matrix);

        map = new MapMain();
    }
    public void run(){
        init();

        //---FPS---
        long lastTime = System.nanoTime();
        double delta = 0.0;
        double ns = 1000000000.0 / 60.0;
        long timer = System.currentTimeMillis();
        int updates = 0;
        int frames = 0;

        //---GL error detection---
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            System.out.println(error); //1282 == Invalid operation ( error )
        }
        while(running){
            long now = System.nanoTime();
            delta += ( now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1.0){
                update();
                updates++;
                delta--;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000){
                timer+=1000;
                System.out.println(updates + "ups, " + frames + " fps");
                updates = 0;
                frames = 0;
            }

            if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
                return;
            }
        }
        destroy();
    }

    private void update(){
        map.update();
        glfwPollEvents();
    }

    public void destroy(){
        Input.destroy();
        glfwWindowShouldClose(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }
    private void render(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        map.render();
        glfwSwapBuffers(window);
    }

}
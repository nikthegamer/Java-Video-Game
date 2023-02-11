import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;

public class WindowMain {

    private int width = 1024;
    private int height = 512;

    //-----FPS-----
    private long lastTime = System.nanoTime();
    private double delta = 0.0;
    private double ns = 1000000000.0 / 60.0;
    private long timer = System.currentTimeMillis();
    private int frames = 0;
    public int getFrames() {
        return frames;
    }
    public void run(){
        try{
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.setTitle("Hello World");
            Display.create();
        } catch(LWJGLException e){
            e.printStackTrace();
        }

        //Initiaize code OpenGL
        glClearColor(0.3f,0.3f,0.3f,1);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height,0,1.0f,-1.0f);
        glMatrixMode(GL_MODELVIEW);

        Render render = new Render();
        float pa = render.getPa();
        render.setData(((float)Math.cos(pa)*5), ((float)Math.sin(pa)*5), 300, 300);

        //render loop
        while(!Display.isCloseRequested()){
            //-----FPS-----
            long now = System.nanoTime();
            delta += ( now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1.0){
                delta--;
            }

            glClear(GL_COLOR_BUFFER_BIT);
            render.game();

            frames++;

            //-----FPS-----
            if (System.currentTimeMillis() - timer > 1000){
                timer+=1000;
                System.out.println(frames + " fps");
                frames = 0;
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
                Display.destroy();
                System.exit(0);
            }

            Display.setResizable(false);
            Display.update();
            Display.sync(60);
        }

        Display.destroy();
    }
}
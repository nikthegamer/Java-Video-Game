import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public class WindowMain {

    private final int width = 960;
    private final int height = 640;
    private final double ns = 1000000000.0 / 60.0;
    public int frames = 0;
    //-----FPS-----
    private long lastTime = System.nanoTime();
    private double delta = 0.0;
    private long timer = System.currentTimeMillis();

    public void run() {
        try {
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.setTitle("Hello World");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        //TEXTURE Initialization
        Textures textures = new Textures();
        //HOUSE
        Textures.houseWall = textures.readFileTexture("src/Textures/House/Wall.csv");
        Textures.houseDoor = textures.readFileTexture("src/Textures/House/Door.csv");
        Textures.houseWindow = textures.readFileTexture("src/Textures/House/Window.csv");
        Textures.houseCeiling = textures.readFileTexture("src/Textures/House/Ceiling.csv");
        Textures.houseFloor = textures.readFileTexture("src/Textures/House/Floor.csv");
        Textures.houseDecoOne = textures.readFileTexture("src/Textures/House/Wall_deco_1.csv");
        Textures.houseDecoTwo = textures.readFileTexture("src/Textures/House/Wall_deco_2.csv");
        Textures.Missing_Texture = textures.readFileTexture("src/Textures/Missing_Texture.csv");
        //OUTSIDE
        Textures.OutsideGround = textures.readFileTexture("src/Textures/Outside/OutsideGround.csv");
        Textures.OutsideSky = textures.readFileTexture("src/Textures/Outside/OutsideSky.csv");
        Textures.OutsideWall = textures.readFileTexture("src/Textures/Outside/OutsideWall.csv");
        //SEWER
        Textures.SewerCeiling = textures.readFileTexture("src/Textures/Sewer/SewerCeiling.csv");
        Textures.SewerFloor = textures.readFileTexture("src/Textures/Sewer/SewerFloor.csv");
        Textures.SewerWall = textures.readFileTexture("src/Textures/Sewer/SewerWall.csv");
        //TEXTURES

        //Initialize code OpenGL
        glClearColor(0.3f, 0.3f, 0.3f, 1);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, 1.0f, -1.0f);
        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_BLEND);// Enable the OpenGL Blending functionality
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glDepthMask(true);
        glEnable(GL_CULL_FACE);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        //glBlendFunc(GL_SRC_COLOR, GL_BLEND_COLOR);// Set the blend mode to blend our current RGBA with what is already in the buffer
        //In your case, this means that the source0 color will be multiplied by itself (therefore making the result darker).
        //glBlendFunc (GL_ONE, GL_ONE);
        //INITIALIZE SPRITE
        Render render = new Render();
        float pa = render.getPa();
        render.setData(((float) Math.cos(pa) * 5), ((float) Math.sin(pa) * 5), 150, 400);

        //render loop
        while (!Display.isCloseRequested()) {
            //-----FPS-----
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1.0) {
                delta--;
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            render.game();
            glCullFace(GL_BACK);//This will cause OpenGL to not render any polygons that are facing away from the camera.
            glFlush();
            frames++;

            //-----FPS-----
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println(frames + " fps");
                frames = 0;
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
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
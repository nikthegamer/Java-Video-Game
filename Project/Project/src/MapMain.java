import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class MapMain {

    private int mapX =8, mapY=8, mapS=64;

    private int map[] = {
                  1,1,1,1,1,1,1,1,
                  1,0,0,0,0,0,0,1,
                  1,0,0,0,0,0,0,1,
                  1,0,0,0,0,0,0,1,
                  1,0,0,0,0,1,0,1,
                  1,0,1,0,0,0,0,1,
                  1,0,1,0,0,0,0,1,
                  1,1,1,1,1,1,1,1
    };

    void drawMap2D(){
        int x,y,xo,yo;
        for (y=0;y<mapY;y++){
            for (x=0;x<mapX;x++){
                if (map[y*mapX+x]==1)
                {
                    glColor3f(1,1,1);
                }
                else
                {
                    glColor3f(0,0,0);
                }
                xo=x*mapS; yo=y*mapS;
                glBegin(GL_QUADS);
                glVertex2i(xo, yo);
                glVertex2i(xo, yo+mapS);
                glVertex2i(xo+mapS, yo+mapS);
                glVertex2i(xo+mapS, yo);
                glEnd();
            }
        }
    }
}

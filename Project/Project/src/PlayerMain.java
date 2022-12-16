import static org.lwjgl.opengl.GL11.*;

public class PlayerMain
{
    private float PlayerX, PlayerY; //player position

    void drawPlayer()
    {
        glColor3f(1,1,0); //Sets player color
        glPointSize(8);
        glBegin(GL_POINTS);
//        glVertex3f(0.5f, 0.5f, 0);
//        glVertex3f(-0.5f, 0.5f, 0);
//        glVertex3f(0, -0.5f, 0);
        glVertex2f(PlayerX, PlayerY); //Sets player pos
        glEnd();

        glLineWidth(3);
        glBegin(GL_LINES);
        glVertex2f(PlayerX, PlayerY);
        glEnd();
    }
    public void setPlayerX(float playerX)
    {
        PlayerX = playerX;
    }

    public void setPlayerY(float playerY)
    {
        PlayerY = playerY;
    }

    public float getPlayerX() {
        return PlayerX;
    }

    public float getPlayerY() {
        return PlayerY;
    }
}

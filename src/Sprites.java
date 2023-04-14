import static org.lwjgl.opengl.GL11.*;

public class Sprites {
    int type; // static, key, enemy
    int state; //on off
    int map; // texture
    float posX, posY, spriteZ; // position
    int textureID;
    int screenWidth = 960, screenHeight = 640;

    public Sprites(int type, int state, int map, float x, float y, float z, String filename) {
        this.textureID = new ImageTextures(filename).id;
        this.type = type;
        this.state = state;
        this.map = map;
        this.posX = x;
        this.posY = y;
        this.spriteZ = z;
    }

    float bandAid(float angle) {
        return angle > 180 ? -(360 - angle) : angle;
    }

    float degToRad(float deg) {
        return (float) ((deg * Math.PI) / 180);
    }

    public final void renderSprite(float px, float py, float pa, int[] depth) {

        glEnable(GL_TEXTURE_2D);

        float sx = this.posX - px;
        float sy = this.posY - py;
        float sz = 20;

        float CS = (float) Math.cos(degToRad(pa)), SN = (float) Math.sin(degToRad(pa));
        float a = sy * CS + sx * SN;
        float b = sx * CS - sy * SN;
        sx = a;
        sy = b;

        sx = (sx * 108.0f / sy) + (120 / 2); //I love magic numbers!!!
        sy = (sz * 108.0f / sy) + (80 / 2);

        float spriteZ = this.spriteZ;
        float spriteX = this.posX - px;
        float spriteY = this.posY - py;

        // calculate sprite's distance from the player
        float spriteDist = (float) Math.sqrt(spriteX * spriteX + spriteY * spriteY);

        // calculate sprite's angle relative to the player's viewing angle
        float spriteAngle = bandAid((float) Math.toDegrees(degToRad(bandAid(pa)) + (Math.atan2(spriteY, spriteX))));
        if (spriteAngle < -180 && spriteX < 0 && spriteY < 0) spriteAngle += 360;

        // calculate the sprite's size on the screen
        float spriteSize = (12 * (float) ((screenWidth / 2) / Math.tan(degToRad(60 / 2))) / spriteDist);

        // calculate the top-left corner of the sprite on the screen
        float spriteScreenX = (screenWidth / 2) + spriteAngle * (screenWidth / 60) - (spriteSize / 2);
        float spriteScreenY = (screenHeight / 2) - (spriteSize / 2);

        // draw the sprite as a purple square
        // Enable texturing
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, this.textureID);
        glEnable(GL_DEPTH_TEST);
        if (sx > 0 && sx < 120 && b < depth[(int) sx]) {
            glBegin(GL_QUADS);
            System.out.println(spriteAngle);
            glColor3f(1.0f, 1.0f, 1.0f);
            glTexCoord2f(0, 0);
            glVertex2f(spriteScreenX, spriteScreenY);
            glTexCoord2f(0, 1);
            glVertex2f(spriteScreenX, spriteScreenY + spriteSize);
            glTexCoord2f(1, 1);
            glVertex2f(spriteScreenX + spriteSize, spriteScreenY + spriteSize);
            glTexCoord2f(1, 0);
            glVertex2f(spriteScreenX + spriteSize, spriteScreenY);
            glEnd();
        }
        // disable texture mapping
        glDisable(GL_TEXTURE_2D);
        glFinish(); // Block until all GL executions are completed.
    }
}

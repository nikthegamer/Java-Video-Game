import static org.lwjgl.opengl.GL11.*;

public class Sprites {
    int type; // static, key, enemy
    int state; //on off
    int map; // texture
    float posX, posY, posZ; // position
    float width = 5, height = 5;
    String filename;
    int textureID;

    public Sprites(int type, int state, int map, float x, float y, float z, String filename) {
        this.textureID = new ImageTextures(filename).id;
        this.type = type;
        this.state = state;
        this.map = map;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

//    void bindImage(String filename){
//        ImageTextures test = new ImageTextures(filename);
//        test.bind();
//    }

    int screenWidth = 960,screenHeight = 640;
    float bandAid(float angle) {
        return angle > 180 ? -(360 - angle) : angle;
    }
    float degToRad(float deg) {
        return (float) ((deg * Math.PI) / 180);
    }

    public final void renderSprite(float px, float py, float pa){
        //System.out.println(px + " " + py + " " + spriteOne.x + " " + spriteOne.y + " " + pa);
        // calculate sprite's relative position to the player

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
        glBegin(GL_QUADS);
        glColor3f(1.0f,1.0f,1.0f);
        glTexCoord2f(0, 0);
        glVertex2f(spriteScreenX, spriteScreenY);
        glTexCoord2f(0, 1);
        glVertex2f(spriteScreenX, spriteScreenY + spriteSize);
        glTexCoord2f(1, 1);
        glVertex2f(spriteScreenX + spriteSize, spriteScreenY + spriteSize);
        glTexCoord2f(1, 0);
        glVertex2f(spriteScreenX + spriteSize, spriteScreenY);
        glEnd();

        // disable texture mapping
        glDisable(GL_TEXTURE_2D);
        glFinish(); // Block until all GL executions are completed.
        glEnd();
    }
}

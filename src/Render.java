import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Render {
    private final int mapX = 8;
    private final int mapY = 8;
    private final int mapS = 64;
    public int[] depth = new int[120];
    int red, green, blue;
    //---DRAW RAYS---
    int[] mapW = {
            2, 2, 7, 2, 2, 5, 2, 2,
            6, 0, 0, 0, 6, 0, 0, 2,
            2, 0, 0, 0, 4, 0, 0, 3,
            2, 6, 4, 2, 5, 6, 4, 2,
            2, 0, 0, 0, 4, 0, 0, 2,
            3, 0, 0, 0, 5, 0, 0, 4,
            2, 0, 0, 0, 2, 0, 0, 2,
            2, 2, 2, 3, 4, 2, 2, 2
    };
    int[] mapF = {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 0,
            0, 0, 0, 1, 0, 0, 0, 0,
            0, 0, 0, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };
    int[] mapC = {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0
    };
    //---DRAW SPRITES---
    int state = 1;
    List<Sprites> spritesList = new ArrayList<Sprites>() {{
        //pa < 180
        add(new Sprites(1, 0, 0, 150, 314, 0.5f, "src/Textures/Textures/TakeThemOutTakeThemOutTheWormsTakeThemOut.png"));
        //180 < pa
        add(new Sprites(1, 0, 0, 350, 170, 0.7f, "src/Textures/unknown.png"));
    }};
    //????
    private float px, py, pdx, pdy, pa;

    public void setData(float pdx, float pdy, float px, float py) {
        this.pdx = pdx;
        this.pdy = pdy;
        this.px = px;
        this.py = py;
    }

    public float getPa() {
        return pa;
    }

    float cos(float a) {
        return (float) Math.cos(a);
    }

    float sin(float a) {
        return (float) Math.sin(a);
    }

    float degToRad(float deg) {
        return (float) ((deg * Math.PI) / 180);
    }

    public void game() {
        buttons();
        drawRays3D();
        drawSpritesTest();
    }
    void drawSpritesTest() {
        for (var sprite : spritesList) {
            sprite.renderSprite(px, py, pa, depth);
        }
    }

    //---RENDER MAP----
    private float FixAng(float a) {
        if (a > 359) {
            a -= 360;
        }
        if (a < 0) {
            a += 360;
        }
        return a;
    }

    void drawRays3D() {
        int r, mx, my, mp, dof, side;
        float vx, vy, rx, ry, ra, xo = 0, yo = 0, disV, disH, zBuffer = 1f;

        ra = FixAng(pa + 30);                                                              //ray set back 30 degrees

        for (r = 0; r < 120; r++) {
            int vmt = 0, hmt = 0;                                                              //vertical and horizontal map texture number
            //---Vertical---
            dof = 0;
            side = 0;
            disV = 100000;
            float Tan = (float) Math.tan(degToRad(ra));
            if (cos(degToRad(ra)) > 0.001) {
                rx = (((int) px >> 6) << 6) + 64;
                ry = (px - rx) * Tan + py;
                xo = 64;
                yo = -xo * Tan;
            }//looking left
            else if (cos(degToRad(ra)) < -0.001) {
                rx = (float) ((((int) px >> 6) << 6) - 0.0001);
                ry = (px - rx) * Tan + py;
                xo = -64;
                yo = -xo * Tan;
            }//looking right
            else {
                rx = px;
                ry = py;
                dof = 8;
            }                                                  //looking up or down. no hit

            while (dof < 8) {
                mx = (int) (rx) >> 6;
                my = (int) (ry) >> 6;
                mp = my * mapX + mx;
                if (mp > 0 && mp < mapX * mapY && mapW[mp] > 0) {
                    vmt = mapW[mp] - 1;
                    dof = 8;
                    disV = cos(degToRad(ra)) * (rx - px) - sin(degToRad(ra)) * (ry - py);
                }//hit
                else {
                    rx += xo;
                    ry += yo;
                    dof += 1;
                }                                               //check next horizontal
            }
            vx = rx;
            vy = ry;

            //---Horizontal---
            dof = 0;
            disH = 100000;
            Tan = 1.0f / Tan;
            if (sin(degToRad(ra)) > 0.001) {
                ry = (((int) py >> 6) << 6) - 0.0001f;
                rx = (py - ry) * Tan + px;
                yo = -64;
                xo = -yo * Tan;
            }//looking up
            else if (sin(degToRad(ra)) < -0.001) {
                ry = (((int) py >> 6) << 6) + 64;
                rx = (py - ry) * Tan + px;
                yo = 64;
                xo = -yo * Tan;
            }//looking down
            else {
                rx = px;
                ry = py;
                dof = 8;
            }                                                   //looking straight left or right

            while (dof < 8) {
                mx = (int) (rx) >> 6;
                my = (int) (ry) >> 6;
                mp = my * mapX + mx;
                if (mp > 0 && mp < mapX * mapY && mapW[mp] > 0) {
                    hmt = mapW[mp] - 1;
                    dof = 8;
                    disH = cos(degToRad(ra)) * (rx - px) - sin(degToRad(ra)) * (ry - py);
                }//hit
                else {
                    rx += xo;
                    ry += yo;
                    dof += 1;
                }                                               //check next horizontal
            }

            int spriteState = 1;
            float shade = 1;
            glColor3f(0, 0.8f, 0);
            if (disV < disH) {
                hmt = vmt;
                shade = 0.5f;
                rx = vx;
                ry = vy;
                disH = disV;
                glColor3f(0, 0.6f, 0);
            }//horizontal hit first

            int ca = (int) FixAng(pa - ra);
            disH = disH * cos(degToRad(ca));                            //fix fisheye
            int lineH = (int) ((mapS * 640) / (disH));
            float ty_step = 32.0f / (float) lineH;
            float ty_off = 0;
            if (lineH > 640) {
                ty_off = (lineH - 640) / 2.0f;
                lineH = 640;
            }                            //line height and limit
            int lineOff = 320 - (lineH >> 1);                                               //line offset

            depth[r] = (int) disH; //save this line's depth
            //---Draw walls---
            int y;
            float ty = ty_off * ty_step;//+hmt*32;
            float tx;


            if (shade == 1) { //VERTICAL
                tx = (int) (rx / 2.0) % 32;
                if (ra > 180) {
                    tx = 31 - tx;
                }
                //System.out.println("pa = " + FixAng(pa));
            } else { //HORIZONTAL
                tx = (int) (ry / 2.0) % 32;
                if (ra > 90 && ra < 270) {
                    tx = 31 - tx;
                }
            }
            for (y = 0; y < lineH; y++) {
                int pixel = ((int) ty * 32 + (int) tx) * 3 + (hmt * 32 * 32 * 3);
                //System.out.println("px : " + px); //top left = 84  / bottom right = 84
                //System.out.println("py : " + py); //top left = 429   / bottom right = 429
                if (hmt == 0) {
                    red = (int) (Textures.Missing_Texture[(pixel) % 3072] * shade);
                    green = (int) (Textures.Missing_Texture[(pixel + 1) % 3072] * shade);
                    blue = (int) (Textures.Missing_Texture[(pixel + 2) % 3072] * shade);
                }
                if (hmt == 1) {
                    red = (int) (Textures.houseWall[(pixel) % 3072] * shade);
                    green = (int) (Textures.houseWall[(pixel + 1) % 3072] * shade);
                    blue = (int) (Textures.houseWall[(pixel + 2) % 3072] * shade);
                }
                if (hmt == 2) {
                    red = (int) (Textures.houseWindow[(pixel) % 3072] * shade);
                    green = (int) (Textures.houseWindow[(pixel + 1) % 3072] * shade);
                    blue = (int) (Textures.houseWindow[(pixel + 2) % 3072] * shade);
                }
                if (hmt == 3) {
                    red = (int) (Textures.houseDoor[(pixel) % 3072] * shade);
                    green = (int) (Textures.houseDoor[(pixel + 1) % 3072] * shade);
                    blue = (int) (Textures.houseDoor[(pixel + 2) % 3072] * shade);
                }
                if (hmt == 4) {
                    red = (int) (Textures.houseDecoOne[(pixel) % 3072] * shade);
                    green = (int) (Textures.houseDecoOne[(pixel + 1) % 3072] * shade);
                    blue = (int) (Textures.houseDecoOne[(pixel + 2) % 3072] * shade);
                }
                if (hmt == 5) {
                    red = (int) (Textures.houseDecoTwo[(pixel) % 3072] * shade);
                    green = (int) (Textures.houseDecoTwo[(pixel + 1) % 3072] * shade);
                    blue = (int) (Textures.houseDecoTwo[(pixel + 2) % 3072] * shade);
                }
                if (hmt == 6) {
                    red = (int) (Textures.houseDoor[(pixel) % 3072] * shade);
                    green = (int) (Textures.houseDoor[(pixel + 1) % 3072] * shade);
                    blue = (int) (Textures.houseDoor[(pixel + 2) % 3072] * shade);
                }
                glPointSize(8);
                glColor3ub((byte) red, (byte) green, (byte) blue);
                glBegin(GL_POINTS);
                glVertex3f(r * 8, y + lineOff, zBuffer);
                glEnd();

                ty += ty_step;
            }
            //---Draw floor---
            for (y = (lineOff + lineH); y < 640; y++) {
                float dy = y - (640 / 2.0f), deg = degToRad(ra), raFix = cos(degToRad(FixAng(pa - ra)));
                tx = px / 2 + cos(deg) * 158 * 2 * 32 / dy / raFix;
                ty = py / 2 - sin(deg) * 158 * 2 * 32 / dy / raFix;
                mp = mapF[(int) (ty / 32.0) * mapX + (int) (tx / 32.0)] * 32 * 32;
                int pixel = (((int) (ty) & 31) * 32 + ((int) (tx) & 31)) * 3 + mp * 3;

                red = Textures.houseFloor[(pixel) % 3072];
                green = Textures.houseFloor[(pixel + 1) % 3072];
                blue = Textures.houseFloor[(pixel + 2) % 3072];
                glPointSize(8);
                glColor3ub((byte) red, (byte) green, (byte) blue);
                glBegin(GL_POINTS);
                glVertex3f(r * 8, y, 1);
                glEnd();

                //draw ceiling
                mp = mapC[(int) (ty / 32.0) * mapX + (int) (tx / 32.0)] * 32 * 32;
                pixel = (((int) (ty) & 31) * 32 + ((int) (tx) & 31)) * 3 + mp * 3;
                red = Textures.houseCeiling[(pixel) % 3072];
                green = Textures.houseCeiling[(pixel + 1) % 3072];
                blue = Textures.houseCeiling[(pixel + 2) % 3072];

                glPointSize(8);
                glColor3ub((byte) (red * 0.7), (byte) (green * 0.7), (byte) (blue * 0.7));
                glBegin(GL_POINTS);
                glVertex3f(r * 8, 640 - y, 1);
                glEnd();

            }
            ra = FixAng(ra - 0.5f);
        }
    }

    void buttons() {

        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            pa += 3;
            pa = FixAng(pa);
            pdx = cos(degToRad(pa));
            pdy = -sin(degToRad(pa));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            pa -= 3;
            pa = FixAng(pa);
            pdx = cos(degToRad(pa));
            pdy = -sin(degToRad(pa));
        }
        int xo = 0;
        if (pdx < 0) {
            xo = -20;
        } else {
            xo = 20;
        }
        int yo = 0;
        if (pdy < 0) {
            yo = -20;
        } else {
            yo = 20;
        }

        int ipx = (int) (px / 64.0f), ipx_add_xo = (int) ((px + xo) / 64.0f), ipx_sub_xo = (int) ((px - xo) / 64.0f);
        int ipy = (int) (py / 64.0f), ipy_add_yo = (int) ((py + yo) / 64.0f), ipy_sub_yo = (int) ((py - yo) / 64.0f);

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            if (mapW[ipy * mapX + ipx_add_xo] == 0) {
                px += 3 * pdx;
            } //Collision detection
            if (mapW[ipy_add_yo * mapX + ipx] == 0) {
                py += 3 * pdy;
            } //If map[x] == 1 don't move
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            if (mapW[ipy * mapX + ipx_sub_xo] == 0) {
                px -= 3 * pdx;
            }
            if (mapW[ipy_sub_yo * mapX + ipx] == 0) {
                py -= 3 * pdy;
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_E)) { //interact button
            if (mapW[ipy_add_yo * mapX + ipx_add_xo] == 4) {
                mapW[ipy_add_yo * mapX + ipx_add_xo] = 0;
            }

            if (mapW[ipy_add_yo * mapX + ipx_add_xo] == 7) {
                mapW = new int[]{
                        2, 2, 7, 2, 2, 5, 2, 2,
                        6, 0, 0, 0, 0, 0, 0, 2,
                        2, 0, 0, 0, 0, 0, 0, 3,
                        2, 0, 0, 0, 0, 2, 4, 2,
                        2, 0, 0, 0, 0, 0, 0, 2,
                        3, 0, 0, 0, 0, 0, 0, 3,
                        2, 0, 0, 0, 0, 0, 0, 2,
                        2, 2, 2, 3, 6, 2, 2, 2
                };
            }
        }
    }

    void drawMap2D() {
        int y, x, yo, xo;
        for (y = 0; y < mapY; y++) {
            for (x = 0; x < mapX; x++) {
                if (mapW[y * mapX + x] > 0) {
                    glColor3f(1, 1, 1);
                } else glColor3f(0, 0, 0);
                xo = x * mapS;
                yo = y * mapS;
                glBegin(GL_QUADS);
                glVertex2i(xo + 1, yo + 1);
                glVertex2i(xo + 1, yo + mapS - 1);
                glVertex2i(xo + mapS - 1, yo + mapS - 1);
                glVertex2i(xo + mapS - 1, yo + 1);
                glVertex2i(xo + 1, yo + 1);
                glVertex2i(xo + 1, yo + mapS - 1);
                glVertex2i(xo + mapS - 1, yo + mapS - 1);
                glVertex2i(xo + mapS - 1, yo + 1);
                glEnd();
            }
        }
    }
}
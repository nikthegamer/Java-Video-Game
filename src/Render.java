import org.lwjgl.input.Keyboard;

import static org.lwjgl.opengl.GL11.*;

public class Render {
    //---DRAW RAYS---

    private final double PI = Math.PI;
    private final double P2 = PI / 2;
    private final double P3 = 3 * PI / 2;
    private final float DR = 0.0174533f; //1 degree in radians
    //---RENDER PLAYER---
    private final int mapX = 8;
    private final int mapY = 8;
    private final int mapS = 64;
    int[] mapW = {
            2, 2, 7, 2, 2, 5, 2, 2,
            6, 0, 0, 0, 2, 0, 0, 2,
            2, 0, 0, 0, 4, 0, 0, 3,
            2, 2, 4, 2, 5, 2, 4, 2,
            2, 0, 0, 0, 0, 0, 0, 2,
            3, 0, 0, 0, 0, 0, 0, 3,
            2, 0, 0, 0, 0, 0, 0, 2,
            2, 2, 2, 3, 6, 2, 2, 2
    };
    int[] mapF = {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 0, 0, 0,
            0, 0, 0, 1, 0, 0, 0, 0,
            0, 0, 0, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0
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
    private float px, py, pdx, pdy, pa;
    private int r, mx, my, mp, dof;
    private float rx, ry, ra, xo, yo, disT, shade = 1;

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

    public void game() {
        buttons();
        drawRays3D();
    }

    //---RENDER MAP----

    void drawRays3D() {
        ra = pa - DR * 30;
        if (ra < 0) {
            ra += 2 * PI;
        } else if (ra > 2 * PI) {
            ra -= 2 * PI;
        }
        for (r = 0; r < 120; r++) //Num of rays
        {
            int vmt = 0, hmt = 0;
            dof = 0;
            float disH = 1000000000, hx = px, hy = py;
            float aTan = (float) (-1 / Math.tan(ra));

            //----CHECK HORIZONTAL LINE---
            if (ra > PI) { //looking up
                ry = (((int) py >> 6) << 6) - 0.0001f;
                rx = (py - ry) * aTan + px;
                yo = -64;
                xo = -yo * aTan;
            }

            if (ra < PI) { //looking down
                ry = (((int) py >> 6) << 6) + 64;
                rx = (py - ry) * aTan + px;
                yo = 64;
                xo = -yo * aTan;
            }

            if (ra == 0 || ra == PI) {
                rx = px;
                ry = py;
                dof = 8; //Looking straight left or right
            }

            while (dof < 8) {
                mx = (int) (rx) >> 6;
                my = (int) (ry) >> 6;
                mp = my * mapX + mx;
                if (mp > 0 && mp < mapX * mapY && mapW[mp] > 0) { //Hit wall
                    hmt = mapW[mp] - 1;
                    hx = rx;
                    hy = ry;
                    disH = dist(px, py, hx, hy, ra);
                    dof = 8;
                } else {
                    rx += xo;
                    ry += yo;
                    dof += 1;
                }
            }

            //----CHECK VERTICAL LINE----
            dof = 0;
            float disV = 1000000000, vx = px, vy = py;
            float nTan = (float) (-Math.tan(ra));

            if (ra > P2 && ra < P3) { //looking left
                rx = (((int) px >> 6) << 6) - 0.0001f;
                ry = (px - rx) * nTan + py;
                xo = -64;
                yo = -xo * nTan;
            }

            if (ra < P2 || ra > P3) { //looking right
                rx = (((int) px >> 6) << 6) + 64;
                ry = (px - rx) * nTan + py;
                xo = 64;
                yo = -xo * nTan;
            }

            if (ra == 0 || ra == PI) {
                rx = px;
                ry = py;
                dof = 8; //Looking straight up or down
            }

            while (dof < 8) {
                mx = (int) (rx) >> 6;
                my = (int) (ry) >> 6;
                mp = my * mapX + mx;
                if (mp > 0 && mp < mapX * mapY && mapW[mp] > 0) { //Hit wall
                    vmt = mapW[mp] - 1;
                    vx = rx;
                    vy = ry;
                    disV = dist(px, py, vx, vy, ra);
                    dof = 8;
                } else {
                    rx += xo;
                    ry += yo;
                    dof += 1;
                }
            }

            if (disV < disH) {
                hmt = vmt;
                rx = vx;
                ry = vy;
                disT = disV;
                shade = 1f;
            }
            if (disH < disV) {
                rx = hx;
                ry = hy;
                disT = disH;
                shade = 0.5f;
            }

            float ca = pa - ra;
            if (ca < 0) {
                ca += 2 * PI;
            }
            if (ca > 2 * PI) {
                ca -= 2 * PI;
            }
            disT = (float) (disT * Math.cos(ca)); //Fix fisheye
            float lineH = (mapS * 640) / disT; //Line height

            float ty_step = 32.0f / lineH;
            float ty_off = 0;

            if (lineH > 640) {
                ty_off = (lineH - 640) / 2.0f;
                lineH = 640;
            }
            float lineO = 320 - ((int) lineH >> 1); //Line offset


            //---Draw walls---
            int y;
            float ty = ty_off * ty_step; //+ hmt * 32;
            float tx;
            int red = 0, green = 0, blue = 0;


            if (shade == 1) {
                tx = (int) ((ry / 2.0f) % 32);
                if (ra > degToRad(90) && ra < degToRad(270)) {
                    tx = 31 - tx;
                }
            } else {
                tx = (int) ((rx / 2.0f) % 32);
                if (ra < degToRad(180)) {
                    tx = 31 - tx;
                }
            }

            for (y = 0; y < lineH; y++) {
                int pixel = ((int) ty * 32 + (int) tx) * 3 + (hmt * 32 * 32 * 3);
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
                glVertex2f(r * 8, y + lineO);
                glEnd();
                ty += ty_step;
            }
            //---Draw floor---
            for (y = (int) (lineO + lineH); y < 640; y++) {
                float dy = y - (640.0f / 2.0f);
                float raFix = cos(pa - ra);
                tx = px / 2 + cos(ra) * 158 * 2 * 32 / dy / raFix;
                ty = py / 2 + sin(ra) * 158 * 2 * 32 / dy / raFix;

                int mp = mapF[(int) (ty / 32.0f) * mapX + (int) (tx / 32.0f)] * 32 * 32;
                int pixel = (((int) (ty) & 31) * 32 + ((int) (tx) & 31)) * 3 + mp * 3;
                red = Textures.houseFloor[(pixel) % 3072];
                green = Textures.houseFloor[(pixel + 1) % 3072];
                blue = Textures.houseFloor[(pixel + 2) % 3072];
                glPointSize(8);
                glColor3ub((byte) red, (byte) green, (byte) blue);
                glBegin(GL_POINTS);
                glVertex2f(r * 8, y);
                glEnd();

                //draw ceiling
                mp = mapC[(int) (ty / 32.0f) * mapX + (int) (tx / 32.0f)] * 32 * 32;
                pixel = (((int) (ty) & 31) * 32 + ((int) (tx) & 31)) * 3 + mp * 3;
                red = Textures.houseCeiling[(pixel) % 3072];
                green = Textures.houseCeiling[(pixel + 1) % 3072];
                blue = Textures.houseCeiling[(pixel + 2) % 3072];
                glPointSize(8);
                glColor3ub((byte) (red * 0.7), (byte) (green * 0.7), (byte) (blue * 0.7));
                glBegin(GL_POINTS);
                glVertex2f(r * 8, 640 - y);
                glEnd();
            }

            ra += DR / 2;
            if (ra < 0) {
                ra += 2 * PI;
            } else if (ra > 2 * PI) {
                ra -= 2 * PI;
            }
        }
    }

    float dist(float ax, float ay, float bx, float by, float ang) {
        return (float) (Math.sqrt((bx - ax) * (bx - ax) + (by - ay) * (by - ay))); //pythagorean theorem, length of the ray
    }

    float degToRad(float deg) {
        return (float) ((deg * Math.PI) / 180);
    }

    void buttons() {

        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            pa -= 0.1;
            if (pa < 0) {
                pa += 2 * PI;
            }
            pdx = (float) Math.cos(pa) * 5;
            pdy = (float) Math.sin(pa) * 5;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            pa += 0.1;
            if (pa > 2 * PI) {
                pa -= 2 * PI;
            }
            pdx = (float) Math.cos(pa) * 5;
            pdy = (float) Math.sin(pa) * 5;
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
                px += pdx;
            } //Collision detection
            if (mapW[ipy_add_yo * mapX + ipx] == 0) {
                py += pdy;
            } //If map[x] == 1 don't move
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            if (mapW[ipy * mapX + ipx_sub_xo] == 0) {
                px -= pdx;
            }
            if (mapW[ipy_sub_yo * mapX + ipx] == 0) {
                py -= pdy;
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_E)) { //interact button
            if (mapW[ipy_add_yo * mapX + ipx_add_xo] == 4) {
                mapW[ipy_add_yo * mapX + ipx_add_xo] = 0;
            }

//            if (mapW[ipy_add_yo * mapX + ipx_add_xo] == 7) {
//                mapW = new int[]{
//                        2, 2, 7, 2, 2, 5, 2, 2,
//                        6, 0, 0, 0, 0, 0, 0, 2,
//                        2, 0, 0, 0, 0, 0, 0, 3,
//                        2, 0, 0, 0, 0, 2, 4, 2,
//                        2, 0, 0, 0, 0, 0, 0, 2,
//                        3, 0, 0, 0, 0, 0, 0, 3,
//                        2, 0, 0, 0, 0, 0, 0, 2,
//                        2, 2, 2, 3, 6, 2, 2, 2
//                };
//            }
        }
    }
}
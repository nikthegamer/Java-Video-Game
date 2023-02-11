import org.lwjgl.input.Keyboard;

import java.awt.Window;
import java.security.Key;

import static org.lwjgl.opengl.GL11.*;

public class Render {
    private float px,py,pdx,pdy,pa;
    private double PI = Math.PI;
    private double P2 = PI/2;
    private double P3 = 3*PI/2;
    private float DR = 0.0174533f; //1 degree in radians

    public void setData(float pdx, float pdy, float px, float py) {
        this.pdx = pdx;
        this.pdy = pdy;
        this.px = px;
        this.py = py;
    }

    public float getPa() {
        return pa;
    }

    public void game(){
        buttons();
        drawMap2D();
        drawPlayer();
    }

    //---RENDER PLAYER---

    void drawPlayer(){
        glColor3f(1,1,0);
        glPointSize(8);
        glBegin(GL_POINTS);
        glVertex2f(px,py);
        glEnd();

        //drawLineOfSight();
        drawRays3D();
    }

//    void drawLineOfSight(){
//        glLineWidth(3);
//        glBegin(GL_LINES);
//        glVertex2f(px,py);
//        glVertex2f(px+pdx*5,py+pdy*5);
//        glEnd();
//    }

    //---DRAW RAYS---
    Textures textures = new Textures();
    int[] All_textures = textures.All_Textures;

    private int r,mx,my,mp,dof; private float rx,ry,ra,xo,yo,disT,tx;
    void drawRays3D(){

        glColor3f(0,1,1); glBegin(GL_QUADS); glVertex2i(526,  0); glVertex2i(1006,  0); glVertex2i(1006,160); glVertex2i(526,160); glEnd();
        glColor3f(0,0,1); glBegin(GL_QUADS); glVertex2i(526,160); glVertex2i(1006,160); glVertex2i(1006,320); glVertex2i(526,320); glEnd();

        ra=pa-DR*30; if (ra<0){
            ra+=2*PI;
        } else if (ra>2*PI) {
            ra-=2*PI;
        }
        for (r=0;r<60;r++) //Num of rays
        {
            dof=0;
            float disH=1000000000, hx=px, hy=py;
            float aTan= (float) ( -1/Math.tan(ra));

            //----CHECK HORIZONTAL LINE---
            if (ra>PI){ //looking up
                ry=(((int)py>>6)<<6)-0.0001f;
                rx=(py-ry)*aTan+px;
                yo=-64;
                xo=-yo*aTan;
            }

            if (ra<PI){ //looking down
                ry=(((int)py>>6)<<6)+64;
                rx=(py-ry)*aTan+px;
                yo=64;
                xo=-yo*aTan;
            }

            if (ra==0 || ra==PI){
                rx=px; ry=py; dof=8; //Looking straight left or right
            }

            while(dof<8){
                mx=(int) (rx)>>6;
                my=(int) (ry)>>6;
                mp=my*mapX+mx;
                if (mp>0 && mp<mapX*mapY && map[mp]==1){ //Hit wall
                    hx=rx; hy=ry; disH=dist(px,py,hx,hy,ra);
                    dof=8;
                }else{
                    rx+=xo; ry+=yo; dof+=1;
                }
            }

            //----CHECK VERTICAL LINE----
            dof=0;
            float disV=1000000000, vx=px, vy=py;
            float nTan= (float) ( -Math.tan(ra));

            if (ra>P2 && ra<P3){ //looking left
                rx=(((int)px>>6)<<6)-0.0001f;
                ry=(px-rx)*nTan+py;
                xo=-64;
                yo=-xo*nTan;
            }

            if (ra<P2 || ra>P3){ //looking right
                rx=(((int)px>>6)<<6)+64;
                ry=(px-rx)*nTan+py;
                xo=64;
                yo=-xo*nTan;
            }

            if (ra==0 || ra==PI){
                rx=px; ry=py; dof=8; //Looking straight up or down
            }

            while(dof<8){
                mx=(int) (rx)>>6;
                my=(int) (ry)>>6;
                mp=my*mapX+mx;
                if (mp>0 && mp<mapX*mapY && map[mp]==1){ //Hit wall
                    vx=rx; vy=ry; disV=dist(px,py,vx,vy,ra);
                    dof=8;
                }else{
                    rx+=xo; ry+=yo; dof+=1;
                }
            }

            if (disV<disH){
                rx=vx;
                ry=vy;
                disT=disV;
                glColor3f(0.9f,0,0);
            }
            if (disH<disV){
                rx=hx;
                ry=hy;
                disT=disH;
                glColor3f(0.7f,0,0);
            }

            glLineWidth(1);
            glBegin(GL_LINES);
            glVertex2f(px,py);
            glVertex2f(rx,ry);
            glEnd();

            float ca=pa-ra; if (ca<0){ca+=2*PI;} if (ca>2*PI){ca-=2*PI;}
            disT= (float) (disT*Math.cos(ca)); //Fix fisheye
            float lineH=(mapS*320)/disT; if (lineH>320){lineH=320;} //Line height
            float lineO=160-lineH/2; //Line offset

            float shade = 0.3f;
            int y;
            float ty=0;
            float ty_step= 32.0f/lineH;

            for (y=0;y<lineH;y++)
            {
                float c=All_textures[(int)(ty)*32+(int)(tx)];
                if (c==1){tx=(int)(rx/2.0f)%32;} if (ra > 180) {tx=31-tx;}
                else{tx=(int)(ry/2.0f)%32; if (ra>90 && ra<270){tx=31-tx;}}

                if (disV<disH){
                    glColor3f(c-shade,c-shade,c-shade);
                }
                if (disH<disV){
                    glColor3f(c,c,c);
                }
                glPointSize(8);
                glBegin(GL_POINTS);
                glVertex2f(r*8+530,y+lineO);
                glEnd();
                ty+=ty_step;
            }

            ra+=DR; if (ra<0){
            ra+=2*PI;
        } else if (ra>2*PI) {
            ra-=2*PI;
        }
        }
    }
    float dist(float ax, float ay, float bx, float by, float ang){
        return(float)(Math.sqrt((bx-ax)*(bx-ax) + (by-ay)*(by-ay)));
    }

    void buttons(){
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            pa-=0.1; if (pa<    0){
                pa+=2*PI;
            }
            pdx=(float)Math.cos(pa)*5;
            pdy=(float)Math.sin(pa)*5;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            pa+=0.1; if (pa>    2*PI){
                pa-=2*PI;
            }
            pdx=(float)Math.cos(pa)*5;
            pdy=(float)Math.sin(pa)*5;
        }

        int xo=0;
        if(pdx<0){
            xo=-20;
        }else{
            xo=20;
        }
        int yo=0;
        if(pdy<0){
            yo=-20;
        }else{
            yo=20;
        }

        int ipx= (int) (px/64.0f), ipx_add_xo= (int) ((px+xo)/64.0f), ipx_sub_xo= (int) ((px-xo)/64.0f);
        int ipy= (int) (py/64.0f), ipy_add_yo= (int) ((py+yo)/64.0f), ipy_sub_yo= (int) ((py-yo)/64.0f);

        if (Keyboard.isKeyDown(Keyboard.KEY_W)){
            if (map[ipy*mapX    +    ipx_add_xo]==0){px+=pdx;}
            if (map[ipy_add_yo*mapX    +    ipx]==0){py+=pdy;}
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_S)){
            if (map[ipy*mapX    +    ipx_sub_xo]==0){px-=pdx;}
            if (map[ipy_sub_yo*mapX    +    ipx]==0){py-=pdy;}
        }
    }

    //---RENDER MAP----

    private int mapX=8, mapY=8, mapS=64;

    int map[]={
            1,1,1,1,1,1,1,1,
            1,0,0,0,0,1,0,1,
            1,0,1,0,0,1,0,1,
            1,0,0,0,0,0,0,1,
            1,1,0,1,0,0,0,1,
            1,0,0,0,0,0,0,1,
            1,0,1,0,0,0,0,1,
            1,1,1,1,1,1,1,1
    };

    void drawMap2D(){
        int y,x,yo,xo;
        for (y=0;y<mapY;y++){
            for (x=0;x<mapX;x++){
                if (map[y*mapX+x]==1){
                    glColor3f(1,1,1);
                }
                else glColor3f(0,0,0);
                xo=x*mapS; yo=y*mapS;

                glBegin(GL_QUADS);
                glVertex2i(xo+1,yo+1);
                glVertex2i(xo+1, yo+mapS-1);
                glVertex2i(xo+mapS-1,yo+mapS-1);
                glVertex2i(xo+mapS-1,yo+1);
                glEnd();
            }
        }
    }
}
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    private int VAO, VBO; //vertex array object //vertex buffer object

    private int vertexCount;

    public Mesh(){

   }

   public boolean create(float vertices[]){
        //Gen Vertex
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);
        //Gen Buffers
        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW /*Sends data on to VRAM */);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindVertexArray(0);

        vertexCount = vertices.length / 3;
        return true;
   }

   public void destroy(){
        glDeleteBuffers(VBO);
        glDeleteVertexArrays(VAO);
   }

   public void draw(){
        glBindVertexArray(VAO);

        glEnableVertexAttribArray(0);
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
   }
}

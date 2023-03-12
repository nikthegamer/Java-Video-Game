import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Textures {
    public static int[] houseWall;
    public static int[] houseDoor;
    public static int[] houseDecoOne;
    public static int[] houseDecoTwo;
    public static int[] houseWindow;
    public static int[] houseFloor;
    public static int[] houseCeiling;
    public static int[] Missing_Texture;

    public static int[] WindowTrans;

    int[] readFileTexture(String file) {
        List<Integer> result = new ArrayList<Integer>();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((line = reader.readLine()) != null) {
                result.add(Integer.parseInt(line.split(",")[0]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.stream().mapToInt(i -> i).toArray();
    }
}

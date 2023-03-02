import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Textures {
    public static int[] houseWall;
    public static int[] houseDoor;
    public static int[] houseDecoOne;
    public static int[] houseDecoTwo;
    public static int[] houseWindow;
    public static int[] houseFloor;
    public static int[] houseCeiling;
    public static int[] Missing_Texture;
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

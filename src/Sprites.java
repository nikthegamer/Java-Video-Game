public class Sprites {
    int type; // static, key, enemy
    int state; //on off
    int map; // texture
    float x, y, z; // position
    float width = 5, height = 5;

    public Sprites(int type, int state, int map, float x, float y, float z) {
        this.type = type;
        this.state = state;
        this.map = map;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

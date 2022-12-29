import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.*;
public class Controls {

    //Key press handler
    private static final boolean[] keys = new boolean[GLFW_KEY_LAST];
    private static final boolean[] buttons = new boolean[GLFW_MOUSE_BUTTON_LAST];
    private static double mouseX, mouseY;
    private static GLFWKeyCallback keyboard;
    private static GLFWCursorPosCallback mouseMove;
    private static GLFWMouseButtonCallback mouseButton;

    public Controls(){
        keyboard = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                keys[key] = (action != GLFW_RELEASE);
            }
        };

        mouseMove = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mouseX = xpos;
                mouseY = ypos;
            }
        };

        mouseButton = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                buttons[button] = (action != GLFW_RELEASE);
            }
        };


    }

    public static boolean isKeyDown(int key){
        return keys[key];
    }
    public static boolean isMouseButtonDown(int button){
        return buttons[button];
    }

    public void destroy(){
        keyboard.free();
        mouseMove.free();
        mouseButton.free();
    }

    public static double getMouseX() {
        return mouseX;
    }

    public static double getMouseY() {
        return mouseY;
    }

    public static GLFWKeyCallback getKeyboardCallback() {return keyboard;}

    public static GLFWCursorPosCallback getMouseMoveCallback() {
        return mouseMove;
    }

    public static GLFWMouseButtonCallback getMouseButtonCallback() {
        return mouseButton;
    }
}

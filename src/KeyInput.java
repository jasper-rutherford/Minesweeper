
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class KeyInput extends KeyAdapter {

    private static ArrayList<Integer> keysPressed;

    public KeyInput() {

        keysPressed = new ArrayList<Integer>();

    }

    public void keyPressed(KeyEvent e) {

        if (!keysPressed.contains(e.getKeyCode()))
            keysPressed.add(new Integer(e.getKeyCode()));

    }

    public void keyReleased(KeyEvent e) {

        if (keysPressed.contains(new Integer(e.getKeyCode())))
            keysPressed.remove(new Integer(e.getKeyCode()));

    }

    public static ArrayList<Integer> getKeysPressed(){

        return keysPressed;

    }

    public static boolean getKey(int key) {

        return keysPressed.contains(new Integer(key));

    }

}


import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Window extends Canvas
{
    private MouseInput mouseInput = new MouseInput();
    private Frame frame;

    public Window(int width, int height, String title, Game game)
    {
        frame = new Frame(title);

        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.add(game);


        frame.setVisible(true);

    }

    public JFrame getFrame()
    {
        return frame;
    }

}
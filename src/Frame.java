import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Frame extends JFrame
{
    private String myTitle;
    private JTextArea textArea;
    private int myRotation;

    public Frame(String title)
    {
        myTitle = title;
        textArea = new JTextArea();
        textArea.addMouseWheelListener(new MouseWheelListener()
        {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                myRotation = e.getWheelRotation();
                System.out.println(myRotation + " " + e.getScrollAmount());
            }
        });

        getContentPane().add(textArea);
    }


}

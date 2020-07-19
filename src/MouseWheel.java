
import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseWheel extends JPanel implements MouseWheelListener
{
    private static double rot;

    public MouseWheel()
    {
        rot = 0;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent m)
    {

        // TODO Auto-generated method stub
        rot = m.getWheelRotation();
        //System.out.println(rot + " " + m.getScrollAmount());


    }

    public double getRot()
    {
        double val = rot;
        rot = 0;
        return val;
    }

}


import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

public class MouseInput implements MouseListener
{

    private static boolean heldL;
    private static boolean heldR;
    private static MouseEvent mouseEvent;

    public MouseInput()
    {
        heldL = false;
        heldR = false;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    @Override
    public void mouseEntered(MouseEvent arg0)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if (SwingUtilities.isLeftMouseButton(e))
            heldL = true;
        if (SwingUtilities.isRightMouseButton(e))
            heldR = true;
        mouseEvent = e;
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {

        if (SwingUtilities.isLeftMouseButton(e))
            heldL = false;
        if (SwingUtilities.isRightMouseButton(e))
            heldR = false;
        mouseEvent = null;
    }

    public boolean leftIsHeld()
    {
        return heldL;
    }

    public boolean rightIsHeld()
    {
        return heldR;
    }

    public static MouseEvent getMouseEvent()
    {
        return mouseEvent;
    }

    public double getX()
    {

        return mouseEvent.getX();

    }

    public double getY()
    {
        return mouseEvent.getY();
    }
}

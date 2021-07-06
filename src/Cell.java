public class Cell
{
    private boolean isflagged;
    private boolean isBomb;
    private int adjBombs;
    private boolean revealed;
    private boolean isValid;
    private boolean active;
    private int x;
    private int y;

    public Cell(int x1, int y1)
    {
        isflagged = false;
        isBomb = false;
        adjBombs = 0;
        revealed = false;
        isValid = true;
        active = false;
        x = x1;
        y = y1;
    }

    public void toggleFlag()
    {
        isflagged = !isflagged;
    }

    public boolean isFlagged()
    {
        return isflagged;
    }

    public boolean checkBomb()
    {
        return isBomb;
    }

    public void setBomb(boolean aIsBomb)
    {
        isBomb = aIsBomb;
    }

    public void setAdjBombs(int numBombs)
    {
        adjBombs = numBombs;
    }

    public int getAdjBombs()
    {
        return adjBombs;
    }

    public boolean isRevealed()
    {
        return revealed;
    }

    public void setRevealed(boolean in)
    {
        revealed = in;
        if (revealed)
        {
            active = false;
        }
    }

    public void setValid(boolean valid)
    {
        isValid = valid;
    }

    public boolean getValid()
    {
        return isValid;
    }

    public void setActive(boolean activity)
    {
        active = activity;
    }

    public boolean getActive()
    {
        return active;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
}

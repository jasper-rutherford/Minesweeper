
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Handler
{

    private boolean restart;
    private MouseInput mouse;
    private int mouseX;
    private int mouseY;
    private int oldMouseX;
    private int oldMouseY;
    private int badX;
    private int badY;
    private MouseWheel wheel;
    private int boardWidth;
    private int boardHeight;
    private boolean movedL;
    private boolean movedR;
    private JFrame myFrame;
    private int gameHeight;
    private int gameWidth;
    private boolean leftHeld;
    private boolean rightHeld;
    private boolean lIsHeld;
    private int rot;

    private Cell[][] board;
    private double bombChance;
    private int sideLength;
    private int numBombs;
    private boolean lose;
    private int bombsFlagged;
    private boolean win;
    private boolean enterPressed;
    private int flagsRemaining;
    private long startTime;
    private long endTime;
    private int revealedTiles;
    private boolean fPressed;
    private boolean solverActive;
    private boolean startSet;
    private int solverInhibitor;
    private int solverCounter;
    private boolean player;
    private int losses;
    private int wins;
    private int trials;
    private boolean solverRestarts;
    private boolean lPressed;
    private int ticks;

    public Handler(JFrame frame)
    {
        restart = false;
        gameHeight = 50;
        gameWidth = 75;
        mouse = new MouseInput();
        wheel = new MouseWheel();
        mouseX = badX;
        mouseY = badY;
        oldMouseX = badX;
        oldMouseY = badY;
        wheel = new MouseWheel();
        movedL = false;
        movedR = false;
        myFrame = frame;
        rot = 0;
        leftHeld = false;
        rightHeld = false;
        lIsHeld = false;


        //initialize the coords
        mouseX = getMouseX();
        mouseY = getMouseY();

        bombChance = .1;
        lose = false;
        bombsFlagged = 0;
        win = false;
        boardWidth = 20;
        boardHeight = boardWidth;
        numBombs = (int) (.125 * boardWidth * boardHeight);
        sideLength = 600 / boardHeight;
        enterPressed = false;
        flagsRemaining = numBombs;
        startTime = System.currentTimeMillis();
        endTime = 0;
        revealedTiles = 0;
        fPressed = false;
        solverActive = false;
        startSet = false;
        solverInhibitor = 0;
        solverCounter = 0;
        player = false;
        wins = 0;
        losses = 0;
        trials = 0;
        solverRestarts = false;
        lPressed = false;
        ticks = 0;

        board = new Cell[boardWidth][boardHeight];
        generateWorld();
    }


    public void generateWorld()
    {

        //spawn empty cells
        for (int x = 0; x < board.length; x++)
        {
            for (int y = 0; y < board[0].length; y++)
            {
                board[x][y] = new Cell(x, y);
            }
        }

        //populate with bombs
        for (int bombCount = numBombs; bombCount > 0; bombCount--)
        {
            int x = (int) (Math.random() * boardWidth);
            int y = (int) (Math.random() * boardHeight);

            Cell bombTest = board[x][y];

            if (!bombTest.checkBomb())
                bombTest.setBomb(true);
            else
                bombCount++;
        }

        //update adjBombs per tile
        for (int x = 0; x < board.length; x++)
        {
            for (int y = 0; y < board[0].length; y++)
            {
                int adjBombs = 0;
                for (int relX = -1; relX <= 1; relX++)
                {
                    for (int relY = -1; relY <= 1; relY++)
                    {
                        int tempX = x + relX;
                        int tempY = y + relY;

                        if (tempX >= 0 && tempX < board.length && tempY >= 0 && tempY < board[0].length && board[tempX][tempY].checkBomb())
                            adjBombs++;
                    }
                }
                board[x][y].setAdjBombs(adjBombs);
            }
        }
    }

    public void tick()
    {
//        ticks++;
////        System.out.println(ticks);
//        if (ticks == 60)
//        {
//            ticks = 0;
//            System.out.println("Flags/Bombs: " + bombsFlagged + "/" + numBombs);
//        }

        if (restart)
        {
            if (lose)
                losses++;
            if (win)
                wins++;
            trials++;
            System.out.println("restarting");
            lose = false;
            win = false;
            generateWorld();
            bombsFlagged = 0;
            flagsRemaining = numBombs;
            startTime = System.currentTimeMillis();
            startSet = true;
//            solverActive = false;
            restart = false;
            System.out.println("\n\nWins: " + wins + "\nLosses: " + losses + "\nTrials: " + trials + "\nSuccess Rate: " + (int) ((double) wins / trials * 100) + "%");
        }

        if (bombsFlagged == numBombs)
        {
            if (!win)
                endTime = System.currentTimeMillis();
            win = true;
            if (!player && solverRestarts)
            {
                restart = true;
            }
        }

        doKeyInput();

        //update mouse coords
        mouseX = getMouseX();
        mouseY = getMouseY();

        runLeftMouse();
        runRightMouse();

        doMouseWheel();

        if (solverActive && !lose && !win)
        {
            if (solverCounter == 0)
            {
                solverCounter = solverInhibitor;
//                System.out.println("Ticking Solver");
                tickSolver();
            }
            else
            {
                solverCounter--;
            }
        }
    }

    public void render(Graphics g)
    {
        for (int x = 0; x < board.length; x++)
        {
            for (int y = 0; y < board[0].length; y++)
            {
                Cell rendering = board[x][y];


                if (!rendering.isRevealed())
                {
                    g.setColor(new Color(52, 177, 47));

//                    if (rendering.getActive())
//                    {
//                        g.setColor(new Color(23, 179, 177));
//                    }

                    g.fillRect(x * sideLength, y * sideLength, sideLength, sideLength);


                    if (rendering.isFlagged())
                    {
                        g.setColor(new Color(211, 1, 1));

                        int baseX = x * sideLength;
                        int baseY = y * sideLength;

                        int[] xVals = {baseX + sideLength / 4, baseX + sideLength / 4, baseX + sideLength * 4 / 5, baseX + sideLength * 7 / 20, baseX + sideLength * 7 / 20};
                        int[] yVals = {baseY + sideLength * 9 / 10, baseY + sideLength / 10, baseY + sideLength / 4, baseY + sideLength / 2, baseY + sideLength * 9 / 10};

                        g.fillPolygon(xVals, yVals, 5);
                    }
                }
                else
                {
                    g.setColor(new Color(197, 193, 160));
                    g.fillRect(x * sideLength, y * sideLength, sideLength, sideLength);

                    int adjBombs = rendering.getAdjBombs();
                    if (adjBombs != 0)
                    {
                        if (adjBombs == 1)
                            g.setColor(new Color(36, 34, 164));
                        else if (adjBombs == 2)
                            g.setColor(new Color(48, 184, 50));
                        else if (adjBombs == 3)
                            g.setColor(new Color(223, 33, 34));
                        else if (adjBombs == 4)
                            g.setColor(new Color(141, 26, 117));
                        else if (adjBombs == 5)
                            g.setColor(new Color(128, 0, 0));
                        else if (adjBombs == 6)
                            g.setColor(new Color(23, 179, 177));
                        else if (adjBombs == 7)
                            g.setColor(new Color(1, 1, 1));
                        else if (adjBombs == 8)
                            g.setColor(new Color(119, 119, 119));
                        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                        g.drawString("" + rendering.getAdjBombs(), x * sideLength + sideLength / 4, y * sideLength + sideLength * 3 / 4);
                    }
                }

                //draw red x over bombs after you lose
                if (lose && rendering.checkBomb())
                {
                    g.setColor(new Color(154, 26, 30));
                    g.drawLine(x * sideLength, y * sideLength, x * sideLength + sideLength, y * sideLength + sideLength);
                    g.drawLine(x * sideLength + sideLength, y * sideLength, x * sideLength, y * sideLength + sideLength);
                }

            }

            g.setColor(new Color(43, 139, 149));
            g.drawString(numBombs + "", 605, 605);
        }

        for (int x = 0; x < board.length; x++)
        {
            for (int y = 0; y < board[0].length; y++)
            {
                Cell rendering = board[x][y];

                g.setColor(new Color(1, 1, 1));
                g.drawRect(x * sideLength, y * sideLength, sideLength, sideLength);
            }
        }

        if (win)
        {
            g.setColor(new Color(238, 29, 219));
            g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
            g.drawString("You Win!", 300 - sideLength * 7 / 2, 300);

            g.setColor(new Color(82, 83, 80));
            g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
            g.drawString("Press Enter to Restart", 300 - sideLength * 5, 350);

            g.setColor(new Color(1, 1, 1));
            g.setFont(new Font("TimesRoman", Font.BOLD, 20));

            int seconds = (int) (endTime - startTime) / 1000;
            int minutes = seconds / 60;
            seconds %= 60;

            if (seconds > 9)
                g.drawString("Your Time: " + minutes + ":" + seconds, 300 - sideLength * 5 / 2, 410 - sideLength);
            else
                g.drawString("Your Time: " + minutes + ":0" + seconds, 300 - sideLength * 5 / 2, 410 - sideLength);
        }
        else if (lose)
        {
            g.setColor(new Color(127, 19, 14));
            g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
            g.drawString("You Lose", 300 - sideLength * 7 / 2, 300);

            g.setColor(new Color(82, 83, 80));
            g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
            g.drawString("Press Enter to Restart", 300 - sideLength * 5, 350);
        }
    }

//    public boolean getRestart()
//    {
//        return restart;
//    }

    public int getHoverTile() /// method that returns the tile the mouse is currently hovering over
    {
        //get the coords
        int screenX = (int) MouseInfo.getPointerInfo().getLocation().getX();
        int screenY = (int) MouseInfo.getPointerInfo().getLocation().getY();

        Point point = new Point(screenX, screenY);
        SwingUtilities.convertPointFromScreen(point, myFrame);

        mouseX = (int) point.getX();
        mouseY = (int) point.getY();

        return 0;
    }


    public void doKeyInput()
    {

        // close game on pressing escape
        if (KeyInput.getKey(KeyEvent.VK_ESCAPE))
        {
            System.exit(0);
        }

        //restart by pressing enter
        if (KeyInput.getKey(KeyEvent.VK_ENTER))
        {
            if (!enterPressed)
            {
                restart = true;
            }

            enterPressed = true;
        }
        else
            enterPressed = false;

        //toggle solver
        if (KeyInput.getKey(KeyEvent.VK_F))
        {
            if (!fPressed)
            {
                solverActive = !solverActive;
                if (!startSet)
                {
                    startTime = System.currentTimeMillis();
                    startSet = true;
                }
            }
            fPressed = true;
        }
        else
            fPressed = false;

        //toggle restart when solver wins/loses
        if (KeyInput.getKey(KeyEvent.VK_L))
        {
            if (!lPressed)
            {
                solverRestarts = !solverRestarts;
            }
            lPressed = true;
        }
        else
            lPressed = false;
    }


    public void runLeftMouse()
    {
        if (!lose && !win)
        {
            boolean holdTest = leftHeld; //saves what leftHeld was
            leftHeld = mouse.leftIsHeld(); //updates leftHeld

            if (!holdTest && leftHeld)
                runLeftMousePressed();
            if (leftHeld)
                runLeftMouseHeld();
            if (holdTest && !leftHeld)
                runLeftMouseReleased();
        }
    }

    public void runLeftMousePressed()
    {
        int x = mouseX / sideLength;
        int y = mouseY / sideLength;

        if (x < boardWidth && y < boardHeight)
        {
            Cell testing = board[x][y];

            player = true;
            revealTile(x, y);
        }
    }

    public void runLeftMouseHeld()
    {

    }

    public void runLeftMouseReleased()
    {

    }

    public void runRightMouse()
    {
        if (!lose && !win)
        {
            boolean holdTest = rightHeld; //saves what rightHeld was
            rightHeld = mouse.rightIsHeld(); //updates rightHeld

            if (!holdTest && rightHeld)
                runRightMousePressed();
            if (rightHeld)
                runRightMouseHeld();
            if (holdTest && !rightHeld)
                runRightMouseReleased();
        }
    }

    public void runRightMousePressed()
    {
        int x = mouseX / sideLength;
        int y = mouseY / sideLength;

        Cell flagged = board[x][y];
        boolean isFlagged = flagged.isFlagged();

        if (!flagged.isRevealed() && !isFlagged && flagsRemaining > 0)
        {
            flagged.toggleFlag();
            flagsRemaining--;
            if (flagged.checkBomb())
            {
                bombsFlagged++;
            }
        }
        else if (isFlagged)
        {
            flagged.toggleFlag();
            flagsRemaining++;
            if (flagged.checkBomb())
            {
                bombsFlagged--;
            }
        }
    }

    public void runRightMouseHeld()
    {

    }

    public void runRightMouseReleased()
    {

    }

    public void doMouseWheel()
    {
        rot = (int) wheel.getRot();
    }


    public int getMouseX()
    {
        int screenX = (int) MouseInfo.getPointerInfo().getLocation().getX();
        int screenY = (int) MouseInfo.getPointerInfo().getLocation().getY();

        Point point = new Point(screenX, screenY);
        SwingUtilities.convertPointFromScreen(point, myFrame);

        return (int) point.getX() - 5;
    }

    public int getMouseY()
    {
        int screenX = (int) MouseInfo.getPointerInfo().getLocation().getX();
        int screenY = (int) MouseInfo.getPointerInfo().getLocation().getY();

        Point point = new Point(screenX, screenY);
        SwingUtilities.convertPointFromScreen(point, myFrame);

        return (int) point.getY() - 30;
    }

    public void revealTile(int x, int y)
    {
        if (x >= 0 && y >= 0 && x < board.length && y < board[0].length && !board[x][y].isRevealed())
        {
            Cell revealed = board[x][y];

            if (revealed.checkBomb())
            {
                lose = true;
                if (!player && solverRestarts)
                {
                    restart = true;

                }
            }
            else
            {


                revealed.setRevealed(true); //also sets to not active
                revealedTiles++;
                if (revealed.isFlagged())
                {
                    revealed.toggleFlag();
                    flagsRemaining++;
                }

                //sets adjacent tiles to active
                for (int lcv = 0; lcv < 8; lcv++)
                {
                    Cell testing = getAdjTile(lcv, revealed);
                    if (testing.getValid())
                    {
                        testing.setActive(true);
                    }
                }

                if (board[x][y].getAdjBombs() == 0)
                {
                    for (int relX = -1; relX <= 1; relX++)
                    {
                        for (int relY = -1; relY <= 1; relY++)
                        {
                            int tempX = x + relX;
                            int tempY = y + relY;
                            revealTile(tempX, tempY);
                        }
                    }
                }
            }
        }
    }

    public void tickSolver()
    {
        //find revealed tiles
        int revealeds = 0;

        for (int ry = 0; ry < boardHeight; ry++)
        {
            for (int rx = 0; rx < boardWidth; rx++)
            {
                Cell revealed = board[rx][ry];
                if (revealed.isRevealed())
                {
                    revealeds++;

                    //count the hidden tiles and flags around the revealed tile
                    int flags = 0, hidden = 0;
                    for (int lcv = 0; lcv < 8; lcv++)
                    {
                        Cell adj = getAdjTile(lcv, revealed);
                        if (adj.getValid() && !adj.isRevealed())
                        {
                            hidden++;
                        }
                    }

                    //if the number of hidden tiles around the revealed tile is equal to the number of bombs around that tile, flag the first hidden unflagged tile around the revealed tile, if it exists
                    if (hidden == revealed.getAdjBombs())
                    {
                        for (int lcv = 0; lcv < 8; lcv++)
                        {
                            Cell adj = getAdjTile(lcv, revealed);
                            if (adj.getValid() && !adj.isRevealed() && !adj.isFlagged())
                            {
                                adj.toggleFlag();
                                bombsFlagged++;
                                return;
                            }
                        }
                    }

                    for (int lcv = 0; lcv < 8; lcv++)
                    {
                        Cell adj = getAdjTile(lcv, revealed);
                        if (adj.getValid() && adj.isFlagged())
                        {
                            flags++;
                        }
                    }

                    //if the number of flags around a revealed tile is equal to the number of bombs around the revealed tile reveal the first unflagged hidden tile around the revealed tile if it exists
                    if (flags == revealed.getAdjBombs())
                    {
                        for (int lcv = 0; lcv < 8; lcv++)
                        {
                            Cell adj = getAdjTile(lcv, revealed);
                            if (adj.getValid() && !adj.isRevealed() && !adj.isFlagged())
                            {
                                player = false;
                                revealTile(adj.getX(), adj.getY());
                                return;
                            }
                        }
                    }
                }
            }
        }


//        if (bombsFlagged == numBombs)
//        {
//            if (win == false)
//                endTime = System.currentTimeMillis();
//            win = true;
//        }

        if (revealeds > 0) //if there are no clear moves
        {
            //find the first hidden tile adjacent to a revealed and clear it
            for (int x = 0; x < boardWidth; x++)
            {
                for (int y = 0; y < boardHeight; y++)
                {
                    Cell first = board[x][y];
                    if (first.isRevealed())
                    {
                        for (int lcv = 0; lcv < 8; lcv++)
                        {
                            Cell adj = getAdjTile(lcv, first);
                            if (adj.getValid() && !adj.isRevealed() && !adj.isFlagged())
                            {
                                player = false;
                                revealTile(adj.getX(), adj.getY());
                                return;
                            }
                        }
                    }
                }
            }
        }
        else //if there are no active tiles
        {
            //reveal a random tile
            player = false;
            revealTile((int) (Math.random() * boardWidth), (int) (Math.random() * boardHeight));
        }
    }

    //returns the cell adjacent to the given cell (0 - 7, clockwise from the top middle), returns an invalid cell if it goes over the edge
    public Cell getAdjTile(int val, Cell in)
    {
        int x = in.getX();
        int y = in.getY();

        if (val == 0 && y != 0)
        {
            return board[x][y - 1];
        }
        else if (val == 1 && y != 0 && x != boardWidth - 1)
        {
            return board[x + 1][y - 1];
        }
        else if (val == 2 && x != boardWidth - 1)
        {
            return board[x + 1][y];
        }
        else if (val == 3 && x != boardWidth - 1 && y != boardHeight - 1)
        {
            return board[x + 1][y + 1];
        }
        if (val == 4 && y != boardHeight - 1)
        {
            return board[x][y + 1];
        }
        else if (val == 5 && x != 0 && y != boardHeight - 1)
        {
            return board[x - 1][y + 1];
        }
        else if (val == 6 && x != 0)
        {
            return board[x - 1][y];
        }
        else if (val == 7 && x != 0 && y != 0)
        {
            return board[x - 1][y - 1];
        }

        Cell invalid = new Cell(-1, -1);
        invalid.setValid(false);

        return invalid;
    }
}
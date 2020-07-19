
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.io.File;

import javax.print.attribute.standard.Media;

public class Game extends Canvas
{

    public static int WIDTH; // = 1500;//640;
    public static int HEIGHT; // = 1000;//480;
    private Handler handler;
    private Window window;
    private boolean restart;

    public Game()
    {
        super();


        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//        WIDTH = gd.getDisplayMode().getWidth();
//        HEIGHT = gd.getDisplayMode().getHeight();
        WIDTH = 615;
        HEIGHT = 608;


        this.setFocusable(true);
        KeyInput keyInput = new KeyInput();
        MouseInput mouseInput = new MouseInput();
        MouseWheel wheelInput = new MouseWheel();
        window = new Window(WIDTH, HEIGHT + 30, "Jasper-Sweeper", this);

        handler = new Handler(window.getFrame());
        this.addKeyListener(keyInput);
        this.addMouseListener(mouseInput);
        this.addMouseWheelListener(wheelInput);
        restart = false;
    }


    public void run()
    {
        //MAIN GAME LOOP
        restart = false;
        long lastTime = System.nanoTime();
        double amountOfTicks = 30.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int ticks = 0;

        //play music (not implemented yet)

        System.out.println("arg");
        while (!restart)
        {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1)
            {
//                System.out.println("1");
                tick();
//                System.out.println("2");
                delta--;
//				ticks++;
//				System.out.println(ticks);
            }
            render();
//            System.out.println("render");
            frames++;
            if (System.currentTimeMillis() - timer > 1000)
            {
                timer += 1000;
                //	System.out.println("FPS: " + frames);
                frames = 0;
            }
//            if (handler.getRestart())
//                restart = true;
        }
//        restart();
    }

    private void tick()
    {
        handler.tick();
    }

    private void render()
    {

        BufferStrategy bfs = this.getBufferStrategy();
        if (bfs == null)
        {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bfs.getDrawGraphics();

        handler.render(g);

        g.dispose();
        bfs.show();
    }

    public Handler getHandler()
    {
        return handler;
    }


}

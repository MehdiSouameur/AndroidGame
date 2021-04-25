package com.example.theplatform;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Class which contains the mainThread on which the game will run
 */
public class MainThread extends Thread {
    private SurfaceHolder surfaceholder;
    private GameView gameView;
    private boolean running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder, GameView gameView){

        super();
        this.surfaceholder = surfaceHolder;
        this.gameView = gameView;

    }

    public void setRunning(boolean isRunning) {
        running = isRunning;
    }

    /**
     * Run the main thread to draw the game on the screen
     */
    @Override
    public void run(){
        long startTime = System.nanoTime();

        while(running) {
            canvas = null;

            try{
                canvas = this.surfaceholder.lockCanvas();

                synchronized (surfaceholder) {
                    if(gameView.getLevelLoaded() == true)
                        this.gameView.update();
                    this.gameView.draw(canvas);
                }
            } catch (Exception e) {
            }
            //Error catching so it can compile
            finally {
                if(canvas!=null)
                {
                    try {
                        surfaceholder.unlockCanvasAndPost(canvas);
                    }
                    catch(Exception e){e.printStackTrace();}
                }
            }
            long now = System.nanoTime() ;
            // Interval to redraw game
            // (Change nanoseconds to milliseconds)
            long waitTime = (now - startTime)/1000000;
            if(waitTime < 10)  {
                waitTime= 10; // Millisecond.
            }
            System.out.print(" Wait Time="+ waitTime);

            try {
                // Sleep.
                this.sleep(waitTime);
            } catch(InterruptedException e)  {

            }
            startTime = System.nanoTime();
            System.out.print(".");
        }
        }
    }



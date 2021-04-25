package com.example.theplatform;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Class for obstacle type: Springs
 * The player is propules upwards in the air on contact with
 * this obstacle.
 * This allows the player to reach heights he couldnt reach before
 */
public class Spring extends GameEntity{
    private GameView gameView;
    private static int yVelocity = 50;

    private static float a = 0;
    private boolean isOnGround;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    /**
     * Constructor for the spring class
     * @param gameView
     * @param image
     * @param x
     * @param y
     */
    public Spring(GameView gameView, Bitmap image, int x, int y) {
        super(image, 1, 2, x, y);

        this.gameView = gameView;

    }

    /**
     * Method which handles gravity
     * Springs need gravity to facilitate the initialisation of springs
     *This way, if we spawn the springs slightly above a platform it will firmly
     * land on the platform
     */
    public void gravity(){
        //Check if there is a platform below
        Platform object = gameView.platformCollision(this.x, this.y + this.yVelocity,width,height);

        //if no platform, continue falling
        if(object == null){
            this.isOnGround = false;
        }

        //If platform, land on top of it and stop falling
        if(object != null){
            this.isOnGround = true;
            if(object.getY() > this.y)
                this.y = object.getY() - this.HEIGHT;
            else
                this.y = object.getY() + object.HEIGHT;
            this.yVelocity = 5;
            return;
        }

        //If we reach bottom of screen, stop falling
        if(this.y + yVelocity >=screenHeight-199) {
            this.isOnGround = true;
            this.a = 0;
            this.yVelocity = 5;
            this.y = screenHeight-199;
            return;
        }else {
            this.y += yVelocity;
        }

    }

    /**
     * Method to draw the spring on the canvas
     * @param canvas
     */
    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x,y, null);
    }

    //Getters
    public int getX(){return this.x;}
    public int getY(){return this.y;}
}

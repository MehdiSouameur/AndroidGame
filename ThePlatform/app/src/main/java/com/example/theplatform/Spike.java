package com.example.theplatform;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Class for obstacle type: Spikes
 * The player dies on contact with these spikes
 */
public class Spike extends GameEntity {

    private GameView gameView;
    private Bitmap image;
    private int x;
    private int y;
    private int distanceCounter = 0;
    private static int yVelocity = 50;

    private static float a = 0;
    private boolean isOnGround;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    private Bitmap[] animation;

    /**
     * Constructor for spike class
     * @param gameView
     * @param image
     * @param x
     * @param y
     */
    public Spike(GameView gameView, Bitmap image, int x, int y) {
        super(image, 1, 2, x, y);

        this.gameView = gameView;
        this.image = image;
        this.x = x;
        this.y = y;

    }

    /**
     * Handles gravity
     * Spikes need gravity to facilitate the initialisation of spikes
     * This way, if we spawn the spikes slightly above a platform it will firmly
     * land on the platform
     */
    public void gravity(){
        //Check if there is a platform below
        Platform object = gameView.platformCollision(this.x, this.y + this.yVelocity,width,height);

        //if no platform, keep falling
        if(object == null){
            this.isOnGround = false;
        }
        //If there is a platform, land on it
        if(object != null){
            this.isOnGround = true;
            if(object.getY() > this.y)
                this.y = object.getY() - this.HEIGHT;
            else
                this.y = object.getY() + object.HEIGHT;
            this.yVelocity = 5;
            return;
        }

        //If we fall to bottom of screen, stop falling
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
     * Draw the Spikes on the canvas
     * @param canvas
     */
    public void draw(Canvas canvas) {
        int newX = (screenWidth / 1080) * this.x;
        int newY = (screenHeight / 1794) * this.y;

        canvas.drawBitmap(image, newX, newY, null);
    }

    public int getX(){return this.x;}
    public int getY(){return this.y;}
}

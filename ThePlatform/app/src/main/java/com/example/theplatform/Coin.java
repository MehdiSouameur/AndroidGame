package com.example.theplatform;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Coin extends GameEntity{
    private GameView gameView;

    private int x;
    private int y;
    private int health;
    private Bitmap image;

    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    /**
     * Constructor for coin class
     * @param gameView
     * @param image
     * @param x
     * @param y
     */
    public Coin(GameView gameView, Bitmap image, int x, int y){
        super(image, 1, 1, x, y);
        //Check if the coin is inside the boundaries before initialising
        if(width > x)
            x = width;
        if(screenWidth - width < x)
            x = screenWidth - width;
        if(height > y)
            y = height;
        if(screenHeight - height < y)
            y = screenHeight - height;
        //Check if the coin is inside a platform
        Platform object = gameView.platformCollision(x,y,width,height);
        if(object != null)
            if(object.getY() > this.y)
                y = object.getY() - this.HEIGHT;
            else
                y = object.getY() + object.HEIGHT;

        //Initialise values
        this.x = x;
        this.y = y;
        this.gameView = gameView;
        this.health = 1;
        this.image = image;
    }

    /**
     * Draw Coin on canvas
     * @param canvas
     */
    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.image, x, y, null);
    }

    //Getters
    public void removeHealth(){this.health--;}
    public int getHealth(){return this.health;}
    public int getX(){return this.x;}
    public int getY(){return this.y;}
}

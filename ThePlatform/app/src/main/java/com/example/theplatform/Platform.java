package com.example.theplatform;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Class for the platform entities
 */
public class Platform extends GameEntity{
    //Initialise values
    private Bitmap image;
    private int x,y;

    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    /**
     * Constructor for Platform
     * @param gameView
     * @param image
     * @param x
     * @param y
     */
    public Platform(GameView gameView, Bitmap image, int x, int y){
        super(image, 2, 2,x ,y);
        this.image = image;
        this.x = x;
        this.y = y;


    }

    /**
     * Draw method for Platform
     * @param canvas
     */
    public void draw(Canvas canvas) {
        Bitmap bitmap = this.image;

        int newX = (screenWidth / 1080) * this.x;
        int newY = (screenHeight / 1794) * this.y;

        canvas.drawBitmap(bitmap, newX, newY, null);
    }

    //Getters
    public int getX(){ return this.x;}
    public int getY(){ return this.y;}
}

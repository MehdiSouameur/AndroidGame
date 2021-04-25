package com.example.theplatform;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Class for the enemy type: Goomba
 */
public class Goomba extends GameEntity{

    //Row and column for animation
    private int rowUsing = 0;
    private int colUsing;
    private int colUsingTempo = 0;

    //Initialise values
    private GameView gameView;
    private Bitmap image;
    private int x;
    private int y;
    private boolean goRight = false;
    private int distance;
    private int distanceCounter = 0;
    private int yVelocity = 50;
    private int xVelocity = 5;
    private int maxYVelocity = 50;
    private static float a = 0;
    private boolean isOnGround;
    int health = 1;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    private Bitmap[] animation;

    public Goomba(GameView gameView, Bitmap image, int x, int y, int distance){
        super(image, 1, 2, x,y);

        this.gameView = gameView;
        this.image = image;
        this.x = x;
        this.y = y;
        this.distance = distance;

        this.animation = new Bitmap[colCount];

        for(int col = 0; col< this.colCount; col++ ) {
            this.animation[col]  = this.createSubImageAt(rowUsing, col);
        }
    }

    /**
     * Update method to update position of goomba
     */
    public void update(){
        //If we reached max distance turn around
        if(distanceCounter <= 0){
            this.goRight = !this.goRight;
            distanceCounter = distance/xVelocity;
        }
        //If we hit boundary turn around and reset distancecounter
        if(width > x + xVelocity || screenWidth - width < x + xVelocity) {
            if(width > x + xVelocity)
                this.x = width;
            else
                this.x = screenWidth - width;
            this.goRight = !this.goRight;
            distanceCounter = distance / xVelocity;
        }

        if(this.goRight == true){
            this.x += xVelocity;
        }
        else if(this.goRight == false) {
            this.x -= xVelocity;
        }

        this.distanceCounter -= 1;
        colUsingTempo++;
        if(colUsingTempo%4 == 0)
            this.colUsing++; //increment column to use different picture for animation
        if(colUsing >= this.colCount)  {
            this.colUsing =0;
        }

        gravity();

    }

    public void removeHealth(){ this.health-=1;}

    /**
     * Get the image in animation
     * @return
     */
    public Bitmap getCurrentMoveBitmap() {
        Bitmap[] bitmaps = this.animation;
        return bitmaps[this.colUsing];
    }

    /**
     * Draw the goomva on the canvas
     * @param canvas
     */
    public void draw(Canvas canvas) {
        Bitmap bitmap = this.getCurrentMoveBitmap();

        canvas.drawBitmap(bitmap, x, y, null);
    }

    /**
     * Handles gravity
     */
    public void gravity(){
        //Change the yVelocity using an acceleration if the entity is in the air
        if(this.isOnGround == false){
            if(this.yVelocity<=this.maxYVelocity) {
                this.yVelocity = (int) (this.yVelocity + (this.a * this.maxYVelocity));
                if(this.yVelocity >= this.maxYVelocity)
                    this.yVelocity = this.maxYVelocity;
            }
        }

        if(a < 1)
            this.a += 0.01;
        //Check if there is a platform underneath, or above
        Platform object = gameView.platformCollision(this.x, this.y + this.yVelocity,width,height);

        //If there is no platform, we keep falling
        if(object == null){
            this.isOnGround = false;
        }

        //If there is a platform, we stop falling
        if(object != null){
            this.isOnGround = true;
            if(object.getY() > this.y)
                this.y = object.getY() - this.HEIGHT;
            else
                this.y = object.getY() + object.HEIGHT;
            this.a = 0;
            this.yVelocity = 5;
            return;
        }
        //If we reach bottom of screen, we stop falling
        if(this.y + yVelocity >=screenHeight-width) {
            this.isOnGround = true;
            this.a = 0;
            this.yVelocity = 5;
            this.y = screenHeight-width;
            return;
        }else {
            this.y += yVelocity;
        }

    }

    //Getters
    public boolean getIsOnGround(){ return this.isOnGround; }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public int getWidth(){ return this.width;}
    public int getHealth(){return this.health;}
}

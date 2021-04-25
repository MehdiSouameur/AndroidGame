package com.example.theplatform;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Player class: The user can control this player by
 * pressing the left side of the screen to go left,
 * the right side to go right and the top to jump
 */
public class Player extends GameEntity{
    //See if we are going left or right
    private static final int ROW_LEFT_TO_RIGHT = 0;
    private static final int ROW_RIGHT_TO_LEFT = 1;
    private int rowUsing = ROW_LEFT_TO_RIGHT;
    //Intialise variables for animation
    private int colUsing;
    private int colUsingTempo = 0;
    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    protected final int rowCount;
    protected final int colCount;

    private boolean endLevel = false;
    private boolean touchEvent = false;
    private boolean goLeft = false;
    private boolean isOnGround;
    private boolean wasOnPlatform;
    //Initialise values
    private Bitmap image;
    private int x,y;
    public static final float VELOCITY = 0.5f;
    private static double a; //Acceleration
    private int xVelocity = 15;
    private int yVelocity = 50;
    private int maxYVelocity = 50;

    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    int health = 1;
    private GameView gameView;

    /**
     * Constructor class for player
     * @param gameView
     * @param image
     * @param x
     * @param y
     */
    public Player(GameView gameView, Bitmap image, int x, int y) {
        super(image, 2, 2, x,y);

        this.gameView = gameView;
        this.image = image;
        this.x = x;
        this.y = y;
        this.colCount = 2;
        this.rowCount = 2;
        this.isOnGround = false;
        this.a = 0;

        this.rightToLefts = new Bitmap[colCount];
        this.leftToRights = new Bitmap[colCount];

        for(int col = 0; col< this.colCount; col++ ) {
            this.rightToLefts[col]  = this.createSubImageAt(ROW_RIGHT_TO_LEFT, col);
            this.leftToRights[col] = this.createSubImageAt(ROW_LEFT_TO_RIGHT, col);

        }
    }

    /**
     * Get the direction in which we are headed
     * @return
     */
    public Bitmap[] getMoveBitmaps(){
        switch(rowUsing) {
            case ROW_LEFT_TO_RIGHT:
                return this.leftToRights;
            case ROW_RIGHT_TO_LEFT:
                return this.rightToLefts;
            default:
                return null;
        }
    }

    /**
     * get the current image in the animation
     * @return
     */
    public Bitmap getCurrentMoveBitmap() {
        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[this.colUsing];
    }

    /**
     * Draw the player on the canvas
     * @param canvas
     */
    public void draw(Canvas canvas) {
        Bitmap bitmap = this.getCurrentMoveBitmap();
        /*X = (targetScreenWidth / defaultScreenWidth) * defaultXCoordinate
        Y = (targetScreenHeight / defaultScreenHeight) * defaultYCoordinate
        */
        //Scale x and y coordinates to new phone size
        int newX = (screenWidth / 1080) * this.x;
        int newY = (screenHeight / 1794) * this.y;

        canvas.drawBitmap(bitmap, newX, newY, null);
    }

    /**
     * Method to update the values of player
     * this is always called, and does not require the input of the user
     */
    public void constantly(){
        //Check if there is  aplatform below or above
        Platform object = gameView.platformCollision(this.x, this.y + yVelocity,this.colCount,this.rowCount);
        if(object == null){
            this.isOnGround = false;
            if(wasOnPlatform == true){
                this.a = 0.02;
                this.wasOnPlatform = false;
            }
        }
        //Collision with entities
        gameView.springCollision(x,y,width,height);
        gameView.coinCollision(x,y,width,height);
        if(gameView.goombaCollision(this.x,y,width,height) != null){
            this.health--;
        }
        if(gameView.jumperCollision(this.x,y,width,height) != null){
            this.health--;
        }
    }

    /**
     * Method to update the values of player
     * this is only called when the user touches the screen
     */
    public void update() {
        //Coltempo to slow down the animation
        colUsingTempo++;
        if(colUsingTempo%4 == 0)
            this.colUsing++; //increment column to use different picture for animation
        if(colUsing >= this.colCount)  {
            this.colUsing =0;
        }
        //Next position (set by default to be to the right)
        int newPosX = x + xVelocity;

        //test if we are going left or right
        if(this.goLeft == false){
            newPosX = x + xVelocity;
        } else {
            newPosX = x - xVelocity;
        }

        //Test if we are going left or right and change animation side
        if(newPosX > x){
            this.rowUsing = ROW_RIGHT_TO_LEFT;
        } else {
            this.rowUsing = ROW_LEFT_TO_RIGHT;
        }

        //collision with a goomba
        if(gameView.goombaCollision(newPosX,y,width,height) != null || gameView.spikesCollision(newPosX,y,width,height) != null ){
            this.health--;
        }


        //Test if we hit a wall
        if(newPosX < screenWidth - (image.getWidth()/2) && newPosX > 0){
            //Update position
            if(gameView.platformCollision(newPosX, this.y,width,height) == null){
                this.x = newPosX;
            }
        }
    }

    /**
     * Hnadles gravity for player
     */
    public void gravity(){

        if(this.yVelocity!=maxYVelocity) {
            this.yVelocity = (int) (yVelocity + (this.a * maxYVelocity));
            if(this.yVelocity >= maxYVelocity)
                this.yVelocity = maxYVelocity;
        }
        if(a < 1)
            this.a += 0.0009;
        //Check if there is a platform below or above
        Platform object = gameView.platformCollision(this.x, this.y + yVelocity, width, height);

        //If there is platform, handle it
        if(object != null){
            this.isOnGround = true;
            this.wasOnPlatform = true;
            if(object.getY() > this.y)
               this.y = object.getY() - this.HEIGHT/2;
            else
                this.y = object.getY() + object.HEIGHT;
            this.a = 0;
            this.yVelocity = 5;
            return;
        }

        //Collision with spikes
        if(gameView.spikesCollision(x, this.y + yVelocity,width,height) != null)
            this.health--;
        this.y += yVelocity;


       }

    //Setters and getters

    public void setXvector(int x){
        this.xVelocity = x;
    }

    public void setYvector(int y){
        this.yVelocity = y;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public void setTouchEvent(boolean set){
        this.touchEvent = set;
    }
    public void setIsOnGround(boolean set) { this.isOnGround = set;}
    public void setyVelocity(int yVelocity) {this.yVelocity = yVelocity;}
    public void setEndLevel(boolean bol) { this.endLevel = bol;}
    public void setA(int a){this.a = a;}
    public void setWasOnPlatform(boolean bol){this.wasOnPlatform = bol;}

    public void setGoLeft(boolean b){
        this.goLeft = b;
    }

    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public int getHealth(){ return this.health;}

    public boolean getTouchEvent(){
        return this.touchEvent;
    }
    public boolean getIsOnGround(){ return this.isOnGround; }
    public boolean getEndLevel(){ return this.endLevel; }


}

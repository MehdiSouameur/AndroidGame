package com.example.theplatform;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Class for enemy type: jumper
 * An enemy which jumps left and right
 */
public class Jumper extends GameEntity{

    //Values for animation
    private int rowUsing = 0;
    private int colUsing;
    private int colUsingTempo = 0;

    //Initialise values
    private GameView gameView;
    private Bitmap image;
    private int x;
    private int y;
    private boolean goRight = true;
    private int jumps;
    private int jumpCounter = 0;
    private int yVelocity = 50;
    private int xVelocity = 5;
    private int maxYVelocity = 30;
    private static double acceleration = 0;
    private boolean isOnGround = false;
    int health = 1;
    int tempo = 0;

    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;


    /**
     * Constructor for Jumper
     * @param gameView
     * @param image
     * @param x
     * @param y
     * @param jumps
     */
    public Jumper(GameView gameView, Bitmap image, int x, int y, int jumps){
        super(image, 1, 2, x,y);

        this.gameView = gameView;
        this.image = image;
        this.x = x;
        this.y = y;
        this.jumps = jumps;

        this.image = image;
    }

    /**
     * Causes the start of a jump
     */
    private void jump(){
        isOnGround = false;
        yVelocity = -20;
        y -= 1;
        jumpCounter++;
    }

    /**
     * Update the position of the jumper
     */
    public void update(){
        //If we are on the ground then we jump
        if(isOnGround == true){
            jump();
        }

        //If we collide with the wall, turn around
        if(0 > x + xVelocity || screenWidth - width < x + xVelocity) {
            jumpCounter = 0;
            if(width > x + xVelocity)
                this.x = 0;
            else
                this.x = screenWidth - width;
            this.goRight = !this.goRight;
        }

        //If we reached max amount of jumps, we turn around
        if(jumpCounter == jumps){
            this.goRight = !this.goRight;
            jumpCounter = 0;
        }
        if(goRight == true)
            this.x += xVelocity;
        else
            this.x -= xVelocity;

        gravity();
    }

    public void removeHealth(){ this.health-=1;}

    public void draw(Canvas canvas) {

        canvas.drawBitmap(this.image, x, y, null);
    }

    /**
     * Method which handles gravity
     */
    private void gravity(){
        //If we are in the air we accelerate downwards
        if(this.isOnGround == false){
            if(this.yVelocity<=this.maxYVelocity) {
                this.yVelocity = (int) (this.yVelocity + (this.acceleration * this.maxYVelocity));
                if(yVelocity == 0)
                    this.acceleration += 0.01;
                if(this.yVelocity >= this.maxYVelocity)
                    this.yVelocity = this.maxYVelocity;
            }
        }

        if(this.acceleration < 1)
            this.acceleration += 0.000001;
        //Check if there is a platorm beneath or above
        Platform object = gameView.platformCollision(this.x, this.y + this.yVelocity,width,height);


        //If there is a platform beneath or above, handle it
        if(object != null){
            this.isOnGround = true;
            if(object.getY() > this.y)
                this.y = object.getY() - this.HEIGHT + 5;
            else
                this.y = object.getY() + object.HEIGHT;
            this.acceleration = 0;
            this.yVelocity = 5;
            return;
        }
        //If we reach bottom of screen, stop falling
        if(this.y  >=1700-HEIGHT) {
            this.isOnGround = true;
            this.acceleration = 0;
            this.yVelocity = 5;
            this.y = 1500;
            return;
        }else if(object == null){
            this.y += yVelocity;
        }

    }

    //Getters
    public boolean getIsOnGround(){ return this.isOnGround; }
    public int getX(){ return this.x; }
    public int getY(){ return this.y; }
    public int getWidth(){ return this.width;}
    public int getHealth(){return this.health;}
}

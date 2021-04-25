package com.example.theplatform;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.*;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    //Threads
    private MainThread thread;
    private Thread goombaThread;
    private Thread jumperThread;
    private Thread staticEntityThread;
    private Thread playerThread;

    //Images
    private Bitmap background;
    private Bitmap link;
    private Bitmap block;
    private Bitmap goomba;
    private Bitmap spike;
    private Bitmap spring;
    private Bitmap jumper;
    private Bitmap coin;

    //Application values
    private Context context;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    //Scores
    private int coinScore = 0;
    private int timeScore = 1700;
    private int enemyScore = 0;
    private int totalScore = 0;

    //Entities
    private Player player;
    private static ArrayList <Platform> platforms;
    private static ArrayList <Goomba> goombas;
    private static ArrayList<Jumper> jumpers;
    private static ArrayList<Spike> spikes;
    private static ArrayList<Spring> springs;
    private static ArrayList<Coin> coins;

    //level values
    private int levelCounter = 1;
    boolean endOfLevel = false;
    private Random random;
    private int endGame = 0;
    private boolean levelLoaded = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GameView(Context context) {
        super(context);
        this.context = context;

        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        setFocusable(true);

        this.link = BitmapFactory.decodeResource(getResources(),R.drawable.link);
        this.background = BitmapFactory.decodeResource(getResources(),R.drawable.background);
        this.background = Bitmap.createScaledBitmap(this.background, this.screenWidth, this.screenHeight, true);
        this.block = BitmapFactory.decodeResource(getResources(),R.drawable.platform);
        this.goomba = BitmapFactory.decodeResource(getResources(),R.drawable.goomba);
        this.spike = BitmapFactory.decodeResource(getResources(),R.drawable.spikes);
        this.spring = BitmapFactory.decodeResource(getResources(),R.drawable.spring);
        this.jumper = BitmapFactory.decodeResource(getResources(),R.drawable.spider);
        this.coin = BitmapFactory.decodeResource(getResources(),R.drawable.coin);

        random = new Random();
    }


    /**
     *method which handles user input for player movement
     * @param event the position of the users click
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int)event.getY();
        int x =  (int)event.getX();
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(y < 150 && player.getIsOnGround() == true){
               player.setIsOnGround(false);
               player.setyVelocity(-20);
               player.setWasOnPlatform(false);
               player.setY(player.getY()-1);
            }
        }
        //Hold down action
        if (event.getAction() != MotionEvent.ACTION_UP && y > 100) {
            if(x >= screenWidth/2){
                player.setGoLeft(false);
            } else {
                player.setGoLeft(true);
            }

            player.setTouchEvent(true);
            return true;
        }
        player.setTouchEvent(false);
        return false;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    /**
     * Method which creates the surface for the game
     * @param holder
     */
    //Create the surface in other words the game itself
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void surfaceCreated(SurfaceHolder holder ) {
        //Note to self: 1px is about 2.67x and 2.67 y
        //Screen width is 1080, screen height is 1794
        //width of platform: x=142

         //start the thread
         thread.setRunning(true);
         thread.start();

         //initialise all the data structures for entities and the player
        player = new Player(this, link, screenWidth/2 +100, 0);
        platforms = new ArrayList<>();
        goombas = new ArrayList<>();
        spikes = new ArrayList<>();
        jumpers = new ArrayList<>();
        springs = new ArrayList<>();
        coins = new ArrayList<>();

        //Initialise the threads for updating entities
        PlayerThread updatePlayer = new PlayerThread(player);
        playerThread = new Thread(updatePlayer);
        StaticEntityThread updateStaticEntity = new StaticEntityThread(coins,springs,spikes);
        staticEntityThread = new Thread(updateStaticEntity);
        JumperThread updateJumper = new JumperThread(jumpers);
        jumperThread= new Thread(updateJumper);
        GoombaThread updateGoomba = new GoombaThread(goombas);
        goombaThread = new Thread(updateGoomba);

        //Initialise all the entities in the game
        initialiseObjects();
    }

    /**
     * Loads the levels, in other words loads all the entities from text files.
     */
    public void initialiseObjects(){
        endOfLevel = false;
        InputStream input;
        AssetManager assetManager = context.getAssets();
        //Switch statement to check which level to load
        try{
            switch(this.levelCounter){
                case(1):
                    input = assetManager.open("Level1.txt");
                    break;
                case(2):
                    input = assetManager.open("Level2.txt");
                    break;
                case(3):
                    input = assetManager.open("Level3.txt");
                    break;
                case(4):
                    input = assetManager.open("Level4.txt");
                    break;
                default:
                    input = assetManager.open("Level1.txt");
                    break;

            }
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            // byte buffer into a string
            String text = new String(buffer);
            String[] parts = text.split(",");
            int n = 0;
            int distance;
            int jumps;

            //Read the file and initialise objects accordingly
            while(n != -1) {
                switch(parts[n].trim().replace("[\n]{2,}", "")) {
                    case("platform"):
                        int px = Integer.parseInt(parts[n+1].replaceAll("\\s", ""));
                        int py = Integer.parseInt(parts[n+2].replaceAll("\\s", ""));
                        Platform platform = new Platform(this, this.block, px, py);
                        platforms.add(platform);
                        if(n+3 < parts.length)
                            n+=3;
                        else {
                            n = -1;
                        }
                        break;

                    case("spike"):
                        int sx = Integer.parseInt(parts[n+1].replaceAll("\\s", ""));
                        int sy = Integer.parseInt(parts[n+2].replaceAll("\\s", ""));
                        Spike spike = new Spike(this, this.spike, sx, sy);
                        spikes.add(spike);
                        if(n+3 < parts.length)
                            n+=3;
                        else {
                            n = -1;
                        }
                        break;

                    case("goomba"):
                        int gx = Integer.parseInt(parts[n+1].replaceAll("\\s", ""));
                        int gy = Integer.parseInt(parts[n+2].replaceAll("\\s", ""));
                        distance = Integer.parseInt(parts[n+3].replaceAll("\\s", ""));
                        Goomba goomba = new Goomba(this,this.goomba,gx,gy,distance);
                        goombas.add(goomba);
                        if(n+4 < parts.length)
                            n+=4;
                        else {
                            n = -1;
                        }
                        break;

                    case("jumper"):
                        int jx = Integer.parseInt(parts[n+1].replaceAll("\\s", ""));
                        int jy = Integer.parseInt(parts[n+2].replaceAll("\\s", ""));
                        jumps = Integer.parseInt(parts[n+3].replaceAll("\\s", ""));
                        Jumper jumper = new Jumper(this,this.jumper,jx,jy,jumps);
                        jumpers.add(jumper);
                        if(n+4 < parts.length)
                            n+=4;
                        else {
                            n = -1;
                        }
                        break;

                    case("spring"):
                        int springx = Integer.parseInt(parts[n+1].replaceAll("\\s", ""));
                        int springy = Integer.parseInt(parts[n+2].replaceAll("\\s", ""));
                        Spring spring = new Spring(this, this.spring, springx, springy);
                        springs.add(spring);
                        if(n+3 < parts.length)
                            n+=3;
                        else {
                            n = -1;
                        }
                        break;
                    case("coin"):
                        int coinx = Integer.parseInt(parts[n+1].replaceAll("\\s", ""));
                        int coiny = Integer.parseInt(parts[n+2].replaceAll("\\s", ""));
                        Coin coin = new Coin(this, this.coin, coinx, coiny);
                        coins.add(coin);
                        if(n+3 < parts.length)
                            n+=3;
                        else {
                            n = -1;
                        }
                        break;

                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        //Based on level, initialise enemies randomly

        switch(this.levelCounter){
            case(1):
                while(goombas.size() != 3){
                    int gx, gy, gdistance;
                    gx = (int)Math.floor(Math.random() * (screenWidth - 100 - 100 + 1 ) + 100);
                    gy = (int)Math.floor(Math.random() * (1400 - 500 + 1) + 500);
                    gdistance = (int)Math.floor(Math.random() * (500 - 100 + 1) + 150);
                    Goomba goomba = new Goomba(this,this.goomba,gx,gy,gdistance);
                    goombas.add(goomba);
                }
                while(coins.size() != 10){
                    int coinX, coinY;
                    coinX = (int)Math.floor(Math.random() * (screenWidth - 100 - 100 + 1 ) + 100);
                    coinY = (int)Math.floor(Math.random() * (1400 - 500 + 1) + 500);
                    Coin coin = new Coin(this,this.coin,coinX,coinY);
                    coins.add(coin);
                }
                break;

            case(2):
                while(jumpers.size() != 3){
                    int jx, jy, jumps;
                    jx = (int)Math.floor(Math.random() * (screenWidth - 100 - 100 + 1 ) + 100);
                    jy = (int)Math.floor(Math.random() * (1400 - 500 + 1) + 500);
                    jumps = (int)Math.floor(Math.random() * (5 - 1 + 1) + 1);
                    Jumper jumper = new Jumper(this,this.jumper,jx,jy,jumps);
                    jumpers.add(jumper);
                }
                while(coins.size() != 10){
                    int coinX, coinY;
                    coinX = (int)Math.floor(Math.random() * (screenWidth - 100 - 100 + 1 ) + 100);
                    coinY = (int)Math.floor(Math.random() * (1400 - 500 + 1) + 500);
                    Coin coin = new Coin(this,this.coin,coinX,coinY);
                    coins.add(coin);
                }
                break;
            case(3):
                break;
            case(4):
                while(jumpers.size() != 3){
                    int jx, jy, jumps;
                    jx = (int)Math.floor(Math.random() * (screenWidth - 100 - 100 + 1 ) + 100);
                    jy = (int)Math.floor(Math.random() * (1400 - 500 + 1) + 500);
                    jumps = (int)Math.floor(Math.random() * (5 - 1 + 1) + 1);
                    Jumper jumper = new Jumper(this,this.jumper,jx,jy,jumps);
                    jumpers.add(jumper);
                }
                while(goombas.size() != 3){
                    int gx, gy, gdistance;
                    gx = (int)Math.floor(Math.random() * (screenWidth - 100 - 100 + 1 ) + 100);
                    gy = (int)Math.floor(Math.random() * (1400 - 500 + 1) + 500);
                    gdistance = (int)Math.floor(Math.random() * (500 - 100 + 1) + 150);
                    Goomba goomba = new Goomba(this,this.goomba,gx,gy,gdistance);
                    goombas.add(goomba);
                }
                while(coins.size() != 4){
                    int coinX, coinY;
                    coinX = (int)Math.floor(Math.random() * (screenWidth - 100 - 100 + 1 ) + 100);
                    coinY = (int)Math.floor(Math.random() * (1400 - 500 + 1) + 500);
                    Coin coin = new Coin(this,this.coin,coinX,coinY);
                    coins.add(coin);
                }
                break;
            default:
                break;
        }
        this.levelLoaded = true;
    }

    /**
     * Delete all the entities in order to load a new level or end the game
     */
    public void deleteObjects(){
        //Clear all the arraylists
        platforms.clear();
        goombas.clear();
        spikes.clear();
        jumpers.clear();
        springs.clear();
        coins.clear();
        //increment level
        this.levelCounter++;
        //If we're done with game, proceed to endScreen activity
        if(levelCounter>=5){
            totalScore = this.coinScore + this.timeScore + this.enemyScore;
            Intent intent = new Intent(context, EndScreen.class);
            intent.putExtra("enemyScore", String.valueOf(this.enemyScore));
            intent.putExtra("coinScore", String.valueOf(this.coinScore));
            intent.putExtra("timeScore", String.valueOf(this.timeScore));
            intent.putExtra("totalScore", String.valueOf(this.totalScore));
            context.startActivity(intent);
            System.exit(0);
        }
        //initialise entities again and start player back at the top
        initialiseObjects();
        player.setX(screenWidth/2 +100);
        player.setY(0);
        this.player.setEndLevel(false);
    }

    /**
     * Once enemies are all defeated, open path to next level
     */
    public void openEndLevel(){
        endOfLevel = true;
        platforms.remove(platforms.size()-1);
        platforms.remove(platforms.size()-1);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //Stop Thread
        boolean retry = true;
        while(retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }


    }

    /**
     * Update the positions and values of the entities in the game
     */
    public void update(){
        //Decrease time score each time we update
        if(timeScore >0)
            timeScore--;

        //run the threads which update the game entities.
        playerThread.run();
        goombaThread.run();
        staticEntityThread.run();
        jumperThread.run();


        if(player.getY() >=screenHeight){
            deleteObjects();
        }
        if(player.getHealth() <= 0){
            levelCounter = 10;
            timeScore = 0;
            deleteObjects();
        }

        if(goombas.isEmpty() && jumpers.isEmpty() &&endOfLevel == false ){
            openEndLevel();
        }
    }

    /**
     * Collision with platoforms
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public Platform platformCollision(int x, int y, int width, int height){
        for(Platform element : platforms) {
            if(element.getX() < x + width && x < element.getX() + element.width)
                if(element.getY() < y + height && y < element.getY() + element.height)
                    return element;
        }
        return null;
    }

    /**
     * Collision with goombas
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public Goomba goombaCollision(int x, int y, int width, int height){
        for(Goomba element : goombas) {
            if(element.getX() < x + width && x < element.getX() + element.width)
                if(element.getY() < y + height && y < element.getY() + element.height){
                    if(y + height/2 < element.getY() && height == player.height){
                        element.removeHealth();
                        this.enemyScore += 50;
                        player.setyVelocity(-20);
                        player.setA(0);
                        return null;
                    }
                    return element;
                }
        }
        return null;
    }

    /**
     * Collision with jumpers
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public Jumper jumperCollision(int x, int y, int width, int height){
        for(Jumper element : jumpers) {
            if(element.getX() < x + width && x < element.getX() + element.width)
                if(element.getY() < y + height && y < element.getY() + element.height){
                    if(y + height/2 < element.getY() && height == player.height){
                        element.removeHealth();
                        this.enemyScore += 50;
                        player.setyVelocity(-20);
                        player.setA(0);
                        return null;
                    }
                    return element;
                }
        }
        return null;
    }

    /**
     * Collision with spikes
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public Spike spikesCollision(int x, int y, int width, int height){
        for(Spike element : spikes) {
            if(element.getX() < x + width - 10 && x < element.getX() + element.WIDTH)
                if(element.getY() < y + height - 20 && y < element.getY() + element.height){
                    return element;
                }
        }
        return null;
    }

    /**
     * Collision with Springs
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void springCollision(int x, int y, int width, int height){
        for(Spring element : springs) {
            if(element.getX() < x + width && x < element.getX() + element.WIDTH)
                if(element.getY() < y + height && y < element.getY() + element.height){
                    player.setyVelocity(-32);
                    player.setA(0);
                }
        }
    }

    /**
     * Collision witgh coins
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void coinCollision(int x, int y, int width, int height){
        for(Coin element : coins) {
            if(element.getX() < x + width && x < element.getX() + element.WIDTH)
                if(element.getY() < y + height && y < element.getY() + element.WIDTH){
                    element.removeHealth();
                    this.coinScore+=50;
                }
        }
    }

    /**
     * Draw all the entities on the canvas
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
        if(canvas!=null) {
            canvas.drawBitmap(background,0, 0, null);
            player.draw(canvas);
            for(Platform element: platforms)
                element.draw(canvas);
            for(Goomba element: goombas){
                element.draw(canvas);
            }
            for(Spike element: spikes)
                element.draw(canvas);
            for(Jumper element: jumpers)
                element.draw(canvas);
            for(Coin element: coins)
                element.draw(canvas);
            for(Spring element: springs)
                element.draw(canvas);
        }
    }

    public int getEndGame(){return this.endGame;}
    public boolean getLevelLoaded(){return this.levelLoaded;}


}

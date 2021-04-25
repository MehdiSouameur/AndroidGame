package com.example.theplatform;

import java.util.ArrayList;

/**
 * Class which contains the thread for
 * the player
 */
public class PlayerThread implements Runnable{
    private static Player player;

    public PlayerThread(Player player){
        this.player = player;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Iterate and update goombas
        player.constantly();
        if(player.getTouchEvent() == true){
            player.update();
        }
        if(player.getIsOnGround() == false)
            player.gravity();
    }
}

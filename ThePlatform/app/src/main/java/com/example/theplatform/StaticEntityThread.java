package com.example.theplatform;

import java.util.ArrayList;

/**
 * Class which containts the thread for static entities
 * Static entities are obstacles in the game, they do not move and as such
 * they do not require alot of processing power
 * one thread suffices for these entities
 */
public class StaticEntityThread implements Runnable{
    //Initialise the array lists
    private static ArrayList<Spike> spikes = new ArrayList<>();
    private static ArrayList<Spring> springs = new ArrayList<>();
    private static ArrayList<Coin> coins = new ArrayList<>();

    public StaticEntityThread(ArrayList<Coin> coins, ArrayList<Spring> springs, ArrayList<Spike> spikes){
        this.coins = coins;
        this.spikes = spikes;
        this.springs = springs;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Iterate and update static entities
        for(Spike element: spikes){
            element.gravity();
        }
        for(Spring element: springs){
            element.gravity();
        }
        for(Coin element: coins){
            if(element.getHealth() <=0)
                coins.remove(element);
        }
    }
}

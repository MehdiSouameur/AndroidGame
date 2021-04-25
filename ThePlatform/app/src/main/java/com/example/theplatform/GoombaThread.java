package com.example.theplatform;

import java.util.ArrayList;

//Class to update the position and values of the goomba isntances using a thread

public class GoombaThread implements Runnable{
    private static ArrayList<Goomba> goombas = new ArrayList<>();

    public GoombaThread(ArrayList<Goomba> goombas){
        this.goombas = goombas;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Iterate and update goombas
        for(Goomba element: goombas){
            element.gravity();
            element.update();
            if(element.getHealth() <= 0){
                goombas.remove(element);
            }
        }
    }
}

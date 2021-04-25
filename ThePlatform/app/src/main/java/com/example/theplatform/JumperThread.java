package com.example.theplatform;

import java.util.ArrayList;

/**
 * Class to handle thread for jumper entities
 */
public class JumperThread implements Runnable{
    private static ArrayList<Jumper> jumpers = new ArrayList<Jumper>();

    public JumperThread(ArrayList<Jumper> jumpers){
        this.jumpers = jumpers;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int index = 0;
        for(Jumper element: jumpers){
            element.update();
                if(element.getHealth() <= 0){
                jumpers.remove(element);
            }
        }
    }
}

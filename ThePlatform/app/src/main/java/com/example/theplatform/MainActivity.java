package com.example.theplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * MainActivity class which contains the main menu screen
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Make the game full screen
        setContentView(R.layout.activity_main);
        Button startButton = findViewById(R.id.startButton);
        //Start button
        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Game.class);
                startActivity(intent);
            }
        });

        Button exitButton = findViewById(R.id.exitButton);
        //Exit button
        exitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        Button hiscoreButton = findViewById(R.id.hiscoreButton);
       //Button to access highscore screen
        hiscoreButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Highscore.class);
                startActivity(intent);
            }
        });
    }
}
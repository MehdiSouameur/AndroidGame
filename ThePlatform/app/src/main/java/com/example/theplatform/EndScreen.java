package com.example.theplatform;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Class for the end screen
 */
public class EndScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endgame);

        //Get all of the scores and store them in variables
        String enemyScore = getIntent().getStringExtra("enemyScore");
        TextView enemy = findViewById(R.id.EnemyScore);
        enemy.setText("Enemy score: " + enemyScore);

        String coinScore = getIntent().getStringExtra("coinScore");
        TextView coin = findViewById(R.id.CoinScore);
        coin.setText("Coin score: " + coinScore);

        String timeScore = getIntent().getStringExtra("timeScore");
        TextView time = findViewById(R.id.TimeScore);
        time.setText("bonus time score: " + timeScore);

        String totalScore = getIntent().getStringExtra("totalScore");
        TextView total = findViewById(R.id.TotalScore);
        total.setText("Total score: " + totalScore);

        //Button to submit the new score
        Button submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.username);
                WriteToDatabase writer = new WriteToDatabase(input.getText().toString(),Integer.parseInt(totalScore));
                writer.Write();
                Intent intent = new Intent(getApplicationContext(), Highscore.class);
                startActivity(intent);
            }
        });

        //Button to return to main menu
        Button menuButton = findViewById(R.id.mainmenu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });



    }
}

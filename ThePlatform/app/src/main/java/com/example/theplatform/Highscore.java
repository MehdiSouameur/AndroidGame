package com.example.theplatform;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to handle the Highscore activity
 * To store the scores, we use a Firebase database
 */
public class Highscore extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView hiscoreText;

    /**
     * Set up the firebase database values
     */
    private void setup() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Scores");
        hiscoreText= (TextView) findViewById(R.id.hiscoreText);
    }

    /**
     * We check if we are connected to the internet for error handling
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isConnected(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        return connected;
    }

    /**
     * We read from the database in order to update the scores
     * on the Highscore activity.
     */
    private void readFromDatabase(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> users = new ArrayList<>();

                //Read the entries from the database
                for(DataSnapshot data : snapshot.getChildren()) {
                    String name = "";
                    String score = "";
                    int i = 0;

                    for(DataSnapshot dataChildren : data.getChildren()) {

                        if(i == 0) {
                            name = dataChildren.getValue().toString();

                        } else if(i == 1) {
                            score = dataChildren.getValue().toString();
                        }

                        i++;
                    }

                    User user = new User(name, Integer.parseInt(score));
                    users.add(user);
                }

                int n = users.size();

                //Sort the entries from Highest to lowest score
                for(int i = 0; i < n; i++) {

                    for(int j = 1; j < n - i; j++) {

                        if(Integer.parseInt(users.get(j - 1).getScore())<= Integer.parseInt(users.get(j).getScore())) {
                            Collections.swap(users, j - 1, j);
                        }

                    }

                }

                String txtPrint = "";
                int count = 1;

                //Print the highest ten results
                for(int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    txtPrint += user.getName() + " " + user.getScore() + "\n";

                    if(count == 10) break;
                    count++;
                }

                hiscoreText.setText(txtPrint);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("", "Failed to read value.", error.toException());
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        getSupportActionBar().hide();

        setup();
        //Check if connected to internet
        if(isConnected() == false){
            hiscoreText.setText("No internet, connect to the internet to see all the highscores!");
        }else{
            readFromDatabase();
        }
    }

}
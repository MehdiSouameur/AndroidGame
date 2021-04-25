package com.example.theplatform;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Class which writes a new user and new score to the highscore
 * table
 */
public class WriteToDatabase extends Highscore {
    private String username;
    private int score;
    ArrayList<User> users;
    FirebaseDatabase database;
    DatabaseReference myRef;

    /**
     * Constructor for WriteToDatabase class
     * @param username
     * @param score
     */
    public WriteToDatabase(String username, int score) {
        this.username = username;
        this.score = score;
        this.database = FirebaseDatabase.getInstance();
        this.myRef = database.getReference("Scores");
        users = new ArrayList<>();
    }

    /**
     * Write the new score to the table if requires
     */
    public void Write() {
        DatabaseReference playerUser = myRef.child(username);
        playerUser.child("name").setValue(username);
        playerUser.child("score").setValue(score);
    }

    /**
     * Get the entries in the table for comparing
     * Same method from Highscore class
     */
    public void getEntries() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Read the entries from the database
                for (DataSnapshot data : snapshot.getChildren()) {
                    String name = "";
                    String score = "";
                    int i = 0;

                    for (DataSnapshot dataChildren : data.getChildren()) {

                        if (i == 0) {
                            name = dataChildren.getValue().toString();

                        } else if (i == 1) {
                            score = dataChildren.getValue().toString();
                        }

                        i++;
                    }

                    User player = new User(name, Integer.parseInt(score));
                    users.add(player);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("", "Failed to read value.", error.toException());
            }
        });
    }
}


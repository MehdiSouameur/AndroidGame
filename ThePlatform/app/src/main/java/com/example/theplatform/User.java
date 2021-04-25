package com.example.theplatform;

/**
 * Class for the users in the Highscore Firebase table
 * This allows us to make a new entry to add to the highscore table
 */
public class User {
    private String name;
    private int score;

    public User(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return String.valueOf(score);
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setName(String name) {
        this.name = name;
    }
}

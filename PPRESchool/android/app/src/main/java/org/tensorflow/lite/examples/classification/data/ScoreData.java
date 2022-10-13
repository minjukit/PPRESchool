package org.tensorflow.lite.examples.classification.data;

import com.google.gson.annotations.SerializedName;

public class ScoreData {

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("score")
    private String score;

    @SerializedName("date")
    private String date;

    @SerializedName("userGirl")
    private int userGirl;
    //여자면 1

    @SerializedName("userLevel")
    private int userLevel;

    public ScoreData(String userEmail, String score, String date) {
        this.userEmail= userEmail;
        this.score = score;
        this.date = date;
    }
    public ScoreData(String userEmail) {
        this.userEmail= userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }
}


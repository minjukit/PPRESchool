package org.tensorflow.lite.examples.classification.data;

import com.google.gson.annotations.SerializedName;

public class ChildData {
    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userNick")
    private String userNick;

    @SerializedName("userBirth")
    private String userBirth;

    @SerializedName("userGirl")
    private int userGirl;
    //여자면 1

    @SerializedName("userLevel")
    private int userLevel;

    public ChildData(String userEmail, String userNick, int userGirl, String userBirth, int userLevel) {
        this.userEmail= userEmail;
        this.userGirl = userGirl;
        this.userNick = userNick;
        this.userBirth = userBirth;
        this.userLevel = userLevel;
    }

    public ChildData(String userEmail,int userLevel) {
        this.userEmail= userEmail;
        this.userLevel = userLevel;
    }
    public ChildData(String userEmail,int userLevel, String userNick) {
        this.userEmail= userEmail;
        this.userLevel = userLevel;
        this.userNick = userNick;
    }
    public ChildData(String userEmail) {
        this.userEmail= userEmail;
    }


    public String getUserEmail() {
        return userEmail;
    }

    public String getUserNick() {
        return userNick;
    }

    public int getUserLevel() {
        return userLevel;
    }
}

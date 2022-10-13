package org.tensorflow.lite.examples.classification.data;


import com.google.gson.annotations.SerializedName;

public class JoinData {
    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userPwd")
    private String userPwd;

    @SerializedName("userName")
    private String userName;
/*
    @SerializedName("userNick")
    private String userNick;

    @SerializedName("userBirth")
    private String userBirth;

    @SerializedName("userGirl")
    private boolean userGirl;

    @SerializedName("userLevel")
    private int userLevel;

    public JoinData(String userEmail, String userPwd, String userName, String userNick, boolean userGirl, String userBirth, int userLevel) {
        this.userEmail = userEmail;
        this.userPwd = userPwd;
        this.userName = userName;
        this.userNick = userNick;
        this.userGirl = userGirl;
        this.userBirth = userBirth;
        this.userLevel = userLevel;
    }
*/
    public JoinData(String userEmail, String userPwd, String userName) {
        this.userEmail = userEmail;
        this.userPwd = userPwd;
        this.userName = userName;
    }



/*
   public JoinData(String userNick, boolean userGirl, String userBirth, int userLevel) {
        this.userGirl = userGirl;
        this.userNick = userNick;
        this.userBirth = userBirth;
        this.userLevel = userLevel;
    }


*/
}

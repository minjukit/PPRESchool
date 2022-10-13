package org.tensorflow.lite.examples.classification.data;


import com.google.gson.annotations.SerializedName;
public class LoginResponse {

    @SerializedName("success")
    private String result;

    @SerializedName("fLogin")
    private String fLogin;

    public boolean getResult(){
        boolean resultBool = false;
        if (result.equals("true")){
            resultBool = true;
        }
        return resultBool;
    }

    public boolean getfLogin(){
        boolean firstBool = false;
        if (fLogin.equals("true")){
            firstBool = true;
        }
        return firstBool;
    }

}

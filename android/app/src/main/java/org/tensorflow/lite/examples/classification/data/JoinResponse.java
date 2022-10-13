package org.tensorflow.lite.examples.classification.data;

import com.google.gson.annotations.SerializedName;

public class JoinResponse {

    @SerializedName("success")
    //private boolean result;
    private String result;

    public boolean getResult(){
        boolean resultBool = false;
        if (result.equals("true")){
            resultBool = true;
        }
        return resultBool;
    }

}
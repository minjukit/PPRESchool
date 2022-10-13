package org.tensorflow.lite.examples.classification.data;

import com.google.gson.annotations.SerializedName;

public class ClassificationResponse {

    @SerializedName("success")
    private boolean result;

    public boolean getResult(){
        return result;
    }

}

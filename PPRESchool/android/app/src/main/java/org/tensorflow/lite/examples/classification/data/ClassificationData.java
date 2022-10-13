package org.tensorflow.lite.examples.classification.data;

import com.google.gson.annotations.SerializedName;

public class ClassificationData {

    @SerializedName("Title")
    String title;

    public ClassificationData(String title) {
        this.title = title;
    }
}

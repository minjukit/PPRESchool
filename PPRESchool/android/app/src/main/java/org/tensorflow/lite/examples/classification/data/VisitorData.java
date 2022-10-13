package org.tensorflow.lite.examples.classification.data;

import com.google.gson.annotations.SerializedName;

public class VisitorData {
    @SerializedName("name")
    private String name;

    @SerializedName("genderW")
    private int genderW;

    @SerializedName("birth")
    private String birth;

    public VisitorData(String name, int genderW, String birth) {
        this.name= name;
        this.genderW = genderW;
        this.birth = birth;
    }

}

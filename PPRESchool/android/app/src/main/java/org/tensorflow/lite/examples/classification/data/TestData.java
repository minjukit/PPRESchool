package org.tensorflow.lite.examples.classification.data;

import com.google.gson.annotations.SerializedName;

public class TestData {

    @SerializedName("titleName")
    private String titleName;

    @SerializedName("krName")
    private String krName;



    public TestData(String titleName, String krName) {
        this.titleName = titleName;
        this.krName = krName;
    }

    public String getTitleName() {
        return titleName;
    }

    public String getKrName() {
        return krName;
    }

}

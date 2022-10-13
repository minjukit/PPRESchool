package org.tensorflow.lite.examples.classification.data;
import com.google.gson.annotations.SerializedName;

public class VocaData {
    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("titleName")
    private String titleName;

    @SerializedName("krName")
    private String krName;

    @SerializedName("date")
    private String date;

    private int set;

    public VocaData(String userEmail, String titleName, String krName, String date) {
        this.userEmail = userEmail;
        this.titleName = titleName;
        this.krName = krName;
        this.date = date;
    }

    public VocaData(String userEmail, int set) {
        this.userEmail = userEmail;
        this.set = set;
    }

    public VocaData(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTitleName() {
        return titleName;
    }

    public String getKrName() {
        return krName;
    }

    public String getDate() {
        return date;
    }
}

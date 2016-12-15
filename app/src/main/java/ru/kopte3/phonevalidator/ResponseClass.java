package ru.kopte3.phonevalidator;


import com.google.gson.annotations.SerializedName;

public final class ResponseClass {
    boolean valid;
    @SerializedName("international-number")
    String internationalNumber = null;
    @SerializedName("local-number")
    String localNumber;
    String location;

    public ResponseClass(boolean valid, String internationalNumber, String localNumber, String location) {
        super();
        this.valid = valid;
        this.internationalNumber = internationalNumber;
        this.localNumber = localNumber;
        this.location = location;
    }
}

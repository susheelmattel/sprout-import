package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by subram13 on 1/11/18.
 */

public class ErrorBody {
    @SerializedName("errors")
    private ArrayList<Error> mErrors;

    public ErrorBody(ArrayList<Error> errors) {
        mErrors = errors;
    }

    public ArrayList<Error> getErrors() {
        return mErrors;
    }
}

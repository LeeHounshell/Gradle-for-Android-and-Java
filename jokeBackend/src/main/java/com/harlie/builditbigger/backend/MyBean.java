package com.harlie.builditbigger.backend;

/** The object model for the data we are sending through endpoints */
public class MyBean {

    private String myData;

    @SuppressWarnings("unused")
    public String getData() {
        return myData;
    }

    public void setData(String data) {
        myData = data;
    }
}
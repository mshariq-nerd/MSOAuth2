package com.nerdapplabs.msoauth2.pojo;

import com.google.gson.annotations.SerializedName;
import com.nerdapplabs.msoauth2.utility.ReadProperties;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

/**
 * Created by Mohd Shariq on 27/01/17.
 */

public class User implements Serializable {

    public User() {
        try {
            Properties properties = ReadProperties.getPropertiesValues();
            this.locale = properties.getProperty("LOCALE");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SerializedName("code")
    private int code;

    @SerializedName("username")
    private String userName;

    @SerializedName("firstname")
    private String firstName;

    @SerializedName("lastname")
    private String lastName;

    @SerializedName("email")
    private String emailAddress;

    @SerializedName("dob")
    private String dob;

    @SerializedName("password")
    private String password;

    @SerializedName("show_message")
    private String showMessage;

    @SerializedName("_locale")
    private String locale;

    @SerializedName("image_url")
    private String imageURL;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public String getShowMessage() {
        return showMessage;
    }

    public void setShowMessage(String showMessage) {
        this.showMessage = showMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}

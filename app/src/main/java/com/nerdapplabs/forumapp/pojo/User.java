package com.nerdapplabs.forumapp.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mohd on 27/01/17.
 */

public class User {
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

    @SerializedName("email_confirmation")
    private String emailConfirmation;

    @SerializedName("scope")
    private String scope;

//
//    public User() {
//        this.scope = "API";
//    }

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

    public String getEmailConfirmation() {
        return emailConfirmation;
    }

    public void setEmailConfirmation(String emailConfirmation) {
        this.emailConfirmation = emailConfirmation;
    }
}

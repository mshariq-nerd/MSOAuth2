package com.nerdapplabs.forumapp.oauth.request;


public class SignUpRequest {
    private String firstname;
    private String lastname;
    private String dob;
    private String email;
    private String username;
    private String password;
    private String email_confirmation;
    private String client_id;
    private String client_secret;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail_confirmation(String email_confirmation) {
        this.email_confirmation = email_confirmation;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}


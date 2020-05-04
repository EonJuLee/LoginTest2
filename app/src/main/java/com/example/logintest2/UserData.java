package com.example.logintest2;

import java.io.Serializable;

public class UserData implements Serializable {
    private String Username;
    private String Useremail;

    public UserData(){

    }

    public UserData(String Username,String Useremail){
        this.Username=Username;
        this.Useremail=Useremail;
    }

    public String getUsername() {
        return Username;
    }

    public String getUseremail() {
        return Useremail;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setUseremail(String useremail) {
        Useremail = useremail;
    }

}
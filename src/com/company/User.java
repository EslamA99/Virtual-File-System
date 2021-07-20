package com.company;


import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String username;
    private String password;
    //private ArrayList<String> capabilities;
    //private ArrayList<String> folders;

    public User( String username,  String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
/*
    public ArrayList<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(ArrayList<String> capabilities) {
        this.capabilities = capabilities;
    }

    public ArrayList<String> getFolders() {
        return folders;
    }

    public void setFolders(ArrayList<String> folders) {
        this.folders = folders;
    }
*/



    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
               // ", capabilities=" + capabilities +
               // ", folders=" + folders +
                '}';
    }
}

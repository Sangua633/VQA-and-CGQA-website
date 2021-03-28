package com.example.demo;

import java.io.Serializable;
import java.util.List;

public class PermissionResponse implements Serializable {


    public PermissionResponse(String token, List<String> roles, String username, String fullname) {
        this.token = token;
        this.roles = roles;
        this.username = username;
        this.fullname = fullname;
    }

    private static final long serialVersionUID = -1L;
    private String token;
    private List<String> roles;
    private String username;
    private String fullname;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }
    public String getFullname() {
        return fullname;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}

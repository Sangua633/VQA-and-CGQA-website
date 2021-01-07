package com.example.demo;
import java.io.Serializable;
import java.util.HashMap;

public class ResponseRole implements Serializable{
    private static final long serialVersionUID = -3L;
    private HashMap<String,Object> token;

    public ResponseRole(HashMap<String, Object> token) {
        this.token = token;
    }

    public HashMap<String,Object> getToken() {
        return token;
    }

    public void setToken(HashMap<String,Object> token) {
        this.token = token;
    }

}

package com.project.iway.Model;

import java.util.Date;

public class Token {
    private String id;
    public String token;

    public Token() {

    }

    public Token(String id, String token) {
        this.id = id;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
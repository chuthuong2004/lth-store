package com.chuthuong.lthstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListUser {
    public ListUser() {
    }

    public ListUser(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @SerializedName("users")
    private List<User> users;

    @Override
    public String toString() {
        return "ListUser{" +
                "users=" + users +
                '}';
    }
}

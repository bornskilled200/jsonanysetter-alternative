package com.unseenspace.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.EnumSet;

/**
 * A regular user
 *
 * Created by madsk_000 on 8/30/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String username;
    private String password;
    private EnumSet<Permission> permissions = EnumSet.noneOf(Permission.class);

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EnumSet<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(EnumSet<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        return permissions != null ? permissions.equals(user.permissions) : user.permissions == null;

    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "com.unseenspace.jackson.User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}

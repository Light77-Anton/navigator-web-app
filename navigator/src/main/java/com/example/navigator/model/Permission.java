package com.example.navigator.model;

public enum Permission {

    HIRE("user:hire"),
    WORK("user:work"),
    MODERATE("user:moderate");

    private final String permissions;

    Permission(String permissions) {
        this.permissions = permissions;
    }

    public String getPermission() {
        return permissions;
    }
}

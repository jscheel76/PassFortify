package com.queomedia.scheel;

public class DataEntry {

    /**
     * String used to store service.
     */
    private final String service;

    /**
     * String used to store username.
     */
    private final String username;

    /**
     * String used to save password.
     */
    private String password;

    /**
     * Method called upon adding data to tableview.
     * @param service Service to add.
     * @param username Username to add.
     * @param password Password to add.
     */
    public DataEntry(final String service, final String username, final String password) {
        this.service = service;
        this.username = username;
        this.password = password;
    }

    public String getService() {
        return service;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}

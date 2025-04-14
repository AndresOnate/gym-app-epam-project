package com.epam.gymapp.dto;


public class PasswordChangeRequest {

    private String username;
    private String oldPassword;
    private String newPassword;

    public PasswordChangeRequest() {
    }

    public PasswordChangeRequest(String oldPassword, String newPassword, String username) {
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }  
}

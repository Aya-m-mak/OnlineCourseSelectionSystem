package com.courseselection;

public class Administrator {

    private String adminId;
    private String adminName;
    private String password;

    public Administrator(String adminId, String adminName, String password) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.password = password;
    }

    public boolean authenticate(String inputId, String inputPassword) {
        return adminId.equals(inputId) && password.equals(inputPassword);
    }

    public String getAdminId() {
        return adminId;
    }
    public String getAdminName() {
        return adminName;
    }
}

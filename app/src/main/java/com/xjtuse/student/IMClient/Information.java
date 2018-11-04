package com.xjtuse.student.IMClient;

public class    Information {
    private String name;
    private String enterprise;

    public Information() {
    }

    public Information(String name, String enterprise) {
        this.name = name;
        this.enterprise = enterprise;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(String enterprise) {
        this.enterprise = enterprise;
    }
}

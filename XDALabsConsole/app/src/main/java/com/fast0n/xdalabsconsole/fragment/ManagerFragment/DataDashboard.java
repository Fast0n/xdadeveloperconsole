package com.fast0n.xdalabsconsole.fragment.ManagerFragment;

public class DataDashboard {

    private String title;
    private String value;
    private String color;

    DataDashboard(String title, String value, String color) {
        this.title = title;
        this.value = value;
        this.color = color;

    }

    public String getTitle() {
        return title;
    }

    String getValue() {
        return value;
    }

    String getColor() {
        return color;
    }

}

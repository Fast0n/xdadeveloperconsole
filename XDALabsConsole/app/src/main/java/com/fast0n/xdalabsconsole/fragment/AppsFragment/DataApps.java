package com.fast0n.xdalabsconsole.fragment.AppsFragment;

public class DataApps {

    private String title;
    private String img;
    private String color;

    DataApps(String title, String img, String color) {
        this.title = title;
        this.img = img;
        this.color = color;

    }

    public String getTitle() {
        return title;
    }

    String getImg() {
        return img;
    }

    String getColor() {
        return color;
    }

}

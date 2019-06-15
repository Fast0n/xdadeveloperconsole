package com.fast0n.xdalabsconsole.fragment.AppsFragment;

public class DataApps {

    private String title;
    private String img;
    private String color;
    private String id;

    DataApps(String title, String img, String color, String id) {
        this.title = title;
        this.img = img;
        this.color = color;
        this.id = id;

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

    String getId() { return id; }

}

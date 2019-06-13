package com.fast0n.xdalabsconsole.fragment.ManagerFragment;

public class DataItems {

    private String title;
    private String value;
    private String url;

    DataItems(String title, String value, String url) {
        this.title = title;
        this.value = value;
        this.url = url;

    }

    public String getTitle() {
        return title;
    }

    String getValue() {
        return value;
    }

    String getUrl() {
        return url;
    }

}

package com.fast0n.xdalabsconsole.fragment.SettingsFragment;

public class DataSettings {

    public final String idName;
    public final String title;
    public final String second_title;
    public final String value;
    public final String alert;


    DataSettings(String idName, String title, String second_title, String value, String alert) {
        this.idName = idName;
        this.title = title;
        this.value = value;
        this.second_title = second_title;
        this.alert = alert;
    }

}

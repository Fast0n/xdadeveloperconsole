package com.fast0n.xdalabsconsole.fragment.DetailsFragment;

import java.util.ArrayList;

public class DataDetails {

    public final String id;
    final String title;
    final String type;
    final String value;
    final String alert;
    final ArrayList<String> options;
    final ArrayList<String> listValue;


    DataDetails(String id, String title, String type, String value, String alert, ArrayList<String> options, ArrayList<String> listValue) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.value = value;
        this.alert = alert;
        this.options = options;
        this.listValue = listValue;
    }

}

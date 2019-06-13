package com.fast0n.xdalabsconsole.fragment.ManagerFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;



import com.fast0n.xdalabsconsole.R;

import java.util.ArrayList;

public class CustomAdapterManagerFragment extends ArrayAdapter<DataItems> {

    String name;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    String theme;
    private Context context;

    CustomAdapterManagerFragment(Context context, ArrayList<DataItems> data) {
        super(context, R.layout.row_item, data);
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataItems dataItems = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.row_item, parent, false);


            // addressed
            viewHolder.txtTitle = convertView.findViewById(R.id.title);
            viewHolder.txtValue = convertView.findViewById(R.id.value);
            ViewHolder.btnOpenURL = convertView.findViewById(R.id.btnOpenURL);
            viewHolder.url_link = convertView.findViewById(R.id.url_link);


            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();


        ViewHolder.btnOpenURL.setOnClickListener(view -> {
            //ciao
            context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(viewHolder.url_link.getText().toString())));

        });
        viewHolder.txtTitle.setText(dataItems.getTitle());


        settings = getContext().getSharedPreferences("sharedPreferences", 0);
        editor = settings.edit();
        theme = settings.getString("toggleTheme", null);


        int mColor;
        if (theme.equals("0"))
            mColor = Color.parseColor("#F5F5F5");
        else
            mColor = Color.parseColor("#212121");



        viewHolder.url_link.setText(dataItems.getUrl());
        viewHolder.txtValue.setText(dataItems.getValue());


        return convertView;
    }

    private static class ViewHolder {
        static Button btnOpenURL;
        TextView txtTitle, txtValue,  url_link;
    }
}



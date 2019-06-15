package com.fast0n.xdalabsconsole.fragment.AppsFragment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.fast0n.xdalabsconsole.R;

import java.util.ArrayList;

public class CustomAdapterAppsFragment extends ArrayAdapter<DataApps> {

    private Context context;

    CustomAdapterAppsFragment(Context context, ArrayList<DataApps> data) {
        super(context, R.layout.row_apps, data);
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataApps dataItems = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.row_apps, parent, false);


            // addressed
            viewHolder.txtTitle = convertView.findViewById(R.id.title);
            viewHolder.ivImg = convertView.findViewById(R.id.img);
            viewHolder.txtColor = convertView.findViewById(R.id.color);
            viewHolder.cardView = convertView.findViewById(R.id.cardView);


            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();


        Glide.with(context).load(dataItems.getImg()).into(viewHolder.ivImg);
        viewHolder.txtTitle.setText(dataItems.getTitle());
        viewHolder.txtColor.setText(dataItems.getColor());
        viewHolder.cardView.setCardBackgroundColor(Color.parseColor(viewHolder.txtColor.getText().toString()));


        return convertView;
    }

    private static class ViewHolder {
        TextView txtTitle, txtColor;
        CardView cardView;
        ImageView ivImg;
    }
}



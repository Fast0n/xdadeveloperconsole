package com.fast0n.xdalabsconsole.fragment.AppsFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fast0n.xdalabsconsole.EditActivity;
import com.fast0n.xdalabsconsole.R;

import java.util.List;


public class CustomAdapterAppsFragment extends RecyclerView.Adapter<CustomAdapterAppsFragment.MyViewHolder> {

    private final List<DataApps> infoList;
    private final Context context;

    CustomAdapterAppsFragment(Context context, List<DataApps> infoList) {
        this.context = context;
        this.infoList = infoList;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataApps c = infoList.get(position);

        Glide.with(context).load(c.img).into(holder.ivImg);
        holder.txtTitle.setText(c.title);
        holder.txtColor.setText(c.color);
        holder.cardView.setCardBackgroundColor(Color.parseColor(holder.txtColor.getText().toString()));

        holder.cardView.setOnClickListener(view1 ->
                context.startActivity(new Intent(context, EditActivity.class).putExtra("idApp", c.id )));

    }


    @Override
    public int getItemCount() {
        return infoList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_apps, parent, false);
        return new MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtColor;
        CardView cardView;
        ImageView ivImg;

        MyViewHolder(View view) {
            super(view);
            // addressed
            txtTitle = view.findViewById(R.id.title);
            ivImg = view.findViewById(R.id.img);
            txtColor = view.findViewById(R.id.color);
            cardView = view.findViewById(R.id.cardView);
        }
    }
}



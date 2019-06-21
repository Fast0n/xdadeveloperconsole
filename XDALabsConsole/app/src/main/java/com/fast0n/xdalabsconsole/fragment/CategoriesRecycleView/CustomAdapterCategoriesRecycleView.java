package com.fast0n.xdalabsconsole.fragment.CategoriesRecycleView;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fast0n.xdalabsconsole.R;

import java.util.List;


public class CustomAdapterCategoriesRecycleView extends RecyclerView.Adapter<CustomAdapterCategoriesRecycleView.MyViewHolder> {

    private final List<DataCategories> infoList;
    private final Context context;

    public CustomAdapterCategoriesRecycleView(Context context, List<DataCategories> infoList) {
        this.context = context;
        this.infoList = infoList;

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataCategories c = infoList.get(position);
        holder.txtTitle.setText(c.title);
        Glide.with(context).load(c.url_img).into(holder.ivImg);
        holder.checkBox.setChecked(c.status);


    }


    @Override
    public int getItemCount() {
        return infoList.size();
    }

    @NonNull
    @Override
    public CustomAdapterCategoriesRecycleView.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_categories, parent, false);
        return new CustomAdapterCategoriesRecycleView.MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImg;
        TextView txtTitle;
        CheckBox checkBox;

        MyViewHolder(View view) {
            super(view);
            // addressed
            ivImg = view.findViewById(R.id.imageView);
            txtTitle = view.findViewById(R.id.title);
            checkBox = view.findViewById(R.id.checkBox);
        }
    }
}



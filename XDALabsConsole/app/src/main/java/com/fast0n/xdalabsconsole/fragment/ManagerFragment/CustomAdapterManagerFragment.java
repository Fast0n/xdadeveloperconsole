package com.fast0n.xdalabsconsole.fragment.ManagerFragment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fast0n.xdalabsconsole.R;

import java.util.List;


public class CustomAdapterManagerFragment extends RecyclerView.Adapter<CustomAdapterManagerFragment.MyViewHolder> {

    private final List<DataDashboard> infoList;
    private final Context context;

    CustomAdapterManagerFragment(Context context, List<DataDashboard> infoList) {
        this.context = context;
        this.infoList = infoList;

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataDashboard c = infoList.get(position);

        holder.txtValue.setText(c.value);
        holder.txtTitle.setText(c.title);
        holder.txtColor.setText(c.color);
        holder.cardView.setCardBackgroundColor(Color.parseColor(holder.txtColor.getText().toString()));


    }


    @Override
    public int getItemCount() {
        return infoList.size();
    }

    @NonNull
    @Override
    public CustomAdapterManagerFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dashboard, parent, false);
        return new CustomAdapterManagerFragment.MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtValue, txtColor;
        CardView cardView;

        MyViewHolder(View view) {
            super(view);
            // addressed
            txtTitle = view.findViewById(R.id.title);
            txtValue = view.findViewById(R.id.value);
            txtColor = view.findViewById(R.id.color);
            cardView = view.findViewById(R.id.cardView);
        }
    }
}



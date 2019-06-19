package com.fast0n.xdalabsconsole.fragment.SettingsFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fast0n.xdalabsconsole.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;


public class CustomAdapterSettingsFragment extends RecyclerView.Adapter<CustomAdapterSettingsFragment.MyViewHolder> {

    private final List<DataSettings> infoList;
    private final Context context;

    CustomAdapterSettingsFragment(Context context, List<DataSettings> infoList) {
        this.context = context;
        this.infoList = infoList;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataSettings c = infoList.get(position);

        holder.edtValue.setText(c.value);

        holder.txtSecondTitle.setVisibility(View.GONE);
        holder.edtHint.setHint(c.second_title);

        holder.txtTitle.setText(c.title);
        holder.txtAlert.setText(c.alert);

        if (c.title != null)
            holder.txtTitle.setVisibility(View.VISIBLE);
        else
            holder.txtTitle.setVisibility(View.GONE);

        if (c.alert != null)
            holder.txtAlert.setVisibility(View.VISIBLE);
        else
            holder.txtAlert.setVisibility(View.GONE);

        holder.edtValue.setTag(c.idName);
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    @NonNull
    @Override
    public CustomAdapterSettingsFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_settings, parent, false);
        return new CustomAdapterSettingsFragment.MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtSecondTitle, txtAlert;
        EditText edtValue;
        TextInputLayout edtHint;

        MyViewHolder(View view) {
            super(view);
            // addressed
            txtTitle = view.findViewById(R.id.title);
            edtValue = view.findViewById(R.id.value);
            txtSecondTitle = view.findViewById(R.id.second_title);
            txtAlert = view.findViewById(R.id.title_developer2);
            edtHint = view.findViewById(R.id.til_email);

        }
    }
}



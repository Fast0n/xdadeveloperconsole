package com.fast0n.xdalabsconsole.fragment.SettingsFragment;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fast0n.xdalabsconsole.R;

import java.util.List;


public class CustomAdapterSettingsFragment extends RecyclerView.Adapter<CustomAdapterSettingsFragment.MyViewHolder> {

    private final List<DataSettings> infoList;
    private Context context;

    CustomAdapterSettingsFragment(Context context, List<DataSettings> infoList) {
        this.context = context;
        this.infoList = infoList;

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataSettings c = infoList.get(position);

        holder.edtValue.setText(c.value);
        holder.txtSecondTitle.setText(c.second_title);
        holder.txtTitle.setText(c.title);
        if (c.title.equals("1") || c.title.equals("3"))
            holder.txtTitle.setVisibility(View.VISIBLE);
        else
            holder.txtTitle.setVisibility(View.GONE);


        holder.edtValue.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                PreferenceManager.getDefaultSharedPreferences(context).edit()
                        .putString("edtText_" + position, c.toString()).apply();

            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });
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

        TextView txtTitle, txtSecondTitle;
        EditText edtValue;

        MyViewHolder(View view) {
            super(view);
            // addressed
            txtTitle = view.findViewById(R.id.title);
            edtValue = view.findViewById(R.id.value);
            txtSecondTitle = view.findViewById(R.id.second_title);
        }
    }
}



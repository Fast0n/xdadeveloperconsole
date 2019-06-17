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
        holder.txtAlert.setText(c.alert);

        if (c.title != null)
            holder.txtTitle.setVisibility(View.VISIBLE);
        else
            holder.txtTitle.setVisibility(View.GONE);

        if (c.alert != null)
            holder.txtAlert.setVisibility(View.VISIBLE);
        else
            holder.txtAlert.setVisibility(View.GONE);

        if (c.idName != null){
            int count = PreferenceManager.getDefaultSharedPreferences(context).getInt("edtTextCounter", 0);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("edtTextCounter", count + 1).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("edtTextId" + position, c.idName).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("edtText" + position, c.idName + "=" + c.value).apply();
        }

        holder.edtValue.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                String id = PreferenceManager.getDefaultSharedPreferences(context).getString("edtTextId" + position, null);
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("edtText" + position, id + "=" + c.toString()).apply();

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

        TextView txtTitle, txtSecondTitle, txtAlert;
        EditText edtValue;

        MyViewHolder(View view) {
            super(view);
            // addressed
            txtTitle = view.findViewById(R.id.title);
            edtValue = view.findViewById(R.id.value);
            txtSecondTitle = view.findViewById(R.id.second_title);
            txtAlert = view.findViewById(R.id.title_developer2);

        }
    }
}



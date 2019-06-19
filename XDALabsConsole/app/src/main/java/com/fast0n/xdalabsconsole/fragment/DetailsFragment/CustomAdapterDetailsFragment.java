package com.fast0n.xdalabsconsole.fragment.DetailsFragment;

import android.content.Context;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fast0n.xdalabsconsole.R;
import com.fast0n.xdalabsconsole.fragment.SettingsFragment.DataSettings;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;


public class CustomAdapterDetailsFragment extends RecyclerView.Adapter<CustomAdapterDetailsFragment.MyViewHolder> {

    private final List<DataDetails> infoList;
    private Context context;

    CustomAdapterDetailsFragment(Context context, List<DataDetails> infoList) {
        this.context = context;
        this.infoList = infoList;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataDetails c = infoList.get(position);

        if (c.type.equals("input") || c.type.equals("textarea")){
            holder.swtValue.setVisibility(View.GONE);

            holder.txtTitle.setText(c.title);

            if (c.type.equals("input")){
                holder.edtValue.getEditText().setSingleLine(true);
                holder.edtValue.getEditText().setFilters(new InputFilter[] { new InputFilter.LengthFilter(30) });
            }

            if (c.value.equals("null"))
                holder.edtValue.getEditText().setText("");
            else
                holder.edtValue.getEditText().setText(c.value);

        }
        else if (c.type.equals("checkbox")){
            holder.edtValue.setVisibility(View.GONE);
            holder.txtTitle.setText(c.title);
            if(c.value.equals("1"))
                holder.swtValue.setChecked(true);
            else
                holder.swtValue.setChecked(false);
        }

        if (c.alert != null) {
            holder.txtAlert.setText(c.alert);
        }
        else
            holder.txtAlert.setVisibility(View.GONE);

        holder.edtValue.setTag(c.id);
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    @NonNull
    @Override
    public CustomAdapterDetailsFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_details, parent, false);
        return new CustomAdapterDetailsFragment.MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtAlert;
        Switch swtValue;
        TextInputLayout edtValue;

        MyViewHolder(View view) {
            super(view);
            // addressed
            txtTitle = view.findViewById(R.id.second_title);
            edtValue = view.findViewById(R.id.til_email);
            swtValue = view.findViewById(R.id.switch2);
            txtAlert = view.findViewById(R.id.alert);

        }
    }
}



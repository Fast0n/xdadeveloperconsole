package com.fast0n.xdalabsconsole.fragment.DetailsFragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fast0n.xdalabsconsole.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class CustomAdapterDetailsFragment extends RecyclerView.Adapter<CustomAdapterDetailsFragment.MyViewHolder> {

    private final List<DataDetails> infoList;
    private Context context;
    private FloatingActionButton fab;
    private String chache;

    CustomAdapterDetailsFragment(Context context, List<DataDetails> infoList) {
        this.context = context;
        this.infoList = infoList;
        fab = ((Activity) context).findViewById(R.id.fab);
        chache = PreferenceManager.getDefaultSharedPreferences(context).getString("details", null);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataDetails c = infoList.get(position);

        if (c.type.equals("input") || c.type.equals("textarea")){

            EditText edtValue = holder.tilValue.getEditText(); // get EditText from TextInputLayout
            assert edtValue != null;

            // region gone elements
            holder.swtValue.setVisibility(View.GONE);
            // endregion

            // region input EditText
            if (c.type.equals("input")){
                edtValue.setSingleLine(true); // set single line true
                edtValue.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30) }); // set 30 char to max length
            }
            // endregion

            // region EditText value
            if (c.value.equals("null"))
                edtValue.setText("");
            else
                edtValue.setText(c.value);
            // endregion

            // region price editText
            if (!c.id.equals("price")) {
                edtValue.setCompoundDrawables(null, null, null, null); // delete drawables from EditText
            }
            else {
                edtValue.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            // endregion

            // region on text change
            edtValue.addTextChangedListener(new TextWatcher() {

                // the user's changes are saved here
                public void onTextChanged(CharSequence c, int start, int before, int count) {

                    // region cache manager
                    String jsonDetailsChanged = PreferenceManager.getDefaultSharedPreferences(context).getString("detailsChanged", null);
                    try {
                        // parsing and update JSON
                        JSONObject obj = new JSONObject(chache);
                        JSONArray array = obj.getJSONArray("app");
                        JSONObject element = array.getJSONObject(position);
                        element.put("value", edtValue.getText().toString());
                        // save JSON
                        jsonDetailsChanged = obj.toString();
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("detailsChanged", jsonDetailsChanged).commit();


                        // try
                        String prova = PreferenceManager.getDefaultSharedPreferences(context).getString("detailsChanged", null);
                        System.out.println(prova);


                    }catch (Exception ignored){}
                    // endregion

                    FloatingActionButton fab = ((Activity) context).findViewById(R.id.fab);
                    if (!chache.equals(jsonDetailsChanged)) {
                        fab.show();
                        ((Activity) context).findViewById(R.id.fab).setVisibility(View.VISIBLE);
                    }
                    else
                        fab.hide();
                }

                public void beforeTextChanged(CharSequence c, int start, int count, int after) {}

                public void afterTextChanged(Editable c) {}
            });

            // endregion

            holder.tilValue.setHint(c.title); // set TextInputLayout hint
            holder.tilValue.setHelperText(c.alert); // set TextInputLayout helper text
            holder.tilValue.setTag(c.id); // set TextInputLayout tag

        }
        else if (c.type.equals("checkbox")){

            // region gone elements
            holder.tilValue.setVisibility(View.GONE);
            // endregion

            // region Switch value
            boolean cacheValue;
            if(c.value.equals("1")){
                holder.swtValue.setChecked(true);
                cacheValue = true;
            }
            else {
                holder.swtValue.setChecked(false);
                cacheValue = false;
            }
            // endregion

            holder.swtValue.setOnCheckedChangeListener((buttonView, isChecked) -> {

                String jsonDetailsChanged = PreferenceManager.getDefaultSharedPreferences(context).getString("detailsChanged", null);

                // region cache manager
                String value = null;

                if(holder.swtValue.isChecked()){
                    value = "1";
                }
                else {
                    value = "1";
                }

                try {
                    JSONObject obj = new JSONObject(chache);
                    JSONArray array = obj.getJSONArray("app");
                    JSONObject element = array.getJSONObject(position);
                    element.put("value", value);
                    jsonDetailsChanged = obj.toString();

                    // save JSON string
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("detailsChanged", jsonDetailsChanged);
                    editor.apply();

                } catch (Exception ignored){}

                // endregion

                if (cacheValue != holder.swtValue.isChecked()) {
                    fab.show();
                    ((Activity) context).findViewById(R.id.fab).setVisibility(View.VISIBLE);
                }
                else
                    fab.hide();
            });

            holder.swtValue.setHint(c.title);
            holder.swtValue.setTag(c.id); // set tag to Switch
        }
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

        Switch swtValue;
        TextInputLayout tilValue;

        MyViewHolder(View view) {
            super(view);
            // addressed
            tilValue = view.findViewById(R.id.til_email);
            swtValue = view.findViewById(R.id.switch2);

        }
    }

}



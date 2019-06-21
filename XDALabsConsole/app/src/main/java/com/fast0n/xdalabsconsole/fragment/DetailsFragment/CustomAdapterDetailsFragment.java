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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fast0n.xdalabsconsole.R;
import com.fast0n.xdalabsconsole.fragment.CategoriesRecycleView.CustomAdapterCategoriesRecycleView;
import com.fast0n.xdalabsconsole.fragment.CategoriesRecycleView.DataCategories;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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

        if (c.type.equals("input") || c.type.equals("textarea")) {

            EditText edtValue = holder.tilValue.getEditText(); // get EditText from TextInputLayout
            assert edtValue != null;

            // region gone elements
            holder.swtValue.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.GONE);
            // endregion

            // region input EditText
            if (c.type.equals("input")) {
                edtValue.setSingleLine(true); // set single line true
                edtValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)}); // set 30 char to max length
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
            } else {
                edtValue.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            // endregion

            // region on text change
            edtValue.addTextChangedListener(new TextWatcher() {

                // the user's changes are saved here
                public void onTextChanged(CharSequence c, int start, int before, int count) {

                    // region cache manager
                    String jsonDetailsChanged;
                    try {
                        PreferenceManager.getDefaultSharedPreferences(context).edit()
                                .remove("detailsChanged").apply();
                        // parsing and update JSON
                        JSONObject obj = new JSONObject(chache);
                        JSONArray array = obj.getJSONArray("app");
                        JSONObject element = array.getJSONObject(position);
                        element.put("value", edtValue.getText().toString());
                        // save JSON
                        jsonDetailsChanged = obj.toString();

                        PreferenceManager.getDefaultSharedPreferences(context).edit()
                                .putString("detailsChanged", jsonDetailsChanged).apply();


                        // try
                        String prova = PreferenceManager.getDefaultSharedPreferences(context).getString("detailsChanged", null);
                        System.out.println(prova);


                    } catch (Exception ignored) {
                    }
                    // endregion

                    FloatingActionButton fab = ((Activity) context).findViewById(R.id.fab);
                    jsonDetailsChanged = PreferenceManager.getDefaultSharedPreferences(context).getString("detailsChanged", null);
                    if (!chache.equals(jsonDetailsChanged)) {
                        fab.show();
                        ((Activity) context).findViewById(R.id.fab).setVisibility(View.VISIBLE);
                    } else
                        fab.hide();
                }

                public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                }

                public void afterTextChanged(Editable c) {
                }
            });

            // endregion

            holder.tilValue.setHint(c.title); // set TextInputLayout hint
            holder.tilValue.setHelperText(c.alert); // set TextInputLayout helper text

            holder.tilValue.setTag(c.id); // set TextInputLayout tag

        }
        else if (c.type.equals("checkbox")) {

            // region gone elements
            holder.tilValue.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.GONE);
            // endregion

            // region Switch value
            boolean cacheValue;
            if (c.value.equals("1")) {
                holder.swtValue.setChecked(true);
                cacheValue = true;
            } else {
                holder.swtValue.setChecked(false);
                cacheValue = false;
            }
            // endregion

            holder.swtValue.setOnCheckedChangeListener((buttonView, isChecked) -> {

                String jsonDetailsChanged = PreferenceManager.getDefaultSharedPreferences(context).getString("detailsChanged", null);

                // region cache manager
                String value = null;

                if (holder.swtValue.isChecked()) {
                    value = "1";
                } else {
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
                    editor.putString("detailsChanged", "pippo");
                    editor.apply();

                } catch (Exception ignored) {
                }

                // endregion

                if (cacheValue != holder.swtValue.isChecked()) {
                    fab.show();
                    ((Activity) context).findViewById(R.id.fab).setVisibility(View.VISIBLE);
                } else
                    fab.hide();
            });

            holder.swtValue.setHint(c.title);
            holder.swtValue.setTag(c.id); // set tag to Switch
        }
        else if (c.type.equals("multiple")){

            // region gone elements
            holder.tilValue.setVisibility(View.GONE);
            holder.swtValue.setVisibility(View.GONE);
            // endregion

            // region set category recycle view
            holder.recyclerView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(context);
            llm.setOrientation(RecyclerView.VERTICAL);
            holder.recyclerView.setLayoutManager(llm);
            holder.recyclerView.setNestedScrollingEnabled(true);
            // endregion

            SharedPreferences settings = context.getSharedPreferences("sharedPreferences", 0); // init SharedPreferences
            String theme = settings.getString("toggleTheme", null);
            assert theme != null;

            final List<DataCategories> dataCategories = new ArrayList<>();

            for (int i = 0; i < c.listValue.size(); i++) {

                if (theme.equals("0"))
                    dataCategories.add(new DataCategories("http://simpleicon.com/wp-content/uploads/sun.png", c.options.get(i), false));
                else
                    dataCategories.add(new DataCategories("https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/1d7b5f7e-29d3-41c3-8ec5-ba4731b49c25/d887i3g-19cf6e30-2f75-4d87-b54d-9ea378d5f7a0.png/v1/fill/w_894,h_894,strp/skylanders_dark_icon_by_omniferious_d887i3g-pre.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MTAyNCIsInBhdGgiOiJcL2ZcLzFkN2I1ZjdlLTI5ZDMtNDFjMy04ZWM1LWJhNDczMWI0OWMyNVwvZDg4N2kzZy0xOWNmNmUzMC0yZjc1LTRkODctYjU0ZC05ZWEzNzhkNWY3YTAucG5nIiwid2lkdGgiOiI8PTEwMjQifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.GprAH8Lw_ciTYqo-686Nhj-DsHdPUwkwT7hyQYimJps", c.options.get(i), false));

            }

            for (int i = 0; i < c.options.size(); i++) {

                if (theme.equals("0"))
                    dataCategories.add(new DataCategories("http://simpleicon.com/wp-content/uploads/sun.png", c.options.get(i), false));
                else
                    dataCategories.add(new DataCategories("https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/1d7b5f7e-29d3-41c3-8ec5-ba4731b49c25/d887i3g-19cf6e30-2f75-4d87-b54d-9ea378d5f7a0.png/v1/fill/w_894,h_894,strp/skylanders_dark_icon_by_omniferious_d887i3g-pre.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MTAyNCIsInBhdGgiOiJcL2ZcLzFkN2I1ZjdlLTI5ZDMtNDFjMy04ZWM1LWJhNDczMWI0OWMyNVwvZDg4N2kzZy0xOWNmNmUzMC0yZjc1LTRkODctYjU0ZC05ZWEzNzhkNWY3YTAucG5nIiwid2lkdGgiOiI8PTEwMjQifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.GprAH8Lw_ciTYqo-686Nhj-DsHdPUwkwT7hyQYimJps", c.options.get(i), false));

            }

            // region set custom adapter
            CustomAdapterCategoriesRecycleView ca = new CustomAdapterCategoriesRecycleView(context, dataCategories);
            holder.recyclerView.setAdapter(ca);
            // endregion

            holder.recyclerView.setTag(c.id); // set tag to RecyclerView
        }
        else {
            // region gone elements
            holder.tilValue.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.GONE);
            holder.swtValue.setVisibility(View.GONE);
            // endregion
        }

    }

    @Override
    public int getItemCount() { return infoList.size(); }

    @NonNull
    @Override
    public CustomAdapterDetailsFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_details, parent, false);
        return new CustomAdapterDetailsFragment.MyViewHolder(v);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        Switch swtValue;
        TextInputLayout tilValue;
        RecyclerView recyclerView;

        MyViewHolder(View view) {
            super(view);
            // addressed
            tilValue = view.findViewById(R.id.til_email);
            swtValue = view.findViewById(R.id.switch2);
            recyclerView = view.findViewById(R.id.recycler_view_one);

        }
    }

}



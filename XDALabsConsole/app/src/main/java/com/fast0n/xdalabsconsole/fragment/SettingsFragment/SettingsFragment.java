package com.fast0n.xdalabsconsole.fragment.SettingsFragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.xdalabsconsole.BuildConfig;
import com.fast0n.xdalabsconsole.R;
import com.fast0n.xdalabsconsole.java.SnackbarHelper;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    final List<DataSettings> dataSettings = new ArrayList<>();
    String theme;
    TextView info, title_developer2;
    RecyclerView recyclerView;
    CustomAdapterSettingsFragment ca;
    private Context context;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private String domain;
    private Snackbar snack;
    // declare objects
    private EditText edt_name, edt_email, edt_bitcoin, edt_paypal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);


        recyclerView = view.findViewById(R.id.recycler_view);

        // initial recycle view
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(llm);


        context = getActivity().getApplicationContext();
        settings = context.getSharedPreferences("sharedPreferences", 0);
        editor = settings.edit();


        domain = getResources().getString(R.string.url);

        String sessionid = settings.getString("sessionid", null);

        info = view.findViewById(R.id.info);
        title_developer2 = view.findViewById(R.id.title_developer2);

        Button btn_save = view.findViewById(R.id.btn_save);
        Switch aSwitch = view.findViewById(R.id.switch1);

        theme = settings.getString("toggleTheme", null);
        if (theme.equals("0"))
            aSwitch.setChecked(false);
        else
            aSwitch.setChecked(true);


        Handler handler = new Handler();
        final Runnable runnable = () -> {
            getActivity().finish();
            getActivity().overridePendingTransition(0, 0);
            startActivity(getActivity().getIntent());
            getActivity().overridePendingTransition(0, 0);

        };

        aSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                editor.putString("toggleTheme", "1");
                editor.apply();
                handler.postDelayed(runnable, 500);
            } else {
                editor.putString("toggleTheme", "0");
                editor.apply();
                handler.postDelayed(runnable, 500);

            }
        });


        String checkSessionUrl = String.format("%s/settings?sessionid=%s", domain, sessionid);


        try {
            if (isOnline())
                handler.postDelayed(() -> {
                    dataSettings.clear();
                    settings(view, checkSessionUrl, 0);
                }, 1000);
            else
                settings(view, checkSessionUrl, 1);

        } catch (Exception e) {
            settings(view, checkSessionUrl, 0);
        }

        btn_save.setOnClickListener(view1 -> {

            String saveUrl = String.format("%s/change_settings?sessionid=%s&", domain, sessionid);

            int n = PreferenceManager.getDefaultSharedPreferences(context).getInt("edtTextCounter", 0);

            for(int i = 0; i < n; i++){
                String value = PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("edtText" + i, null);
                saveUrl += value;
                if (i != n - 1) saveUrl += "&";
            }
            saveUrl += String.format("&csrfmiddlewaretoken=%s", settings.getString("token", null));

            dataSettings.clear();
            settings(view, saveUrl, 0);
        });

        info.setOnClickListener(view1 -> {

            snack = Snackbar.make(
                    view1,
                    getString(R.string.version) + BuildConfig.VERSION_NAME + "\n(" + BuildConfig.VERSION_CODE + ")",
                    Snackbar.LENGTH_LONG
            );
            SnackbarHelper.configSnackbar(view1.getContext(), snack);
            snack.setAction(getString(R.string.copy), v -> {

                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copied", BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
                clipboard.setPrimaryClip(clip);

            });
            snack.show();


        });


        return view;
    }


    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null ? cm.getActiveNetworkInfo() : null) != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void settings(View view, String url, int i) {

        if (i == 1) {
            try {

                String jsonSettings = PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("settings", null);

                JSONObject response = new JSONObject(jsonSettings);

                JSONArray array = response.getJSONArray("settings");

                int n = array.length();

                // set recycle view
                String title = null;
                String alert = null;
                String id = null;

                // set recycle view
                for (int j = 0; j < n; j++) {
                    JSONObject element= array.getJSONObject(j);

                    String name = element.getString("name");
                    String value = element.getString("value");
                    if (name.equals("csrfmiddlewaretoken")){
                        editor.putString("token", value);
                        editor.apply();
                    }
                    else{

                        if (element.has("alert")){
                            alert = element.getString("alert");
                        }
                        if (element.has("id_name")){
                            id = element.getString("id_name");
                        }
                        if (element.getString("tag").equals("h2")){
                            title = element.getString("value");
                        }
                        else{
                            dataSettings.add(new DataSettings(id, title, name, value, alert));
                            title = null;
                            alert = null;
                            id = null;
                        }

                    }
                }
                ca = new CustomAdapterSettingsFragment(getContext(), dataSettings);
                recyclerView.setAdapter(ca);


            } catch (JSONException e) {
                snack = Snackbar.make(view, "Data error", Snackbar.LENGTH_LONG);
                SnackbarHelper.configSnackbar(view.getContext(), snack);
                snack.show();
            }


        }
        else {

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.getCache().clear();
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {

                        try {

                            String jsonSettings = PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("settings", null);

                            if (jsonSettings != null) {

                                // get local json clone
                                String localJson = jsonSettings;
                                JSONObject obj = new JSONObject(localJson);

                                // get response json clone
                                String responseJson = response.toString();
                                JSONObject obj2 = new JSONObject(responseJson);

                                // remove token from objects to compare
                                obj.remove("csrfmiddlewaretoken");
                                obj2.remove("csrfmiddlewaretoken");

                                localJson = obj.toString();
                                responseJson = obj2.toString();

                                if (jsonSettings == null || !localJson.equals(responseJson)) {

                                    PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit()
                                            .remove("settings").apply();


                                    PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit()
                                            .putString("settings", response.toString()).apply();

                                    JSONArray array = response.getJSONArray("settings");

                                    int n = array.length();

                                    // set recycle view
                                    String title = null;
                                    String alert = null;
                                    String id = null;

                                    for (int j = 0; j < n; j++) {
                                        JSONObject element= array.getJSONObject(j);

                                        String name = element.getString("name");
                                        String value = element.getString("value");
                                        if (name.equals("csrfmiddlewaretoken")){
                                            editor.putString("token", value);
                                            editor.apply();
                                        }
                                        else{

                                            if (element.has("alert")){
                                                alert = element.getString("alert");
                                            }
                                            if (element.has("id_name")){
                                                id = element.getString("id_name");
                                            }
                                            if (element.getString("tag").equals("h2")){
                                                title = element.getString("value");
                                            }
                                            else{
                                                dataSettings.add(new DataSettings(id, title, name, value, alert));
                                                title = null;
                                                alert = null;
                                                id = null;
                                            }

                                        }
                                    }

                                    PreferenceManager.getDefaultSharedPreferences(context).edit()
                                            .remove("edtTextCounter").apply();

                                    ca = new CustomAdapterSettingsFragment(context, dataSettings);
                                    recyclerView.setAdapter(ca);

                                    // show success message
                                    snack = Snackbar.make(view, getString(R.string.alert1), Snackbar.LENGTH_LONG);
                                    SnackbarHelper.configSnackbar(view.getContext(), snack);
                                    snack.show();

                                }
                            }


                        } catch (JSONException e) {
                            snack = Snackbar.make(view, "API error", Snackbar.LENGTH_LONG);
                            SnackbarHelper.configSnackbar(view.getContext(), snack);
                            snack.show();
                        }

                    },
                    e -> {
                        snack = Snackbar.make(view, "API error", Snackbar.LENGTH_LONG);
                        SnackbarHelper.configSnackbar(view.getContext(), snack);
                        snack.show();

                    });


            queue.add(getRequest);

        }

    }


}

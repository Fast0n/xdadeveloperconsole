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
import android.widget.LinearLayout;
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
import com.google.android.material.textfield.TextInputLayout;

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
    private LinearLayoutManager llm;
    private RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // region declare init
        context = getActivity().getApplicationContext();

        recyclerView = view.findViewById(R.id.recycler_view);

        // initial recycle view
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(context);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(llm);

        settings = context.getSharedPreferences("sharedPreferences", 0);
        editor = settings.edit();

        domain = getResources().getString(R.string.url);

        String sessionid = settings.getString("sessionid", null);

        info = view.findViewById(R.id.info);

        Button btn_save = view.findViewById(R.id.btn_save);
        Switch aSwitch = view.findViewById(R.id.switch1);

        // endregion

        // region set theme
        theme = settings.getString("toggleTheme", null);
        if (theme.equals("0"))
            aSwitch.setChecked(false);
        else
            aSwitch.setChecked(true);
        // endregion

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

        // region init requests
        settings(view, checkSessionUrl, 1);
        if (isOnline())
            settings(view, checkSessionUrl, 0);
        // endregion

        btn_save.setOnClickListener(view1 -> {

            String saveUrl = String.format("%s/change_settings?sessionid=%s&", domain, sessionid);

            rv = view.findViewById(R.id.recycler_view);

            for (int i = 0; i < rv.getChildCount(); i++) {
                LinearLayout ly = (LinearLayout) rv.getChildAt(i);

                String id = ((TextInputLayout) ly.getChildAt(1)).getEditText().getTag().toString();
                String value = ((TextInputLayout) ly.getChildAt(1)).getEditText().getText().toString();
                saveUrl += String.format("%s=%s", id, value);

                if (i != rv.getChildCount() - 1) saveUrl += "&";
            }

            saveUrl += String.format("&csrfmiddlewaretoken=%s", settings.getString("token", null));

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

                if (jsonSettings != null) {

                    JSONObject response = new JSONObject(jsonSettings);

                    JSONArray array = response.getJSONArray("settings");

                    int n = array.length();

                    // set recycle view
                    String title = null;
                    String alert = null;
                    String id = null;

                    // set recycle view
                    for (int j = 0; j < n; j++) {
                        JSONObject element = array.getJSONObject(j);

                        String name = element.getString("name");
                        String value = element.getString("value");
                        if (name.equals("csrfmiddlewaretoken")) {
                            editor.putString("token", value);
                            editor.apply();
                        } else {

                            if (element.has("alert")) {
                                alert = element.getString("alert");
                            }
                            if (element.has("id_name")) {
                                id = element.getString("id_name");
                            }
                            if (element.getString("tag").equals("h2")) {
                                title = element.getString("value");
                            } else {
                                dataSettings.add(new DataSettings(id, title, name, value, alert));
                                title = null;
                                alert = null;
                                id = null;
                            }

                        }
                    }

                    ca = new CustomAdapterSettingsFragment(getContext(), dataSettings);
                    recyclerView.setAdapter(ca);
                }


            } catch (JSONException e) {
                snack = Snackbar.make(view, "Data error", Snackbar.LENGTH_LONG);
                SnackbarHelper.configSnackbar(view.getContext(), snack);
                snack.show();
            }


        } else {

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.getCache().clear();
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {

                        try {

                            String jsonSettings = PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("settings", null);

                            String localJson = "";
                            String responseJson = "";

                            if (jsonSettings != null) {

                                // get local json clone
                                localJson = jsonSettings;
                                JSONArray obj = new JSONObject(localJson).getJSONArray("settings");

                                // get response json clone
                                responseJson = response.toString();
                                JSONArray obj2 = new JSONObject(responseJson).getJSONArray("settings");

                                // remove token from objects to compare
                                obj.remove(obj.length() - 1);
                                obj2.remove(obj2.length() - 1);

                                localJson = obj.toString();
                                responseJson = obj2.toString();


                            }

                            if (jsonSettings == null || !localJson.equals(responseJson)) {

                                dataSettings.clear();

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
                                    JSONObject element = array.getJSONObject(j);

                                    String name = element.getString("name");
                                    String value = element.getString("value");
                                    if (name.equals("csrfmiddlewaretoken")) {
                                        editor.putString("token", value);
                                        editor.apply();
                                    } else {

                                        if (element.has("alert")) {
                                            alert = element.getString("alert");
                                        }
                                        if (element.has("id_name")) {
                                            id = element.getString("id_name");
                                        }
                                        if (element.getString("tag").equals("h2")) {
                                            title = element.getString("value");
                                        } else {
                                            dataSettings.add(new DataSettings(id, title, name, value, alert));
                                            title = null;
                                            alert = null;
                                            id = null;
                                        }

                                    }
                                }

                                ca = new CustomAdapterSettingsFragment(context, dataSettings);
                                recyclerView.setAdapter(ca);

                                // show success message
                                snack = Snackbar.make(view, getString(R.string.alert1), Snackbar.LENGTH_LONG);
                                SnackbarHelper.configSnackbar(view.getContext(), snack);
                                snack.show();

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

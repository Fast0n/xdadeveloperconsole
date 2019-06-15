package com.fast0n.xdalabsconsole.fragment.AppsFragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.xdalabsconsole.R;
import com.fast0n.xdalabsconsole.java.SnackbarHelper;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppsFragment extends Fragment {

    Context context;

    CustomAdapterAppsFragment adapter;
    SharedPreferences settings;
    String domain;
    String sessionid;
    ListView listView;
    TextView title;
    Snackbar snack;
    Handler handler = new Handler();
    ArrayList<DataApps> dataApps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);

        context = getActivity().getApplicationContext();
        domain = getResources().getString(R.string.url);
        settings = context.getSharedPreferences("sharedPreferences", 0);

        title = view.findViewById(R.id.title);

        listView = view.findViewById(R.id.list);
        dataApps = new ArrayList<>();

        sessionid = settings.getString("sessionid", null);

        String url = String.format("%s/apps?sessionid=%s", domain, sessionid);


        try {

            getApp(view, url, 1);

            if (isOnline())
                handler.postDelayed(() -> {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    getApp(view, url, 0);
                }, 1000);


        } catch (Exception e) {
            getApp(view, url, 0);
        }

        return view;
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null ? cm.getActiveNetworkInfo() : null) != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void getApp(View view, String url, int i) {

        if (i == 1) {
            try {
                String jsonApps = PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("apps", null);

                JSONObject response = new JSONObject(jsonApps);

                JSONArray array = response.getJSONArray("apps");

                title.setText(response.getString("title"));

                int n = array.length();

                for (int j = 0; j < n; j++) {
                    String name = array.getJSONObject(j).getString("name");
                    String value = array.getJSONObject(j).getString("img");
                    String color = array.getJSONObject(j).getString("color");
                    String id = array.getJSONObject(j).getString("id");
                    dataApps.add(new DataApps(name, value, color, id));
                }

                adapter = new CustomAdapterAppsFragment(view.getContext(), dataApps);

                listView.setAdapter(adapter);

            } catch (JSONException e) {

                snack = Snackbar.make(
                        view,
                        "API error",
                        Snackbar.LENGTH_LONG
                );
                SnackbarHelper.configSnackbar(view.getContext(), snack);
                snack.show();
            }
        } else {
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.getCache().clear();
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {

                            PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit()
                                    .remove("apps").apply();


                            PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit()
                                    .putString("apps", response.toString()).apply();


                            JSONArray array = response.getJSONArray("apps");

                            title.setText(response.getString("title"));

                            int n = array.length();

                            for (int j = 0; j < n; j++) {
                                String name = array.getJSONObject(j).getString("name");
                                String img = array.getJSONObject(j).getString("img");
                                String color = array.getJSONObject(j).getString("color");
                                String id = array.getJSONObject(j).getString("id");

                                dataApps.add(new DataApps(name, img, color, id));
                            }

                            adapter = new CustomAdapterAppsFragment(view.getContext(), dataApps);

                            listView.setAdapter(adapter); // Do not work

                        } catch (JSONException e) {

                            snack = Snackbar.make(
                                    view,
                                    "API error",
                                    Snackbar.LENGTH_LONG
                            );
                            SnackbarHelper.configSnackbar(view.getContext(), snack);
                            snack.show();
                        }

                    }, e -> {

                snack = Snackbar.make(
                        view,
                        "API error",
                        Snackbar.LENGTH_LONG
                );
                SnackbarHelper.configSnackbar(view.getContext(), snack);
                snack.show();

            });

            getRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) {

                }
            });

            queue.add(getRequest);

        }

    }
}

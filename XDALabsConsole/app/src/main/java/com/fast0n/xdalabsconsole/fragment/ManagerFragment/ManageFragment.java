package com.fast0n.xdalabsconsole.fragment.ManagerFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.xdalabsconsole.HomeActivity;
import com.fast0n.xdalabsconsole.LoginActivity;
import com.fast0n.xdalabsconsole.R;
import com.fast0n.xdalabsconsole.java.SnackbarHelper;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ManageFragment extends Fragment {

    Context context;

    CustomAdapterManagerFragment adapter;
    SharedPreferences settings;
    String domain;
    String sessionid;
    ListView listView;

    Snackbar snack;

    ArrayList<DataItems> dataHours;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        context = getActivity().getApplicationContext();
        domain = getResources().getString(R.string.url);
        settings = context.getSharedPreferences("sharedPreferences", 0);

        listView = view.findViewById(R.id.list);
        dataHours = new ArrayList<>();

        sessionid = settings.getString("sessionid", null);

        String url = String.format("%s/dashboard?sessionid=%s", domain, sessionid);

        getDashbord(view, url);

        return view;
    }

    private void getDashbord(View view, String url) {

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.getCache().clear();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray array = response.getJSONArray("dashboard");

                        int n = array.length();

                        for (int i = 0; i < n; i++)
                        {
                            String name = array.getJSONObject(i).getString("name");
                            String value = array.getJSONObject(i).getString("value");
                            dataHours.add(new DataItems(name, value, ""));
                        }

                        adapter = new CustomAdapterManagerFragment(view.getContext(), dataHours);
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

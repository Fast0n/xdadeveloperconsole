package com.fast0n.xdalabsconsole.fragment.DetailsFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.xdalabsconsole.R;
import com.fast0n.xdalabsconsole.fragment.SettingsFragment.CustomAdapterSettingsFragment;
import com.fast0n.xdalabsconsole.fragment.SettingsFragment.DataSettings;
import com.fast0n.xdalabsconsole.java.SnackbarHelper;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends Fragment {

    private Snackbar snack;
    private Context context;

    final List<DataDetails> dataDetails = new ArrayList<>();
    RecyclerView recyclerView;
    CustomAdapterDetailsFragment ca;
    private LinearLayoutManager llm;

    private String domain;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        domain = getResources().getString(R.string.url);

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

        String id = getArguments().getString("idApp");

        String sessionid = settings.getString("sessionid", null);
        String url =String.format("%s/get_app?sessionid=%s&id=%s", domain, sessionid, id);
        getApp(view, url);

        return view;
    }

    private void getApp(View view, String url){

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.getCache().clear();
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {

                        try {



                            JSONArray array = response.getJSONArray("app");

                            int n = array.length();

                            for (int i = 0; i < n; i++){
                                JSONObject element = array.getJSONObject(i);
                                String id = element.getString("id");
                                String title = element.getString("title");
                                String type = element.getString("type");
                                String value = element.getString("value");
                                String alert = null;
                                if (element.has("alert")){
                                    alert = element.getString("alert");
                                }

                                dataDetails.add(new DataDetails(id, title, type, value, alert));
                            }

                            ca = new CustomAdapterDetailsFragment(getContext(), dataDetails);
                            recyclerView.setAdapter(ca);

                            // show success message
                            snack = Snackbar.make(view, getString(R.string.alert1), Snackbar.LENGTH_LONG);
                            SnackbarHelper.configSnackbar(view.getContext(), snack);
                            snack.show();


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


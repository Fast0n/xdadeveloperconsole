package com.fast0n.xdalabsconsole.fragment.DetailsFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.fast0n.xdalabsconsole.java.SnackbarHelper;
import com.google.android.material.snackbar.Snackbar;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailsFragment extends Fragment {

    // context
    private Context context;

    // recycleView
    private final List<DataDetails> dataDetails = new ArrayList<>();
    private RecyclerView recyclerView;
    private CustomAdapterDetailsFragment ca;

    // animation
    private RotateLoading rotateloading;

    // snack
    private Snackbar snack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        String domain = getResources().getString(R.string.url); // get server domain

        context = Objects.requireNonNull(getActivity()).getApplicationContext(); // get context

        SharedPreferences settings = context.getSharedPreferences("sharedPreferences", 0); // init SharedPreferences

        // region set recycle view
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(llm);
        // endregion

        // region get arguments
        assert getArguments() != null;
        String id = getArguments().getString("idApp");
        // endregion

        String sessionid = settings.getString("sessionid", null); // get sessionid
        String url = String.format("%s/get_app?sessionid=%s&id=%s", domain, sessionid, id); // generate request url

        getApp(view, url);

        return view;
    }


    private void getApp(View view, String url){

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.getCache().clear();
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

                try {

                    // region PreferenceManager
                    PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit().remove("details").apply();
                    PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit().putString("details", response.toString()).apply();
                    PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit().remove("detailsChanged").apply();
                    PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit().putString("detailsChanged", response.toString()).apply();
                    // endregion

                    // region parse JSON
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
                    // endregion

                    // region stop animation
                    rotateloading = Objects.requireNonNull(getActivity()).findViewById(R.id.rotateloading);
                    rotateloading.stop();
                    // endregion

                    // region set adapter
                    ca = new CustomAdapterDetailsFragment(getContext(), dataDetails);
                    recyclerView.setAdapter(ca);
                    // endregion

                    // region show success message
                    snack = Snackbar.make(view, getString(R.string.alert1), Snackbar.LENGTH_SHORT);
                    SnackbarHelper.configSnackbar(view.getContext(), snack);
                    snack.show();
                    // endregion


                } catch (JSONException e) {
                    // region show error message
                    snack = Snackbar.make(view, "Parsing error", Snackbar.LENGTH_LONG);
                    SnackbarHelper.configSnackbar(view.getContext(), snack);
                    snack.show();
                    // endregion
                }

            },
            e -> {
                // region show error message
                snack = Snackbar.make(view, "API error", Snackbar.LENGTH_LONG);
                SnackbarHelper.configSnackbar(view.getContext(), snack);
                snack.show();
                // endregion
            });

            queue.add(getRequest);

    }

}


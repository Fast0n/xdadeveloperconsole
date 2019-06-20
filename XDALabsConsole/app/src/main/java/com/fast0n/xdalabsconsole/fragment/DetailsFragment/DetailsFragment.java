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
import com.fast0n.xdalabsconsole.fragment.CategoriesRecycleView.CustomAdapterCategoriesRecycleView;
import com.fast0n.xdalabsconsole.fragment.CategoriesRecycleView.DataCategories;
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

    // recycleView
    private final List<DataDetails> dataDetails = new ArrayList<>();
    private final List<DataCategories> dataCategories = new ArrayList<>();
    // context
    private Context context;
    private RecyclerView recyclerView, recyclerViewOne;
    private CustomAdapterDetailsFragment ca;
    private CustomAdapterCategoriesRecycleView ca1;

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

        // region set recycle view
        recyclerViewOne = view.findViewById(R.id.recycler_view_one);
        recyclerViewOne.setHasFixedSize(true);
        LinearLayoutManager llm2 = new LinearLayoutManager(context);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerViewOne.setLayoutManager(llm2);
        // endregion

        // region get arguments
        assert getArguments() != null;
        String id = getArguments().getString("idApp");
        // endregion

        String sessionid = settings.getString("sessionid", null); // get sessionid
        String url = String.format("%s/get_app?sessionid=%s&id=%s", domain, sessionid, id); // generate request url

        getApp(view, url);


        for (int j = 1; j < 500; j++) {

            String theme = settings.getString("toggleTheme", null);

            assert theme != null;
            if (theme.equals("0"))
                dataCategories.add(new DataCategories("http://simpleicon.com/wp-content/uploads/sun.png", j + "pippo", true));

            else
                dataCategories.add(new DataCategories("https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/1d7b5f7e-29d3-41c3-8ec5-ba4731b49c25/d887i3g-19cf6e30-2f75-4d87-b54d-9ea378d5f7a0.png/v1/fill/w_894,h_894,strp/skylanders_dark_icon_by_omniferious_d887i3g-pre.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MTAyNCIsInBhdGgiOiJcL2ZcLzFkN2I1ZjdlLTI5ZDMtNDFjMy04ZWM1LWJhNDczMWI0OWMyNVwvZDg4N2kzZy0xOWNmNmUzMC0yZjc1LTRkODctYjU0ZC05ZWEzNzhkNWY3YTAucG5nIiwid2lkdGgiOiI8PTEwMjQifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.GprAH8Lw_ciTYqo-686Nhj-DsHdPUwkwT7hyQYimJps", j + "pippo", true));


        }
        ca1 = new CustomAdapterCategoriesRecycleView(context, dataCategories);
        recyclerViewOne.setAdapter(ca1);


        return view;
    }


    private void getApp(View view, String url) {

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

                for (int i = 0; i < n; i++) {
                    JSONObject element = array.getJSONObject(i);
                    String id = element.getString("id");
                    String title = element.getString("title");
                    String type = element.getString("type");
                    String value = element.getString("value");

                    String alert = null;
                    if (element.has("alert")) {
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


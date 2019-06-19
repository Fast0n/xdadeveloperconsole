package com.fast0n.xdalabsconsole.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.xdalabsconsole.R;
import com.fast0n.xdalabsconsole.java.SnackbarHelper;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;

public class DetailsFragment extends Fragment {

    private Snackbar snack;
    private Context context;

    private EditText edt1, edt2, edt3;
    private Switch swc;

    private String domain;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        domain = getResources().getString(R.string.url);

        context = getActivity().getApplicationContext();

        settings = context.getSharedPreferences("sharedPreferences", 0);
        editor = settings.edit();

        // declare objects
        edt1 = view.findViewById(R.id.edt_title);
        edt2 = view.findViewById(R.id.edt_desc);
        edt3 = view.findViewById(R.id.edt_price);
        swc = view.findViewById(R.id.switch1);

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

                            /*

                            JSONArray array = response.getJSONArray("app");

                            int n = array.length();

                            for (int i = 0; i < n; i++){
                                JSONObject element = array.getJSONObject(0);
                                String type = element.getString("type");
                            }

                            */

                            JSONArray array = response.getJSONArray("app");
                            String str1 = array.getJSONObject(0).getString("value");
                            String str2 = array.getJSONObject(1).getString("value");
                            String str3 = array.getJSONObject(2).getString("value");
                            boolean ckd1 = array.getJSONObject(3).getBoolean("value");

                            edt1.setText(str1);
                            edt2.setText(str2);
                            edt3.setText(str3);
                            swc.setChecked(ckd1);

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

    public void changeApp(View view){
        edt1 = view.findViewById(R.id.edt_title);
        System.out.println("SUCA");
    }


}


package com.fast0n.xdalabsconsole.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fast0n.xdalabsconsole.BuildConfig;
import com.fast0n.xdalabsconsole.R;
import com.fast0n.xdalabsconsole.java.SnackbarHelper;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsFragment extends Fragment {

    private Context context;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    private String domain;

    // declare objects
    private EditText edt_name;
    private EditText edt_email;
    private EditText edt_bitcoin;
    private EditText edt_paypal;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        context = getActivity().getApplicationContext();
        settings = context.getSharedPreferences("sharedPreferences", 0);
        editor = settings.edit();

        domain = getResources().getString(R.string.url);

        String sessionid = settings.getString("sessionid", null);

        TextView info = view.findViewById(R.id.info);
        edt_name = view.findViewById(R.id.edt_name);
        edt_email = view.findViewById(R.id.edt_email);
        edt_bitcoin = view.findViewById(R.id.edt_bitcoin);
        edt_paypal = view.findViewById(R.id.edt_paypal);
        Button btn_save = view.findViewById(R.id.btn_save);

        String checkSessionUrl = String.format("%s/settings?sessionid=%s", domain, sessionid);




        try {
            if (isOnline()) {
                settings(view, checkSessionUrl, 0);
                settings(view, checkSessionUrl, 1);
            } else
                settings(view, checkSessionUrl, 1);
        } catch (Exception e) {
            settings(view, checkSessionUrl, 0);
        }





        btn_save.setOnClickListener(view1 -> {

            String name = edt_name.getText().toString();
            String email = edt_email.getText().toString();
            String bitcoin = edt_bitcoin.getText().toString();
            String paypal = edt_paypal.getText().toString();
            String token = settings.getString("token", null);

            String saveUrl = String.format(
                    "%s/change_settings?sessionid=%s&display_name=%s&support_email=%s&bitcoin_address=%s&paypal_address=%s&csrfmiddlewaretoken=%s",
                    domain, sessionid, name, email, bitcoin, paypal, token
            );

            settings(view, saveUrl, 1);

        });


        info.setOnClickListener(view1 -> {


            Snackbar snack = Snackbar.make(
                    view1,
                    "Version XDA Labs Console: " + BuildConfig.VERSION_NAME + "\n(" + BuildConfig.VERSION_CODE + ")",
                    Snackbar.LENGTH_LONG
            );
            SnackbarHelper.configSnackbar(view1.getContext(), snack);
            snack.setAction("Copia", v -> {

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


    private void settings(View view, String url, int i){









        if (i == 1) {





            try {

                String jsonCredit = PreferenceManager.
                        getDefaultSharedPreferences(view.getContext()).getString("settings", null);

                JSONObject response = new JSONObject(jsonCredit);


                // get json values
                JSONObject name = response.getJSONObject("display_name");
                JSONObject email = response.getJSONObject("support_email");
                JSONObject bitcoin = response.getJSONObject("bitcoin_address");
                JSONObject paypal = response.getJSONObject("paypal_address");
                JSONObject token = response.getJSONObject("csrfmiddlewaretoken");

                // insert json values
                edt_name.setText(name.getString("value"));
                edt_email.setText(email.getString("value"));
                edt_bitcoin.setText(bitcoin.getString("value"));
                edt_paypal.setText(paypal.getString("value"));
                editor.putString("token", token.getString("value"));
                editor.apply();


            } catch (JSONException e) {
                Snackbar snack = Snackbar.make(
                        view,
                        "API error",
                        Snackbar.LENGTH_LONG
                );
                SnackbarHelper.configSnackbar(view.getContext(), snack);
                snack.show();
            }


        }
        else {


            RequestQueue queue = Volley.newRequestQueue(context);
            queue.getCache().clear();
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {


                        PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit()
                                .remove("settings").apply();


                        PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit()
                                .putString("settings", response.toString()).apply();


                        try {

                            // get json values
                            JSONObject name = response.getJSONObject("display_name");
                            JSONObject email = response.getJSONObject("support_email");
                            JSONObject bitcoin = response.getJSONObject("bitcoin_address");
                            JSONObject paypal = response.getJSONObject("paypal_address");
                            JSONObject token = response.getJSONObject("csrfmiddlewaretoken");

                            // insert json values
                            edt_name.setText(name.getString("value"));
                            edt_email.setText(email.getString("value"));
                            edt_bitcoin.setText(bitcoin.getString("value"));
                            edt_paypal.setText(paypal.getString("value"));
                            editor.putString("token", token.getString("value"));
                            editor.apply();

                            // show success message
                            Snackbar snack = Snackbar.make(view, "Impostazioni aggiornate", Snackbar.LENGTH_LONG);
                            SnackbarHelper.configSnackbar(view.getContext(), snack);
                            snack.show();


                        } catch (JSONException e) {
                            Snackbar snack = Snackbar.make(
                                    view,
                                    "API error",
                                    Snackbar.LENGTH_LONG
                            );
                            SnackbarHelper.configSnackbar(view.getContext(), snack);
                            snack.show();
                        }

                    }, e -> {

                Snackbar snack = Snackbar.make(
                        view,
                        "API error",
                        Snackbar.LENGTH_LONG
                );
                SnackbarHelper.configSnackbar(view.getContext(), snack);
                snack.show();

            });


            queue.add(getRequest);

        }

    }
}

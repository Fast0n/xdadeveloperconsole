package com.fast0n.xdalabsconsole;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.fast0n.xdalabsconsole.Classes.B64;
import com.fast0n.xdalabsconsole.Classes.RSA;

import com.google.android.material.snackbar.Snackbar;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    Context context = LoginActivity.this;

    B64 B64 = new B64();

    String domain;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View view = findViewById(android.R.id.content);

        settings = getSharedPreferences("sharedPreferences", 0);
        editor = settings.edit();
        domain = getResources().getString(R.string.url);



        // get theme
        String theme = settings.getString("toggleTheme", null);

        // check theme
        if (theme == null) {
            editor.putString("toggleTheme", "0");
            editor.apply();
        }
        else if (theme.equals("0"))
            setTheme(R.style.AppTheme);
        else
            setTheme(R.style.DarkTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // declare objects
        RotateLoading rotateloading = findViewById(R.id.rotateloading);
        CardView loginCard = findViewById(R.id.cardView);
        TextView forgotPassword = findViewById(R.id.textView2);
        EditText edt_username = findViewById(R.id.edt_id);
        EditText edt_password = findViewById(R.id.edt_password);
        Button btn_login = findViewById(R.id.btn_login);

        // check session id
        String sessionId = settings.getString("sessionid", null);
        if (sessionId != null){
            // get invisible element for animation
            loginCard.setVisibility(View.INVISIBLE);
            forgotPassword.setVisibility(View.INVISIBLE);
            btn_login.setVisibility(View.INVISIBLE);

            // start animation
            rotateloading.start();

            String url = String.format("%s/check?sessionid=%s", domain, sessionId);
            checkSessionId(view, url);
        }

        btn_login.setOnClickListener(v -> {

            String username = edt_username.getText().toString();
            String password = edt_password.getText().toString();

            if (!username.isEmpty() && !password.isEmpty()) {

                // get invisible element for animation
                loginCard.setVisibility(View.INVISIBLE);
                forgotPassword.setVisibility(View.INVISIBLE);
                btn_login.setVisibility(View.INVISIBLE);

                // TODO: get keyboard down :)

                // start animation
                rotateloading.start();

                getKey(v, username, password);
            } else {

                String message = "Compila tutti i campi";
                Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
                SnackbarHelper.configSnackbar(v.getContext(), snack);
                snack.show();
            }

        });

    }

    private void login(View view, String url){

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.getCache().clear();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        //check "sessionid" exist
                        if (response.has("sessionid")) {

                            editor.putString("sessionid", response.getString("sessionid"));
                            editor.putString("bbuserid", response.getString("bbuserid"));
                            editor.putString("bbpassword", response.getString("bbpassword"));
                            editor.apply();
                            // activity change
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        } else{
                            // get error message
                            JSONObject error = response.getJSONObject("error");
                            String message = error.getString("message");
                            // show error message
                            Snackbar snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
                            SnackbarHelper.configSnackbar(view.getContext(), snack);
                            snack.show();
                        }

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

    private void checkSessionId(View view, String url){

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.getCache().clear();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // check key "success" exist
                        boolean result = response.getBoolean("success");
                        if (result){

                            // activity change
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                        } else {

                            // session id update
                            String userid = settings.getString("bbuserid", null);
                            String password = settings.getString("bbpassword", null);

                            String update_url = String.format("%s/login?bbuserid=%s&bbpassword=%s", domain, userid, password);
                            updateSessionId(view, update_url);
                        }



                    } catch (JSONException e) {
                        Snackbar snack = Snackbar.make(view,"API error", Snackbar.LENGTH_LONG);
                        SnackbarHelper.configSnackbar(view.getContext(), snack);
                        snack.show();
                    }


                },
                e -> {
                    Snackbar snack = Snackbar.make(view,"API error", Snackbar.LENGTH_LONG);
                    SnackbarHelper.configSnackbar(view.getContext(), snack);
                    snack.show();

                });

        queue.add(getRequest);


    }

    private void updateSessionId(View view, String url){

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.getCache().clear();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        // salvataggio della sessionid nelle SharedPreferences
                        editor.putString("sessionid", response.getString("sessionid"));
                        editor.apply();

                        // activity change
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));



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

        getRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() { return 50000; }

            @Override
            public int getCurrentRetryCount() { return 50000; }

            @Override
            public void retry(VolleyError error) {}
        });

        queue.add(getRequest);

    }

    private void getKey(View view, String username, final String password) {

        String url = domain +  "/get_key";

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.getCache().clear();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        String key = response.getString("key");

                        // decode key from base 64
                        byte[] binaryKey = B64.decodeString(key);

                        // encrypt password with key
                        RSA Crypt = new RSA();
                        Crypt.importPublicKey(binaryKey);
                        byte[] encrypt = Crypt.encrypt(password);

                        // encode key in base 64
                        String base64Password = new String(B64.encodeBinary(encrypt));

                        String urlLogin = String.format("%s/login?username=%s&password=%s", domain, username, base64Password);

                        login(view, urlLogin);


                    } catch (Exception e) {

                        Snackbar snack = Snackbar.make(view,"API error", Snackbar.LENGTH_LONG);
                        SnackbarHelper.configSnackbar(view.getContext(), snack);
                        snack.show();
                    }

                },
                e -> {
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

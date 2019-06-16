package com.fast0n.xdalabsconsole;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.fast0n.xdalabsconsole.classes.B64;
import com.fast0n.xdalabsconsole.classes.RSA;
import com.fast0n.xdalabsconsole.java.SnackbarHelper;
import com.google.android.material.snackbar.Snackbar;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    Context context = LoginActivity.this;

    B64 B64 = new B64();
    String domain;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Snackbar snack;
    RotateLoading rotateLoading;
    CardView loginCard;
    Button btn_login;
    TextView forgotPassword;
    EditText edt_username;
    EditText edt_password;

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
            editor.putString("toggleTheme", "1");
            editor.apply();
            setTheme(R.style.DarkTheme);
        } else if (theme.equals("0"))
            setTheme(R.style.AppTheme);
        else
            setTheme(R.style.DarkTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // declare objects
        rotateLoading = findViewById(R.id.rotateloading);
        loginCard = findViewById(R.id.cardView);
        forgotPassword = findViewById(R.id.textView2);
        edt_username = findViewById(R.id.edt_id);
        edt_password = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_login);

        // check session id
        String sessionId = settings.getString("sessionid", null);

        if (sessionId != null) {
            // get invisible element for animation
            loginCard.setVisibility(View.INVISIBLE);
            forgotPassword.setVisibility(View.INVISIBLE);
            btn_login.setVisibility(View.INVISIBLE);

            // start animation
            rotateLoading.start();

            String url = String.format("%s/check?sessionid=%s", domain, sessionId);
            checkSessionId(view, url);
        }

        btn_login.setOnClickListener(v -> {

            String username = edt_username.getText().toString();
            String password = edt_password.getText().toString();

            if (!username.isEmpty() && !password.isEmpty() && !btn_login.getText().toString().equals("Riprova")) {

                // get invisible element for animation
                loginCard.setVisibility(View.INVISIBLE);
                forgotPassword.setVisibility(View.INVISIBLE);
                btn_login.setVisibility(View.INVISIBLE);

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                // start animation
                rotateLoading.start();

                getKey(v, username, password);
            }
            else {

                String message = "Compila tutti i campi";
                Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
                SnackbarHelper.configSnackbar(v.getContext(), snack);
                snack.show();
            }

        });

    }

    private void apiError(View view, String message, int mode){

        // stop animation
        rotateLoading.stop();

        if (mode == 1){

            snack = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
            SnackbarHelper.configSnackbar(view.getContext(), snack);
            snack.setAction("Riprova", v -> {

                // get sessionId
                String sessionId = settings.getString("sessionid", null);

                // start animation
                rotateLoading.start();

                String url = String.format("%s/check?sessionid=%s", domain, sessionId);
                checkSessionId(view, url);
            });
            snack.show();
        }
        else if (mode == 0){
            // get visible element for animation
            btn_login.setVisibility(View.VISIBLE);
            loginCard.setVisibility(View.VISIBLE);
            forgotPassword.setVisibility(View.VISIBLE);

            snack = Snackbar.make(view,"API error", Snackbar.LENGTH_LONG);
            SnackbarHelper.configSnackbar(view.getContext(), snack);
            snack.show();
        }

    }

    private void login(View view, String url) {

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
                        } else {
                            // get error message
                            JSONObject error = response.getJSONObject("error");
                            String message = error.getString("message");
                            apiError(view, message, 0);
                        }

                    } catch (JSONException e) {
                        String message = "API Error";
                        apiError(view, message, 0);
                    }

                }, e -> {
                    String message = "API Error";
                    apiError(view, message, 0);
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

    private void checkSessionId(View view, String url) {

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.getCache().clear();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // check key "success" exist
                        boolean result = response.getBoolean("success");
                        if (result) {

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
                        String message = "API Error";
                        apiError(view, message,1);
                    }


                },
                e -> {
                    String message = "API Error";
                    apiError(view, message,1);
                });

        queue.add(getRequest);


    }

    private void updateSessionId(View view, String url) {

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
                        String message = "API Error";
                        apiError(view, message, 1);
                    }

                }, e -> {
                    String message = "API Error";
                    apiError(view, message,1);
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

    private void getKey(View view, String username, final String password) {

        String url = domain + "/get_key";

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.getCache().clear();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {

                        String key = response.getString("key");
                        // String id = response.getString("id"); TODO: implement :)

                        // gen random salt
                        Random random = new Random();
                        int val = random.nextInt(2147483647);
                        String salt = String.format("%1$08X", val);

                        // add salt to password
                        String saltedPassword = String.format("%s%s", password, salt);
                        System.out.println(saltedPassword);

                        // decode key from base 64
                        byte[] binaryKey = B64.decodeString(key);

                        // encrypt password with key
                        RSA Crypt = new RSA();
                        Crypt.importPublicKey(binaryKey);
                        byte[] encrypt = Crypt.encrypt(saltedPassword);

                        // encode key in base 64
                        String base64Password = new String(B64.encodeBinary(encrypt));

                        String urlLogin = String.format("%s/login?username=%s&password=%s", domain, username, base64Password);

                        login(view, urlLogin);


                    } catch (Exception e) {
                        String message = "API Error";
                        apiError(view, message,0);
                    }

                },
                e -> {
                    String message = "API Error";
                    apiError(view, message,0);
                });

        queue.add(getRequest);
    }

}


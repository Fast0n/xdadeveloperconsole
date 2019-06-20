package com.fast0n.xdalabsconsole;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fast0n.xdalabsconsole.fragment.DetailsFragment.DetailsFragment;
import com.fast0n.xdalabsconsole.fragment.ScreenshotFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.victor.loading.rotate.RotateLoading;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

import java.util.Objects;

import static android.view.View.GONE;

public class EditActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    SharedPreferences settings;
    String domain;
    Bundle extras;
    Unregistrar mUnregistrar;

    BottomNavigationView navView;
    FloatingActionButton save;
    RecyclerView rv;
    RotateLoading rotateloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        domain = getResources().getString(R.string.url); // get server domain

        // region set theme
        settings = getSharedPreferences("sharedPreferences", 0);
        String theme = settings.getString("toggleTheme", null);

        assert theme != null;
        if (theme.equals("0"))
            setTheme(R.style.AppTheme);
        else
            setTheme(R.style.DarkTheme);
        // endregion

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        navView = findViewById(R.id.nav_view);
        save = findViewById(R.id.fab);
        navView.setOnNavigationItemSelectedListener(this);

        mUnregistrar = KeyboardVisibilityEvent.registerEventListener(this, this::updateKeyboardStatusText);
        updateKeyboardStatusText(KeyboardVisibilityEvent.isKeyboardVisible(this));

        save.setOnClickListener(v -> {

            // region parse recyclerView
            rv = findViewById(R.id.recycler_view);
            // String saveUrl = String.format("%s/change_settings?sessionid=%s&", domain, sessionid);

            for (int i = 0; i < rv.getChildCount(); i++) {
                LinearLayout ly = (LinearLayout) rv.getChildAt(i);
                for (int j = 0; j < ly.getChildCount(); j++) {
                    if (ly.getChildAt(j).getVisibility() != GONE && ly.getChildAt(j) instanceof TextInputLayout) {
                        TextInputLayout til = (TextInputLayout) ly.getChildAt(j);
                        System.out.println(til.getTag() + ": " + Objects.requireNonNull(til.getEditText()).getText().toString());
                    } else if (ly.getChildAt(j).getVisibility() != GONE && ly.getChildAt(j) instanceof Switch) {
                        Switch swt = (Switch) ly.getChildAt(j);
                        System.out.println(swt.getTag() + ": " + swt.isChecked());
                    }
                }
            }
            // endregion

        });

        // region start animation
        findViewById(R.id.fab).setVisibility(GONE);
        rotateloading = findViewById(R.id.rotateloading);
        rotateloading.start();
        // endregion

        loadFragment(new DetailsFragment());

    }

    /**
     * hide keyboard if open
     */
    private void updateKeyboardStatusText(boolean isOpen) {

        if (isOpen)
            navView.setVisibility(GONE);
        else
            navView.setVisibility(View.VISIBLE);


    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            extras = getIntent().getExtras();
            Bundle bundle = new Bundle();
            bundle.putString("idApp", extras.getString("idApp"));
            fragment.setArguments(bundle);
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_details:
                fragment = new DetailsFragment();
                break;
            case R.id.navigation_screenshot:
                fragment = new ScreenshotFragment();
                break;
        }


        return loadFragment(fragment);
    }

}

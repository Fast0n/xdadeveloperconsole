package com.fast0n.xdalabsconsole;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.fast0n.xdalabsconsole.fragment.AppsFragment.AppsFragment;
import com.fast0n.xdalabsconsole.fragment.ManagerFragment.ManageFragment;
import com.fast0n.xdalabsconsole.fragment.SettingsFragment.SettingsFragment;
import com.fast0n.xdalabsconsole.fragment.XposedFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    SharedPreferences settings;
    BottomNavigationView navView;
    Unregistrar mUnregistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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
        setContentView(R.layout.activity_home);

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);

        ImageButton imageButton = findViewById(R.id.imageButton);

        mUnregistrar = KeyboardVisibilityEvent.registerEventListener(this, this::updateKeyboardStatusText);
        updateKeyboardStatusText(KeyboardVisibilityEvent.isKeyboardVisible(this));

        imageButton.setOnClickListener(view -> {
            loadFragment(new SettingsFragment());

            navView.getMenu().findItem(R.id.uncheckedItem).setChecked(true);
            navView.findViewById(R.id.uncheckedItem).setVisibility(View.GONE);
        });

        loadFragment(new ManageFragment());

    }

    /**
     * hide keyboard if open
     */
    private void updateKeyboardStatusText(boolean isOpen) {

        if (isOpen)
            navView.setVisibility(View.GONE);
        else
            navView.setVisibility(View.VISIBLE);


    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_manage:
                fragment = new ManageFragment();
                break;
            case R.id.navigation_myapps:
                fragment = new AppsFragment();
                break;
            case R.id.navigation_xposed:
                fragment = new XposedFragment();
                break;
        }


        return loadFragment(fragment);
    }

    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }
}

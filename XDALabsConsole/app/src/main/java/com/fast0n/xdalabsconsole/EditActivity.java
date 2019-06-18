package com.fast0n.xdalabsconsole;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.fast0n.xdalabsconsole.fragment.AppsFragment.AppsFragment;
import com.fast0n.xdalabsconsole.fragment.DetailsFragment;
import com.fast0n.xdalabsconsole.fragment.ManagerFragment.ManageFragment;
import com.fast0n.xdalabsconsole.fragment.ScreenshotFragment;
import com.fast0n.xdalabsconsole.fragment.SettingsFragment.SettingsFragment;
import com.fast0n.xdalabsconsole.fragment.XposedFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

public class EditActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    SharedPreferences settings;
    SharedPreferences.Editor editor;
    BottomNavigationView navView;
    Unregistrar mUnregistrar;
    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        settings = getSharedPreferences("sharedPreferences", 0);
        editor = settings.edit();

        String theme = settings.getString("toggleTheme", null);

        if (theme.equals("0"))
            setTheme(R.style.AppTheme);
        else
            setTheme(R.style.DarkTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);

        mUnregistrar = KeyboardVisibilityEvent.registerEventListener(this, this::updateKeyboardStatusText);
        updateKeyboardStatusText(KeyboardVisibilityEvent.isKeyboardVisible(this));



        loadFragment( new DetailsFragment());

    }

    /**
     * Nascondi layout se la tastierà è aperta
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
            extras = getIntent().getExtras();
            Bundle bundle = new Bundle();
            bundle.putString("idApp",extras.getString("idApp"));
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

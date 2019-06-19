package com.fast0n.xdalabsconsole;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.fast0n.xdalabsconsole.fragment.DetailsFragment.DetailsFragment;
import com.fast0n.xdalabsconsole.fragment.ScreenshotFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

public class EditActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    SharedPreferences settings;
    SharedPreferences.Editor editor;
    BottomNavigationView navView;
    Unregistrar mUnregistrar;
    Bundle extras;
    FloatingActionButton save;
    DetailsFragment myFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        settings = getSharedPreferences("sharedPreferences", 0);
        editor = settings.edit();

        myFragment = new DetailsFragment();


        String theme = settings.getString("toggleTheme", null);

        if (theme.equals("0"))
            setTheme(R.style.AppTheme);
        else
            setTheme(R.style.DarkTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        navView = findViewById(R.id.nav_view);
        save = findViewById(R.id.fab);
        navView.setOnNavigationItemSelectedListener(this);

        mUnregistrar = KeyboardVisibilityEvent.registerEventListener(this, this::updateKeyboardStatusText);
        updateKeyboardStatusText(KeyboardVisibilityEvent.isKeyboardVisible(this));

        save.setOnClickListener(v -> {


            // TextView textFragment = findViewById(R.id.title);
            // System.out.println(textFragment.getText().toString());
            // textFragment.setText("FUNZIONAAAAAA");

        });

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

package edu.jxut.meteormemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 设置Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("设置");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));

        // 初始化主题设置
        Switch themeSwitch = findViewById(R.id.switch_theme);
        themeSwitch.setChecked(isDarkThemeEnabled());
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setDarkThemeEnabled(isChecked);
            recreate();
        });
    }

    private boolean isDarkThemeEnabled() {
        String sharedPreferencesName = PreferenceManager.getDefaultSharedPreferencesName(this);
        SharedPreferences preferences = this.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        return preferences.getBoolean("dark_theme_enabled", false);
    }

    private void setDarkThemeEnabled(boolean enabled) {
        String sharedPreferencesName = PreferenceManager.getDefaultSharedPreferencesName(this);
        SharedPreferences preferences = this.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("dark_theme_enabled", enabled);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 应用主题设置
        boolean darkThemeEnabled = isDarkThemeEnabled();
        if (darkThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.dev.psalmstrip.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.dev.psalmstrip.R;
import com.dev.psalmstrip.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {
private ActivityAuthBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.transparent));
        } else {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        WindowInsetsControllerCompat insetsControllerCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsControllerCompat.setAppearanceLightStatusBars(true);
    }
}
package com.dev.psalmstrip.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.dev.psalmstrip.R;
import com.dev.psalmstrip.auth.AuthActivity;
import com.dev.psalmstrip.databinding.ActivityMainBinding;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public NavController navController;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        navController = navHostFragment.getNavController();
        if (!sharedPreferences.getBoolean("login", false)) {
            Intent intentToLogin = new Intent(this, AuthActivity.class);
            startActivity(intentToLogin);
            finish();
        }

        setBottomNavigation();


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

    private void setBottomNavigation() {

        binding.bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_chat));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.diagnose_menu));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_cup));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.ic_profile));

//        Navigation.findNavController(this, R.id.dashboardFragment);
        binding.bottomNavigation.show(1, true);
//        navController.navigate(R.id.dashboardFragment);

        binding.bottomNavigation.setOnClickMenuListener(model -> {

            switch (model.getId()) {
                case 1:
                    navController.navigate(R.id.dashboardFragment);
//                    Navigation.findNavController(MainActivity.this, R.id.dashboardFragment);
                    break;
                case 2:
//                    Navigation.findNavController(MainActivity.this, R.id.chatFragment);
                    navController.navigate(R.id.chatFragment);
                    break;
                case 3:
//                    Navigation.findNavController(MainActivity.this, R.id.FAQFragment);
                    navController.navigate(R.id.diagnoseFragment);
                    break;
                case 4:
//                    Navigation.findNavController(MainActivity.this, R.id.diagnozarPointsFragment);
                    navController.navigate(R.id.diagnozarPointsFragment);
                    break;
                case 5:
//                    Navigation.findNavController(MainActivity.this, R.id.profileFragment);
                    navController.navigate(R.id.profileFragment);
                    break;

            }

            return null;
        });

    }

}
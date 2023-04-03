package com.dev.psalmstrip.main.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.psalmstrip.R;
import com.dev.psalmstrip.auth.AuthActivity;
import com.dev.psalmstrip.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        initClicks();
        return binding.getRoot();
    }

    private void initClicks() {
        auth = FirebaseAuth.getInstance();
        binding.tvLogout.setOnClickListener(view -> {
            if (auth.getCurrentUser() != null) {
                auth.signOut();
            }
            sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();

            Intent intentToLogin = new Intent(getContext(), AuthActivity.class);
            startActivity(intentToLogin);
            requireActivity().finish();
        });

        binding.textView5.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.accountSettings);
        });

        binding.textView6.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.languageFragment);
        });

        binding.textView7.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.PrivacyFragment);
        });

        binding.textView8.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.ReportFragment);

        });
    }
}
package com.dev.psalmstrip.auth.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.psalmstrip.R;
import com.dev.psalmstrip.databinding.FragmentSplashBinding;

public class SplashFragment extends Fragment {
    private FragmentSplashBinding binding;
    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        initClicks();
        return binding.getRoot();
    }

    private void initClicks() {
        binding.buttonGetStarted.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_loginFragment);
        });
    }
}
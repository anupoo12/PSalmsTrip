package com.dev.psalmstrip.main.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.psalmstrip.R;
import com.dev.psalmstrip.databinding.FragmentDiagnoseBinding;

public class DiagnoseFragment extends Fragment {

    private FragmentDiagnoseBinding binding;


    public DiagnoseFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDiagnoseBinding.inflate(inflater, container, false);

        initClicks();
        return binding.getRoot();
    }

    private void initClicks() {
        binding.dCvFaq.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.obdFragment);
        });

        binding.dCvReportProblem.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.voiceFragment);
        });

        binding.cvAboutUs.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.videoFragment);
        });
    }
}
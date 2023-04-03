package com.dev.psalmstrip.main.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.psalmstrip.R;
import com.dev.psalmstrip.databinding.ActivityMainBinding;
import com.dev.psalmstrip.databinding.FragmentDashboardBinding;
import com.dev.psalmstrip.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;

//    private ActivityMainBinding binding1;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
//        binding1 = ActivityMainBinding.inflate(getLayoutInflater());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        TextView welcomeText = binding.tvDashWelcome.findViewById(R.id.tv_dash_welcome);

        if (user != null) {
            String name = user.getDisplayName();
            String welcomeMessage = "Welcome " + name + "!";
            welcomeText.setText(welcomeMessage);
        }

        initClicks();
        return binding.getRoot();
    }

    private void initClicks() {

        binding.ivFaq.setOnClickListener(view -> {
            MainActivity activity = (MainActivity) getActivity();

//            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);
//            bottomNavigationView.getMenu().findItem(R.drawable.ic_chat).setChecked(true);
            Navigation.findNavController(view).navigate(R.id.chatFragment);
        });

        binding.cvReportProblem.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.diagnoseFragment);
        });
        binding.ivAboutUs.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_dashboardFragment_to_aboutUSFragment);
        });

        binding.ivLocateUs.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_dashboardFragment_to_locateUsFragment);
        });
    }
}
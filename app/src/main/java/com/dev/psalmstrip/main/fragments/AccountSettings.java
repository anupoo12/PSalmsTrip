package com.dev.psalmstrip.main.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.psalmstrip.R;
import com.dev.psalmstrip.databinding.FragmentAccountSettings2Binding;
import com.dev.psalmstrip.databinding.FragmentDashboardBinding;
import com.dev.psalmstrip.main.MainActivity;
import com.dev.psalmstrip.models.User;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountSettings extends Fragment {

    private FragmentAccountSettings2Binding binding;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public AccountSettings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountSettings.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountSettings newInstance(String param1, String param2) {
        AccountSettings fragment = new AccountSettings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountSettings2Binding.inflate(inflater, container, false);
//        binding1 = ActivityMainBinding.inflate(getLayoutInflater());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();


        EditText fullName = binding.asEtFullName.findViewById(R.id.as_et_full_name);
        EditText email = binding.asEtEmail.findViewById(R.id.as_et_email);
        Button save_changes = binding.asBtnRegister.findViewById(R.id.as_btn_register);
        Button change_password_btn = binding.asBtnNewpass.findViewById(R.id.as_btn_newpass);

        if (user != null) {
            String name = user.getDisplayName();
            String e_mail = user.getEmail();

            fullName.setText(name);
            email.setText(e_mail);

            save_changes.setOnClickListener(view -> {

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName.getText().toString())
                        .build();

                user.updateEmail(email.getText().toString());
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Updated user details", Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(view).navigate(R.id.profileFragment);
                                }
                            }
                        });
            });

            change_password_btn.setOnClickListener(view -> {
                if(!e_mail.isEmpty()){
                    auth.sendPasswordResetEmail(email.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Change Password Email sent", Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(view).navigate(R.id.profileFragment);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                else{
                    Toast.makeText(getContext(), "Enter valid email", Toast.LENGTH_SHORT).show();
                }
            });
        }

//        initClicks();
        return binding.getRoot();
    }


}
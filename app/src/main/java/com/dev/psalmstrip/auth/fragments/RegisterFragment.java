package com.dev.psalmstrip.auth.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dev.psalmstrip.R;
import com.dev.psalmstrip.databinding.FragmentRegisterBinding;
import com.dev.psalmstrip.models.User;
import com.dev.psalmstrip.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore fireStore;
    ProgressDialog progressDialog;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(requireContext());
        initClicks();
        return binding.getRoot();
    }

    private void initClicks() {
        binding.tvAlreadyAccount.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);
        });
        binding.btnRegister.setOnClickListener(view -> {
            validation();
        });
    }

    private void validation() {
        if (binding.etFullName.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please Enter Full Name", Toast.LENGTH_SHORT).show();
        } else if (binding.etEmail.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please Enter Email", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()) {
            Toast.makeText(getContext(), "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
        } else if (binding.etPassword.getText().toString().length() < 6) {
            Toast.makeText(getContext(), "Password must be 6 Character", Toast.LENGTH_SHORT).show();
        } else if (!binding.etPassword.getText().toString().equalsIgnoreCase(binding.etConfirmPassword.getText().toString())) {
            Toast.makeText(getContext(), "Password and Confirm Password must be same", Toast.LENGTH_SHORT).show();
        } else {
            signupNow();
        }
    }

    private void signupNow() {
        progressDialog.setTitle("Going Good...");
        progressDialog.setMessage("It takes Just a few Seconds... ");
        progressDialog.setIcon(R.drawable.happy);
        progressDialog.setCancelable(false);
        progressDialog.show();
        auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = new User(task.getResult().getUser().getUid(), binding.etFullName.getText().toString(), task.getResult().getUser().getEmail(), binding.etPassword.getText().toString());

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(user.getFullName())
                        .build();

                FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
                currentuser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Display name updated successfully
                                }
                            }
                        });

                fireStore.collection(Constants.USERS).document(user.getId()).set(user).addOnCompleteListener(task1 -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Account Created Successfully", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).navigate(R.id.action_registerFragment_to_loginFragment);
                });
            } else {
                progressDialog.dismiss();
                try {
                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                    Toast.makeText(getContext(), "Error : " + errorCode, Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}
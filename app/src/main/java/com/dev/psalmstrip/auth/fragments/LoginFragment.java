package com.dev.psalmstrip.auth.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.psalmstrip.R;
import com.dev.psalmstrip.databinding.FragmentLoginBinding;
import com.dev.psalmstrip.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private FirebaseAuth auth;
    private SharedPreferences.Editor sharedPreferences;
    ProgressDialog progressDialog;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(requireContext());
        sharedPreferences = requireContext().getSharedPreferences("MySharedPref", MODE_PRIVATE).edit();
        initClicks();
        return binding.getRoot();
    }

    private void initClicks() {
        binding.buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etEmail.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please Enter Email", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()) {
                    Toast.makeText(getContext(), "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
                } else if (binding.etPassword.getText().toString().length() < 6) {
                    Toast.makeText(getContext(), "Password must be 6 Character", Toast.LENGTH_SHORT).show();
                } else {
                    loginNow();
                }
            }
        });
        binding.tvDontHaveAccount.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment));

        binding.tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.etEmail.getText().toString();
                if(!email.isEmpty()){
                auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Email sent", Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    private void loginNow() {
        progressDialog.setTitle("Going Good...");
        progressDialog.setMessage("It takes Just a few Seconds... ");
        progressDialog.setIcon(R.drawable.happy);
        progressDialog.setCancelable(false);
        progressDialog.show();
        auth.signInWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    sharedPreferences.putString("email",task.getResult().getUser().getEmail());
                    sharedPreferences.putBoolean("login",true);
                    sharedPreferences.apply();
                    Intent towardMain = new Intent(getContext(), MainActivity.class);
                    startActivity(towardMain);
                    requireActivity().finish();


                } else {
                    progressDialog.dismiss();
                    try {
                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                        Toast.makeText(getContext(), "Error : " + errorCode, Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Toast.makeText(getContext(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

        });
    }
}
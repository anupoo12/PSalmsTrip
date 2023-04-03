package com.dev.psalmstrip.main.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.psalmstrip.R;
import com.dev.psalmstrip.databinding.FragmentAccountSettings2Binding;
import com.dev.psalmstrip.databinding.FragmentReportProblemBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportProblemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportProblemFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentReportProblemBinding binding;

    public ReportProblemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportProblemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportProblemFragment newInstance(String param1, String param2) {
        ReportProblemFragment fragment = new ReportProblemFragment();
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

        binding = FragmentReportProblemBinding.inflate(inflater, container, false);

        EditText complaint = binding.myEdittext.findViewById(R.id.my_edittext);

        Button submit = binding.asBtnRegister.findViewById(R.id.as_btn_register);

        submit.setOnClickListener(view -> {

            if(!complaint.getText().toString().isEmpty()){
                Toast.makeText(getContext(), "Thanks for sending the report :)", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.profileFragment);
            }
            else{
                Toast.makeText(getContext(), "Empty report", Toast.LENGTH_SHORT).show();
            }


        });
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}
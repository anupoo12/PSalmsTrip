package com.dev.psalmstrip.main.fragments;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

import com.dev.psalmstrip.R;
import com.dev.psalmstrip.databinding.FragmentVideoBinding;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link videoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class videoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentVideoBinding binding;

    private VideoView videoView;

    private MediaRecorder mMediaRecorder;

    private Button btn;

    private boolean mIsRecording = false;

    private static final int PERMISSION_REQUEST_CAMERA_MICROPHONE = 12;
    public videoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment videoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static videoFragment newInstance(String param1, String param2) {
        videoFragment fragment = new videoFragment();
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
        binding = FragmentVideoBinding.inflate(inflater, container, false);

        videoView = binding.videoView3.findViewById(R.id.videoView3);

        btn = binding.button4.findViewById(R.id.button4);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Ask for the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_CAMERA_MICROPHONE);
        } else {
            // Permission is already granted
            // Start camera or audio recording
        }



        btn.setOnClickListener(view -> {
            if(!mIsRecording){
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mMediaRecorder.setOutputFile(getRecordingFilePath());
                try {
                    mMediaRecorder.prepare();
                    mMediaRecorder.start();
                    mIsRecording = true;
                    btn.setText("Stop");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
                mIsRecording = false;
                btn.setText("Record");
            }

        });

        return binding.getRoot();
    }

    private String getRecordingFilePath(){
        ContextWrapper contextWrapper = new ContextWrapper(getContext().getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File file = new File(musicDirectory, "testRecording.mp4");

        return file.getPath();
    }
}
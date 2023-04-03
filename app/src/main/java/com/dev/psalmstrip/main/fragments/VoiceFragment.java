package com.dev.psalmstrip.main.fragments;

import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.psalmstrip.R;
import com.dev.psalmstrip.databinding.FragmentVoiceBinding;
import com.dev.psalmstrip.main.MainActivity;
import com.dev.psalmstrip.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import be.tarsos.dsp.mfcc.MFCC;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.mfcc.MFCC;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoiceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FragmentVoiceBinding binding;
    boolean isIconChanged = false;
    Interpreter interpreter;

    private static int MICROPHONE_PERMISSION_CODE = 200;

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private float[][] reshapedMfccs;

    public VoiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VoiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VoiceFragment newInstance(String param1, String param2) {
        VoiceFragment fragment = new VoiceFragment();
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

        if(isMicrophonePresent()){
            getMicrophonePremission();
        }
        // Inflate the layout for this fragment
        binding = FragmentVoiceBinding.inflate(inflater, container, false);

        AndroidFFMPEGLocator locator = new AndroidFFMPEGLocator(getActivity());

        ImageView iconImage = binding.imageView5.findViewById(R.id.imageView5);
        CircleImageView circleMic = (CircleImageView) binding.imageView5.findViewById(R.id.imageView5);
        iconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isIconChanged) {

                    try {
                        iconImage.setImageResource(R.drawable.voice);
                        circleMic.setBorderColor(getContext().getResources().getColor(R.color.teal_700));
                        isIconChanged = false;

                        mediaRecorder.stop();
                        mediaRecorder.release();
                        mediaRecorder = null;
                        Toast.makeText(getActivity(), "Recording has stopped", Toast.LENGTH_LONG).show();
                        binding.button2.setEnabled(true);
                        binding.button2.setBackgroundColor(getResources().getColor(R.color.teal_700));


                    } catch (Exception e){
                        e.printStackTrace();
                    }

                } else {

                    try{
                        iconImage.setImageResource(R.drawable.stop_icon);
                        circleMic.setBorderColor(getContext().getResources().getColor(R.color.emer_red));
                        isIconChanged = true;
                        mediaRecorder = new MediaRecorder();
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mediaRecorder.setOutputFile(getRecordingFilePath());
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mediaRecorder.prepare();
                        mediaRecorder.start();

                        Toast.makeText(getActivity(), "Recording has started", Toast.LENGTH_LONG).show();
                    } catch (Exception e){
                        e.printStackTrace();
                    }


                }
            }
        });

        Button btn = binding.button2;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    mediaPlayer = new MediaPlayer();
//                    mediaPlayer.setDataSource(getRecordingFilePath());
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
//
//
//                } catch (Exception e){
//                    e.printStackTrace();
//                }


                try {

                    InputStream inStream = new FileInputStream(getRecordingFilePath());
                    AudioDispatcher dispatcher = new AudioDispatcher(new UniversalAudioInputStream(inStream, new TarsosDSPAudioFormat(44100, 4096, 1, true, true)), 4096, 0);
                    // Create an MFCC object
                    MFCC mfcc = new MFCC(4096, 44100, 20, 20, 50, 20000);

                    // Create a feature array
                    float[][] features = new float[1][20];

                    // Extract features from audio
                    dispatcher.addAudioProcessor(mfcc);
                    dispatcher.addAudioProcessor(new AudioProcessor() {
                        @Override
                        public boolean process(AudioEvent audioEvent) {
                            mfcc.process(audioEvent);
                            System.arraycopy(mfcc.getMFCC(), 0, features[0], 0, features[0].length);
                            return true;
                        }

                        @Override
                        public void processingFinished() {

                        }
                    });
                    dispatcher.run();
//
//                        Model model = Model.newInstance(getActivity());
//
//                        // Creates inputs for reference.
//                        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 20}, DataType.FLOAT32);
//                        inputFeature0.loadArray(features[0]);
//
//                        // Runs model inference and gets result.
//                        Model.Outputs outputs = model.process(inputFeature0);
//                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//
////                        System.out.println("ARRAY: "+ Math.round(outputFeature0.getFloatArray()[5]));
//
//
//                        TextView tempText = binding.problem.findViewById(R.id.problem);
//                        String[] classLabels = {"Bad battery", "Hole-In-Muffler", "Obstruction of heater fan", "Worn out Serpentine", "Tweaked sound(timing belt)", "Tweaked-bad transmission", "RADIATOR BOILING", "Engine running without oil"};
//
//                    float[][] pred = new float[1][classLabels.length];
//                    for (int i = 0; i < classLabels.length; i++) {
//                        pred[0][i] = outputFeature0.getFloatArray()[i];
//                    }
//                    float[] maxVals = new float[pred.length];
//                    int[] classIdx = new int[pred.length];
//
//// find the index of the maximum value for each row in pred
//                    for (int i = 0; i < pred.length; i++) {
//                        maxVals[i] = pred[i][0];
//                        classIdx[i] = 0;
//                        for (int j = 1; j < pred[i].length; j++) {
//                            if (pred[i][j] > maxVals[i]) {
//                                maxVals[i] = pred[i][j];
//                                classIdx[i] = j;
//                            }
//                        }
//                    }
//
//// get the predicted class name
//                    String predictedClass = classLabels[classIdx[0]];
//
//
//                        tempText.setText("Result: "+ predictedClass);
//
////                        tempText.setText("Result: "+ classLabels[largest(outputFeature0.getFloatArray())]);
//                        // Releases model resources if no longer used.
//                        model.close();

//

                    Interpreter interpreter = new Interpreter(loadModelFile("my_model.tflite"));
                    interpreter.allocateTensors();

                    // Get input and output tensors.
                    int inputIndex = 0;
                    int outputIndex = 0;
                    int[] inputShape = interpreter.getInputTensor(inputIndex).shape();
                    int[] outputShape = interpreter.getOutputTensor(outputIndex).shape();
                    DataType inputDataType = interpreter.getInputTensor(inputIndex).dataType();
                    DataType outputDataType = interpreter.getOutputTensor(outputIndex).dataType();

                    // Test model on random input data.
                    float[][] input = features;
                    Object inputArray = input;
                    Object outputArray = new Object[1][outputShape[1]];

//                    TensorBuffer inputBuffer = TensorBuffer.createFixedSize(inputShape, inputDataType);
                    TensorBuffer inputBuffer = TensorBuffer.createFixedSize(new int[]{1, 20}, DataType.FLOAT32);
                    inputBuffer.loadBuffer(convertInputDataToByteBuffer(input));

                    TensorBuffer outputBuffer = TensorBuffer.createFixedSize(outputShape, outputDataType);
                    interpreter.run(inputBuffer.getBuffer(), outputBuffer.getBuffer());

//                    interpreter.run(inputArray, outputArray);

//                    float[][] outputData = (float[][]) outputArray;
                    float[] scores = outputBuffer.getFloatArray();


                    int classIdx = argmax(scores);
                    String[] classLabels = {"bad battery", "engine running without oil", "Hole-In-Muffler", "Obstruction of heater fan", "RADIATOR BOILING", "tweaked sound(timing belt)", "tweaked-bad transmission", "Worn out Serpentine"};
                    String predictedClass = classLabels[classIdx];
                    System.out.println("This Audio is Belong to " + predictedClass + " Class");
                    TextView tempText = binding.problem.findViewById(R.id.problem);
                    tempText.setText("Result: "+ predictedClass);

                    interpreter.close();
                    dispatcher.stop();

                } catch (Exception e) {
                        // TODO Handle the exception
                    System.out.println(e.getMessage());
                    }
            }
        });


        return binding.getRoot();
    }

    private boolean isMicrophonePresent(){
        if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }
        else{
            return false;
        }
    }

    private int largest(float[] arr)
    {
        int maxIndex = 0;

        for (int i = 1; i < arr.length; i++){
            if(arr[i] > arr[maxIndex]){
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private void getMicrophonePremission(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
    }

    private String getRecordingFilePath(){
        ContextWrapper contextWrapper = new ContextWrapper(getContext().getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "testRecording.mp3");

        return file.getPath();
    }

    // Helper method to load the TFLite model file.
    private MappedByteBuffer loadModelFile(String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = getContext().getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Helper method to find the index of the maximum element in an array.
    private int argmax(float[] array) {
        int maxIdx = 0;
        float maxVal = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxVal) {
                maxVal = array[i];
                maxIdx = i;
            }
        }
        return maxIdx;
    }

    // Helper method to convert 2D float array to ByteBuffer.
    private ByteBuffer convertInputDataToByteBuffer(float[][] inputData) {
        int inputSize = inputData.length * inputData[0].length * 4;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(inputSize);
        byteBuffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < inputData.length; i++) {
            for (int j = 0; j < inputData[0].length; j++) {
                byteBuffer.putFloat(inputData[i][j]);
            }
        }
        return byteBuffer;
    }


}
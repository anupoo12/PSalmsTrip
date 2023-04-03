package com.dev.psalmstrip.main;
//import static java.security.AccessController.getContext;

import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import android.content.Context;

public class MyModel {
    private Interpreter interpreter;
    private ByteBuffer inputBuffer;
    private float[][] outputBuffer;

    MainActivity ma;


    public MyModel() throws IOException {
        // Load the model from file
        interpreter = new Interpreter(loadModelFile());

        // Allocate the input buffer
        int inputSize = interpreter.getInputTensor(0).numElements() * 4;
        inputBuffer = ByteBuffer.allocateDirect(inputSize);
        inputBuffer.order(ByteOrder.nativeOrder());

        // Allocate the output buffer
        int outputSize = interpreter.getOutputTensor(0).numElements() * 4;
        outputBuffer = new float[1][8];
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = ma.getAssets().openFd("my_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public int predict(float[] mfccs) {
        // Copy the input data into the input buffer
        inputBuffer.rewind();
        for (int i = 0; i < mfccs.length; i++) {
            inputBuffer.putFloat(mfccs[i]);
        }

        // Run inference
        interpreter.run(inputBuffer, outputBuffer);

        // Find the predicted class
        int predictedClass = -1;
        float maxProb = 0;
        for (int i = 0; i < outputBuffer[0].length; i++) {
            if (outputBuffer[0][i] > maxProb) {
                predictedClass = i;
                maxProb = outputBuffer[0][i];
            }
        }

        return predictedClass;
    }
}


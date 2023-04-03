package com.dev.psalmstrip.main.fragments;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.psalmstrip.R;
import com.dev.psalmstrip.databinding.FragmentOBDBinding;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OBDFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OBDFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private BluetoothSocket socket;

    private FragmentOBDBinding binding;

    private static int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    public OBDFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OBDFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OBDFragment newInstance(String param1, String param2) {
        OBDFragment fragment = new OBDFragment();
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
        if (isBluetoothPresent()) {
            requestBluetoothPermissions();
        }
        // Inflate the layout for this fragment
        binding = FragmentOBDBinding.inflate(inflater, container, false);

        OBD();
        return binding.getRoot();
    }


    private void OBD() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("00:00:00:00:00:01");
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button speedButton = binding.button2.findViewById(R.id.button2);
        speedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObdCommand command = new SpeedCommand();
                try {
                    command.run(socket.getInputStream(), socket.getOutputStream());
                    String speed = command.getCalculatedResult();
                    System.out.println("speeeeddd: "+speed);
                    TextView resultTextView = binding.result.findViewById(R.id.result);
                    resultTextView.setText("Result: \n0 Confirmed Fault Code\n\n 3 Pending Fault Code\n\nP0120\nThe Intake Air Temperature (IAT) sensor contains a semiconductor device which changes the\n" +
                            "resistance based on the temperature (a thermistor). The IAT sensor is located in the air intake\n" +
                            "passage of the engine air induction system. The IAT sensor has a signal circuit and a ground\n" +
                            "circuit. The Powertrain Control Module (PCM) monitors the Intake Air Temperature Sensor\n" +
                            "signal. The PCM sets the OBDII code when the Intake Air Temperature Sensor signal is out of\n" +
                            "factory specificationFaulty Intake Air Temperature (IAT) sensor Dirty air filter Intake Air Temperature (IAT)\n" +
                            "sensor harness is open or shorted Intake Air Temperature (IAT) sensor circuit poor electrical\n" +
                            "connection\n" +
                            "P0122\n" +
                            "The throttle position sensor (TPS) responds to the accelerator pedal movement. This sensor is\n" +
                            "a kind of potentiometer which transforms the throttle position into output voltage, and emits\n" +
                            "the voltage signal to the Engine Control Module (ECM). In addition, the sensor detects the\n");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isBluetoothPresent() {
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            return true;
        } else {
            return false;
        }
    }

    private void requestBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_BLUETOOTH_PERMISSIONS);
        }
    }


    private String getObdMacAddress() {
        String obdMacAddress = null;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals("OBDII")) { // Replace "OBD-II" with the name of your OBD device
                obdMacAddress = device.getAddress();
                break;
            }
        }
        return obdMacAddress;
    }

}
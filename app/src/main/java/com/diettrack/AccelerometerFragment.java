package com.diettrack;

import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.hardware.SensorEventListener;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Collections;


public class AccelerometerFragment extends Fragment implements SensorEventListener, View.OnClickListener{
    // Class Variables
    private View view;
    // Fragment Variables
    // Nessesary for making fragment run
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // Constructur
    // Nessesary for having Fragment as class
    public AccelerometerFragment(){
        // Required empty public constructor
    }

    // Creating Fragment
    public static   AccelerometerFragment newInstance(String param1, String param2) {
        AccelerometerFragment fragment = new AccelerometerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // onActivityCreated
    // Run methods when started
    // Set toolbar menu items
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set title
        ((FragmentActivityNew)getActivity()).getSupportActionBar().setTitle("Steps");

        // Create menu
        setHasOptionsMenu(true);

    } // onActivityCreated

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // On create view
    // Sets main View variable to the view, so we can change views in fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_accelerometer, container, false);
        //super.onCreate(savedInstanceState);
        Log.i("test", "test");
        //setContentView(R.layout.fragment_accelerometer);

        initializeViews(view);

        startTime = System.currentTimeMillis();

        x = new ArrayList<Double>();
        y = new ArrayList<Double>();
        z = new ArrayList<Double>();

        dataX = new ArrayList<Double>();
        dataY = new ArrayList<Double>();
        dataZ = new ArrayList<Double>();

        start.setOnClickListener(this);
        stop.setOnClickListener(this);

        stepsNumber.setEnabled(false);
        distance.setEnabled(false);
        speed.setEnabled(false);
        calories.setEnabled(false);
        stop.setEnabled(false);
        start.setEnabled(true);

        //SensorManager lets you access the device's sensors.
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        } else {
            // error
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private static double precision = 2;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private double deltaX = 0, deltaY = 0, deltaZ = 0;
    private double lastX = 0, lastY = 0, lastZ = 0;
    private int steps = 0, counter = 0, oldSteps = 0;


    private long startTime = 0, startWatch = 0, stopWatch = 0;
    private int walked = 0;
    private double meters = 0, runningSpeed = 0;

    private ArrayList<Double> x;
    private ArrayList<Double> y;
    private ArrayList<Double> z;

    private ArrayList<Double> dataX;
    private ArrayList<Double> dataY;
    private ArrayList<Double> dataZ;

    private TextView stepsNumber, distance, speed, calories;
    private Chronometer watch;
    private Button start, stop;
    protected Context mContext;


    public void initializeViews(View view) {
        stepsNumber = (TextView) view.findViewById(R.id.stepsNumber);
        distance = (TextView) view.findViewById(R.id.distance);
        speed = (TextView) view.findViewById(R.id.speed);
        calories = (TextView) view.findViewById(R.id.calories);
        watch = (Chronometer) view.findViewById(R.id.chronometer5);
        start = (Button) view.findViewById(R.id.start_button);
        stop = (Button) view.findViewById(R.id.stop_button);
    }

    @Override
    public void onClick(View v) {
        //chronometer
        if (v.equals(start)) {
            watch.setBase(SystemClock.elapsedRealtime() + stopWatch);
            watch.start();

            startWatch = System.currentTimeMillis();

            stepsNumber.setEnabled(true);
            distance.setEnabled(true);
            speed.setEnabled(true);
            calories.setEnabled(true);
            stop.setEnabled(true);
            start.setEnabled(false);


            resetValues();

            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        } else if (v.equals(stop)) {

            stepsNumber.setEnabled(false);
            distance.setEnabled(false);
            speed.setEnabled(false);
            calories.setEnabled(false);
            stop.setEnabled(false);
            start.setEnabled(true);


            stopWatch = watch.getBase() - SystemClock.elapsedRealtime();
            watch.stop();

            sensorManager.unregisterListener(this);

        }
    }

    public void resetValues() {

        watch.setBase(SystemClock.elapsedRealtime());
        meters = 0;
        steps = 0;
        runningSpeed = 0;

        distance.setText("0");
        stepsNumber.setText("0");
        speed.setText("0");
        calories.setText("0");
    }

    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // detect motion
        //which axis's acceleration change
        // if the change is below 2, it is just noise
        if (deltaX > precision) {
            x.add((double) event.values[0]);
            lastX = event.values[0];
        } else {
            x.add(lastX);
        }
        if (deltaY > precision) {
            y.add((double) event.values[1]);
            lastY = event.values[1];
        } else {
            y.add(lastY);
        }
        if (deltaZ > precision) {
            z.add((double) event.values[2]);
            lastZ = event.values[2];
        } else {
            z.add(lastZ);
        }

        counter++;
       //it updates periodically, every 2 s
        if (counter == 200) {
            if (System.currentTimeMillis() > startTime + 200) {
                walked = steps - oldSteps;
                startTime = System.currentTimeMillis();
                oldSteps = steps;

            }
            updateSteps();
            calculateData();
            counter = 0;

        }

    }

    public void calculateData() {

        // Get data from database
        // Database
        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        //Get row number one from users
        long rowID = 1;
        String fields[] = new String[] {
                "_id",
                "user_dob",
                "user_gender",
                "user_height",
                "user_mesurment"
        };

        Cursor c = db.select("users", fields, "_id", rowID);
        String stringUserHeight = c.getString(3);


        // Get height
        double doubleUserHeight = 0;

        try {
            doubleUserHeight = Double.parseDouble(stringUserHeight);
        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        long goalID = 1;

        //  distance
        if (walked > 0 && walked < 2) {
            meters += doubleUserHeight/ 500.0;
        } else if (doubleUserHeight >= 2 && walked < 3) {
            meters +=doubleUserHeight/ 400.0;
        } else if (doubleUserHeight >= 3 && walked < 4) {
            meters += doubleUserHeight/ 300.0;
        } else if (doubleUserHeight >= 4 && walked < 5) {
            meters += doubleUserHeight / 200.0;
        } else if (walked >= 5 && walked < 6) {
            meters +=doubleUserHeight / 120.0;
        } else if (walked >= 6 && walked < 8) {
            meters += doubleUserHeight;
        } else if (walked >= 8) {
            meters += doubleUserHeight* 120.0;
        }

        // update speed
        runningSpeed = meters / (((System.currentTimeMillis() - startWatch) * 1000));
        speed.setText(Double.toString(runningSpeed));

        // update calories
        calories.setText(Double.toString((runningSpeed * 3.6) * 1.25));

        // update distance
        distance.setText(Double.toString(meters));

        //
        db.close();
    }

    public void updateSteps() {

        // Filter Out the data
        // pursuance of smoothing the signals
        for (int i = 0; i < 200; i += 4) {
            double sumX = 0, sumY = 0, sumZ = 0;
            for (int j = 0; j < 4 && i + j < 200; j++) {
                sumX += x.get(i + j);
                sumY += y.get(i + j);
                sumZ += z.get(i + j);
            }
            dataX.add(sumX / 4);
            dataY.add(sumY / 4);
            dataZ.add(sumZ / 4);

        }

        // clear the array list of input
        x.clear();
        y.clear();
        z.clear();

        //Every 50 samples, the minimum and maximum values of the 3-axis acceleration is updated.
        //The average value of them: (maximum+ minimum)/2 is called called the dynamic threshold level
        //This value is used after every 50 samples to determine if the steps have been taken or not.

        // select the axis to work on
        // the axis most active is used with highest acceleration value
        int workingAxis = -1; // 0 = x, 1 = y, 2 = z

        double maxX = Collections.max(dataX), minX = Collections.min(dataX);
        double maxY = Collections.max(dataY), minY = Collections.min(dataY);
        double maxZ = Collections.max(dataZ), minZ = Collections.min(dataZ);

        double diffX = maxX - minX ;
        double diffY = maxY - minY;
        double diffZ = maxZ - minZ;

        double maxDiff = Math.max(diffX, Math.max(diffY, diffZ));

        if (maxDiff == diffX) {
            workingAxis = 0;
        } else if (maxDiff == diffY) {
            workingAxis = 1;
        } else if (maxDiff == diffZ) {
            workingAxis = 2;
        }

        // check how many steps now

        if (workingAxis == 0) {
            double average = (maxX + minX) / 2;
            for (int i = 0, j = 1; i < 49; i++, j++) {
                if (average > dataX.get(j) && average < dataX.get(i)) {
                    steps++;
                    displayCurrentsValues();
                }
            }

        } else if (workingAxis == 1) {
            double average = (maxY + minY) / 2;
            for (int i = 0, j = 1; i < 49; i++, j++) {
                if (average > dataY.get(j) && average < dataY.get(i)) {
                    steps++;
                    displayCurrentsValues();
                }
            }

        } else if (workingAxis == 2) {
            double average = (maxZ + minZ) / 2;
            for (int i = 0, j = 1; i < 49; i++, j++) {
                if (average > dataZ.get(j) && average < dataZ.get(i)) {
                    steps++;
                    displayCurrentsValues();
                }
            }

        } else {
            // error
        }

        // clear the array list of filter
        dataX.clear();
        dataY.clear();
        dataZ.clear();


    }


    public void displayCurrentsValues() {
        stepsNumber.setText(Integer.toString(steps));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
package com.diettrack;

import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import java.util.concurrent.TimeUnit;


public class GFitStepsFragment extends Fragment implements   GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    // Fragment Variables
    // Nessesary for making fragment run
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View view;

    private String mParam1;
    private String mParam2;

    private GFitStepsFragment.OnFragmentInteractionListener mListener;
    // Constructur
    // Nessesary for having Fragment as class
    public GFitStepsFragment(){
        // Required empty public constructor
    }
    // Creating Fragment
    public static   GFitStepsFragment newInstance(String param1, String param2) {
        GFitStepsFragment fragment = new GFitStepsFragment();
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
        ((FragmentActivityNew)getActivity()).getSupportActionBar().setTitle("Google Fit ");


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

    //On create view
    // Sets main View variable to the view, so we can change views in fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //  layout for this fragment
        view = inflater.inflate(R.layout.fragment_gfit_steps, container, false);


        //setContentView;
        connectFitness();
        mApiClient.connect();
        mApiClient.isConnected();
        if (mApiClient.isConnected()){
        }
        return view;


    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "Connected!!!");
        // Now you can make calls to the Fitness APIs.
        findFitnessDataSources();
    }


    @Override
    public void onConnectionSuspended(int i) {
        // If connection to the sensor gets lost at some point
        // enable to determine the reason and react to it here
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Log.i(TAG, "Connection lost.  Cause: Network Lost.");
        } else if (i
                == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Log.i(TAG,
                    "Connection lost.  Reason: Service Disconnected");
        }
    }

    private static final int REQUEST_OAUTH = 1;
    final String TAG = "FitActivity";
    private GoogleApiClient mApiClient = null;
    private OnDataPointListener mApiListener;

    // Authentication
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(getActivity(), REQUEST_OAUTH);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume () {
        super.onResume();

    }

  //start connection
    @Override
    public void onStart () {
        super.onStart();
        connectFitness();
        mApiClient.connect();
    }
  //disconnect
    @Override
    public void onStop () {
        super.onStop();
        mApiClient.disconnect();
    }


    private void connectFitness (){
        if (mApiClient == null) {
            mApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Fitness.SENSORS_API)
                    .addOnConnectionFailedListener(this)
                    .addScope(new Scope(Scopes.FITNESS_LOCATION_READ)) // GET STEP VALUES
                    .addConnectionCallbacks(this)
                    .build();
        }

    }

    // Create API CLIENT
    private void findFitnessDataSources () {
        Fitness.SensorsApi.findDataSources(
                mApiClient,
                new DataSourcesRequest.Builder()
                        .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                        .setDataSourceTypes(DataSource.TYPE_DERIVED)
                        .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        Log.e(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            Log.e(TAG, "Data source found: " + dataSource.toString());
                            Log.e(TAG, "Data Source type: " + dataSource.getDataType().getName());

                            //Let's register a listener to receive Activity data!
                            if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA) && mApiListener == null) {
                                Log.i(TAG, "Data source for TYPE_STEP_COUNT_DELTA found!  Registering.");

                                registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_DELTA);
                            }
                        }
                    }
                });
    }

    private void registerFitnessDataListener ( final DataSource dataSource, DataType dataType){

        // [START register_data_listener]
        mApiListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.e(TAG, "Detected DataPoint field: " + field.getName());
                    Log.e(TAG, "Detected DataPoint value: " + val);

                }
            }
        };

        Fitness.SensorsApi.add(
                mApiClient,
                new SensorRequest.Builder()
                        .setDataSource(dataSource) // Optional but recommended for custom data sets.
                        .setDataType(dataType)
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build(),
                mApiListener).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.i(TAG, "Listener registered!");
                } else {
                    Log.i(TAG, "Listener not registered.");
                }
            }


        });

    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}

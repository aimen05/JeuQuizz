package com.bochenchleba.mapquiz;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bochenchleba.mapquiz.db.DbHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Main extends FragmentActivity implements OnMapReadyCallback, SettingsDialog.SettingsListener {

    private GoogleMap map;

    private SeekBar seekBarZoom;
    private TextView tvQuestion;
    private Button btnSkip;
    private ImageButton btnSettings;
    private TextView tvAttempts;

    private int prevProgress = 1;

    private int mistakesCount = 0;

    private String clickedCountry = "";
    private String countryToGuess = "";

    private SharedPreferences prefs;

    List<String> countriesToGuess = new ArrayList<>();


    private static final String DB_NAME = "database";

    private boolean skipped = false;

    private int totalAttempts;
    private int correctAttempts;


    private void startGame(){

        btnSkip.setOnClickListener(skipClickListener);

        countriesToGuess = getListOfRandomCountries();

        totalAttempts = -1;
        correctAttempts = -1;
        mistakesCount = 0;

        nextQuestion();
    }

    private void nextQuestion(){

        totalAttempts++;

        if (mistakesCount==0)
            correctAttempts++;

        String ttd = correctAttempts+"/"+totalAttempts;
        tvAttempts.setText(ttd);

        if (countriesToGuess.size() == 0){
            gameIsOver();
        }

        else {
            mistakesCount = 0;
            btnSkip.setVisibility(View.INVISIBLE);
            skipped = false;

            countryToGuess = countriesToGuess.get(0);

            tvQuestion.setText(getString(R.string.tv_question, countryToGuess));
        }
    }

    private List<String> getListOfRandomCountries(){

        Set<String> defaultContinents = new HashSet<>(Arrays.asList("AF", "AS", "EU", "OTHER", "SA", "US"));
        Set<String> selectedContinents = prefs.getStringSet(Fields.PREFS_KEY_CONTINENTS, defaultContinents);
        String selectedDifficulty = prefs.getString(Fields.PREFS_KEY_DIFFICULTY, "2");

        DbHandler dbHandler = new DbHandler(this);

        GetCountriesTask.TaskParams params = new GetCountriesTask.TaskParams(
                Fields.TASK_GET_MATCHING_COUNTRIES, selectedContinents, selectedDifficulty);

        List<String> listOfCountries = new ArrayList<>();

        try {
            listOfCountries = new GetCountriesTask(dbHandler).execute(params).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.shuffle(listOfCountries);

        return listOfCountries;
    }

    private LatLng getCoordinatesByName(String countryName){

        DbHandler dbHandler = new DbHandler(this);

        GetCountriesTask.TaskParams params = new GetCountriesTask.TaskParams(
                Fields.TASK_GET_COORDINATES, null, countryName);

        try{
            List<String> coordinates = new GetCountriesTask(dbHandler).execute(params).get();

            return new LatLng(
                    Double.parseDouble(coordinates.get(0)),
                    Double.parseDouble(coordinates.get(1)));

        } catch (Exception e){
            e.printStackTrace();
        }

        return new LatLng(0,0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getPreferences(Context.MODE_PRIVATE);

        seekBarZoom = findViewById(R.id.zoom_seekbar);
        tvQuestion = findViewById(R.id.question_tv);
        btnSkip = findViewById(R.id.btn_skip);
        btnSettings = findViewById(R.id.settings_imgbtn);
        tvAttempts = findViewById(R.id.tv_attempts);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        seekBarZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (map != null){

                    float zoomFactor = progress * Fields.ZOOM_FACTOR + Fields.START_ZOOM;

                    if (progress > prevProgress)
                        map.animateCamera(CameraUpdateFactory.zoomTo(zoomFactor));
                    else
                        map.animateCamera(CameraUpdateFactory.zoomTo(-1*zoomFactor));
                }
                else
                    seekBar.setProgress(1);



                prevProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SettingsDialog().show(getSupportFragmentManager(), "settings");
            }
        });

        startGame();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e("tag", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("tag", "Can't find style. Error: ", e);
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Fields.START_LOCATION, Fields.START_ZOOM));
        map.setOnMapClickListener(mapClickListener);
    }

    private GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {

            if (!skipped){
                try {

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.UK);

                    List<Address> addresses = geocoder.getFromLocation(
                            latLng.latitude, latLng.longitude, 1);

                    if ( addresses.size()>0 && addresses.get(0)!=null ) {

                        clickedCountry = addresses.get(0).getCountryName();
                    }

                    if (clickedCountry.equals(countryToGuess)){

                        Toast.makeText(getApplicationContext(), R.string.toast_correct,
                                Toast.LENGTH_SHORT).show();

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                Fields.START_LOCATION, Fields.START_ZOOM));

                        seekBarZoom.setProgress(1);

                        if (countriesToGuess.size()>0)
                            countriesToGuess.remove(0);

                        nextQuestion();
                    }
                    else{

                        Toast.makeText(getApplicationContext(),
                                getString(R.string.toast_wrong, clickedCountry), Toast.LENGTH_SHORT).show();

                        if (++mistakesCount > 2){
                            btnSkip.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (IOException e) {
                    Log.e("error", "error during reversed geocoding: " + e.getMessage());
                }
            }
        }
    };

    private View.OnClickListener skipClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            skipped=true;

            LatLng coordinates = getCoordinatesByName(countryToGuess);

            if (map != null){

                map.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
                //seekBarZoom.setProgress(Fields.ZOOM_SKIP);

                map.addMarker(new MarkerOptions()
                        .position (coordinates)
                        .title (countryToGuess)
                        .icon (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                        .showInfoWindow();
            }

            if (countriesToGuess.size()>0)
                countriesToGuess.remove(0);

            tvQuestion.setText(getString(R.string.skip_hint));

            Button thisBtn = (Button) v;

            thisBtn.setText(getString(R.string.btn_skip_next));

            thisBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (map!=null){

                        map.clear();

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                Fields.START_LOCATION, Fields.START_ZOOM));

                        seekBarZoom.setProgress(1);
                    }

                    Button thisBtn = (Button) v;

                    thisBtn.setText(getString(R.string.btn_skip));

                    thisBtn.setOnClickListener(skipClickListener);

                    nextQuestion();
                }
            });
        }
    };

    @Override
    public void dataSaved() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage(getString(R.string.reset_alert_msg));
        alertDialogBuilder.setTitle(getString(R.string.reset_alert_title));

        alertDialogBuilder.setPositiveButton( getString(R.string.alert_positive),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                startGame();
                            }
                        });

        alertDialogBuilder.setNegativeButton(getString(R.string.alert_negative),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                    }
                });

        alertDialogBuilder.create().show();
    }

    private void gameIsOver(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage(getString(R.string.gameover_alert_msg,
                correctAttempts, totalAttempts));
        alertDialogBuilder.setTitle(getString(R.string.reset_alert_title));

        alertDialogBuilder.setPositiveButton( getString(R.string.alert_positive),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startGame();
                    }
                });

        alertDialogBuilder.setNegativeButton(getString(R.string.alert_negative),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        btnSkip.setText("AGAIN");
                        btnSkip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startGame();
                                Button thisBtn = (Button)view;

                                thisBtn.setText("SKIP");
                            }
                        });
                    }
                });

        alertDialogBuilder.create().show();
    }
}

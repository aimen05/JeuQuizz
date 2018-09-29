package com.bochenchleba.mapquiz;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by bochenchleba on 22/02/18.
 */

public class Fields {

    public static final LatLng START_LOCATION = new LatLng(35.264126, 24.955303);
    public static final float START_ZOOM = 2.2f;
    public static final float ZOOM_FACTOR = 0.085f;
    public static final int ZOOM_SKIP = 5;

    public static final String PREFS_KEY_CONTINENTS = "param0";
    public static final String PREFS_KEY_DIFFICULTY = "param1";

    public static final int TASK_GET_MATCHING_COUNTRIES = 1;
    public static final int TASK_GET_COORDINATES = 2;
}

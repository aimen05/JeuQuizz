package com.bochenchleba.mapquiz.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bochenchleba.mapquiz.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by bochenchleba on 23/02/18.
 */

public class DbHandler extends SQLiteOpenHelper {

    Context context;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "database";
    private static final String TABLE_COUNTRIES = "countries";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CONTINENT = "cont";
    private static final String KEY_DIFFICULTY = "diff";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    public DbHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_COUNTRIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_CONTINENT + " TEXT,"
                + KEY_DIFFICULTY + " INTEGER,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT"
                + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);

        String[] countriesList = context.getResources().getStringArray(R.array.countries);
        String[] continentsList = context.getResources().getStringArray(R.array.continents);
        String[] difficultiesList = context.getResources().getStringArray(R.array.difficulties);
        String[] latitudesList = context.getResources().getStringArray(R.array.latitudes);
        String[] longitudesList = context.getResources().getStringArray(R.array.longitudes);

        for (int i=0 ; i < countriesList.length ; i++){

            ContentValues values = new ContentValues();
            values.put(KEY_NAME, countriesList[i]);
            values.put(KEY_CONTINENT, continentsList[i]);
            values.put(KEY_DIFFICULTY, difficultiesList[i]);
            values.put(KEY_LATITUDE, latitudesList[i]);
            values.put(KEY_LONGITUDE, longitudesList[i]);

            db.insert(TABLE_COUNTRIES, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRIES);
        onCreate(db);
    }


    public void addRecord(Record r) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, r.getName());
        values.put(KEY_CONTINENT, r.getContinent());
        values.put(KEY_DIFFICULTY, r.getDifficulty());
        values.put(KEY_LATITUDE, r.getLatitude());
        values.put(KEY_LONGITUDE, r.getLongitude());

        db.insert(TABLE_COUNTRIES, null, values);
        db.close();
    }

    public Record getRecord(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_COUNTRIES,
                new String[] { KEY_ID, KEY_NAME, KEY_CONTINENT, KEY_DIFFICULTY, KEY_LATITUDE, KEY_LONGITUDE },
                KEY_ID + "=?",
                new String[] { String.valueOf(id) },
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Record record = new Record(Integer.parseInt(
                cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5));

        cursor.close();

        return record;
    }

    public List<String> getCountries (Set<String> continents, String difficulty) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> records = new ArrayList<>();

        for (String continent:continents) {
            Cursor cursor = db.query(
                    TABLE_COUNTRIES,
                    new String[] { KEY_NAME},
                    KEY_CONTINENT + " LIKE ? AND " + KEY_DIFFICULTY + " <= ?",
                    new String[] {"%"+continent+"%", difficulty},
                    null,null,null,null
            );

            if (cursor.moveToFirst()) {
                do {
                    records.add(cursor.getString(0));
                }
                while (cursor.moveToNext());
            }

            cursor.close();
        }

        return records;
    }

    public List<String> getCoordinates (String countryName){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_COUNTRIES,
                new String[] { KEY_LATITUDE, KEY_LONGITUDE},
                KEY_NAME + "=?",
                new String[] {countryName},
                null,null,null,null
        );

        List<String> coordinates = new ArrayList<>();

        if (cursor.moveToFirst()) {

            coordinates.add(cursor.getString(0));
            coordinates.add(cursor.getString(1));
        } else {

            coordinates.add("0");
            coordinates.add("0");
        }

        cursor.close();

        return coordinates;
    }

    public List<Record> getAll() {

        SQLiteDatabase db = this.getReadableDatabase();
        List<Record> records = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_COUNTRIES + " ORDER BY " + KEY_CONTINENT;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {
                Record contact = new Record();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setContinent(cursor.getString(2));
                contact.setDifficulty(cursor.getString(3));
                contact.setLatitude(cursor.getString(4));
                contact.setLongitude(cursor.getString(5));

                records.add(contact);
            }
            while (cursor.moveToNext());
        }

        cursor.close();

        return records;
    }


    public int getCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = "SELECT  * FROM " + TABLE_COUNTRIES;

        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int update(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, record.getName());
        values.put(KEY_CONTINENT, record.getContinent());
        values.put(KEY_DIFFICULTY, record.getDifficulty());
        values.put(KEY_LATITUDE, record.getLatitude());
        values.put(KEY_LONGITUDE, record.getLongitude());

        return db.update(TABLE_COUNTRIES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(record.getId()) });
    }

    public void delete(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_COUNTRIES, KEY_ID + " = ?",
                new String[] { String.valueOf(record.getId()) });

        db.close();
    }
}

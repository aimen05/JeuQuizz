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


public class DbHandler extends SQLiteOpenHelper {

    Context context;

    private static final int VERSION_BDD = 1;
    private static final String DB_NAME = "database";
    private static final String TABLE_PAYS = "countries";

    private static final String ID = "id";
    private static final String NOMPAYS = "name";
    private static final String CONTINENT = "cont";
    private static final String DIFFICULTE = "diff";
    private static final String ATTITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    public DbHandler(Context context) {
        super(context, DB_NAME, null, VERSION_BDD);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PAYS + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NOMPAYS + " TEXT,"
                + CONTINENT + " TEXT,"
                + DIFFICULTE + " INTEGER,"
                + ATTITUDE + " TEXT,"
                + LONGITUDE + " TEXT"
                + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);

        String[] countriesList = context.getResources().getStringArray(R.array.countries);
        String[] continentsList = context.getResources().getStringArray(R.array.continents);
        String[] difficultiesList = context.getResources().getStringArray(R.array.difficulties);
        String[] latitudesList = context.getResources().getStringArray(R.array.latitudes);
        String[] longitudesList = context.getResources().getStringArray(R.array.longitudes);

        for (int i=0 ; i < countriesList.length ; i++){

            ContentValues values = new ContentValues();
            values.put(NOMPAYS, countriesList[i]);
            values.put(CONTINENT, continentsList[i]);
            values.put(DIFFICULTE, difficultiesList[i]);
            values.put(ATTITUDE, latitudesList[i]);
            values.put(LONGITUDE, longitudesList[i]);

            db.insert(TABLE_PAYS, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYS);

        onCreate(db);
    }


    public void addRecord(Record r) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NOMPAYS, r.getName());
        values.put(CONTINENT, r.getContinent());
        values.put(DIFFICULTE, r.getDifficulty());
        values.put(ATTITUDE, r.getLatitude());
        values.put(LONGITUDE, r.getLongitude());

        db.insert(TABLE_PAYS, null, values);
        db.close();
    }

    public Record getRecord(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PAYS,
                new String[] { ID, NOMPAYS, CONTINENT, DIFFICULTE, ATTITUDE, LONGITUDE },
                ID + "=?",
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
                    TABLE_PAYS,
                    new String[] { NOMPAYS},
                    CONTINENT + " LIKE ? AND " + DIFFICULTE + " <= ?",
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
                TABLE_PAYS,
                new String[] { ATTITUDE, LONGITUDE},
                NOMPAYS + "=?",
                new String[] {countryName},
                null,null,null,null
        );

        List<String> coordinates = new ArrayList<>();

        if (cursor.moveToFirst()) {

            coordinates.add(cursor.getString(0));
            coordinates.add(cursor.getString(1));
        }
        else{

            coordinates.add("0");
            coordinates.add("0");
        }

        cursor.close();

        return coordinates;
    }

    public List<Record> getAll() {

        SQLiteDatabase db = this.getReadableDatabase();
        List<Record> records = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_PAYS + " ORDER BY " + CONTINENT;

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

        String countQuery = "SELECT  * FROM " + TABLE_PAYS;

        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int update(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NOMPAYS, record.getName());
        values.put(CONTINENT, record.getContinent());
        values.put(DIFFICULTE, record.getDifficulty());
        values.put(ATTITUDE, record.getLatitude());
        values.put(LONGITUDE, record.getLongitude());

        return db.update(TABLE_PAYS, values, ID + " = ?",
                new String[] { String.valueOf(record.getId()) });
    }

    public void delete(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PAYS, ID + " = ?",
                new String[] { String.valueOf(record.getId()) });

        db.close();
    }
}

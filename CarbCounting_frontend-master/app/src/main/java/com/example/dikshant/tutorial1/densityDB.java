package com.example.dikshant.tutorial1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dikshant on 2/19/2017.
 */

public class densityDB extends DBHandler {

    //private static final String DB_NAME = "USDA_Nutrition_DB";
    private static final String DB_NAME = "userInfo_";
    private static final int DB_VERSION = 1;

    // table name
    private static final String TABLE_NAME = "densityInfo";

    // columns:
    private static String FOOD = "Food";
    private static String DENSITY = "Density_g_ml";
    //private static String MASS = "Mass_g";
    //private static String VOL = "Volume_ml";
    //private static String DESC = "Desc";

    //private static String[] cols = {FOOD, MASS, VOL, DESC};
    private static String[] cols = {FOOD, DENSITY};

    private static Context cxt;


    public densityDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        cxt = context;
        Log.d("dDB", "constructor");
        //readCSV();
    }
/*
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("dDB", "creating ddb");
        String CREATE_TABLE =  "CREATE TABLE " + TABLE_NAME + "(" + FOOD + " TEXT PRIMARY KEY," + MASS + " TEXT,"
                + VOL + " TEXT," + DESC + " TEXT," + ")";
        db.execSQL(CREATE_TABLE);
        //readCSV();
    }
*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
// Creating tables again
        onCreate(db);
    }

    public void readCSV(){
        try {
            InputStream is = cxt.getAssets().open("Food_Vol2.txt");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));//new BufferedReader(new FileReader("ABBREV_2.txt"));
            String line = "";
            while ((line = buffer.readLine()) != null) {
                addEntry(line);
            }
            buffer.close();
        }
        catch (Exception e){
            Log.d("dDB", "couldn't create database");
        }
    }

    public void addEntry(String row){
        //Log.d("nDB", "adding entry");

        SQLiteDatabase db = this.getWritableDatabase();

        String row_values[] = row.split("\t");
        int rowSize = row_values.length;

        //Log.d("nDB entry", row_values[0]);

        ContentValues values = new ContentValues();

        for (int i = 0; i < rowSize; i++){
            values.put(cols[i], row_values[i]);
        }

        //values.put(DESC, row_values[0]);
        //values.put(CARB, row_values[1]);
        //values.put(WATER, row_values[2]);
        //values.put(ENERGY, row_values[3]);
        //values.put(PROTEIN, row_values[4]);
        //values.put(FIBER, row_values[5]);
        //values.put(SUGAR, row_values[6]);
        //values.put(CHOL, row_values[7]);
        //values.put(WT, row_values[8]);
        //values.put(WT_DESC, row_values[9]);

        // db insertion
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        //db.insert(TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void clear(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

    @Override
    public int getCount(){
        SQLiteDatabase db = this.getReadableDatabase();

        //Cursor cs = db.query(USER_TABLE, new String[]{})
        String countQuery = "SELECT * FROM "  + TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();

        //return 0;
    }

    public Cursor queryContaining(String word){
        Log.d("nDB", "querying");

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {FOOD, DENSITY},
                FOOD + " LIKE ?", new String[] {"%" + word + "%"},
                null, null, null);

        Log.d("nDB", String.valueOf(cursor.getCount()));

        try{
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(FOOD));
                String data2 = cursor.getString(cursor.getColumnIndex(DENSITY));
                //Log.d("nDB query", data2);
            }
        }
        catch (Exception e){
            Log.d("nDB query", "failed cursor browsing");
        }
        //Log.d("nDB query", cursor.getString(cursor.getColumnIndex(DESC)));

        return cursor;
    }

    public Cursor queryContainingRaw(String word){

        Log.d("nDB", "querying raw");

        SQLiteDatabase db = this.getReadableDatabase();

        //Cursor cursor = db.query(TABLE_NAME, new String[] {DESC, CARB, CHOL},
        //      DESC + " LIKE ?", new String[] {"%" + word + "%"},
        //    null, null, null);

        String queryString = "Select * from " + TABLE_NAME + " where " + FOOD + " like ? and " + FOOD + " like ?";

        Cursor cursor = db.rawQuery(queryString, new String[] {"%" + word + "%", "%RAW%"});

        //cursor = db.query(TABLE_NAME, new String[] {DESC, CARB, CHOL},
        //      "(" + KEY_BODY + " like '%" + inputText + "%' OR " + KEY_TITLE + " like '%" + inputText + "%') AND (" + KEY_COLOR + " like '%" + colorvalue + "%')" , null,
        //    null, null, KEY_TIME + " DESC", null);

        Log.d("nDB", String.valueOf(cursor.getCount()));

        //Log.d("nDB query", cursor.getString(cursor.getColumnIndex(DESC)));

        return cursor;
    }

    public Map<String, Double> getMapFromCursor(Cursor cursor){
        Map<String, Double> results = new HashMap<String, Double>();

        try{
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String food = cursor.getString(cursor.getColumnIndex(FOOD));
                String density = cursor.getString(cursor.getColumnIndex(DENSITY));
                Double density2 = Double.valueOf(density);

                /*
                String mass = cursor.getString(cursor.getColumnIndex(MASS));
                String volume = cursor.getString(cursor.getColumnIndex(VOL));
                Integer m = Integer.valueOf(mass);
                Integer v = Integer.valueOf(volume);

                if (v > 0){
                    Integer density = m/v;
                    results.put(food, density);
                }

                Log.d("density", String.valueOf(v));
                Log.d("density map", food);
                */

                //int d = m/(v);

                //Integer density = (Integer.valueOf(mass))/(Integer.valueOf(volume));
                results.put(food, density2);
            }
        }
        catch (Exception e){
            Log.e("density map exception", "exception", e);
            Log.d("density map", "failed");
        }

        return results;
    }

    public List<String> getKeysFromCursor(Cursor cursor){
        List<String> keys = new ArrayList<String>();

        try{
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String food = cursor.getString(cursor.getColumnIndex(FOOD));
                //Log.d("nDB query", data2);
                keys.add(food);
            }
        }
        catch (Exception e){
            Log.d("density keys", "failed");
        }

        return keys;
    }

    public void addEntry(String food, String density){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("DB", "found writable");

        //Calendar cal = Calendar.getInstance();
        //int date = cal.DATE;
        //String date = "2";

        // value creation
        ContentValues values = new ContentValues();

        values.put(FOOD, food);
        values.put(DENSITY, density);

        // db insertion
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }
}

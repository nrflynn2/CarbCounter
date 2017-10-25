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

public class nutritionalDB extends DBHandler {

    //private static final String DB_NAME = "USDA_Nutrition_DB";
    private static final String DB_NAME = "userInfo_";
    private static final int DB_VERSION = 1;

    // table name
    private static final String TABLE_NAME = "nutritionInfo";

    // columns: Shrt_Desc	Carbohydrt_(g)	Water_(g)	Energ_Kcal	Protein_(g)	Fiber_TD_(g)	Sugar_Tot_(g)	Cholestrl_(mg)	GmWt_1	GmWt_Desc2
    private static String DESC = "Shrt_Desc";
    private static String CARB = "Carbohydrt_g";
    private static String WATER = "Water_g";
    private static String ENERGY = "Energ_Kcal";
    private static String PROTEIN = "Protein_g";
    private static String FIBER = "Fiber_TD_g";
    private static String SUGAR = "Sugar_Tot_g";
    private static String CHOL = "Cholestrl_mg";
    private static String WT = "GmWt_1";
    private static String WT_DESC = "GmWt_Desc2";

    private static String[] cols = {DESC, CARB, WATER, ENERGY, PROTEIN, FIBER, SUGAR, CHOL, WT, WT_DESC};


    private static Context cxt;


    public nutritionalDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        cxt = context;
        Log.d("nDB", "constructor");
        //readCSV();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("nDB", "creating ndb");
        String CREATE_TABLE =  "CREATE TABLE " + TABLE_NAME + "(" + DESC + " TEXT PRIMARY KEY," + CARB + " TEXT,"
                + WATER + " TEXT," + ENERGY + " TEXT," + PROTEIN + " TEXT," + FIBER + " TEXT," +
                SUGAR + " TEXT," + CHOL + " TEXT," + WT + " TEXT," + WT_DESC + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
        //readCSV();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
// Creating tables again
        onCreate(db);
    }

    public void readCSV(){
        try {
            InputStream is = cxt.getAssets().open("USDA_DB_ABBREV.txt");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));//new BufferedReader(new FileReader("ABBREV_2.txt"));
            String line = "";
            while ((line = buffer.readLine()) != null) {
                addEntry(line);
            }
            buffer.close();
        }
        catch (Exception e){
            Log.d("DB", "couldn't create database");
        }
    }

    public void readCSV2(){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("DB", "got writable db");
        try {
            InputStream is = cxt.getAssets().open("USDA_DB_ABBREV.txt");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));//new BufferedReader(new FileReader("ABBREV_2.txt"));
            String line = "";
            String tableName = DB_NAME;
            String columns = DESC + ", " + CARB + ", " + WATER + ", " + ENERGY + ", " + PROTEIN + ", " + FIBER
                    + ", " + SUGAR + ", " + CHOL + ", " + WT + ", " + WT_DESC;
            //String columns2 = "_id, name, dt1, dt2, dt3";
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") values(";
            String str2 = ");";
            while ((line = buffer.readLine()) != null) {
                String parts[] = line.split("\t");
                List<String> nutrition = new ArrayList();
                for (int i = 1; i < parts.length; i++){
                    nutrition.add(parts[i]);
                }
                db.beginTransaction();
                while ((line = buffer.readLine()) != null) {
                    StringBuilder sb = new StringBuilder(str1);
                    String[] str = line.split(" ");
                    //sb.append("'" + str[0] + "',");
                    //sb.append(str[1] + "',");
                    //sb.append(str[2] + "',");
                    //sb.append(str[3] + "',");
                    //sb.append(str[4] + "',");
                    //sb.append(str[5] + "',");
                    //sb.append(str[6] + "',");
                    //sb.append(str[7] + "',");
                    //sb.append(str[8] + "',");
                    //sb.append(str[9] + "'");
                    //sb.append(str2);
                    //db.execSQL(sb.toString());
                }
                //db.setTransactionSuccessful();
                db.endTransaction();
                //db.put(parts[0], nutrition);
                //keys.add(parts[0]);
            }
            buffer.close();
        }
        catch (Exception e){
            Log.d("DB", "couldn't create database");
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

        Cursor cursor = db.query(TABLE_NAME, new String[] {DESC, CARB, CHOL},
                DESC + " LIKE ?", new String[] {"%" + word + "%"},
                null, null, null);

        Log.d("nDB", String.valueOf(cursor.getCount()));

        //Log.d("nDB query", cursor.getString(cursor.getColumnIndex(DESC)));

        return cursor;
    }

    public Cursor queryContainingRaw(String word){

        Log.d("nDB", "querying raw");

        SQLiteDatabase db = this.getReadableDatabase();

        //Cursor cursor = db.query(TABLE_NAME, new String[] {DESC, CARB, CHOL},
          //      DESC + " LIKE ?", new String[] {"%" + word + "%"},
            //    null, null, null);

        String queryString = "Select * from " + TABLE_NAME + " where " + DESC + " like ? and " + DESC + " like ?";

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
                String desc = cursor.getString(cursor.getColumnIndex(DESC));
                Double carb = Double.valueOf(cursor.getString(cursor.getColumnIndex(CARB)));

                results.put(desc, carb);
                //Log.d("nutrition map", String.valueOf(desc));
            }
        }
        catch (Exception e){
            Log.e("nutrition map exception", "exception", e);
            Log.d("nutrition map", "failed");
        }

        return results;
    }

    public List<String> getKeysFromCursor(Cursor cursor){
        List<String> keys = new ArrayList<String>();

        try{
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String desc = cursor.getString(cursor.getColumnIndex(DESC));
                String carb = cursor.getString(cursor.getColumnIndex(CARB));
                //Log.d("nDB query", data2);
                keys.add(desc);
            }
        }
        catch (Exception e){
            Log.d("nutrition keys", "failed");
        }

        return keys;
    }
}

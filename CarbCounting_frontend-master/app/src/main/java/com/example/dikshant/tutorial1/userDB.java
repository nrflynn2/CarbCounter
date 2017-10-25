package com.example.dikshant.tutorial1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dikshant on 2/5/2017.
 */

public class userDB extends DBHandler {

    private static final String TABLE_NAME = "userHist_";
    // Table Column Names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_FOOD = "food";
    private static final String KEY_CARBS = "carbs";
    private static final String KEY_MEAL = "meal";

    public userDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //@Override
    public void addEntry(String food, String carbs, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("DB", "found writable");

        //Calendar cal = Calendar.getInstance();
        //int date = cal.DATE;
        //String date = "2";

        // value creation
        ContentValues values = new ContentValues();

        values.put(KEY_DATE, date);
        values.put(KEY_FOOD, food);
        values.put(KEY_CARBS, carbs);

        // db insertion
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void addEntry(String food, String carbs, String date, String meal){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("DB", "found writable");

        //Calendar cal = Calendar.getInstance();
        //int date = cal.DATE;
        //String date = "2";

        // value creation
        ContentValues values = new ContentValues();

        values.put(KEY_DATE, date);
        values.put(KEY_FOOD, food);
        values.put(KEY_CARBS, carbs);
        values.put(KEY_MEAL, meal);

        // db insertion
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void addEntry(nutrMeal meal, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("DB", "found writable");

        // value creation
        ContentValues values = new ContentValues();

        List<String> foods = meal.getFoods();
        List<Double> carbFactors = meal.getCarbFactors();
        List<Double> volumes = meal.getVolumes();
        String name = meal.getName();

        int length = foods.size();

        for (int i = 0; i < length; i++){
            values.put(KEY_DATE, date);
            values.put(KEY_FOOD, foods.get(i));
            values.put(KEY_CARBS, carbFactors.get(i)*volumes.get(i));
            values.put(KEY_MEAL, name);

            // db insertion
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }

        db.close();
    }

    @Override
    public int getCount(){
        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = "SELECT * FROM "  + TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    @Override
    public void clear(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

    public Cursor queryContainingFood(String word){
        Log.d("nDB", "querying");

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {KEY_DATE, KEY_FOOD, KEY_CARBS},
                KEY_FOOD + " LIKE ?", new String[] {"%" + word + "%"},
                null, null, null);

        Log.d("nDB", String.valueOf(cursor.getCount()));

        return cursor;
    }

    public Cursor queryCarb(String word){
        Log.d("nDB", "querying");

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {KEY_DATE, KEY_FOOD, KEY_CARBS},
                KEY_CARBS + " LIKE ?", new String[] {"%" + word + "%"},
                null, null, null);

        Log.d("nDB", String.valueOf(cursor.getCount()));

        return cursor;
    }

    public Cursor queryDate(String word){ // need to change this one
        Log.d("nDB", "querying");

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {KEY_DATE, KEY_FOOD, KEY_CARBS},
                "*", new String[] {"%" + word + "%"},
                null, null, null);

        Log.d("nDB", String.valueOf(cursor.getCount()));

        return cursor;
    }

    public Cursor queryDateSum(){
        // SELECT NAME, SUM(SALARY) FROM COMPANY GROUP BY NAME ORDER BY NAME

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + KEY_DATE + ", SUM(" + KEY_CARBS + ") FROM " + TABLE_NAME + " GROUP BY " + KEY_DATE + " ORDER BY ?";

        Cursor cursor = db.rawQuery(query, new String[] {KEY_CARBS});

        Log.d("datesum cursor", String.valueOf(cursor.getCount()));

        return  cursor;
    }

    public List<Date> getDatesforGraph(Cursor datesumCursor){
        List<Date> dates = new ArrayList<Date>();

        for (datesumCursor.moveToFirst(); !datesumCursor.isAfterLast(); datesumCursor.moveToNext()) {
            String date = datesumCursor.getString(datesumCursor.getColumnIndex(KEY_DATE));

            dates.add(stringToDate(date));
        }

        return dates;
    }

    public List<Double> getSumsforGraph(Cursor datesumCursor){
        List<Double> sums = new ArrayList<Double>();
        for (datesumCursor.moveToFirst(); !datesumCursor.isAfterLast(); datesumCursor.moveToNext()) {
            String carbs = datesumCursor.getString(datesumCursor.getColumnIndex("SUM(" + KEY_CARBS + ")"));
            sums.add(Double.valueOf(carbs));
        }
        return sums;
    }

    public String printDateSumTable(Cursor cursor){

        Log.d("printingdatesum", "entered function");

        String table = "";

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Log.d("file output", "entered for loop");
            //String food = cursor.getString(cursor.getColumnIndex(KEY_FOOD));
            String carbs = cursor.getString(cursor.getColumnIndex("SUM(" + KEY_CARBS + ")"));
            String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));

            Log.d("testing date parse", stringToDate(date).toString());
            //String meal = cursor.getString(cursor.getColumnIndex(KEY_MEAL));

            String line = date + "_" + carbs + "\n";
            table = table + line;
            //Log.d("write test", line);

            //stream.write(line.getBytes());

            //results.put(desc, carb);
            //Log.d("nutrition map", String.valueOf(desc));
        }
        return table;
    }

    public Cursor queryAll(){
        SQLiteDatabase db = this.getReadableDatabase();

        String fullQuery = "SELECT * FROM "  + TABLE_NAME;
        Cursor cursor = db.rawQuery(fullQuery, null);
        return cursor;
    }

    public void exportCSV(Context context){
        Log.d("file output", "start2");

        SQLiteDatabase db = this.getReadableDatabase();
        String fullQuery = "SELECT * FROM "  + TABLE_NAME;
        Cursor cursor = db.rawQuery(fullQuery, null);

        writeFileOnInternalStorage(context, "databasetext.txt", "test");

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Log.d("file output", "entered for loop");
            String food = cursor.getString(cursor.getColumnIndex(KEY_FOOD));
            String carbs = cursor.getString(cursor.getColumnIndex(KEY_CARBS));
            String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
            String meal = cursor.getString(cursor.getColumnIndex(KEY_MEAL));

            String line = date + "," + meal + "," + food + "," + carbs + "\n";
            Log.d("write test", line);
            writeFileOnInternalStorage(context, "databasetext.txt", line);

        }

        Log.d("file export", "end task");
    }

    public void exportCSV2(Context context){
        Log.d("file output", "start");
        //File dbFile= getDatabasePath("MyDBName.db");
        //DBHelper dbhelper = new DBHelper(getApplicationContext());
        //File exportDir = new File(context.getFilesDir(), "databasetest");
        File exportDir = new File(Environment.getExternalStorageDirectory(), "databasetest");
        //File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        //Log.d("file loc", context.getFilesDir().getAbsolutePath()+"/text.txt");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "csvname.csv");
        try
        {
            file.createNewFile();
            Log.d("file output", "created new file");
            //CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = this.getReadableDatabase();

            //Cursor cs = db.query(USER_TABLE, new String[]{})
            String fullQuery = "SELECT * FROM "  + TABLE_NAME;
            Cursor cursor = db.rawQuery(fullQuery, null);
            //csvWrite.writeNext(curCSV.getColumnNames());

            FileOutputStream stream = new FileOutputStream(file);
            Log.d("file output", "created stream");
            stream.write("test".getBytes());

            try{
                Log.d("file output", "entered try loop, size: " + getCount());
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    Log.d("file output", "entered for loop");
                    String food = cursor.getString(cursor.getColumnIndex(KEY_FOOD));
                    String carbs = cursor.getString(cursor.getColumnIndex(KEY_CARBS));
                    String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                    String meal = cursor.getString(cursor.getColumnIndex(KEY_MEAL));

                    String line = date + "," + meal + "," + food + "," + carbs + "\n";
                    Log.d("write test", line);

                    stream.write(line.getBytes());



                    //results.put(desc, carb);
                    //Log.d("nutrition map", String.valueOf(desc));
                }
            }
            catch (Exception e){
                Log.e("usedb failure", "db export", e);
            }

            Log.d("file output", "file written");

            stream.close();

            /*

            while(curCSV.moveToNext())
            {
                //Which column you want to exprort
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            */
        }
        catch(Exception sqlEx)
        {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }

        Log.d("file export", "end task");
    }

    public void writeFileOnInternalStorage(Context mcoContext,String sFileName, String sBody){
        File file = new File(mcoContext.getFilesDir(),"mydir");

        Log.d("file output", "new file");

        if(!file.exists()){
            file.mkdir();
            Log.d("file output", "made dir");
        }


        try{
            Log.d("file output", "trying");
            File gpxfile = new File(file, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();

        }catch (Exception e){

        }
    }

    public String getTableAsString() {
        SQLiteDatabase db = this.getReadableDatabase();
        String tableName = TABLE_NAME;
        Log.d("table to string", "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }

    private Date stringToDate(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        Date date = new Date();
        //String pattern = date.toString();
        try {
            date = format.parse(dateString);
            Log.d("date parse", date.toString());
            //System.out.println(date);
        } catch (ParseException e) {

            e.printStackTrace();
        }

        return date;
    }

    // need reading functions and export and others???
}

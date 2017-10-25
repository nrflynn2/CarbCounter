package com.example.dikshant.tutorial1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Dikshant on 3/4/2017.
 */
public abstract class DBHandler extends SQLiteOpenHelper {
    // Database Version
    protected static final int DB_VERSION = 1;
    // Database Name
    protected static final String DB_NAME = "userInfo_";
    // Table Name
    private static final String TABLE_NAME = "userHist_";
    // Table Column Names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_FOOD = "food";
    private static final String KEY_CARBS = "carbs";
    private static final String KEY_MEAL = "meal";
    // table name
    private static final String TABLE_NAME_2 = "nutritionInfo";
    private static final String TABLE_NAME_3 = "densityInfo";
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

    // columns:
    private static String FOOD = "Food";
    private static String DENSITY = "Density_g_ml";
    //private static String MASS = "Mass_g";
    //private static String VOL = "Volume_ml";
    //private static String DESC_2 = "Desc";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB", "oncreating");
        String CREATE_TABLE =  "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT," + KEY_FOOD + " TEXT," + KEY_CARBS + " TEXT," + KEY_MEAL + " TEXT" + ")";

        Log.d("DB", CREATE_TABLE);

        Log.d("DB", "creating ndb");
        String CREATE_TABLE_2 =  "CREATE TABLE " + TABLE_NAME_2 + "(" + DESC + " TEXT," + CARB + " TEXT,"
                + WATER + " TEXT," + ENERGY + " TEXT," + PROTEIN + " TEXT," + FIBER + " TEXT," +
                SUGAR + " TEXT," + CHOL + " TEXT," + WT + " TEXT," + WT_DESC + " TEXT" + ")";

        Log.d("dDB", "creating ddb");
        String CREATE_TABLE_3 = "CREATE TABLE " + TABLE_NAME_3 + "(" + FOOD + " TEXT," + DENSITY + " TEXT" + ")";
        //String CREATE_TABLE_3 =  "CREATE TABLE " + TABLE_NAME_3 + "(" + FOOD + " TEXT PRIMARY KEY," + MASS + " TEXT,"
          //      + VOL + " TEXT," + DESC_2 + " TEXT" + ")";

        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_2);
        db.execSQL(CREATE_TABLE_3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_3);
// Creating tables again
        onCreate(db);
    }

    //public abstract void addEntry(String food, String carbs, String date);

    public abstract int getCount();

    public abstract void clear();
}

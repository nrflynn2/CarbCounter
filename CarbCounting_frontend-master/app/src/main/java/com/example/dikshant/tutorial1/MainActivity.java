package com.example.dikshant.tutorial1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class MainActivity extends AppCompatActivity {

    // general things
    userDB userInfo;
    clarifaiHandler clarifai;
    nutritionalDB nutrInfo;
    densityDB densityInfo;
    nutrMeal meal;
    List<Concept> predicted;

    // main page
    TextView mainMessageText;
    Button picture;
    Button myData;
    Button nutritionalDatabase;
    Button support;

    public static final int PICK_IMAGE = 100;

    // picture selection page
    Button picSelection;
    Button postSelection;
    ImageView imageSelected;

    // clarification page
    TextView clarificationInstructions;
    Button changetextbtn2;
    Spinner foodSpinner;
    EditText volumeManual;
    TextView volInstr;

    // clarification with text
    EditText foodManual;

    // results page
    Button finalResultButton;
    Button return_to_main;
    TextView results;

    Spinner nutritionalSpinner;
    Spinner densitySpinner;

    // my data page
    TextView dataText;
    Button monthly;
    Button weekly;
    Button daily;

    // ndb page
    Button ndbSearch;
    Button ndbCategories;
    EditText ndbSearchText;
    TextView ndbSearchResults;
    Button ndbreturn_to_main;

    //private ClarifaiClient client;

    //ADDED FOR  DATABASE FUNCTIONALITY
    Map<String, List<String>> db; // IMPORTANT MEMBER : database

    List<String> keys; // IMPORTANT MEMBER : keys to database

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        createDatabases();

        try {
            clarifai = new clarifaiHandler();
        }
        catch (Exception e){
            Log.d("failure", "Clarifai could not initialize; most likely no internet connection");
        }
        introScreen();
    }

    private void createDatabases() {
        meal = new nutrMeal();

        userInfo = new userDB(getBaseContext());
        //Log.d("DB", String.valueOf(userInfo.getCount()));
        //userInfo.clear();
        //Log.d("DB", String.valueOf(userInfo.getCount()));
        //userInfo.getCount();
        Calendar cal = Calendar.getInstance();
        String date = String.valueOf(cal.DATE) + "_" + String.valueOf(cal.HOUR_OF_DAY)
                + "_" + String.valueOf(cal.MINUTE) + "_" + String.valueOf(cal.SECOND);
        //userInfo.addEntry("potato", "30", date);
        //Log.d("DB", String.valueOf(userInfo.getCount()));
        nutrInfo = new nutritionalDB(getBaseContext());
        //nutrInfo.clear();
        densityInfo = new densityDB(getBaseContext());

        try {
            SQLiteDatabase test = nutrInfo.getReadableDatabase();
            Log.d("nDB", "got readable");
            if (nutrInfo.getCount() < 8000){
                nutrInfo.readCSV();
            }
            //Log.d("nDB", String.valueOf(nutrInfo.getCount()));
        } catch (Exception e) {
            Log.d("nDB", "DNE");
            // database doesn't exist yet.
        }

        try {
            SQLiteDatabase test = densityInfo.getReadableDatabase();
            Log.d("dDB", "got readable");
            if (densityInfo.getCount() < 1){
                densityInfo.readCSV();
            }
            //densityInfo.addEntry("banana,raw", "1.14");
            //Log.d("dDB", String.valueOf(densityInfo.getCount()));
        } catch (Exception e) {
            Log.d("dDB", "DNE");
            // database doesn't exist yet.
        }

    }

    public void introScreen() {
        setContentView(R.layout.activity_main);
        //final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.potato_list, android.R.layout.simple_spinner_item);

        mainMessageText = (TextView) findViewById(R.id.message_1);
        mainMessageText.setVisibility(View.INVISIBLE);

        picture = (Button) findViewById(R.id.button_1);
        picture.setText("Select an Image");
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), PICK_IMAGE);
                imageSelection();
            }
        });

        myData = (Button) findViewById(R.id.my_data);
        myData.setText("My Data");
        myData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                myDataPage();
            }
        });

        nutritionalDatabase = (Button) findViewById(R.id.nutritional_database);
        nutritionalDatabase.setText("Nutritional" + "\n" + "Database");
        nutritionalDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                nutritionalDatabasePage();
            }
        });

        support = (Button) findViewById(R.id.support_faqs);
        support.setText("Support & FAQs");
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                test();
            }
        });
    }

    private void imageSelection() {
        setContentView(R.layout.image_selection);

        imageSelected = (ImageView) findViewById(R.id.image);

        picSelection = (Button) findViewById(R.id.select_image);
        picSelection.setText("Select from Gallery");
        picSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), PICK_IMAGE);
                new AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
                    @Override protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
                        startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), PICK_IMAGE);
                        return null;
                    }

                    @Override protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
                        //setBusy(false);
                        //postSelection.setVisibility(View.VISIBLE);
                    }
                }.execute();

            }
        }
        );

        postSelection = (Button) findViewById(R.id.post_image_selection);
        postSelection.setText("Proceed to Clarification");
        postSelection.setVisibility(View.INVISIBLE);
        postSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userClarification();
            }
        }
        );

        TextView proceed= (TextView) findViewById(R.id.proceed_instr);
        proceed.setText("Please wait for results after selecting");

        TextView a = (TextView) findViewById(R.id.textView6);
        a.setVisibility(View.INVISIBLE);

        Button selectionWithText = (Button) findViewById(R.id.userclar_text);
        selectionWithText.setOnClickListener(new View.OnClickListener() {
                                                               @Override
                                                               public void onClick(View v) {
                                                                   userClarificationwithText();
                                                               }
                                                           }
        );
    }

    private void nutritionalDatabasePage() {
        setContentView(R.layout.nutritional_database_page);

        //ndbCategories = (Button) findViewById(R.id.ndb_categories);
        ndbSearch = (Button) findViewById(R.id.ndb_search_button);
        Button ndbSearch2 = (Button) findViewById(R.id.ndb_search2);

        //ndbCategories.setVisibility(View.INVISIBLE);
        ndbSearch.setText("Search Carb Database");

        ndbSearchText = (EditText) findViewById(R.id.ndb_search);
        ndbSearchResults = (TextView) findViewById(R.id.ndb_search_results);
        ndbSearchResults.setVisibility(View.INVISIBLE);

        ndbreturn_to_main = returnToMainButton(R.id.ndb_return_button);

        ndbSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchString = ndbSearchText.getText().toString();
                Log.d("ndbsearch_edittext",searchString);

                // get cursors
                Cursor nutrition = nutrInfo.queryContainingRaw(searchString);

                // get maps
                final Map<String, Double> nutrMap = nutrInfo.getMapFromCursor(nutrition);

                // get keys
                List<String> nutrKeys = nutrInfo.getKeysFromCursor(nutrition);
                Log.d("ndbsearch_keys", "got keys");

                String resultsText = "";
                String resultsTextValues = "";

                int i = 0;
                while (i < 10 & i < nutrKeys.size()){
                    resultsText = resultsText + nutrKeys.get(i) + ": " + nutrMap.get(nutrKeys.get(i)) + " gram/100 gram" + "\n";
                    resultsTextValues = resultsTextValues + nutrMap.get(nutrKeys.get(i)) + "\n" + "\n";
                    i++;
                }

                Log.d("ndbsearch_resultstext", resultsText);

                ndbSearchResults.setText(resultsText);
                ndbSearchResults.setVisibility(View.VISIBLE);
                ndbSearchResults.setMovementMethod(new ScrollingMovementMethod());

                //Double volume = Double.valueOf(volstring); //Float.valueOf(volumeManual.getText().toString());
                //resultsPage(selectedFood, volume);
            }
        });

        ndbSearch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchString = ndbSearchText.getText().toString();
                Log.d("ndbsearch_edittext",searchString);

                // get cursors
                Cursor nutrition = densityInfo.queryContainingRaw(searchString);

                // get maps
                final Map<String, Double> densMap = densityInfo.getMapFromCursor(nutrition);

                // get keys
                List<String> densKeys = densityInfo.getKeysFromCursor(nutrition);
                Log.d("ndbsearch_keys", "got keys");

                String resultsText = "";
                String resultsTextValues = "";

                int i = 0;
                while (i < 10 & i < densKeys.size()){
                    resultsText = resultsText + densKeys.get(i) + ": " + densMap.get(densKeys.get(i)) + " gram/mL" + "\n";
                    resultsTextValues = resultsTextValues + densMap.get(densKeys.get(i)) + "\n" + "\n";
                    i++;
                }

                Log.d("ndbsearch_resultstext", resultsText);

                ndbSearchResults.setText(resultsText);
                ndbSearchResults.setVisibility(View.VISIBLE);
                ndbSearchResults.setMovementMethod(new ScrollingMovementMethod());

                //Double volume = Double.valueOf(volstring); //Float.valueOf(volumeManual.getText().toString());
                //resultsPage(selectedFood, volume);
            }
        });
    }

    private void myDataPage() {
        setContentView(R.layout.my_data_page);

        Log.d("db size", String.valueOf(userInfo.getCount()));
        Log.d("db content", userInfo.getTableAsString());
        //userInfo.exportCSV(MainActivity.this.getApplicationContext());

        monthly = (Button) findViewById(R.id.data_monthly);
        weekly = (Button) findViewById(R.id.data_weekly);
        daily = (Button) findViewById(R.id.data_daily);

        monthly.setText("Monthly Data");
        weekly.setText("Weekly Data");
        daily.setText("Daily Data");

        monthly.setVisibility(View.INVISIBLE);
        weekly.setVisibility(View.INVISIBLE);
        daily.setVisibility(View.INVISIBLE);

        Button mydata_return_to_main = returnToMainButton(R.id.mydata_return_button);
        GraphView graph = (GraphView) findViewById(R.id.graph);

        final graphviewAdapter graphy = new graphviewAdapter(graph);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
                //new DataPoint(0, 1)
        });
        //graph.addSeries(series);

        Cursor datesum = userInfo.queryDateSum();
        Log.d("datesum test", userInfo.printDateSumTable(datesum));
        Log.d("date test", String.valueOf(userInfo.getDatesforGraph(datesum)));
        Log.d("sum test", String.valueOf(userInfo.getSumsforGraph(datesum)));

        List<Date> dates = userInfo.getDatesforGraph(datesum);
        List<Double> sums = userInfo.getSumsforGraph(datesum);

        //graphy.graph(series);
        graphy.graphDate(dates, sums);

        Button export = (Button) findViewById(R.id.graph_export_button);
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                graphy.exportGraphImage(getApplicationContext());
            }
        });

    }

    private void userClarification() {

        //final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.potato_list, android.R.layout.simple_spinner_item);

        setContentView(R.layout.user_clarification);

        final Map<String, Double> volumes = setVolumeMap();

        volumeManual = (EditText) findViewById(R.id.volume_input);
        volumeManual.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        volumeManual.setVisibility(View.INVISIBLE);


        List<Concept> potentialFoods;
        volInstr = (TextView) findViewById(R.id.volumeInstruction);
        volInstr.setText("Input Volume of Food in mL");

        try {
            //potentialFoods = clarifai.returnPredictions();
            Log.d("predictions", "went into try loop");
            if (clarifai.hasPredictions()){
                Log.d("predictions", "went into if statement");
                if (clarifai.predictionsList() == null){
                    Log.d("adapter", "null predictions");
                }
                String[] predictionArray = clarifai.predictionsArray();
                List<String> predictionsArrayList = clarifai.predictionsList();
                Log.d("adapter", String.valueOf(predictionArray.length));
                //adapter.clear();
                final ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, predictionArray);

                Log.d("predictions", "created new adapter");
            }
        }
        catch (Exception e){
            Log.d("predictions", "prediction probably failed");
            userClarificationwithText();
        }

        finalResultButton = (Button) findViewById(R.id.finalResult);
        finalResultButton.setText("Show Final Result");

        foodSpinner = (Spinner) findViewById(R.id.spinner);

        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        String[] predictionArray = clarifai.predictionsArray();
        List<String> predictionsArrayList = clarifai.predictionsList();
        Log.d("adapter", String.valueOf(predictionArray.length));
        //adapter.clear();
        final ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, predictionArray);

        foodSpinner.setAdapter(adapter2);

        //final String selectedFood = foodSpinner.getSelectedItem().toString();
        //Log.d("Spinner", selectedFood);

        foodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //Object item = parent.getItemAtPosition(pos);
                final String selectedFood = foodSpinner.getItemAtPosition(pos).toString();
                Log.d("Spinner", selectedFood);
                //Log.d("results page", "trying to create rpage");

                if (volumes.containsKey(selectedFood)){
                    volumeManual.setText(String.valueOf(volumes.get(selectedFood)));
                }
                else {
                    volumeManual.setText("Please Enter Volume of Food in mL");
                }

                volumeManual.setVisibility(View.VISIBLE);


                finalResultButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //rpage.setCarbs("30");
                        //rpage.setFood(selectedFood);

                        Log.d("results page", "created rpage");
                        //setContentView(R.layout.final_result);

                        String volstring = (String) volumeManual.getText().toString();
                        Log.d("edittext",volstring);

                        Double volume = Double.valueOf(volstring); //Float.valueOf(volumeManual.getText().toString());

                        resultsPage(selectedFood, volume);
                    }
                });
            }
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Spinner", "nothing selected");
            }
        });
    }

    private void userClarificationwithText() {

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.potato_list, android.R.layout.simple_spinner_item);

        setContentView(R.layout.user_clarification_with_text);

        volumeManual = (EditText) findViewById(R.id.volume_input_withtext);
        volumeManual.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);

        List<Concept> potentialFoods;

        foodManual = (EditText) findViewById(R.id.selection_edittext);

        finalResultButton = (Button) findViewById(R.id.finalResult_withtext);
        finalResultButton.setText("Show Final Result");
        finalResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //rpage.setCarbs("30");
                //rpage.setFood(selectedFood);

                Log.d("results page", "created rpage");
                //setContentView(R.layout.final_result);

                String selectedFood = foodManual.getText().toString();

                String volstring = volumeManual.getText().toString();
                Log.d("edittext",volstring);

                Double volume = Double.valueOf(volstring); //Float.valueOf(volumeManual.getText().toString());

                resultsPage(selectedFood, volume);
            }
        });

        Button return_to_main = returnToMainButton(R.id.userclar_return);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    private void userClarificationwithText(String food, Double volume) {

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.potato_list, android.R.layout.simple_spinner_item);

        setContentView(R.layout.user_clarification_with_text);

        volumeManual = (EditText) findViewById(R.id.volume_input_withtext);
        volumeManual.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);

        List<Concept> potentialFoods;

        foodManual = (EditText) findViewById(R.id.selection_edittext);

        foodManual.setText(food);
        volumeManual.setText(String.valueOf(volume));

        finalResultButton = (Button) findViewById(R.id.finalResult_withtext);
        finalResultButton.setText("Show Final Result");
        finalResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //rpage.setCarbs("30");
                //rpage.setFood(selectedFood);

                Log.d("results page", "created rpage");
                //setContentView(R.layout.final_result);

                String selectedFood = foodManual.getText().toString();

                String volstring = volumeManual.getText().toString();
                Log.d("edittext",volstring);

                Double volume = Double.valueOf(volstring); //Float.valueOf(volumeManual.getText().toString());

                resultsPage(selectedFood, volume);
            }
        });

        Button return_to_main = returnToMainButton(R.id.userclar_return);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    private void resultsPage2Spinners(final String selectedFood, final Double volume) {
        setContentView(R.layout.final_result);
        results = (TextView) findViewById(R.id.results_text);
        results.setVisibility(View.INVISIBLE);
        //results.setText(selectedFood);

        final Button resultsAdd = (Button) findViewById(R.id.results_add);
        final Button resultsComplete = (Button) findViewById(R.id.results_complete);

        resultsAdd.setVisibility(View.INVISIBLE);
        resultsComplete.setVisibility(View.INVISIBLE);

        // set spinner
        nutritionalSpinner = (Spinner) findViewById(R.id.nutrSpinner);
        densitySpinner = (Spinner) findViewById(R.id.densitySpinner);
        densitySpinner.setVisibility(View.INVISIBLE);

        // get cursors
        Cursor nutrition = nutrInfo.queryContainingRaw(selectedFood);
        Cursor density = densityInfo.queryContainingRaw(selectedFood);

        // get maps
        final Map<String, Double> nutrMap = nutrInfo.getMapFromCursor(nutrition);
        final Map<String, Double> densityMap = densityInfo.getMapFromCursor(density);

        // get keys
        List<String> nutrKeys = nutrInfo.getKeysFromCursor(nutrition);
        List<String> densityKeys = densityInfo.getKeysFromCursor(density);

        final String nutrSelect = "Select a food for grams Carb/grams of Food";
        final String densSelect = "Select a food for grams of Food/volume of Food";
        nutrKeys.add(0, nutrSelect);
        densityKeys.add(0, densSelect);

        Log.d("results size", String.valueOf(nutrKeys.size()));
        Log.d("results size", String.valueOf(densityKeys.size()));

        String[] nutrArr = new String[nutrKeys.size()];
        nutrArr = nutrKeys.toArray(nutrArr);

        String[] densArr = new String[densityKeys.size()];
        densArr = densityKeys.toArray(densArr);

        Log.d("results size", String.valueOf(nutrArr.length));
        Log.d("results size", String.valueOf(densArr.length));


        // set adapters

        final ArrayAdapter<CharSequence> nutrAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, nutrArr);
        nutritionalSpinner.setAdapter(nutrAdapter);
        final ArrayAdapter<CharSequence> densAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, densArr);
        densitySpinner.setAdapter(densAdapter);

        //final String selectedNutr;

        nutritionalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //Object item = parent.getItemAtPosition(pos);
                final String selectedNutr = nutritionalSpinner.getItemAtPosition(pos).toString();
                Log.d("nutrSpinner", selectedNutr);
                if (selectedNutr != nutrSelect){
                    densitySpinner.setVisibility(View.VISIBLE);
                    densitySpinner.setSelection(0);
                    results.setVisibility(View.INVISIBLE);
                }

                //Log.d("results page", "trying to create rpage");
                densitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        Double nutrdensity = nutrMap.get(selectedNutr);

                        //Object item = parent.getItemAtPosition(pos);
                        final String selectedDens = densitySpinner.getItemAtPosition(pos).toString();
                        Log.d("nutrSpinner", selectedDens);

                        if (selectedDens != densSelect){
                            Double fooddensity = densityMap.get(selectedDens);

                            results.setVisibility(View.VISIBLE);
                            results.setText(String.valueOf(nutrdensity*fooddensity*volume) + " grams of Carb");
                            //meal.addMeal(selectedFood, carbFactor, volume);

                            final Double carbFactor = nutrdensity*fooddensity;

                            resultsAdd.setVisibility(View.VISIBLE);
                            resultsComplete.setVisibility(View.VISIBLE);

                            resultsAdd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    meal.addMeal(selectedFood, carbFactor, volume);
                                    imageSelection();
                                }
                            });
                            resultsComplete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    meal.addMeal(selectedFood, carbFactor, volume);
                                    Log.d("completing meal", "stepping into function");
                                    mealComplete();

                                }
                            });
                        }

                        //Log.d("results page", "trying to create rpage");

                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.d("Spinner", "nothing selected");
                    }
                });
            }
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Spinner", "nothing selected");
            }
        });


        Log.d("Flow", "Final Results Page");

        return_to_main = returnToMainButton(R.id.return_to_start_button);
    }

    private void resultsPage(final String selectedFood, final Double volume) {
        setContentView(R.layout.final_result);
        results = (TextView) findViewById(R.id.results_text);
        results.setVisibility(View.INVISIBLE);
        //results.setText(selectedFood);

        final Button incorrect = (Button) findViewById(R.id.wrong_food);
        final Button resultsAdd = (Button) findViewById(R.id.results_add);
        final Button resultsComplete = (Button) findViewById(R.id.results_complete);

        incorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.activity_main);
                userClarificationwithText(selectedFood, volume);
            }
        });

        resultsAdd.setVisibility(View.INVISIBLE);
        resultsComplete.setVisibility(View.INVISIBLE);

        // set spinner
        nutritionalSpinner = (Spinner) findViewById(R.id.nutrSpinner);
        densitySpinner = (Spinner) findViewById(R.id.densitySpinner);
        densitySpinner.setVisibility(View.INVISIBLE);

        // get cursors
        Cursor nutrition = nutrInfo.queryContainingRaw(selectedFood);
        Cursor density = densityInfo.queryContainingRaw(selectedFood);

        // get maps
        final Map<String, Double> nutrMap = nutrInfo.getMapFromCursor(nutrition);
        final Map<String, Double> densityMap = densityInfo.getMapFromCursor(density);

        // get keys
        List<String> nutrKeys = nutrInfo.getKeysFromCursor(nutrition);
        List<String> densityKeys = densityInfo.getKeysFromCursor(density);

        final String nutrSelect = "Select a food for grams Carb/grams of Food";
        final String densSelect = "Select a food for grams of Food/volume of Food";
        nutrKeys.add(0, nutrSelect);
        densityKeys.add(0, densSelect);

        Log.d("results size", String.valueOf(nutrKeys.size()));
        Log.d("results size", String.valueOf(densityKeys.size()));

        String[] nutrArr = new String[nutrKeys.size()];
        nutrArr = nutrKeys.toArray(nutrArr);

        String[] densArr = new String[densityKeys.size()];
        densArr = densityKeys.toArray(densArr);

        Log.d("results size", String.valueOf(nutrArr.length));
        Log.d("results size", String.valueOf(densArr.length));

        final String selectedDens = densArr[1];
        Log.d("1st item in dens array", selectedDens);


        // set adapters

        final ArrayAdapter<CharSequence> nutrAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, nutrArr);
        nutritionalSpinner.setAdapter(nutrAdapter);
        final ArrayAdapter<CharSequence> densAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, densArr);
        densitySpinner.setAdapter(densAdapter);

        //final String selectedNutr;
        nutritionalSpinner.setSelection(1);

        nutritionalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //Object item = parent.getItemAtPosition(pos);
                final String selectedNutr = nutritionalSpinner.getItemAtPosition(pos).toString();
                Log.d("nutrSpinner", selectedNutr);
                if (selectedNutr != nutrSelect){
                    Double nutrdensity = nutrMap.get(selectedNutr)*0.01;
                    Double fooddensity = densityMap.get(selectedDens);

                    Log.d("meal size", String.valueOf(meal.getSum()));

                    results.setVisibility(View.VISIBLE);
                    results.setText(String.valueOf(meal.getSum() + nutrdensity*fooddensity*volume) + " grams of Carb");
                    //meal.addMeal(selectedFood, carbFactor, volume);

                    final Double carbFactor = nutrdensity*fooddensity;

                    resultsAdd.setVisibility(View.VISIBLE);
                    resultsComplete.setVisibility(View.VISIBLE);

                    resultsAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //setContentView(R.layout.activity_main);
                            meal.addMeal(selectedFood, carbFactor, volume);
                            imageSelection();
                        }
                    });
                    resultsComplete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //setContentView(R.layout.activity_main);
                            meal.addMeal(selectedFood, carbFactor, volume);
                            Log.d("completing meal", "stepping into function");
                            mealComplete();

                        }
                    });
                }

                //Log.d("results page", "trying to create rpage");

            }
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Spinner", "nothing selected");
            }
        });

        //Log.d("results", nutrKeys.get(1));
        //Log.d("results", densityKeys.get(1));

        //Log.d("results", String.valueOf(nutrMap.get(nutrKeys.get(1))));
        //Log.d("results", String.valueOf(densityMap.get(densityKeys.get(1))));

        Log.d("Flow", "Final Results Page");

        return_to_main = returnToMainButton(R.id.return_to_start_button);
        return_to_main.setText("Cancel");
    }

    private void mealComplete(){
        setContentView(R.layout.complete_meal);
        Log.d("meal complete", "entering completion");

        final EditText mealEntry = (EditText) findViewById(R.id.name_of_meal);

        TextView space = (TextView) findViewById(R.id.completeMeal_emptytext);
        space.setVisibility(View.INVISIBLE);

        Button complete = (Button) findViewById(R.id.entermeal_name);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.activity_main);
                String mealName = mealEntry.getText().toString();
                meal.setName(mealName);
                userInfo.addEntry(meal, getDate());
                meal.clear();
                setContentView(R.layout.activity_main);
                //userInfo.exportCSV2(MainActivity.this.getApplicationContext());
                Log.d("meal complete", "returning to intro");
                introScreen();
            }
        });
    }

    public Button returnToMainButton(int identity) {
        Button returning = (Button) findViewById(identity);
        returning.setText("Return to Start Page");
        returning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_main);
                introScreen();
            }
        });
        return returning;
    }

    //@OnClick(R.id.fab)
    void pickImage() {
        startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), PICK_IMAGE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch(requestCode) {
            case PICK_IMAGE:
                final byte[] imageBytes = retrieveSelectedImage(this, data);
                if (imageBytes != null) {
                    //onImagePicked(imageBytes);
                    Log.d("prediction", "attempting");
                    //clarifai.onImagePicked(imageBytes);
                    onImagePicked(imageBytes);
                    try {
                        Log.d("image display", "attempting to set");
                        //imageSelected.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                    }
                    catch (Exception e){
                        Log.d("image display", "failed");
                    }
                    //
                    //List<Concept> predictions = clarifai.getPredictions(imageBytes);
                    //Log.d("predictions"+, predictions.get(0).toString());
                    Log.d("predictions", "predicted");
                }
                break;
        }
    }

    public void onImagePicked(@NonNull final byte[] imageBytes) {
        Log.d("image picked", "running predictor");

        new AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
            @Override protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
                // The default Clarifai model that identifies concepts in images
                //final ConceptModel generalModel = App.get().clarifaiClient().getDefaultModels().foodModel();

                // Use this model to predict, with the image that the user just selected as the input

                Log.d("predictions", "something happened");
                return clarifai.foodModel.predict()
                        .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
                        .executeSync();
            }

            @Override protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
                //setBusy(false);
                if (!response.isSuccessful()) {
                    return;
                }
                final List<ClarifaiOutput<Concept>> predictions = response.get();
                if (predictions.isEmpty()) {
                    Log.d("predictions", "none");
                    return;
                }
                //predictionsFlag = true;
                predicted = predictions.get(0).data();
                //clarifai.predictionsList = predictions.get(0).data();
                clarifai.setPredictions(predicted);
                Log.d("predictions", String.valueOf(predicted.get(0).name()));
                Log.d("predictions", "set flag");
                String food = predicted.get(0).name();
                resultsPage(food, getVolume(food));

            }
        }.execute();
    }

    @Nullable
    public static byte[] retrieveSelectedImage(@NonNull Context context, @NonNull Intent data) {
        InputStream inStream = null;
        Bitmap bitmap = null;
        try {
            inStream = context.getContentResolver().openInputStream(data.getData());
            bitmap = BitmapFactory.decodeStream(inStream);
            final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            return outStream.toByteArray();
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ignored) {
                }
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    public String getDate(){
        Calendar cal = Calendar.getInstance();
        String date = String.valueOf(cal.DATE) + "_" + String.valueOf(cal.HOUR_OF_DAY)
                + "_" + String.valueOf(cal.MINUTE) + "_" + String.valueOf(cal.SECOND);

        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
        //Date newDate = new Date();

        //Log.d("date format", newDate.toString());

        return currentDateTimeString;
    }

    public void test(){
        //userInfo.exportCSV2(MainActivity.this.getApplicationContext());
        /*(Log.d("test", "test");
        Cursor datesum = userInfo.queryDateSum();
        Log.d("datesum test", userInfo.printDateSumTable(datesum));
        Log.d("date test", String.valueOf(userInfo.getDatesforGraph(datesum)));
        Log.d("sum test", String.valueOf(userInfo.getSumsforGraph(datesum)));*/
    }

    public Map<String, Double> setVolumeMap(){
        Map<String, Double> volumes = new HashMap<String, Double>();

        volumes.put("potato", 350.0);
        volumes.put("banana", 280.0);
        volumes.put("apple", 370.0);
        volumes.put("carrot", 113.0);

        return volumes;
    }

    public void getCarbQuick(String food){

        // get cursors
        Cursor nutrition = nutrInfo.queryContainingRaw(food);
        Cursor density = densityInfo.queryContainingRaw(food);

        if (nutrition.getCount() < 1 || density.getCount() < 1){
            userClarificationwithText(food, 0.0);
        }

        // get maps
        final Map<String, Double> nutrMap = nutrInfo.getMapFromCursor(nutrition);
        final Map<String, Double> densityMap = densityInfo.getMapFromCursor(density);

        Double carbFactor = nutrMap.get(food)*densityMap.get(food);
    }

    public double getVolume(String food){


        Map<String, Double> volumes = setVolumeMap();

        //replace with actual volume calculation

        double volume_final = 0;

        if (volumes.containsKey(food)){
            volume_final = volumes.get(food);
        }
        else {
            userClarificationwithText(food, 0.0);
        }

        return volume_final;

    }
}

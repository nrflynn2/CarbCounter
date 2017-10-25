package com.example.dikshant.tutorial1;

/**
 * Created by Dikshant on 2/14/2017.
 */

import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ViewSwitcher;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;


//more clarifai stuff
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class clarifaiHandler extends MainActivity {

    @Nullable
    private ClarifaiClient client;
    public ConceptModel foodModel;
    public List<Concept> predictionsList;

    private boolean predictionsFlag;

    public clarifaiHandler(){
        //client = new ClarifaiBuilder("qeNitO9lCdNO7k7UixA6yUXKIIX3-MolxrXUL4Oq", "zOxPlyS5BfqFCp_CVW1Y2ZkZyNW81IMQ6sAOkodR")
                //.client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
         //       .buildSync(); // or use .build() to get a Future<ClarifaiClient>
        Log.d("Clar", "start");

        predictionsFlag = false;

        client = new ClarifaiBuilder("qeNitO9lCdNO7k7UixA6yUXKIIX3-MolxrXUL4Oq", "zOxPlyS5BfqFCp_CVW1Y2ZkZyNW81IMQ6sAOkodR")
                // Optionally customize HTTP client via a custom OkHttp instance
                .client(new OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS) // Increase timeout for poor mobile networks

                        // Log all incoming and outgoing data
                        // NOTE: You will not want to use the BODY log-level in production, as it will leak your API request details
                        // to the (publicly-viewable) Android log
                        .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                            @Override public void log(String logString) {
                                Timber.e(logString);
                            }
                        }).setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build()
                )
                .buildSync();
        Log.d("Clar", "built");

                // if a Client is registered as a default instance, it will be used
                // automatically, without the user having to keep it around as a field.
                // This can be omitted if you want to manually manage your instance
                //.registerAsDefaultInstance();

        foodModel = client.getDefaultModels().foodModel();
        Log.d("Clar", "model");
    }

    public List<Concept> getPredictions(@NonNull final byte[] imageBytes){

        Log.d("predictions", "inside method");

        ClarifaiResponse<List<ClarifaiOutput<Concept>>> response =  foodModel.predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
                .executeSync();

        Log.d("predictions", "got response");
        List<ClarifaiOutput<Concept>> predictions = response.get();
        Log.d("predictions", "got list");
        return predictions.get(0).data();
    }

    public void onImagePicked(@NonNull final byte[] imageBytes) {
        Log.d("image picked", "running predictor");

        new AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
            @Override protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
                // The default Clarifai model that identifies concepts in images
                //final ConceptModel generalModel = App.get().clarifaiClient().getDefaultModels().foodModel();

                // Use this model to predict, with the image that the user just selected as the input

                Log.d("predictions", "something happened");
                return foodModel.predict()
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
                predictionsFlag = true;
                Log.d("predictions", "set flag");
                predictionsList = predictions.get(0).data();

            }
        }.execute();
    }

    public List<Concept> returnPredictions(){
        return predictionsList;
    }

    public String[] predictionsArray(){
        if (predictionsList.size() == 0 || (predictionsFlag == false)){
            return null;
        }
        int size = predictionsList.size();

        String[] predictionArray = new String[size];
        for (int i = 0; i < size; i++){
            predictionArray[i] = predictionsList.get(i).name();
        }

        return predictionArray;
    }

    public List<String> predictionsList(){

        if (predictionsList.size() == 0 || (predictionsFlag == false)){
            return null;
        }

        List<String> predictionArrayList = new ArrayList<String>();

        int size = predictionsList.size();

        for (int i = 0; i < size; i++){
            predictionArrayList.add(predictionsList.get(i).name());
        }

        return predictionArrayList;
    }

    public boolean hasPredictions(){
        return predictionsFlag;
    }

    public void setPredictions(List<Concept> list){
        predictionsList = list;
    }

    /*public ClarifaiResponse<List<ClarifaiOutput<Concept>>>> predict(@NonNull final byte[] imageBytes){
        return foodModel.predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)));
    }*/

    /*private void setBusy(final boolean busy) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                switcher.setDisplayedChild(busy ? 1 : 0);
                imageView.setVisibility(busy ? GONE : VISIBLE);
                fab.setEnabled(!busy);
            }
        });
    }*/
}

package com.example.dikshant.tutorial1;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Environment;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by Dikshant on 4/4/2017.
 */

public class graphviewAdapter {

    GraphView graph;

    public graphviewAdapter(GraphView graph){
        this.graph = graph;
    }

    public void graph(List<Double> X, List<Double> Y){
        int length = X.size();
        DataPoint[] values = new DataPoint[length];

        for (int i = 0; i < length; i++){
            values[i] = new DataPoint(X.get(i), Y.get(i));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(values);
        graph.addSeries(series);
    }

    public void graph(LineGraphSeries<DataPoint> series){
        graph.addSeries(series);
    }

    public void graphDate(List<Date> X, List<Double> Y){
        int length = X.size();
        DataPoint[] values = new DataPoint[length];

        for (int i = 0; i < length; i++){
            values[i] = new DataPoint(X.get(i).getDate(), Y.get(i));
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(values);
        graph.addSeries(series);

        // set date label formatter
        //graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        //graph.getGridLabelRenderer().setNumHorizontalLabels(length); // only 4 because of the space

// set manual x bounds to have nice steps
        graph.getViewport().setMinX(10);
        graph.getViewport().setMaxX(15);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(600);
        graph.getViewport().setYAxisBoundsManual(true);

        //String[] stockArr = new String[X.size()];
        //stockArr = X.toArray(stockArr);
        //graph.getGridLabelRenderer().

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Date (April)");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Carbohydrates Consumed per Day");
        graph.setTitle("Carbohydrate Consumption History");
    }

    public void exportGraphImage(Context context){
        String outputDir = saveToInternalStorage(exportGraphBitmap(), context);
        Log.d("export image", "exported");
    }

    public Bitmap exportGraphBitmap(){
        Bitmap bitmap;

        graph.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(graph.getDrawingCache());
        graph.setDrawingCacheEnabled(false);

        return bitmap;
    }

    private String saveToInternalStorage(Bitmap bitmapImage, Context context){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        directory = new File(Environment.getExternalStorageDirectory(), "Download");
        // Create imageDir
        if (!directory.exists())
        {
            directory.mkdirs();
        }
        File mypath=new File(directory,"activity.jpg");
        //File mypath = new File(directory,"mydir");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.d("export image", "tried");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("export image", "failed");
        } finally {
            try {
                fos.close();
                Log.d("export image", "closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}

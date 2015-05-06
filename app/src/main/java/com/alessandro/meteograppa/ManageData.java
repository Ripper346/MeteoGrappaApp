package com.alessandro.meteograppa;

import android.os.AsyncTask;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Download and set last weather data.
 *
 * @author Alessandro
 */
public class ManageData extends AsyncTask {

    private MeteoGrappa activity;
    private LinkedTreeMap<String, Object> data;
    private String time;
    private float arrowWindRotation;

    @Override
    protected Object doInBackground(Object... arg0) {
        activity = (MeteoGrappa) arg0[0];

        // Download JSON
        ResourceDownloader resource = new ResourceDownloader(activity.URL + "lastData.json");
        String json = resource.getPage();

        if (json != null) {
            // Extract data from JSON
            Gson gson = new Gson();
            ArrayList list = gson.fromJson(json, ArrayList.class);
            ArrayList<String> wind = ((LinkedTreeMap<String, ArrayList>) list.get(0)).get("directions");
            data = ((LinkedTreeMap<String, Object>) list.get(1));

            // Format date
            time = "";
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss").parse((String) data.get("date"));
                time = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Calculate rotation of the wind direction arrow
            int index = -1;
            do {
                index++;
            } while (!wind.get(index).equals((String) data.get("windDirection")));
            arrowWindRotation = 22.5f * index;

            // Set parameters on the container
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView option;
                    option = (TextView) activity.findViewById(R.id.datetime_value);
                    option.setText(time);
                    option = (TextView) activity.findViewById(R.id.condition_value);
                    option.setText((String) data.get("condition"));
                    option = (TextView) activity.findViewById(R.id.temperature_value);
                    option.setText((double) data.get("temperature") + "°C");
                    option = (TextView) activity.findViewById(R.id.feel_temperature_value);
                    option.setText((double) data.get("feelTemperature") + "°C");
                    option = (TextView) activity.findViewById(R.id.humidity_value);
                    option.setText(((Double) data.get("humidity")).intValue() + "%");
                    option = (TextView) activity.findViewById(R.id.wind_speed_value);
                    option.setText((double) data.get("windSpeed") + " km/h");
                    option = (TextView) activity.findViewById(R.id.wind_direction_value);
                    option.setText((String) data.get("windDirection"));
                    option = (TextView) activity.findViewById(R.id.pressure_value);
                    option.setText((double) data.get("pressure") + " hPa");
                    option = (TextView) activity.findViewById(R.id.solar_value);
                    option.setText(((Double) data.get("solar")).intValue() + " W/m² " + ((Double) data.get("solarPercentage")).intValue() + "%");
                    option = (TextView) activity.findViewById(R.id.uv_value);
                    option.setText((double) data.get("uv") + "");
                    option = (TextView) activity.findViewById(R.id.dew_temperature_value);
                    option.setText((double) data.get("dewTemperature") + "°C");
                    option = (TextView) activity.findViewById(R.id.rain_value);
                    option.setText((double) data.get("rain") + " mm/h");
                    option = (TextView) activity.findViewById(R.id.snow_value);
                    option.setText(((Double) data.get("snow")).intValue() + " cm");
                    ImageView windDirectionImage = (ImageView) activity.findViewById(R.id.wind_direction_image);
                    windDirectionImage.setRotation(arrowWindRotation);
                }
            });
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.setGraphUrl("");
                    WebView graphs = (WebView) activity.findViewById(R.id.graphBrowser);
                    graphs.loadData("", "", "");
                    Toast.makeText(activity, "Tentativo di connessione al server fallito.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return null;
    }
}

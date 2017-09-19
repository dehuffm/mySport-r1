package rottiedog.com.mysport;

/**
 * Created by dehuffm on 8/8/2017.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherReport extends AppCompatActivity {
    //private WebServiceHandler ws=null;
    private final OkHttpClient mOkHttpClient;
    private weatherByPeriod[] weather;
    int periodCnt = 0;
    ListView lv = null;
    public WeatherReport () {
        mOkHttpClient = new OkHttpClient();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_report);
        lv = (ListView) findViewById(R.id.weather_list);
        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("latitude", -1.0);
        double longitude = intent.getDoubleExtra("longitude", -1.0);
        //ws = new WebServiceHandler();
        final String url = "http://api.wunderground.com/api/2f8e6d8668d6b730/forecast/q/" +
                lat + "," + longitude + ".json";
        //   ws.ServiceRequest(url);
        //   weatherByPeriod[] response = ws.ServiceResponse();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("okhttp", "failed!");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();

                for (int i = 0; i < responseHeaders.size(); i++) {
                    Log.d("okhttp", responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                String body = response.body().string();
                LoadArray(body);
                Log.d("okhttp", body);
            }
        });
    }
    public void LoadArray(String body) {
        try {
            JSONObject jsonObj = new JSONObject(body);
            JSONObject forecast = jsonObj.getJSONObject("forecast");
            JSONObject forecast_txt = forecast.getJSONObject("txt_forecast");
            // Getting JSON Array node
            JSONArray week = forecast_txt.getJSONArray("forecastday");
            periodCnt = week.length();
            weather = new weatherByPeriod[week.length()];
            // looping through All Contacts
            for (int i = 0; i < periodCnt; i++) {
                JSONObject c = week.getJSONObject(i);
                weather[i] = new weatherByPeriod(c.getString("title"), c.getString("fcttext"));
            }
            displayWeatherReport();
        } catch (final JSONException e) {
            String err = "Json parsing error: " + e.getMessage();
            Log.d("okhttp", err);
        }
    }
    public void displayWeatherReport() {

        List<Map<String, String>> data = new ArrayList<>();

        for (int cnt = 0; cnt < periodCnt; ++cnt) {
            Map<String, String> row = new HashMap<>(2);
            row.put("Day", weather[cnt].getPeriod());
            row.put("Weather", weather[cnt].getReport());
            data.add(row);
        }
        final SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[]{"Day", "Weather"},
                new int[]{android.R.id.text1, android.R.id.text2});

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //stuff that updates ui
                lv.setAdapter(adapter);
            }
        });


    }
}


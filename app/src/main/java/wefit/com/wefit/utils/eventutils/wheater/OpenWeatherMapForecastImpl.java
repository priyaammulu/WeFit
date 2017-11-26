package wefit.com.wefit.utils.eventutils.wheater;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;

/**
 * Created by gioacchino on 23/11/2017.
 */

public class OpenWeatherMapForecastImpl implements WeatherForecast {


    private static final String REQUEST_SCHEME = "http";
    private static final String BASE_URL = "api.openweathermap.org";

    /**
     * GET parameters for OpenWeatherMap
     */
    private static final String API_KEY_PARAMETER = "APPID";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";

    /**
     * Path
     */
    private static final String API_BASE_PATH = "data";
    private static final String API_VERSION = "2.5";
    private static final String API_OPERATION = "forecast";

    /**
     * Auth key for OpenWeatherAPI
     */
    private String apiKey;


    @Override
    public Flowable<Weather> getForecast(Event event) {
        return this.getForecast(event.getEventLocation(), event.getEventDate());
    }

    @Override
    public Flowable<Weather> getForecast(final EventLocation location, final long date) {


        URL associateURL = null;

        // creation of the HTTP request
        Uri requestURI = new Uri.Builder()
                .scheme(REQUEST_SCHEME)
                .authority(BASE_URL)
                .appendPath(API_BASE_PATH)
                .appendPath(API_VERSION)
                .appendPath(API_OPERATION)
                .appendQueryParameter(API_KEY_PARAMETER, apiKey)
                .appendQueryParameter(LATITUDE, String.valueOf(location.getLatitude()))
                .appendQueryParameter(LONGITUDE, String.valueOf(location.getLongitude()))
                .build();

        try {
            associateURL = new URL(requestURI.toString());
            Log.i("created_url", associateURL.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        final URL requestURL = associateURL;


        return Flowable.create(new FlowableOnSubscribe<Weather>() {
            @Override
            public void subscribe(final FlowableEmitter<Weather> flowableEmitter) throws Exception {

                new WeatherForecastDownloader(flowableEmitter, date).execute(requestURL);

            }

        }, BackpressureStrategy.BUFFER);
    }

    public OpenWeatherMapForecastImpl(String apiKey) {
        this.apiKey = apiKey;
    }

    private static class WeatherForecastDownloader extends AsyncTask<URL, Void, String> {

        private final long secondsDate;
        /**
         * Used to send the callback
         */
        private final FlowableEmitter<Weather> flowableEmitter;

        @Override
        protected String doInBackground(URL... urls) {

            StringBuilder response = new StringBuilder();

            try {


                //Prepare the URL and the connection
                HttpURLConnection conn = (HttpURLConnection) urls[0].openConnection();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    //Get the Stream reader ready
                    BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);

                    //Loop through the return data and copy it over to the response object to be processed
                    String line;

                    while ((line = input.readLine()) != null) {
                        response.append(line);
                    }

                    input.close();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return response.toString();

        }

        @Override
        protected void onPostExecute(String rawJSON) {
            super.onPostExecute(rawJSON);

            // TODO translate raw json to readable result
            Log.i("downloaded", rawJSON);

            try {

                // this map will contain the forecast for each hour
                @SuppressLint("UseSparseArrays") // useless, because API 15 doesn't support it
                        Map<Long, Integer> digestForecasts = new HashMap<>();

                // wrap the data in a json object
                JSONObject wrappedObject = new JSONObject(rawJSON);
                JSONArray forecasts = wrappedObject.getJSONArray("list");

                // iterate over the forecasts
                for (int i = 0; i < forecasts.length(); i++) {

                    JSONObject dayForecast = forecasts.getJSONObject(i);

                    long date = dayForecast.getLong("dt");
                    int weatherID = dayForecast.getJSONArray("weather").getJSONObject(0).getInt("id");

                    digestForecasts.put(date, weatherID);

                }

                List<Long> listDates = new ArrayList<>(digestForecasts.keySet());
                Collections.sort(listDates);

                // get the closest forecast (chosen by date)
                int forecastID = digestForecasts.get(this.searchClosestUnixDate(secondsDate, listDates));

                // need just the first number of the ID
                forecastID = Integer.parseInt(String.valueOf(forecastID).substring(0,1));

                Weather forecastCondition;

                switch (forecastID) {

                    case 2:
                    case 3:
                    case 5:
                        forecastCondition = Weather.RAINY;
                        break;
                    case 6:
                        forecastCondition = Weather.SNOWY;
                        break;
                    case 8:
                        forecastCondition = Weather.SUNNY;
                        break;
                    default:
                        forecastCondition = Weather.DISASTROUS;
                        break;

                }

                flowableEmitter.onNext(forecastCondition);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        public WeatherForecastDownloader(FlowableEmitter<Weather> flowableEmitter, long secondsDate) {
            this.flowableEmitter = flowableEmitter;
            this.secondsDate = secondsDate;
        }

        /**
         * Retrieve the date (Unix format) closest to the specified one
         *
         * @param pivotDate we want the closest to this one
         * @param listDates list of available dates in seconds
         * @return closest date in listDates
         */
        public long searchClosestUnixDate(long pivotDate, List<Long> listDates) {

            if (pivotDate < listDates.get(0)) {
                return listDates.get(0);
            }
            if (pivotDate > listDates.get(listDates.size() - 1)) {
                return listDates.get(listDates.size() - 1);
            }

            int lo = 0;
            int hi = listDates.size() - 1;

            while (lo <= hi) {
                int mid = (hi + lo) / 2;

                if (pivotDate < listDates.get(mid)) {
                    hi = mid - 1;
                } else if (pivotDate > listDates.get(mid)) {
                    lo = mid + 1;
                } else {
                    return listDates.get(mid);
                }
            }
            // lo == hi + 1
            return (listDates.get(lo) - pivotDate) < (pivotDate - listDates.get(hi)) ? listDates.get(lo) : listDates.get(hi);
        }
    }

}

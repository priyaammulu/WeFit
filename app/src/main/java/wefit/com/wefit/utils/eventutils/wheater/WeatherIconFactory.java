package wefit.com.wefit.utils.eventutils.wheater;

import java.util.HashMap;
import java.util.Map;

import wefit.com.wefit.R;

/**
 * Created by gioacchino on 24/11/2017.
 */

public class WeatherIconFactory {

    private Map<Weather, Integer> availableWeathers = new HashMap<>();

    private static final WeatherIconFactory ourInstance = new WeatherIconFactory();

    public static WeatherIconFactory getInstance() {
        return ourInstance;
    }

    private WeatherIconFactory() {

        // TODO change the icons
        this.availableWeathers.put(Weather.SUNNY, R.drawable.ic_gym_cardio);
        this.availableWeathers.put(Weather.RAINY, R.drawable.rain_ico);
        this.availableWeathers.put(Weather.SNOWY, R.drawable.ic_gym_weightlifting);
        this.availableWeathers.put(Weather.DISASTROUS, R.drawable.ic_gym_weightlifting);

    }

    /**
     * Retrieve the weather icon knowing the ID
     */
    public int getWeatherIconByID(Weather weatherID) {
        return this.availableWeathers.get(weatherID);
    }


}

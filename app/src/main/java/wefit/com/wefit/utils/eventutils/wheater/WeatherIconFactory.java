package wefit.com.wefit.utils.eventutils.wheater;

import java.util.HashMap;
import java.util.Map;

import wefit.com.wefit.R;

/**
 * Created by gioacchino on 24/11/2017.
 * This Factory is responsible to manage the access to Weather Icon
 * SINGLETON OBJECT
 */

public class WeatherIconFactory {

    private Map<Weather, Integer> availableWeathers = new HashMap<>();

    private static final WeatherIconFactory ourInstance = new WeatherIconFactory();

    public static WeatherIconFactory getInstance() {
        return ourInstance;
    }

    /**
     * Constructor
     */
    private WeatherIconFactory() {

        this.availableWeathers.put(Weather.SUNNY, R.drawable.ic_sunny_weather);
        this.availableWeathers.put(Weather.RAINY, R.drawable.ic_rain);
        this.availableWeathers.put(Weather.SNOWY, R.drawable.ic_snow);
        this.availableWeathers.put(Weather.DISASTROUS, R.drawable.ic_tornado);

    }

    /**
     * Retrieve the weather icon, knowing the ID
     * @param weatherID weather ID
     * @return resouce ID of the icon
     */
    public int getWeatherIconByID(Weather weatherID) {
        return this.availableWeathers.get(weatherID);
    }


}

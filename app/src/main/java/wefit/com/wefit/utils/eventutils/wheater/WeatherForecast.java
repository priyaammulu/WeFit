package wefit.com.wefit.utils.eventutils.wheater;

import io.reactivex.Flowable;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;

/**
 * Created by gioacchino on 23/11/2017.
 */

public interface WeatherForecast {

    /**
     * Get the weather forecast for a specified event
     *
     * @param event it will use the location of this event
     * @return Weather forecast
     */
    Flowable<Weather> getForecast(Event event);

    /**
     * Get the weather forecast for a specified location
     *
     * @param location forecast for this location
     * @param date forecast for this date
     * @return Weather forecast
     */
    Flowable<Weather> getForecast(EventLocation location, long date);

}

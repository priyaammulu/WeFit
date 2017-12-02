package wefit.com.wefit.utils.eventutils.location;

import java.util.List;

import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;

/**
 * Created by gioacchino on 21/11/2017.
 * This class sorts events by distance
 */

public interface DistanceManager {
    /**
     * Sorts events in a certain range
     * @param center geographical position from which compute the distance
     * @param distanceKmFilter max sorting range (events outside this range are filtered)
     * @param eventsToSort events to sort
     */
    List<Event> sortByDistanceFromLocation(EventLocation center, List<Event> eventsToSort, int distanceKmFilter);
}

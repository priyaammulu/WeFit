package wefit.com.wefit.utils.eventutils.location;

import java.util.List;

import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;

/**
 * Created by gioacchino on 21/11/2017.
 */

public interface DistanceSorter {

    List<Event> sortByDistanceFromLocation(EventLocation center, List<Event> eventToSort);
}

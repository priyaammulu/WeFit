package wefit.com.wefit.utils.eventutils.location;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.EventLocation;

/**
 * Created by gioacchino on 21/11/2017.
 * OVERRIDDEN METHOD COMMENTS in the interface.
 */

public class DistanceManagerImpl implements DistanceManager {

    @Override
    public List<Event> sortByDistanceFromLocation(EventLocation center, List<Event> eventsToSort, int distanceKmFilter) {

        List<Event> orderedList = new ArrayList<>();
        List<DistanceHolder> wrapped = this.getSortableList(center, eventsToSort);
        Collections.sort(wrapped);

        for (DistanceHolder holder : wrapped) {

            if (!(holder.distance > distanceKmFilter)) {
                orderedList.add(holder.event);
            }

        }

        return orderedList;
    }

    /**
     * Returns a sortable list
     */
    private List<DistanceHolder> getSortableList(EventLocation center, List<Event> eventToSort) {

        List<DistanceHolder> listWrappers = new ArrayList<>();
        for (Event event : eventToSort) {
            listWrappers.add(this.wrap(center, event));
        }

        return listWrappers;
    }

    /**
     * Wrap an event in the distance holder
     *
     * @param center      geographical position from which compute the distance
     * @param eventToWrap event to wrap
     * @return wrapped event. it has the distance from the center
     */
    private DistanceHolder wrap(EventLocation center, Event eventToWrap) {

        double distance = this.computeDistance(center, eventToWrap.getEventLocation());
        return new DistanceHolder(eventToWrap, distance);
    }

    /**
     * ref: https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
     * Calculate computeDistance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, center.getLongitude() Start point lat2, eventPosition.getLongitude() End point el1 Start altitude in meters
     *
     * @return Distance in Km
     */
    private double computeDistance(EventLocation center, EventLocation eventPosition) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(eventPosition.getLatitude() - center.getLatitude());
        double lonDistance = Math.toRadians(eventPosition.getLongitude() - center.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(center.getLatitude())) * Math.cos(Math.toRadians(eventPosition.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Utility class.
     * It wraps an event and its distance from the center location
     */
    private class DistanceHolder implements Comparable<DistanceHolder> {

        /**
         * Wrapped event
         */
        Event event;

        /**
         * Distance from a specified location
         */
        double distance;

        /**
         * Constructor
         * @param event Wrapped event
         * @param distance  Distance from a specified location
         */
        DistanceHolder(Event event, double distance) {
            this.event = event;
            this.distance = distance;
        }

        /**
         * It allows to sort the DistanceHolder by distance
         */
        @Override
        public int compareTo(@NonNull DistanceHolder holder) {

            int compareResult = 0;

            if (this.distance > holder.distance) {
                compareResult = -1;
            }
            if (this.distance < holder.distance) {
                compareResult = 1;
            }

            return compareResult;
        }
    }


}

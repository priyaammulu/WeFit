package wefit.com.wefit.utils.eventutils.location;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.Location;

/**
 * Created by gioacchino on 21/11/2017.
 */

public class DistanceSorterImpl implements DistanceSorter {
    @Override
    public List<Event> sortByDistanceFromLocation(Location center, List<Event> eventToSort) {

        List<Event> orderedList = new ArrayList<>();
        List<DistanceHolder> wrapped = this.getSortableList(center, eventToSort);
        Collections.sort(wrapped);

        for (DistanceHolder holder : wrapped) {
            orderedList.add(holder.event);
        }

        return orderedList;
    }

    private List<DistanceHolder> getSortableList(Location center, List<Event> eventToSort) {

        List<DistanceHolder> listWrappers = new ArrayList<>();
        for (Event event : eventToSort) {
            listWrappers.add(this.wrap(center, event));
        }

        return listWrappers;
    }

    private DistanceHolder wrap(Location center, Event eventToWrap) {

        double distance = this.computeDistance(center, eventToWrap.getEventLocation());
        return new DistanceHolder(eventToWrap, distance);
    }

    /**
     * Calculate computeDistance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, center.getLongitude() Start point lat2, eventPosition.getLongitude() End point el1 Start altitude in meters
     * @return Distance in Meters
     */
    private double computeDistance(Location center, Location eventPosition) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(eventPosition.getLatitude() - center.getLatitude());
        double lonDistance = Math.toRadians(eventPosition.getLongitude() - center.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(center.getLatitude())) * Math.cos(Math.toRadians(eventPosition.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // convert to meters
    }

    private class DistanceHolder implements Comparable<DistanceHolder> {

        Event event;
        double distance;

        public DistanceHolder(Event event, double distance) {
            this.event = event;
            this.distance = distance;
        }

        @Override
        public int compareTo(@NonNull DistanceHolder holder) {

            int compareResult = 0;

            if (this.distance > holder.distance) {
                compareResult = -1;
            }
            if (this.distance < holder.distance){
                compareResult = 1;
            }

            return compareResult;
        }
    }


}

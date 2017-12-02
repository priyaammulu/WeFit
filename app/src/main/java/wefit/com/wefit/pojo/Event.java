package wefit.com.wefit.pojo;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lorenzo on 11/3/17.
 * Event Pojo, nothing more than that
 */

public class Event implements Comparable<Event> {

    private String id;

    /**
     * This flag indicates if the event is private. By default an event is public (on the remote store)
     */
    private boolean privateEvent = false;

    /**
     * Event characterizing infos
     */
    private String name;
    private String description;
    private EventLocation eventLocation;
    private String categoryID;
    private long eventDate;
    private long publicationDate;
    private int maxAttendee;

    /**
     * String encoded event image
     */
    private String image;

    /**
     * User that is admin of this event
     */
    private User admin;

    /**
     * User ID of the admin
     */
    private String adminID;

    /**
     * Users that have joined the event
     */
    private Map<String, Boolean> attendingUsers = new HashMap<>();

    public Event() {
    }

    @Exclude
    public boolean isPrivateEvent() {
        return privateEvent;
    }

    @Exclude
    public void setPrivateEvent(boolean privateEvent) {
        this.privateEvent = privateEvent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventLocation getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(EventLocation eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public long getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(long publicationDate) {
        this.publicationDate = publicationDate;
    }

    public int getMaxAttendee() {
        return maxAttendee;
    }

    public void setMaxAttendees(int maxAttendee) {
        this.maxAttendee = maxAttendee;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Exclude
    public User getAdmin() {
        return admin;
    }

    @Exclude
    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public Map<String, Boolean> getAttendingUsers() {
        return attendingUsers;
    }

    public void setAttendingUsers(Map<String, Boolean> attendingUsers) {
        this.attendingUsers = attendingUsers;
    }


    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", eventLocation=" + eventLocation +
                ", categoryID='" + categoryID + '\'' +
                ", eventDate=" + eventDate +
                ", publicationDate=" + publicationDate +
                ", maxAttendee=" + maxAttendee +
                ", image='" + image + '\'' +
                ", admin=" + admin +
                ", adminID='" + adminID + '\'' +
                ", attendingUsers=" + attendingUsers +
                '}';
    }

    @Override
    public int compareTo(@NonNull Event o) {
        return ((Long) this.eventDate).compareTo(o.eventDate);
    }
}

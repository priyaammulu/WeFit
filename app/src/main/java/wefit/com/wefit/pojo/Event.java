package wefit.com.wefit.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lorenzo on 11/3/17.
 */

public class Event implements Parcelable {

    private String id;

    /**
     * Event characterizing infos
     */
    private String name;
    private String description;
    private Location eventLocation;
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

    /**
     * Required for Parcelable interface
     */
    private int mData;

    public Event() {
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

    public Location getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(Location eventLocation) {
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

    public void setMaxAttendee(int maxAttendee) {
        this.maxAttendee = maxAttendee;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Exclude public User getAdmin() {
        return admin;
    }

    @Exclude public void setAdmin(User admin) {
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

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        //todo, we have to make parcelable even objects that are fields of this object!
        out.writeInt(mData);
    }

    public static final Parcelable.Creator<Event> CREATOR
            = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    private Event(Parcel in) {
        mData = in.readInt();
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", eventLocation=" + eventLocation +
                ", admin=" + admin +
                ", eventDate=" + eventDate +
                ", publicationDate=" + publicationDate +
                ", categoryID='" + categoryID + '\'' +
                ", attendingUsers=" + attendingUsers +
                ", mData=" + mData +
                '}';
    }
}

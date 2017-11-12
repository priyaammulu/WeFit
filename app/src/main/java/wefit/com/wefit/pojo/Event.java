package wefit.com.wefit.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Created by lorenzo on 11/3/17.
 */

public class Event implements Parcelable {
    private String description;
    private String title;
    private String image;
    private Location location;
    private User user;
    private Date expire;
    private Date published;
    private Category category;
    private List<User> participants;
    // parcelable stuff
    private int mData;

    public Event() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    @Override
    public String toString() {
        return "Event{" +
                "description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", location=" + location +
                ", user=" + user +
                ", expire=" + expire +
                ", published=" + published +
                ", category=" + category +
                ", participants=" + participants +
                ", mData=" + mData +
                '}';
    }
}

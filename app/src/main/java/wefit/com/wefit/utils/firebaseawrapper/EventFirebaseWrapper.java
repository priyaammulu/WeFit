package wefit.com.wefit.utils.firebaseawrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wefit.com.wefit.pojo.Category;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.pojo.User;

/**
 * Created by gioacchino on 12/11/2017.
 */

public class EventFirebaseWrapper {

    private String title;
    private String description;
    private String imageUrl;
    private Location location;

    private String eventCreatorUserId;
    private Date expiration;
    private Date publication;
    private Category eventCategory;
    private List<String> partecipantsUserIds = new ArrayList<>();

    public EventFirebaseWrapper(Event adaptedEvent) {

        extractInfos(adaptedEvent);

    }

    public EventFirebaseWrapper() {
    }

    private void extractInfos(Event adaptedEvent) {
        this.title = adaptedEvent.getTitle();
        this.description = adaptedEvent.getDescription();
        this.imageUrl = adaptedEvent.getImage();
        this.location = adaptedEvent.getLocation();
        this.eventCreatorUserId = adaptedEvent.getCreator().getUserId();
        this.expiration = adaptedEvent.getExpire();
        this.publication = adaptedEvent.getPublished();
        this.eventCategory = adaptedEvent.getCategory();

        for (User partecipant :adaptedEvent.getParticipants()) {

            this.partecipantsUserIds.add(partecipant.getUserId());

        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getEventCreatorUserId() {
        return eventCreatorUserId;
    }

    public void setEventCreatorUserId(String eventCreatorUserId) {
        this.eventCreatorUserId = eventCreatorUserId;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Date getPublication() {
        return publication;
    }

    public void setPublication(Date publication) {
        this.publication = publication;
    }

    public Category getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(Category eventCategory) {
        this.eventCategory = eventCategory;
    }

    public List<String> getPartecipantsUserIds() {
        return partecipantsUserIds;
    }

    public void setPartecipantsUserIds(List<String> partecipantsUserIds) {
        this.partecipantsUserIds = partecipantsUserIds;
    }
}

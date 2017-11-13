package wefit.com.wefit.utils.persistence.firebasepersistence;

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

public class EventWrapper {

    private String id;

    private String title;
    private String description;
    private String imageUrl;
    private Location location;

    private String eventCreatorUserId;
    private Date expiration;
    private Date publication;
    private Category eventCategory;
    private List<String> partecipantsUserIds = new ArrayList<>();

    public EventWrapper(Event adaptedEvent) {

        extractInfos(adaptedEvent);

    }

    public EventWrapper() {
    }

    private void extractInfos(Event adaptedEvent) {


        this.id = adaptedEvent.getId();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    /**
     * Unwrap the event in the requested App format
     * @return App event
     */
    public Event unwrap() {

        Event unwrapped = new Event();
        User creator = new User();
        List<User> partecipants = new ArrayList<>();

        // creator of the event
        creator.setUserId(this.eventCreatorUserId);

        // partecipants of the event
        for (String partecipantID : this.partecipantsUserIds) {
            User part = new User();
            part.setUserId(partecipantID);
            partecipants.add(part);
        }

        // add informations to the unwrapped event
        unwrapped.setId(this.id);
        unwrapped.setTitle(this.title);
        unwrapped.setDescription(this.description);
        unwrapped.setImage(this.imageUrl);
        unwrapped.setLocation(this.location);
        unwrapped.setCreator(creator);
        unwrapped.setExpire(this.expiration);
        unwrapped.setPublished(this.publication);
        unwrapped.setCategory(this.eventCategory);
        unwrapped.setParticipants(partecipants);

        return unwrapped;

    }

    @Override
    public String toString() {
        return "EventWrapper{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", location=" + location +
                ", eventCreatorUserId='" + eventCreatorUserId + '\'' +
                ", expiration=" + expiration +
                ", publication=" + publication +
                ", eventCategory=" + eventCategory +
                ", partecipantsUserIds=" + partecipantsUserIds +
                '}';
    }
}

package wefit.com.wefit.pojo;

import java.util.List;

/**
 * Created by gioacchino on 31/10/2017.
 */

public class User {

    private String authKey;
    private String userId;
    private String gender;
    private String name;
    private String email;
    private String biography;
    private String photo;
    private int birthDate;
    private List<String> eventPartecipations;

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(int birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<String> getEventPartecipations() {
        return eventPartecipations;
    }

    public void setEventPartecipations(List<String> eventPartecipations) {
        this.eventPartecipations = eventPartecipations;

    }

    @Override
    public String toString() {
        return "User{" +
                "authKey='" + authKey + '\'' +
                ", userId='" + userId + '\'' +
                ", gender='" + gender + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", biography='" + biography + '\'' +
                ", photo='" + photo + '\'' +
                ", birthDate=" + birthDate +
                ", eventPartecipations=" + eventPartecipations +
                '}';
    }
}

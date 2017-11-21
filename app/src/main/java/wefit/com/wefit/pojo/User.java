package wefit.com.wefit.pojo;

import java.util.List;

/**
 * Created by gioacchino on 31/10/2017.
 */

public class User {

    /**
     * Unique ID of the user in the system
     */
    private String id;

    private String gender;
    private String fullName;
    private String email;
    private String biography;

    /**
     * Encoded user pic
     */
    private String photo;

    /**
     * Birth date in un UNIX second format
     */
    private int birthDate;

    /**
     * List of the IDs of the events
     */
    private List<String> attendances;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public List<String> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<String> attendances) {
        this.attendances = attendances;

    }

    @Override
    public String toString() {
        return "User{" +
                ", id='" + id + '\'' +
                ", gender='" + gender + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", biography='" + biography + '\'' +
                ", photo='" + photo + '\'' +
                ", birthDate=" + birthDate +
                ", attendances=" + attendances +
                '}';
    }
}

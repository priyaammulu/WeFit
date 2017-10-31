package wefit.com.wefit.datamodel.user;

/**
 * Created by gioacchino on 31/10/2017.
 */

public class UserModel {


    private String authKey;
    private String userId;
    private String gender;
    private String name;
    private String email;

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

    public void setName(String birthDaay) {
        this.name = birthDaay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

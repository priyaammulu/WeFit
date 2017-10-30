package wefit.com.wefit.datamodel.user;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by gioacchino on 30/10/2017.
 */

public class FBUserImpl implements User {

    private String userId;
    private String authKey;

    private String userPicURL = "http://graph.facebook.com/%s/picture?type=large";

    public FBUserImpl(String userid, String authKey) {
        this.userId = userid;
        this.authKey = authKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthKey() {
        return authKey;
    }

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public String getPicURL() {
        return String.format(userPicURL, userId);
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    @Override
    public String toString() {
        return "FBUserImpl{" +
                "userId='" + userId + '\'' +
                ", authKey='" + authKey + '\'' +
                ", userPicURL='" + userPicURL + '\'' +
                '}';
    }
}

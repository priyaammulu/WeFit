package wefit.com.wefit.pojo;

/**
 * Created by lorenzo on 11/15/17.
 */

public class UserEvent {
    private boolean confirmed;
    private User user;

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

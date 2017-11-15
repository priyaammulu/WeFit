package wefit.com.wefit.pojo.users;

import wefit.com.wefit.pojo.User;

/**
 * Created by gioacchino on 15/11/2017.
 */

public class Partecipation {

    private boolean partecipation;
    private User partecipant;

    public Partecipation(boolean partecipation, User partecipant) {
        this.partecipation = partecipation;
        this.partecipant = partecipant;
    }

    public boolean isPartecipation() {
        return partecipation;
    }

    public void setPartecipation(boolean partecipation) {
        this.partecipation = partecipation;
    }

    public User getPartecipant() {
        return partecipant;
    }

    public void setPartecipant(User partecipant) {
        this.partecipant = partecipant;
    }
}

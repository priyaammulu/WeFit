package wefit.com.wefit.utils.persistence;

import java.util.List;
import io.reactivex.Flowable;
import wefit.com.wefit.pojo.users.Event;

/**
 * Created by gioacchino on 13/11/2017.
 */

public interface LocalEventDao {

    void create();
    void disconnect();
    void wipe();

    Event getEvents(int numResults, int startOffset);

    Event save(Event eventToStore);

    Event loadEventByID(String eventID);
}

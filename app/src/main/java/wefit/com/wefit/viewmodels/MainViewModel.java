package wefit.com.wefit.viewmodels;

import java.util.List;

import io.reactivex.Flowable;
import wefit.com.wefit.datamodel.EventModel;
import wefit.com.wefit.pojo.Event;

/**
 * Created by lorenzo on 11/3/17.
 */

public class MainViewModel {
    private EventModel mEventModel;

    public MainViewModel(EventModel eventModel) {
        this.mEventModel = eventModel;
    }

    public Flowable<List<Event>> getEvents() {
        return mEventModel.getEvents();
    }
}

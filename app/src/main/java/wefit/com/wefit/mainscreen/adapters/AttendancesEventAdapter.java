package wefit.com.wefit.mainscreen.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Category;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.User;
import wefit.com.wefit.utils.eventutils.category.CategoryIconFactory;
import wefit.com.wefit.utils.image.ImageBase64Marshaller;

/**
 * Created by lorenzo on 11/3/17.
 *
 * This class is responsible to populate Attendances in the selected listview
 */
public class AttendancesEventAdapter extends BaseAdapter {

    /**
     * Showed events
     */
    private List<Event> events;

    /**
     * System context
     */
    private Context context;

    /**
     * Current logged user
     */
    private User mCurrentUser;

    /**
     * Constructor
     * @param events events to show
     * @param context sys context
     * @param currentUser Current logged user
     */
    public AttendancesEventAdapter(List<Event> events, Context context, User currentUser) {
        this.events = events;
        this.context = context;
        this.mCurrentUser = currentUser;
    }
    /**
     * Updates the list of events
     * @param events the events to update
     */
    public void setEvents(List<Event> events) {
        if (!this.events.equals(events))
            this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Event getItem(int position) {
        if (position >= events.size())
            throw new IllegalArgumentException(position + "exceeds" + events.size());
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.myevents_list_item, parent, false);
        }

        EventViewHolder holder = (EventViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new EventViewHolder(convertView);
            convertView.setTag(holder);
        }


        // write the event on the screen
        Event event = events.get(position);

        holder.title.setText(event.getName());
        holder.location.setText(event.getEventLocation().getName());
        holder.monthDay.setText(this.getMonthDay(new Date(event.getEventDate())));
        holder.time.setText(getTime(new Date(event.getEventDate())));
        holder.mEventImage.setImageBitmap(ImageBase64Marshaller.decodeBase64BitmapString(event.getImage()));

        Category category = CategoryIconFactory.getInstance().getCategoryByID(event.getCategoryID());
        holder.mCategoryPic.setImageResource(category.getImage());

        boolean belongs = false; // need to check if the event belongs to the logged user
        String ownershipTypeLabel = null;

        // check if the event is private
        if (event.isPrivateEvent()) {
            ownershipTypeLabel = context.getString(R.string.private_event_list_label);
            belongs = true;
        }
        else {

            // the user is the admin
            if (mCurrentUser.getId().equals(event.getAdminID())) {
                ownershipTypeLabel = context.getString(R.string.admin_label);
                belongs = true;
            }

        }

        if (!belongs) {
            holder.ownershipLabel.setVisibility(View.INVISIBLE);
        }
        else {
            holder.ownershipLabel.setText(ownershipTypeLabel);
        }

        return convertView;
    }

    /**
     * It formats a Date in order to retrieve a String containing the month and the day
     * @param date the day to format
     */
    private String getMonthDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        @SuppressLint("SimpleDateFormat") String month = new SimpleDateFormat(context.getString(R.string.date_format_pattern)).format(cal.getTime());
        return month.concat(" ").concat(day);
    }

    /**
     * It formats a Date in order to retrieve a String containing the time expressed in hours and minutes
     * @param date the day to format
     */
    private String getTime(Date date) {
        Locale locale = Locale.ITALIAN;
        return DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(date);
    }
    /**
     * Created by lorenzo on 11/3/17.
     * ViewHolder pattern
     */
    private static class EventViewHolder {
        private ImageView mEventImage;
        private ImageView mCategoryPic;
        private TextView title;
        private TextView location;
        private TextView monthDay;
        private TextView time;
        private TextView ownershipLabel;

        EventViewHolder(View row) {
            this.title = (TextView) row.findViewById(R.id.myevents_title);
            this.location = (TextView) row.findViewById(R.id.myevents_location);
            this.monthDay = (TextView) row.findViewById(R.id.myevents_expire_date);
            this.time = (TextView) row.findViewById(R.id.myevents_expire_time);
            this.mEventImage = (ImageView) row.findViewById(R.id.myevents_image);
            this.mCategoryPic = (ImageView) row.findViewById(R.id.myattendance_category_pic);
            this.ownershipLabel = (TextView) row.findViewById(R.id.ownership_label);

        }

    }
}

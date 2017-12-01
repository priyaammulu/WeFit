package wefit.com.wefit.mainscreen.adapters;

import android.content.Context;
import android.content.Intent;
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
import wefit.com.wefit.UserDetailsActivity;
import wefit.com.wefit.pojo.Category;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.utils.ExtrasLabels;
import wefit.com.wefit.utils.eventutils.category.CategoryIconFactory;
import wefit.com.wefit.utils.image.ImageBase64Marshaller;

/**
 * Created by lorenzo on 11/3/17.
 * This class is responsible to populate Events in the selected listview
 */

public class EventWallAdapter extends BaseAdapter {

    private List<Event> events;
    private Context context;

    public EventWallAdapter(List<Event> events, Context context) {
        this.events = events;
        this.context = context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.mainview_list_item, parent, false);
        }
        EventViewHolder holder = (EventViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new EventViewHolder(convertView);
            convertView.setTag(holder);
        }

        final Event event = events.get(position);

        holder.title.setText(event.getName());
        holder.location.setText(event.getEventLocation().getName());
        holder.monthDay.setText(getMonthDay(new Date(event.getEventDate())));
        holder.time.setText(getTime(new Date(event.getEventDate())));
        holder.published.setText("Published on: " + getDate(new Date(event.getPublicationDate())));

        holder.mEventImage.setImageBitmap(ImageBase64Marshaller.decodeBase64BitmapString(event.getImage()));
        holder.mUserImage.setImageBitmap(ImageBase64Marshaller.decodeBase64BitmapString(event.getAdmin().getPhoto()));
        holder.organizer.setText(event.getAdmin().getFullName());

        // if click on pic, show the user
        // TODO control if it's too small

        View.OnClickListener showUserClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent showUserDetails = new Intent(context, UserDetailsActivity.class);
                showUserDetails.putExtra(ExtrasLabels.USER_ID, event.getAdminID());

                context.startActivity(showUserDetails);
            }
        };

        holder.mUserImage.setOnClickListener(showUserClicked);
        holder.organizer.setOnClickListener(showUserClicked);

        Category category = CategoryIconFactory.getInstance().getCategoryByID(event.getCategoryID());
        holder.mCategoryPic.setImageResource(category.getImage());

        return convertView;
    }

    /**
     * It formats a Date in order to retrieve a String containing the month and the day
     * @param date the date to format
     */
    private String getMonthDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        return month.concat(" ").concat(day);
    }

    /**
     * It formats a Date in order to retrieve a String containing the date in English format
     * @param date the date to format
     */
    private String getDate(Date date) {
        Locale locale = Locale.ENGLISH;
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, locale).format(date);
    }

    /**
     * It formats a Date in order to retrieve a String containing the time expressed in hours and minutes
     * @param date the date to format
     */
    private String getTime(Date date) {
        Locale locale = Locale.ENGLISH;
        return DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(date);
    }

    /**
     * Created by lorenzo on 11/3/17.
     * ViewHolder pattern
     */
    private static class EventViewHolder {
        private ImageView mEventImage;
        private ImageView mUserImage;
        private ImageView mCategoryPic;
        private TextView title;
        private TextView location;
        private TextView monthDay;
        private TextView time;
        private TextView organizer;
        private TextView published;

        EventViewHolder(View row) {
            this.title = (TextView) row.findViewById(R.id.event_title);
            this.location = (TextView) row.findViewById(R.id.event_location);
            this.monthDay = (TextView) row.findViewById(R.id.evet_month_day);
            this.time = (TextView) row.findViewById(R.id.event_time);
            this.organizer = (TextView) row.findViewById(R.id.event_organizer);
            this.published = (TextView) row.findViewById(R.id.event_published);
            this.mEventImage = (ImageView) row.findViewById(R.id.event_image);
            this.mUserImage = (ImageView) row.findViewById(R.id.event_user_image);
            this.mCategoryPic = (ImageView) row.findViewById(R.id.event_game_image);
        }

    }
}

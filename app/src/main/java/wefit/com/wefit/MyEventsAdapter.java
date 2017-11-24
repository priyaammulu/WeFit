package wefit.com.wefit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.pojo.User;

/**
 * Created by lorenzo on 11/3/17.
 */

public class MyEventsAdapter extends BaseAdapter {

    private List<Event> events;
    private Context context;
    private User current;

    public MyEventsAdapter(List<Event> events, Context context, User current) {
        this.events = events;
        this.context = context;
        this.current = current;
    }

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
            convertView = LayoutInflater.from(context).inflate(R.layout.myevents_item, parent, false);
        }
        EventViewHolder holder = (EventViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new EventViewHolder(convertView);
            convertView.setTag(holder);
        }

        Event event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.location.setText(event.getLocation().getName());
        holder.monthDay.setText(getMonthDay(event.getExpire()));
        holder.time.setText(getTime(event.getExpire()));
        Picasso.with(context).load(event.getImage()).into(holder.mEventImage);
        Picasso.with(context).load(event.getCreator().getPhoto()).into(holder.mImageOrganizer);
        if (current.equals(event.getCreator()))
            holder.mOwnImage.setVisibility(View.VISIBLE);
        else
            holder.mOwnImage.setVisibility(View.INVISIBLE);
        return convertView;
    }

    private String getMonthDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        return month.concat(" ").concat(day);
    }

    private String getDate(Date date) {
        Locale locale = Locale.ENGLISH;
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, locale).format(date);
    }

    private String getTime(Date date) {
        Locale locale = Locale.ITALIAN;
        return DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(date);
    }

    private static class EventViewHolder {
        private ImageView mEventImage;
        private ImageView mOwnImage;
        private ImageView mImageOrganizer;
        private TextView title;
        private TextView location;
        private TextView monthDay;
        private TextView time;

        EventViewHolder(View row) {
            this.title = (TextView) row.findViewById(R.id.myevents_title);
            this.location = (TextView) row.findViewById(R.id.myevents_location);
            this.monthDay = (TextView) row.findViewById(R.id.myevents_expire_date);
            this.time = (TextView) row.findViewById(R.id.myevents_expire_time);
            this.mEventImage = (ImageView) row.findViewById(R.id.myevents_image);
            this.mImageOrganizer = (ImageView) row.findViewById(R.id.myevents_creator_image);
            this.mOwnImage = (ImageView) row.findViewById(R.id.myevents_own_image);
        }

    }
}

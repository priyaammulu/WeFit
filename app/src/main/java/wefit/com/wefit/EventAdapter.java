package wefit.com.wefit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import wefit.com.wefit.pojo.Location;
import wefit.com.wefit.pojo.User;

/**
 * Created by lorenzo on 11/3/17.
 */

public class EventAdapter extends BaseAdapter {

    private List<Event> events;
    private Context context;

    public EventAdapter(List<Event> events, Context context) {
        this.events = events;
        this.context = context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
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
        holder.organizer.setText(event.getCreator().getName());
        holder.published.setText("Published on: " + getDate(event.getPublished()));
        holder.mEvent.setImageBitmap(getBitmapFromString(event.getImage()));
        holder.mUser.setImageBitmap(getBitmapFromString(event.getCreator().getPhoto()));
        Picasso.with(context).load(event.getCategory().getImage()).into(holder.mGame);
        return convertView;
    }

    private Bitmap getBitmapFromString(String image) {
        return BitmapFactory.decodeByteArray(image.getBytes(), 0, image.getBytes().length);
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
        Locale locale = Locale.ENGLISH;
        return DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(date);
    }

    private static class EventViewHolder {
        private ImageView mEvent;
        private ImageView mUser;
        private ImageView mGame;
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
            this.mEvent = (ImageView) row.findViewById(R.id.event_image);
            this.mUser = (ImageView) row.findViewById(R.id.event_user_image);
            this.mGame = (ImageView) row.findViewById(R.id.event_game_image);
        }

    }
}

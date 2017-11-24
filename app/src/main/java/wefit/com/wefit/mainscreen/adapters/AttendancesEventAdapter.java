package wefit.com.wefit.mainscreen.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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

import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Category;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.utils.eventutils.category.CategoryIconFactory;

/**
 * Created by lorenzo on 11/3/17.
 */

public class AttendancesEventAdapter extends BaseAdapter {

    private List<Event> events;
    private Context context;

    public AttendancesEventAdapter(List<Event> events, Context context) {

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
        holder.mEventImage.setImageBitmap(decodeBase64BitmapString(event.getImage()));

        // TODO aggiungi category pic
        Category category = CategoryIconFactory.getInstance().getCategoryByID(event.getCategoryID());
        Picasso.with(context).load(category.getImage()).into(holder.mCategoryPic);

        Picasso.with(context).load(event.getImage()).into(holder.mImageOrganizer);

        return convertView;
    }

    private Bitmap decodeBase64BitmapString(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
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
        private ImageView mCategoryPic;
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
            this.mCategoryPic = (ImageView) row.findViewById(R.id.myattendance_category_pic);
            this.mImageOrganizer = (ImageView) row.findViewById(R.id.myevents_organizer_image);
        }

    }
}

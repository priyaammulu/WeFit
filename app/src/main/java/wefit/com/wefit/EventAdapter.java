package wefit.com.wefit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import wefit.com.wefit.pojo.Event;

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
        SwipeViewHolder holder = (SwipeViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new SwipeViewHolder(convertView);
            convertView.setTag(holder);
        }

        Event event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.location.setText(event.getLocation().getName());
        holder.monthDay.setText(event.getDate().toString());
        holder.time.setText(event.getDate().toString());
        holder.organizer.setText(event.getUser().getName());
        holder.published.setText(event.getPublished().toString());
//        Picasso.with(context).load(event.getImage()).into(holder.mImage);
        return convertView;
    }

    private static class SwipeViewHolder {
        private ImageView mImage;
        private TextView title;
        private TextView location;
        private TextView monthDay;
        private TextView time;
        private TextView organizer;
        private TextView published;

        SwipeViewHolder(View row) {
            //this.mImage = (ImageView) row.findViewById(R.id.imageWi);
            this.title = (TextView) row.findViewById(R.id.event_title);
            this.location = (TextView) row.findViewById(R.id.event_location);
            this.monthDay = (TextView) row.findViewById(R.id.evet_month_day);
            this.time = (TextView) row.findViewById(R.id.event_time);
            this.organizer = (TextView) row.findViewById(R.id.event_organizer);
            this.published = (TextView) row.findViewById(R.id.event_published);
        }

    }
}

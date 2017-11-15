package wefit.com.wefit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import wefit.com.wefit.pojo.users.Event;

/**
 * Created by lorenzo on 11/3/17.
 */

public class MyEventsAdapter extends BaseAdapter {

    private List<Event> events;
    private Context context;

    public MyEventsAdapter(List<Event> events, Context context) {
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
        holder.monthDay.setText(event.getExpire().toString().substring(5));
        holder.time.setText(event.getExpire().toString().substring(5));
        Picasso.with(context).load(event.getImage()).into(holder.mEvent);
        Picasso.with(context).load(event.getImage()).into(holder.mGame);
        Picasso.with(context).load(event.getImage()).into(holder.mImageOrganizer);
        return convertView;
    }

    private static class EventViewHolder {
        private ImageView mEvent;
        private ImageView mGame;
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
            this.mEvent = (ImageView) row.findViewById(R.id.myevents_image);
            this.mGame = (ImageView) row.findViewById(R.id.myevents_game_image);
            this.mImageOrganizer = (ImageView) row.findViewById(R.id.myevents_organizer_image);
        }

    }
}

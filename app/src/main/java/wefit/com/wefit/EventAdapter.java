package wefit.com.wefit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

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
        Event prova = new Event();
        prova.setDescription("jncdjnvj");
        prova.setExpire(new Date());
        prova.setTitle("kdlerkfm");
        Location loc = new Location();
        loc.setName("chair");
        prova.setLocation(loc);
        prova.setImage("https://www.google.ie/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=0ahUKEwja-JiD3MDXAhXMDsAKHe3jCFkQjRwIBw&url=https%3A%2F%2Fkids.nationalgeographic.com%2Fanimals%2Flion%2F&psig=AOvVaw2FusIASKcfExnS69TvNHfU&ust=1510840077707924");
        prova.setPublished(new Date());
        User user = new User();
        user.setName("c");
        prova.setCreator(user);
        this.events.add(prova);
    }

    public void setEvents(List<Event> events) {
        if (!this.events.equals(events))
            this.events = events;
        Event prova = new Event();
        prova.setDescription("jncdjnvj");
        prova.setExpire(new Date());
        prova.setTitle("kdlerkfm");
        Location loc = new Location();
        loc.setName("chair");
        prova.setLocation(loc);
        prova.setImage("https://kids.nationalgeographic.com/content/dam/kids/photos/animals/Mammals/H-P/lion-male-roar.ngsversion.1466679939988.adapt.1900.1.jpg");
        prova.setPublished(new Date());
        User user = new User();
        user.setName("Lorenzo");
        prova.setCreator(user);
        events.add(prova);
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
//        holder.monthDay.setText(event.getExpire().toString().substring(5));
//        holder.time.setText(event.getExpire().toString().substring(5));
//        holder.organizer.setText(event.getCreator().getName());
//        holder.published.setText(event.getPublished().toString().substring(5));
        Picasso.with(context).load(event.getImage()).into(holder.mEvent);
        Picasso.with(context).load(event.getImage()).into(holder.mUser);
        Picasso.with(context).load(event.getImage()).into(holder.mGame);
        return convertView;
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

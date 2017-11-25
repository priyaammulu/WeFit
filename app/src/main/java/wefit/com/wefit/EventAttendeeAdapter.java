package wefit.com.wefit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import wefit.com.wefit.mainscreen.adapters.AttendancesEventAdapter;
import wefit.com.wefit.pojo.User;

/**
 * Created by gioacchino on 24/11/2017.
 */

public class EventAttendeeAdapter extends BaseAdapter {

    private Context context;
    private List<Pair<User, Boolean>> attendees;
    private String adminID;

    @Override
    public int getCount() {
        return attendees.size();
    }

    @Override
    public Object getItem(int positionList) {
        if (positionList >= this.getCount())
            throw new IllegalArgumentException(positionList + "exceeds" + getCount());
        return this.attendees.get(positionList);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.attendee_list_item, viewGroup, false);
        }

        AttendeeViewHolder holder = (AttendeeViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new AttendeeViewHolder(convertView);
            convertView.setTag(holder);
        }

        Pair<User, Boolean> attendance = attendees.get(i);

        holder.mUserPic.setImageBitmap(decodeBase64BitmapString(attendance.first.getPhoto()));
        holder.mUserName.setText(attendance.first.getFullName());

        // TODO spostare
        final String ADMIN_LABEL = "admin";
        final String CONFIRMED_LABEL = "confirmed";
        final String NOT_CONFIRMED_LABEL = "NOT confirmed";


        // assign status labels
        if (attendance.first.getId().equals(this.adminID)) {
            holder.mUserStatus.setText(ADMIN_LABEL);
        }
        else {
            if (attendance.second) {
                holder.mUserStatus.setText(CONFIRMED_LABEL);
            }
            else {
                holder.mUserStatus.setText(NOT_CONFIRMED_LABEL);
            }

        }

        return convertView;
    }

    private Bitmap decodeBase64BitmapString(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public EventAttendeeAdapter(Context context, List<Pair<User, Boolean>> attendees, String adminID) {
        this.context = context;
        this.attendees = attendees;
        this.adminID = adminID;
    }

    private static class AttendeeViewHolder {

        private ImageView mUserPic;
        private TextView mUserName;
        private TextView mUserStatus;

        AttendeeViewHolder(View row) {

            this.mUserPic = (ImageView) row.findViewById(R.id.attendee_user_pic);
            this.mUserName = (TextView) row.findViewById(R.id.attendee_username_txt);
            this.mUserStatus = (TextView) row.findViewById(R.id.attendee_status_txt);

        }

    }
}

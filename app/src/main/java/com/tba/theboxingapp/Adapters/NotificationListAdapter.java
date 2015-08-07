package com.tba.theboxingapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.tba.theboxingapp.Model.Notification;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Networking.TBAVolley;
import com.tba.theboxingapp.PrettyTime;
import com.tba.theboxingapp.R;

import java.util.Date;
import java.util.List;

/**
 * Created by christibbs on 1/7/15.
 */
public class NotificationListAdapter extends ArrayAdapter<Notification> {
    private final Context context;
    public List<Notification> notifications;

    public NotificationListAdapter(Context context, List<Notification> notifications){
        super(context, R.layout.tagged_user_cell, notifications);
        this.context = context;

        this.notifications = notifications;
    }

    private static class ViewHolder {
        public NetworkImageView notificationUserImageView;
        public TextView notificationContentTextView;
        public TextView notificationDateTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notification n = notifications.toArray(new Notification[notifications.size()])[position];

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notification_view, parent, false);
            holder = new ViewHolder();
            holder.notificationUserImageView = (NetworkImageView)convertView.findViewById(R.id.notificationUserImageView);
            holder.notificationContentTextView = (TextView)convertView.findViewById(R.id.notificationContentTextView);
            holder.notificationDateTextView = (TextView)convertView.findViewById(R.id.notificationDateTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!n.seen) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.tw__light_gray));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        holder.notificationUserImageView.setImageUrl(n.imgUrl, TBAVolley.getInstance(context).getImageLoader());
        holder.notificationContentTextView.setText(n.msg);
        holder.notificationDateTextView.setText(prettyTimeAgo(n.createdAt));

        return convertView;
    }

    private String prettyTimeAgo(Date date)
    {
        Date currentDate = new Date();

        Log.i("Current date", currentDate.toString());

        long currentDateLong = currentDate.getTime();
        Log.i("Current date long", String.valueOf(currentDateLong));

        long oldDate = date.getTime();
        Log.i("Old date long", String.valueOf(oldDate));

        return PrettyTime.getTimeAgo(oldDate + 21600000);
    }
}

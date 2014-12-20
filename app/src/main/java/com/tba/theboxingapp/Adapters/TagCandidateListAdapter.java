package com.tba.theboxingapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Networking.TBAVolley;
import com.tba.theboxingapp.R;

import java.util.List;

/**
 * Created by christibbs on 11/14/14.
 */
public class TagCandidateListAdapter extends ArrayAdapter<User> {
    private final Context context;
    public User[] users;

    public TagCandidateListAdapter(Context context, List<User> users){
        super(context, R.layout.tagged_user_cell, users);
        this.context = context;

        this.users = users.toArray(new User[users.size()]);
    }

    private static class ViewHolder {
        public NetworkImageView tagCandidateImageView;
        public TextView tagCandidateNameLabel;
        public TextView tagCandidateHandleLabel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User u = users[position];

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.tagged_user_cell, parent, false);
            holder = new ViewHolder();
            holder.tagCandidateImageView = (NetworkImageView)convertView.findViewById(R.id.tag_users_image_view);
            holder.tagCandidateNameLabel = (TextView)convertView.findViewById(R.id.tag_users_name_label);
            holder.tagCandidateHandleLabel = (TextView)convertView.findViewById(R.id.tag_users_handle_label);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tagCandidateImageView.setImageUrl(u.profileImageUrl, TBAVolley.getInstance(context).getImageLoader());
        holder.tagCandidateNameLabel.setText(u.getName());
        holder.tagCandidateHandleLabel.setText("@" + u.getHandle());

        return convertView;
    }
}

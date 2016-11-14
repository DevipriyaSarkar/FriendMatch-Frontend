package com.friendmatch_frontend.friendmatch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


class FriendGridAdapter extends ArrayAdapter<User> {

    private Context context;
    private ArrayList<User> friendArrayList;

    FriendGridAdapter(Context context, ArrayList<User> friendArrayList) {
        super(context, 0, friendArrayList);
        this.context = context;
        this.friendArrayList = friendArrayList;
    }

    @Override
    public int getCount() {
        return friendArrayList.size();
    }

    public User getItem(int index) {
        return friendArrayList.get(index);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup viewGroup) {
        TextView friendName;
        ImageView friendImage;

        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_friend_item, viewGroup, false);

            friendName = (TextView) view.findViewById(R.id.friendName);
            friendImage = (ImageView) view.findViewById(R.id.friendImage);

            friendName.setText(friendArrayList.get(position).getName());
            int gender = ((friendArrayList.get(position).getGender()).equals("Male")) ? 0 : 1; // 0 for male, 1 for female
            if (gender == 0) {
                friendImage.setImageResource(R.drawable.male);
            } else {
                friendImage.setImageResource(R.drawable.female);
            }
            friendImage.setAdjustViewBounds(true);
        }

        return view;
    }

}
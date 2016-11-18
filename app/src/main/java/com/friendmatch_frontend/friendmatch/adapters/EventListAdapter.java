package com.friendmatch_frontend.friendmatch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.friendmatch_frontend.friendmatch.R;
import com.friendmatch_frontend.friendmatch.models.Event;
import com.friendmatch_frontend.friendmatch.utilities.DateHelper;

import java.util.ArrayList;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Event> eventArrayList;
    private static MyClickListener myClickListener;

    public EventListAdapter(Context context, ArrayList<Event> eventArrayList) {
        this.context = context;
        this.eventArrayList = eventArrayList;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        EventListAdapter.myClickListener = myClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_event_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Event event = eventArrayList.get(position);
        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText((new DateHelper(event.getEventDate())).changeDateFormatLong());
        holder.eventCity.setText(event.getEventCity());
        holder.eventImage.setImageResource(event.getEventImg());
    }

    @Override
    public int getItemCount() {
        return eventArrayList == null ? 0 : eventArrayList.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView eventName, eventDate, eventCity;
        private ImageView eventImage;

        MyViewHolder(View itemView) {
            super(itemView);
            eventName = (TextView) itemView.findViewById(R.id.eventName);
            eventDate = (TextView) itemView.findViewById(R.id.eventDate);
            eventCity = (TextView) itemView.findViewById(R.id.eventCity);
            eventImage = (ImageView) itemView.findViewById(R.id.eventImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getAdapterPosition(), view);
        }
    }
}
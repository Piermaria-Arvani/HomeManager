package it.unitn.disi.homemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by piermaria on 22/05/17.
 */

public class ItemsComplexAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> mEvents;
    private Context mContext = null;
    private int mLayout;

    public ItemsComplexAdapter(Context context, int layoutId,	ArrayList<Event> events) {
        super(context, layoutId, events);
        mEvents = events;
        mContext = context;
        mLayout = layoutId;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(mLayout, null);

        Event event = mEvents.get(position);


        TextView eventDescription = (TextView) view
                .findViewById(R.id.event_description);
        eventDescription.setText(event.getEventDescription());

        TextView eventDate = (TextView) view
                .findViewById(R.id.event_date);
        eventDate.setText(event.getEventDate());

        TextView eventHour = (TextView) view
                .findViewById(R.id.event_hour);
        eventHour.setText(event.getEventHour());

        return view;
    }


    public void reset (){
        mEvents.clear();
        notifyDataSetChanged();
    }
}
package it.unitn.disi.homemanager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.FloatProperty;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by piermaria on 26/05/17.
 */

public class MoneySummaryAdapter extends ArrayAdapter<MoneySummary> {

    private ArrayList<MoneySummary> mMoneySummaries;
    private Context mContext = null;
    private int mLayout;

    public MoneySummaryAdapter(Context context, int layoutId, ArrayList<MoneySummary> moneySummaries) {
        super(context, layoutId, moneySummaries);
        mMoneySummaries = moneySummaries;
        mContext = context;
        mLayout = layoutId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(mLayout, null);

        final MoneySummary moneySummary = mMoneySummaries.get(position);

        TextView name = (TextView) view.findViewById(R.id.textViewName);
        name.setText(moneySummary.getName());

        TextView money = (TextView) view.findViewById(R.id.textViewMoney);
        if((Float.parseFloat(moneySummary.getMoney())) < 0){
            money.setTextColor(Color.RED);
        }
        money.setText(moneySummary.getMoney());


        return view;
    }


    public void reset() {
        mMoneySummaries.clear();
        notifyDataSetChanged();
    }


}

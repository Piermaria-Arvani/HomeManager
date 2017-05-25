package it.unitn.disi.homemanager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by piermaria on 25/05/17.
 */

public class ContactsAdapter extends ArrayAdapter<Contact> {

private ArrayList<Contact> mContacts;
private Context mContext = null;
private int mLayout;

public ContactsAdapter(Context context, int layoutId, ArrayList<Contact> contacts) {
        super(context, layoutId, contacts);
        mContacts = contacts;
        mContext = context;
        mLayout = layoutId;
        }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(mLayout, null);

        final Contact contact = mContacts.get(position);

        TextView name = (TextView) view.findViewById(R.id.textViewName);
        name.setText(contact.getName());

        TextView number = (TextView) view.findViewById(R.id.textViewNumber);
        number.setText(contact.getNumber());

        final ImageButton call = (ImageButton) view.findViewById(R.id.call_button);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + contact.getNumber()));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.d("Calling a Phone Number", "Call failed" + activityException);
                }
            }
        });

        return view;
        }


public void reset() {
        mContacts.clear();
        notifyDataSetChanged();
        }

        }

package it.unitn.disi.homemanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by piermaria on 24/05/17.
 */

public class CleanRoundAdapter extends ArrayAdapter<CleanRound> {

    private ArrayList<CleanRound> mCleanRounds;
    private Context mContext = null;
    private int mLayout;

    public CleanRoundAdapter(Context context, int layoutId, ArrayList<CleanRound> cleanRounds) {
        super(context, layoutId, cleanRounds);
        mCleanRounds = cleanRounds;
        mContext = context;
        mLayout = layoutId;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(mLayout, null);

        CleanRound cleanRound = mCleanRounds.get(position);

        TextView componentName = (TextView) view.findViewById(R.id.component_name);
        componentName.setText(cleanRound.getName());

        final TextView description = (TextView) view.findViewById(R.id.clean_description);
        description.setText(cleanRound.getCleanDescription());

        final Button insert = (Button) view.findViewById(R.id.insertButton);
        final Button done = (Button) view.findViewById(R.id.doneButton);
        final String facebook_id = SharedPrefManager.getInstance(mContext).getFacebookID();
        final String group_id = SharedPrefManager.getInstance(mContext).getGroupId();
        final String actual_fbid = cleanRound.getFacebook_id();

        if(facebook_id.equals( cleanRound.getFacebook_id()) && cleanRound.getCleanDescription().length()>0){
            if (!cleanRound.getDone())
                done.setVisibility(View.VISIBLE);
         }

        final LinearLayout row = (LinearLayout) view.findViewById(R.id.row);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_SET_CLEANING_ROUND_DONE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if(obj.getString("message").equals("Done")){
                                        done.setVisibility(View.GONE);
                                        row.setBackgroundColor(Color.parseColor("#ccffcc"));

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("facebook_id", facebook_id);

                        return params;
                    }
                };
                MyVolley.getInstance(mContext).addToRequestQueue(stringRequest);

            }
        });


        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String new_description = description.getText().toString();

                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_INSERT_CLEANING_ROUND,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if(obj.getString("message").equals("Cleaning Schedule registered successfully")){
                                        Toast.makeText(mContext, "Aggiornato", Toast.LENGTH_LONG).show();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("facebook_id", actual_fbid);
                        params.put("group_id",group_id);
                        params.put("description", new_description);

                        return params;
                    }
                };
                MyVolley.getInstance(mContext).addToRequestQueue(stringRequest);

            }
        });

        if (cleanRound.getDone()){
            row.setBackgroundColor(Color.parseColor("#ccffcc"));
        }


        return view;
    }


    public void reset() {
        mCleanRounds.clear();
        notifyDataSetChanged();
    }

}
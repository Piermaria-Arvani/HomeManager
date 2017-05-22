package it.unitn.disi.homemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventsActivity extends AppCompatActivity {

    private Context context;
    private FloatingActionButton fab;
    ProgressDialog progressDialog;
    private Calendar calendar;
    private SimpleDateFormat mdformat;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();

        calendar = Calendar.getInstance();
        mdformat = new SimpleDateFormat("yyyy-MM-dd ");
        date = mdformat.format(calendar.getTime());

        getEvents();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void getEvents(){
        final String group_id= SharedPrefManager.getInstance(context).getGroupId();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_GROUP_EVENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);



                            ListView view = (ListView) findViewById(R.id.listView);
                            final ArrayList<Event> eventList = new ArrayList<>();


                            //riempimento sezione eventi odierni
                            if(Integer.parseInt(obj.getString("numberOfEvents")) > 0) {


                                JSONArray jsonEventsDescriptions = obj.getJSONArray("events_descriptions");
                                for (int i = 0; i < jsonEventsDescriptions.length(); i++) {
                                    Event event = new Event();
                                    JSONObject d = jsonEventsDescriptions.getJSONObject(i);
                                    String event_date = d.getString("event_date");
                                    String event_hour = (d.getString("event_hour")).substring(0,5);
                                    String description =  d.getString("description") ;

                                    event.setEventDate(event_date);
                                    event.setEventDescription(description);
                                    event.setEventHour(event_hour);

                                    eventList.add(event);
                                }
                                view.setAdapter(new ItemsComplexAdapter(context, R.layout.row_events_list_view, eventList));
                                /**view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                 public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                                 Toast.makeText(getApplicationContext(),
                                 eventList.get(position).getArticleName(),
                                 Toast.LENGTH_LONG).show();
                                 }
                                 });
                                 */
                            }else{
                                TextView text_no_eventi= (TextView) findViewById(R.id.text_no_eventi);
                                text_no_eventi.setVisibility(View.VISIBLE);
                            }


                            System.out.println(obj);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(EventsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("group_id", group_id);
                params.put("date", date);

                return params;
            }
        };
        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

}

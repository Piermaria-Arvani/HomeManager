package it.unitn.disi.homemanager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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
    private ProgressDialog progressDialog;
    private Calendar calendar;
    private SimpleDateFormat mdformat;
    private String date;
    private String group_id;
    private ItemsComplexAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();
        group_id= SharedPrefManager.getInstance(context).getGroupId();

        adapter = null;
        calendar = Calendar.getInstance();
        mdformat = new SimpleDateFormat("yyyy-MM-dd ");
        date = mdformat.format(calendar.getTime());

        getEvents();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create custom dialog object
                final Dialog dialog = new Dialog(EventsActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.event_dialog);
                // Set dialog title
                dialog.setTitle("Create Event");

                // set values for custom dialog components


                dialog.show();
                Button insertButton = (Button) dialog.findViewById(R.id.insert);
                Button declineButton = (Button) dialog.findViewById(R.id.annulla);


                // if decline button is clicked, close the custom dialog
                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        dialog.dismiss();
                    }
                });

                insertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TextView descriptionText = (TextView) dialog.findViewById(R.id.dialog_event_description);
                        TextView dateText = (TextView) dialog.findViewById(R.id.dialog_event_date);
                        TextView hourText = (TextView) dialog.findViewById(R.id.dialog_event_hour);

                        final String description =  descriptionText.getText().toString();
                        final String date =  dateText.getText().toString();
                        final String hour =  hourText.getText().toString();
                        System.out.println("descrizione evento" + description);


                        if (description.length()==0||date.length()==0||hour.length()==0) {
                            Toast.makeText(context, "Devi riempire tutti i campi per poter procedere", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            // send to db
                            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_INSERT_EVENT,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                if(obj.getString("message").equals("Event registered successfully")){
                                                    Toast.makeText(context, "Evento Inserito con successo", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    adapter.reset();
                                                    getEvents();

                                                }


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(EventsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }) {

                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("group_id", group_id);
                                    params.put("description", description);
                                    params.put("hour", hour);
                                    params.put("date", date);

                                    return params;
                                }
                            };
                            MyVolley.getInstance(context).addToRequestQueue(stringRequest);

                        }


                    }
                });


            }

        });
    }

    public void getEvents(){

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
                                    String event_date = getDateWellFormed(d.getString("event_date"));
                                    String event_hour = (d.getString("event_hour")).substring(0,5);
                                    String description =  d.getString("description") ;

                                    event.setEventDate(event_date);
                                    event.setEventDescription(description);
                                    event.setEventHour(event_hour);

                                    eventList.add(event);
                                }

                                adapter =new ItemsComplexAdapter(context, R.layout.row_events_list_view, eventList);
                                view.setAdapter(adapter);
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


    private String getDateWellFormed (String completeData){
        Date date = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = date_format.parse(completeData);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String data = String.valueOf(date);

        System.out.println("data" + data);
        String[] splited = data.split("\\s+");
        String nomeMese  = getMonthNameInItalian(splited[1]);
        String numeroGiorno =splited[2];

        return numeroGiorno +" "+ nomeMese;
    }

    private String getMonthNameInItalian(String month){
        String mese = "";

        switch (month) {
            case "Jan":  mese = "Gennaio";
                break;
            case "Feb":  mese = "Febbraio";
                break;
            case "Mar":  mese = "Marzo";
                break;
            case "Apr":  mese = "Aprile";
                break;
            case "May":  mese = "Maggio";
                break;
            case "June":  mese = "Giugno";
                break;
            case "July":  mese = "Luglio";
                break;
            case "Aug":  mese = "Agosto";
                break;
            case "Sept":  mese = "Settembre";
                break;
            case "Oct":  mese = "Ottobre";
                break;
            case "Nov":  mese = "Novembre";
                break;
            case "Dic":  mese = "dicembre";
                break;
        }

        return mese;
    }
}

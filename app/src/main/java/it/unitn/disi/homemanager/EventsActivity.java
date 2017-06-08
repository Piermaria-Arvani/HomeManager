package it.unitn.disi.homemanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
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
    private Calendar calendar;
    private SimpleDateFormat mdformat;
    private String date;
    private String group_id;
    private ItemsComplexAdapter adapter;
    private int counter ;
    private TextView textView;
    private DatePicker datePicker;
    private int year, month, day;;
    private TextView dateView;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView = (TextView) findViewById(R.id.text_no_eventi);
        setSupportActionBar(toolbar);
        context = getApplicationContext();
        group_id= SharedPrefManager.getInstance(context).getGroupId();

        counter = 0;
        adapter = null;
        calendar = Calendar.getInstance();
        mdformat = new SimpleDateFormat("yyyy-MM-dd ");
        date = mdformat.format(calendar.getTime());



        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


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
                dateView = (TextView) dialog.findViewById(R.id.dialog_event_date_text);
                showDate(year, month+1, day);
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

                        TextView descriptionText = (TextView) dialog.findViewById(R.id.dialog_element_description);
                        TextView dateText = (TextView) dialog.findViewById(R.id.dialog_event_date_text);
                        timePicker = (TimePicker) dialog.findViewById(R.id.dialog_event_hour);
                        int getHour = 0;
                        int getMinute = 0;
                        if(Build.VERSION.SDK_INT < 23){
                            getHour = timePicker.getCurrentHour();
                            getMinute = timePicker.getCurrentMinute();

                        } else{
                            getHour = timePicker.getHour();
                            getMinute = timePicker.getMinute();

                        }
                        final String description =  descriptionText.getText().toString();
                        String old_date =  dateText.getText().toString();
                        final String hour =  getHour + ":" + getMinute;
                        final String OLD_FORMAT = "dd/MM/yyyy";
                        final String NEW_FORMAT = "yyyy/MM/dd";


                        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
                        Date d = null;
                        try {
                            d = sdf.parse(old_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        sdf.applyPattern(NEW_FORMAT);
                        final String newDateString = sdf.format(d);

                        if (description.length()==0||newDateString.length()==0||hour.length()==0) {
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
                                                    if (counter>0) {
                                                        adapter.reset();
                                                    }
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
                                    params.put("date", newDateString);

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


                            if(Integer.parseInt(obj.getString("numberOfEvents")) > 0) {


                                JSONArray jsonEventsDescriptions = obj.getJSONArray("events_descriptions");
                                for (int i = 0; i < jsonEventsDescriptions.length(); i++) {
                                    Event event = new Event();
                                    JSONObject d = jsonEventsDescriptions.getJSONObject(i);
                                    String id = d.getString("id");
                                    String event_date = getDateWellFormed(d.getString("event_date"));
                                    String event_hour = (d.getString("event_hour")).substring(0,5);
                                    String description =  d.getString("description") ;
                                    System.out.println("id evento" + id);
                                    event.setId(id);
                                    event.setEventDate(event_date);
                                    event.setEventDescription(description);
                                    event.setEventHour(event_hour);

                                    eventList.add(event);
                                }

                                adapter =new ItemsComplexAdapter(context, R.layout.row_events_list_view, eventList);
                                counter +=1;
                                view.setAdapter(adapter);
                                textView.setVisibility(View.GONE);
                                view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                                        //Creating the instance of PopupMenu
                                        PopupMenu popup = new PopupMenu(EventsActivity.this, view);
                                        //Inflating the Popup using xml file
                                        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                                        //registering popup with OnMenuItemClickListener
                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            public boolean onMenuItemClick(MenuItem item) {
                                                if(item.getTitle().equals("Elimina")){
                                                    final String id = eventList.get(position).getId();
                                                    StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_DELETE_EVENT,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    try {
                                                                        JSONObject obj = new JSONObject(response);
                                                                        if(obj.getString("message").equals("Event removed successfully")){
                                                                            Toast.makeText(context, "Eliminato con successo", Toast.LENGTH_SHORT).show();
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
                                                                }
                                                            }) {

                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                            Map<String, String> params = new HashMap<>();
                                                            params.put("id", id);
                                                            return params;
                                                        }
                                                    };

                                                    MyVolley.getInstance(context).addToRequestQueue(stringRequest);
                                                }
                                                return true;
                                            }
                                        });

                                        popup.show();//showing popup menu

                                        return true;
                                    }

                                });

                            }else{
                                TextView text_no_eventi= (TextView) findViewById(R.id.text_no_eventi);
                                text_no_eventi.setVisibility(View.VISIBLE);
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
            case "Jun":  mese = "Giugno";
                break;
            case "Jul":  mese = "Luglio";
                break;
            case "Aug":  mese = "Agosto";
                break;
            case "Sep":  mese = "Settembre";
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

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, GroupHomeActivity.class));
    }
}

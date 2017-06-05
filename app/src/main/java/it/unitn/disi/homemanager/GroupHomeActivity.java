package it.unitn.disi.homemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.login.LoginManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class GroupHomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private Context context ;
    private Toolbar myToolbar;
    private Calendar calendar;
    private SimpleDateFormat mdformat;
    private String date;
    private TextView calendarText;
    private TextView cleanText;
    private TextView moneyText;
    private String eventString;
    private String cleanString;
    private ImageButton calendarButton;
    private ImageButton shoppingButton;
    private  ImageButton cleanButton;
    private  ImageButton contactsButton;
    private ImageButton moneyButton;
    private int counter_back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_home);

        counter_back_pressed = 0;
        calendarText = (TextView) findViewById(R.id.calendar_text);
        cleanText = (TextView) findViewById(R.id.cleaning_text);
        moneyText =(TextView) findViewById(R.id.money_text);
        calendarButton = (ImageButton) findViewById(R.id.calendar_button);
        shoppingButton = (ImageButton) findViewById(R.id.shoppig_button);
        cleanButton = (ImageButton) findViewById(R.id.cleaning_button);
        contactsButton =(ImageButton) findViewById(R.id.contacts_button);
        moneyButton = (ImageButton) findViewById(R.id.money_button);

        calendar = Calendar.getInstance();
        mdformat = new SimpleDateFormat("yyyy-MM-dd ");
        date = mdformat.format(calendar.getTime());
        System.out.println("data" + date);

        calendarButton.setOnClickListener(this);
        shoppingButton.setOnClickListener(this);
        cleanButton.setOnClickListener(this);
        contactsButton.setOnClickListener(this);
        moneyButton.setOnClickListener(this);

        getGroupInfo();
        context= getApplicationContext();
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(SharedPrefManager.getInstance(context).getGroupName());

        setSupportActionBar(myToolbar);
    }

    @Override
    public void onClick(View view) {
        if (view == calendarButton) {
            startActivity(new Intent(this, EventsActivity.class));
        }
        if (view == shoppingButton){
            startActivity(new Intent(this,ShoppingListActivity.class));
        }
        if (view == cleanButton){
            startActivity(new Intent(this,CleanRoundActivity.class));
        }
        if (view == contactsButton) {
            startActivity(new Intent(this, ContactsActivity.class));
        }
        if (view == moneyButton){
            startActivity(new Intent(this, WalletActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.gruouphomectivitymenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch(id)
        {
            case R.id.aggiungi_membro:
                final String group_id = String.valueOf(SharedPrefManager.getInstance(context).getGroupId());
                String qr_body = group_id;
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix =  multiFormatWriter.encode(qr_body, BarcodeFormat.QR_CODE,350,350);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    Intent intent = new Intent(context, AddMemberActivity.class);
                    intent.putExtra("pic",bitmap);
                    startActivity(intent);

                }catch (WriterException e){
                    e.printStackTrace();
                }
                break;
            case R.id.logout:
                logout();
                break;

        }
        return false;
    }



    public void getGroupInfo(){
        final String group_id= SharedPrefManager.getInstance(context).getGroupId();
        final String facebook_id = SharedPrefManager.getInstance(context).getFacebookID();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_GROUP_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            //riempimento sezione eventi odierni
                            if(Integer.parseInt(obj.getString("numberOfEvents")) > 0) {
                                eventString = "In programma oggi: \n \n";

                                JSONArray jsonEventsDescriptions = obj.getJSONArray("events_descriptions");
                                for (int i = 0; i < jsonEventsDescriptions.length(); i++) {
                                    JSONObject d = jsonEventsDescriptions.getJSONObject(i);
                                    String temp = d.getString("event_hour");
                                    String hour = temp.substring(0,5);
                                    eventString += "- " + d.getString("description") + " alle "+ hour + "\n";
                                }
                                calendarText.setText(eventString);
                             }else{
                                eventString = "Non ci sono eventi in programma per oggi";
                                calendarText.setText(eventString);
                            }

                            //debiti
                            JSONArray jsonDebits = obj.getJSONArray("debits");
                            JSONObject s = jsonDebits.getJSONObject(0);
                            float temp = Float.parseFloat(s.getString("debit_credit"));
                            DecimalFormat df = new DecimalFormat("###.##");
                            String debit_credit = df.format(temp);
                            if(temp == 0){
                                moneyText.setText("Non hai debiti con i tuoi coinquilini");
                            }else if (temp < 0) {
                                moneyText.setText("Sei in debito con i tuoi coinquilini di " + debit_credit.substring(1) + " €");
                            }else{
                                moneyText.setText("I tuoi coinquilini ti devono " + debit_credit + " €");
                            }


                            //riempimento sezione pulizie
                            if(Integer.parseInt(obj.getString("numberOfClean")) > 0) {

                                JSONArray jsonCleanDescriptions = obj.getJSONArray("clean_descriptions");
                                for (int i = 0; i < jsonCleanDescriptions.length(); i++) {
                                    JSONObject o = jsonCleanDescriptions.getJSONObject(i);
                                    if(o.getString("description").length()==0)
                                        cleanString = "Non hai pulizie da fare in programma";
                                    else
                                        cleanString = "Questa settimana devi: \n " + "- "+o.getString("description");
                                }
                                cleanText.setText(cleanString);
                            }else{
                                cleanString = "Non hai pulizie da fare in programma";
                                cleanText.setText(cleanString);
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
                        Toast.makeText(GroupHomeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("facebook_id", facebook_id);
                params.put("group_id", group_id);
                params.put("date", date);

                return params;
            }
        };
        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }


    public String getDayNameInItalian(String day){
        String dayName ="";

        switch (day) {
            case "Mon":  dayName = "Lunedì";
                break;
            case "Tue":  dayName = "Martedì";
                break;
            case "Wed":  dayName = "Mercoledì";
                break;
            case "Thu":  dayName = "Giovedì";
                break;
            case "Fri":  dayName = "Venerdì";
                break;
            case "Sat":  dayName = "Sabato";
                break;
            case "Sun":  dayName = "Domenica";
                break;
        }

        return dayName;
    }
    public void logout(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging out ...");
        progressDialog.show();
        final String facebook_id = SharedPrefManager.getInstance(this).getFacebookID();


        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_DELETE_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        LoginManager.getInstance().logOut();
                        System.out.println(" provo a sloggarmi");
                        startActivity(new Intent(context, Login.class));
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(GroupHomeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("facebook_id", facebook_id);
                return params;
            }
        };
        MyVolley.getInstance(this).addToRequestQueue(stringRequest);

    }

    @Override
    public void onBackPressed() {
        if(counter_back_pressed == 0){
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_LONG).show();
            counter_back_pressed++;
        }else {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
    }
}

package it.unitn.disi.homemanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InsertExpenseActivity extends AppCompatActivity {

    private Context context;
    private Button fab;
    private Calendar calendar;
    private SimpleDateFormat mdformat;
    private String group_id;
    private String facebook_id;
    private DatePicker datePicker;
    private int year, month, day;;
    private TextView dateView;
    List<String> fbids;
    private int counter ;
    private String title;
    private String message;
    private  ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_expense);

        context = getApplicationContext();
        group_id = SharedPrefManager.getInstance(context).getGroupId();
        facebook_id = SharedPrefManager.getInstance(context).getFacebookID();

        counter = 0;
        title = "";
        message = "";
        fbids = new ArrayList<>();
        calendar = Calendar.getInstance();
        mdformat = new SimpleDateFormat("yyyy-MM-dd ");

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateView = (TextView) findViewById(R.id.dialog_date_text);
        showDate(year, month+1, day);

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });


        fab = (Button) findViewById(R.id.insert);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               TextView descriptionText = (TextView) findViewById(R.id.dialog_element_description);
                TextView costText = (TextView) findViewById(R.id.dialog_element_cost);
                TextView dateText = (TextView) findViewById(R.id.dialog_date_text);

                final String description =  descriptionText.getText().toString();
                final String cost =  costText.getText().toString();
                String old_date =  dateText.getText().toString();
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


                if (description.length()==0||newDateString.length()==0||cost.length()==0) {
                    Toast.makeText(context, "Devi riempire tutti i campi per poter procedere", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    // send to db
                    StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_INSERT_EXPENSE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if(obj.getString("message").equals("Insert")){
                                            Toast.makeText(context, "Inserito con successo", Toast.LENGTH_SHORT).show();
                                            JSONArray jsonItems = obj.getJSONArray("users");
                                            for (int i = 0; i < jsonItems.length(); i++){
                                                JSONObject d = jsonItems.getJSONObject(i);
                                                fbids.add(d.getString("facebook_id"));
                                            }
                                            title = "HomeManager";
                                            message = SharedPrefManager.getInstance(context).getFacebookName() + " ha inserito una nuova spesa";
                                            sendPush();
                                            startActivity(new Intent(context, WalletActivity.class));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(InsertExpenseActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }) {

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("group_id", group_id);
                            params.put("facebook_id", facebook_id);
                            params.put("money", cost);
                            params.put("description", description);
                            params.put("date", newDateString);

                            return params;
                        }
                    };
                    MyVolley.getInstance(context).addToRequestQueue(stringRequest);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, WalletActivity.class));
    }

    private void sendPush() {

        for(int i = 0; i < fbids.size() ; i++) {
            final String fb_id = fbids.get(i);
            if (!fb_id.equals(facebook_id)) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                counter++;
                                System.out.println("invio a " + fb_id);
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

                        params.put("facebook_id", fb_id);
                        params.put("title", title);
                        params.put("message", message);
                        return params;
                    }
                };

                MyVolley.getInstance(this).addToRequestQueue(stringRequest);
            }
        }
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


    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Parla ora");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Speech not supported", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        TextView descriptionText = (TextView) findViewById(R.id.dialog_element_description);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    descriptionText.setText(result.get(0));
                }
                break;
            }

        }
    }
}

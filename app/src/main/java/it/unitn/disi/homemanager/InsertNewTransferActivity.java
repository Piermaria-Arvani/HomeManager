package it.unitn.disi.homemanager;

import android.app.ProgressDialog;
import android.app.WallpaperInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertNewTransferActivity extends AppCompatActivity {
    private Spinner spinner;
    private ProgressDialog progressDialog;
    private List<String> users;
    private List<String> fb_ids;
    private Button buttonInsert;
    private EditText moneyText;
    private String myFacebookId;
    private Context context;
    private String group_id;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_new_transfer);

        spinner = (Spinner) findViewById(R.id.spinnerUsers);
        buttonInsert = (Button) findViewById(R.id.insert);

        moneyText = (EditText) findViewById(R.id.dialog_insert);

        context = getApplicationContext();
        myFacebookId = SharedPrefManager.getInstance(context).getFacebookID();
        group_id = SharedPrefManager.getInstance(context).getGroupId();

        users = new ArrayList<>();
        fb_ids = new ArrayList<>();


        buttonInsert.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                getIndex();
                final String hisfacebook_id = fb_ids.get(index);
                final String money = moneyText.getText().toString();
                if (money.length()== 0){
                    Toast.makeText(context, "Completa il campo importo", Toast.LENGTH_LONG).show();
                }
                else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_UPDATE_DEBITS,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        startActivity(new Intent(context, WalletActivity.class));

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
                            params.put("myfacebook_id", myFacebookId);
                            params.put("hisfacebook_id", hisfacebook_id);
                            params.put("money", money);
                            return params;
                        }

                    };
                    MyVolley.getInstance(context).addToRequestQueue(stringRequest);
                }
            }

        });

        loadUsers();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, WalletActivity.class));
    }

    private void getIndex(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3)
            {
                int temp = spinner.getSelectedItemPosition();
                setIndex(temp);
            }
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });
    }

    private void setIndex(int temp){
        index = temp;
    }
    public void loadUsers(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Users...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_GROUP_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            System.out.println(obj);
                            if (!obj.getBoolean("error")) {
                                JSONArray jsonUsers = obj.getJSONArray("users");

                                for (int i = 0; i < jsonUsers.length(); i++) {
                                    JSONObject d = jsonUsers.getJSONObject(i);
                                    if(!d.getString("facebook_id").equals(myFacebookId)){
                                        users.add(d.getString("name"));
                                        fb_ids.add(d.getString("facebook_id"));
                                    }
                                }

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                        InsertNewTransferActivity.this,
                                        android.R.layout.simple_spinner_dropdown_item, users);

                                if(fb_ids.size()==0){
                                    Toast.makeText(context, "Non puoi fare trasferimenti poichè nel tuo gruppo non si sono altre persone", Toast.LENGTH_LONG).show();
                                    buttonInsert.setEnabled(false);
                                    buttonInsert.setBackgroundColor(Color.parseColor("#b3b3b3"));
                                }
                                spinner.setAdapter(arrayAdapter);
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
                params.put("group_id", group_id);
                return params;
            }

        };
        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }
}

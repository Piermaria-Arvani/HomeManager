package it.unitn.disi.homemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private ProgressDialog progressDialog;
    private Context context ;
    private TextView homeText;
    private TextView opText;
    private EditText nameText;
    private String text;
    private String name;
    private Button buttonCreate;
    private Button buttonJoin;
    private Button buttonCreate2;
    private Button buttonAnnulla;
    private Toolbar myToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context= getApplicationContext();
        name = SharedPrefManager.getInstance(context).getFacebookName();

        //getting views from xml
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        buttonCreate = (Button) findViewById(R.id.crea_gruppo);
        buttonJoin = (Button) findViewById(R.id.entra_gruppo);
        homeText = (TextView) findViewById(R.id.HomeText);
        opText = (TextView) findViewById(R.id.oppure);
        nameText = (EditText) findViewById(R.id.text_insert_group_name);
        buttonCreate2 = (Button) findViewById(R.id.crea_gruppo2);
        buttonAnnulla = (Button) findViewById(R.id.annulla);

        setSupportActionBar(myToolbar);
        //adding listener to view
        buttonCreate.setOnClickListener(this);
        buttonJoin.setOnClickListener(this);
        buttonCreate2.setOnClickListener(this);
        buttonAnnulla.setOnClickListener(this);

        text = "Ciao "+ name  + ",\n non fai ancora parte di nessun gruppo-appartamento";
        homeText.setText(text);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonCreate) {
            homeText.setVisibility(View.INVISIBLE);
            buttonCreate.setVisibility(View.INVISIBLE);
            buttonJoin.setVisibility(View.INVISIBLE);
            opText.setVisibility(View.INVISIBLE);
            buttonCreate2.setVisibility(View.VISIBLE);
            nameText.setVisibility(View.VISIBLE);
            buttonAnnulla.setVisibility(View.VISIBLE);
        }

        if (view == buttonAnnulla) {
            homeText.setVisibility(View.VISIBLE);
            buttonCreate.setVisibility(View.VISIBLE);
            buttonJoin.setVisibility(View.VISIBLE);
            opText.setVisibility(View.VISIBLE);
            buttonCreate2.setVisibility(View.INVISIBLE);
            nameText.setVisibility(View.INVISIBLE);
            buttonAnnulla.setVisibility(View.INVISIBLE);
        }

        //starting send notification activity
        if(view == buttonJoin){
            startActivity(new Intent(this, JoinGroupActivity.class));
        }

        if (view == buttonCreate2) {
            final String group_name = nameText.getText().toString();
            if (group_name.length() > 0) {
                SharedPrefManager.getInstance(context).saveGroupName(group_name);

                final String facebook_id = SharedPrefManager.getInstance(this).getFacebookID();


                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_REGISTER_GROUP,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    System.out.println(obj.getString("message"));

                                } catch (JSONException e) {
                                    System.out.println("eccezione JSON");
                                    e.printStackTrace();
                                }
                                startActivity(new Intent(context, GroupHomeActivity.class));
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                System.out.println("eccezione error listener");
                            }
                        }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("facebook_id", facebook_id);
                        params.put("group_name", group_name);

                        return params;
                    }
                };
                MyVolley.getInstance(this).addToRequestQueue(stringRequest);
            }
            else{
                Toast.makeText(HomeActivity.this, "Inserisci il nome del gruppo", Toast.LENGTH_LONG).show();
            }
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        switch(id)
        {
            case R.id.logout:
                logout();
                break;

        }
        return false;
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
                        startActivity(new Intent(context, Login.class));
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
}

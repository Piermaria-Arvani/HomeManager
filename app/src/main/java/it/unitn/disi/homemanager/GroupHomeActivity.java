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
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class GroupHomeActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    Context context ;
    private Toolbar myToolbar;
    private Calendar calendar;
    private SimpleDateFormat mdformat;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_home);

        calendar = Calendar.getInstance();
        mdformat = new SimpleDateFormat("yyyy / MM / dd ");
        date = mdformat.format(calendar.getTime());

       // getGroupInfo();

        context= getApplicationContext();
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("miao");

        setSupportActionBar(myToolbar);
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
                        Toast.makeText(GroupHomeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        System.out.println("eccezione error listener");
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
}

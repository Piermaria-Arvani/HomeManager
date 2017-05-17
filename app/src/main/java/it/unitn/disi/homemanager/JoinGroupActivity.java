package it.unitn.disi.homemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JoinGroupActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private Button buttonScan;
    private Context context;
    private ProgressDialog progressDialog;

    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        buttonScan = (Button) findViewById(R.id.buttonScan);
        context = getApplicationContext();

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
                System.out.println("passato da qui 3");
            } else {
                //if qr contains data
                final String group_id =  result.getContents();
                final String facebook_id = SharedPrefManager.getInstance(context).getFacebookID();
                System.out.println("group_id:" + group_id);
                SharedPrefManager.getInstance(context).saveGroupId(Integer.parseInt(group_id));
                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_JOIN_GROUP,
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
                                Toast.makeText(JoinGroupActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                System.out.println("eccezione error listener");
                            }
                        }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("facebook_id", facebook_id);
                        params.put("group_id", group_id);

                        return params;
                    }
                };
                MyVolley.getInstance(this).addToRequestQueue(stringRequest);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        qrScan.initiateScan();
    }
}

package it.unitn.disi.homemanager;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    Context context ;
    String complete_name;
    String name;
    String surname;
    private ProgressDialog progressDialog;
    String[] splited;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context= getApplicationContext();
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);

        isLogged();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()  {

            @Override
            public void onSuccess(LoginResult loginResult) {
                SharedPrefManager.getInstance(context).saveFacebookID(loginResult.getAccessToken().getUserId());
                SharedPrefManager.getInstance(context).saveFacebookToken(loginResult.getAccessToken().getToken());
                GetFacebookNameAndLogin();
            }

            @Override
            public void onCancel() {
                Toast.makeText(Login.this, "Login Cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(context, "Login Failed", Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }



    public void GetFacebookNameAndLogin(){
        String facebook_token = SharedPrefManager.getInstance(context).getFacebookToken();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, EndPoints.URL_FACEBOOK_GRAPH+facebook_token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            complete_name = new JSONObject(response).getString("name");
                            //store in db
                            sendToServerAndLogin();
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
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    //storing to mysql server
    private void sendToServerAndLogin() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering ...");
        progressDialog.show();

        final String firebase_token = SharedPrefManager.getInstance(this).getDeviceToken();
        final String facebook_id = SharedPrefManager.getInstance(this).getFacebookID();

        if (firebase_token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        splited = complete_name.split("\\s+");
        name  = splited[0];
        surname = splited [1];
        SharedPrefManager.getInstance(context).saveFacebookName(name);

        System.out.println("firebase"+ firebase_token + "facebook"+ facebook_id );
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);

                            if(obj.getString("message").equals("User logged again")){
                                if(obj.getString("group_id").equals("null")){
                                    startActivity(new Intent(context, HomeActivity.class));
                                }else{
                                    SharedPrefManager.getInstance(context).saveGroupId(Integer.parseInt( obj.getString("group_id")));
                                    SharedPrefManager.getInstance(context).saveDebitCredit(Float.parseFloat( obj.getString("debit_credit")));
                                    SharedPrefManager.getInstance(context).saveGroupName(obj.getString("group_name"));
                                    startActivity(new Intent(context, GroupHomeActivity.class));
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("facebook_id", facebook_id);
                params.put("firebase_token", firebase_token);
                params.put("name", name);
                params.put("surname", surname);
                return params;
            }
        };
        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void isLogged(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Starting...");
        progressDialog.show();

        SharedPrefManager spm= SharedPrefManager.getInstance(this);
        SharedPrefManager.retrieveDeviceToken();
        final String token = spm.getDeviceToken();
        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
        }

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_CONTROL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.getString("message").equals("Just Logged")){

                                SharedPrefManager.getInstance(context).saveFacebookID(obj.getString("facebook_id"));
                                SharedPrefManager.getInstance(context).saveFacebookName(obj.getString("name"));
                                SharedPrefManager.getInstance(context).saveDebitCredit(Float.parseFloat(obj.getString("debit_credit")));
                                if(!(obj.getString("group_id").equals("null"))){
                                    SharedPrefManager.getInstance(context).saveGroupId(Integer.parseInt( obj.getString("group_id")));
                                    SharedPrefManager.getInstance(context).saveGroupName(obj.getString("group_name"));
                                    startActivity(new Intent(context,GroupHomeActivity.class));
                                }else{
                                    startActivity(new Intent(context, HomeActivity.class));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("firebase_token", token);
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
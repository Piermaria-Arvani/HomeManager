package it.unitn.disi.homemanager;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CleanRoundActivity extends AppCompatActivity {

    private CleanRoundAdapter adapter;
    private Context context;
    private String group_id;
    private FloatingActionButton fab;
    private ArrayList<String> descrizioni;
    private ArrayList<String> fb_ids;
    private int contatore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_round);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();
        group_id= SharedPrefManager.getInstance(context).getGroupId();
        adapter = null;
        contatore = 0;


        descrizioni = new ArrayList<>();
        fb_ids = new ArrayList<>();

        getCleaningRound();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("pre cambio");
                for(int j = 0; j< descrizioni.size();j++){
                    System.out.println("Descrizione " + descrizioni.get(j) + " fbid "+ fb_ids.get(j));
                }
                System.out.println("post cambio");
                exchangeCleaningRounds();
            }
        });
    }


    public void exchangeCleaningRounds(){
        for(int i = 0; i < descrizioni.size(); i++){
            final String descrizione = descrizioni.get(i);
            String temp = "";

            if (i == descrizioni.size()-1){
                temp = fb_ids.get(0);
            }else{
                temp = fb_ids.get(i+1);
            }
            final String fb_id = temp;


            System.out.println("Descrizione " + descrizione + " fbid "+ fb_id);
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_INSERT_CLEANING_ROUND,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);

                                contatore++;
                                if (contatore == (fb_ids.size()-1)){
                                    aggiorna();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(CleanRoundActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("facebook_id", fb_id);
                    params.put("group_id",group_id);
                    params.put("description", descrizione);

                    return params;
                }
            };
            MyVolley.getInstance(context).addToRequestQueue(stringRequest);
        }
    }


    public void aggiorna(){
        contatore = 0;
        Toast.makeText(context, "Aggiornato", Toast.LENGTH_LONG).show();
        adapter.reset();
        descrizioni.clear();
        fb_ids.clear();
        getCleaningRound();
    }

    public void getCleaningRound(){

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_GROUP_CLEANINGROUND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            System.out.println("passato2");

                            ListView view = (ListView) findViewById(R.id.listView);
                            final ArrayList<CleanRound> cleanRoundList = new ArrayList<>();

                            JSONArray jsonCleaningRounds = obj.getJSONArray("cleaning_rounds");
                            for (int i = 0; i < jsonCleaningRounds.length(); i++) {
                                CleanRound cleanRound = new CleanRound();
                                JSONObject d = jsonCleaningRounds.getJSONObject(i);
                                String facebook_id = d.getString("facebook_id");
                                String name = d.getString("name");
                                String description = d.getString("description").equals("null") ? "" : d.getString("description");
                                boolean done = false;
                                switch(d.getString("done")){
                                    case "0":
                                        done = false;
                                        break;
                                    case "1":
                                        done = true;
                                        break;
                                    case "null":
                                        done = false;
                                        break;
                                }
                                descrizioni.add(description);
                                fb_ids.add(facebook_id);
                                cleanRound.setFacebook_id(facebook_id);
                                cleanRound.setCleanDescription(description);
                                cleanRound.setName(name);
                                cleanRound.setDone(done);

                                cleanRoundList.add(cleanRound);
                            }

                            System.out.println(obj);

                            adapter = new CleanRoundAdapter(context, R.layout.row_clean_round_list, cleanRoundList);
                            view.setAdapter(adapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CleanRoundActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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

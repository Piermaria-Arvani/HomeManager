package it.unitn.disi.homemanager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactsActivity extends AppCompatActivity {

    ContactsAdapter adapter;
    Context context;
    private String group_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();
        adapter = null;
        group_id= SharedPrefManager.getInstance(context).getGroupId();


        getContacts();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ContactsActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.contacts_dialog);
                // Set dialog title
                dialog.setTitle("Insert new contact");

                // set values for custom dialog components


                dialog.show();
                final Button insertButton = (Button) dialog.findViewById(R.id.insert);
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

                        final TextView nameText = (TextView) dialog.findViewById(R.id.dialog_insert_name);
                        final TextView numberText = (TextView) dialog.findViewById(R.id.dialog_insert_number);

                        final String name =  nameText.getText().toString();
                        final String number = numberText.getText().toString();


                        if (name.length()==0||number.length()==0) {
                            Toast.makeText(context, "Inserisci tutti i dati", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            // send to db
                            insertButton.setEnabled(false);
                            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_INSERT_CONTACT,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                if(obj.getString("message").equals("Contact number saved")){
                                                    Toast.makeText(context, "Inserito con successo", Toast.LENGTH_SHORT).show();
                                                    insertButton.setEnabled(true);
                                                    dialog.dismiss();
                                                    adapter.reset();
                                                    getContacts();}


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(ContactsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }) {

                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("group_id", group_id);
                                    params.put("contact_name", name);
                                    params.put("contact_number", number);

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



    public void getContacts(){

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_GROUP_CONTACTSLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            ListView view = (ListView) findViewById(R.id.listView);
                            final ArrayList<Contact> contactList = new ArrayList<>();

                            JSONArray jsonContacts = obj.getJSONArray("contacts");
                            for (int i = 0; i < jsonContacts.length(); i++) {
                                Contact contact = new Contact();
                                JSONObject d = jsonContacts.getJSONObject(i);
                                String id = d.getString("id");
                                String name = d.getString("contact_name");
                                String number = d.getString("contact_number");

                                contact.setId(id);
                                contact.setName(name);
                                contact.setNumber(number);

                                contactList.add(contact);
                            }

                            System.out.println(obj);

                            adapter = new ContactsAdapter(context, R.layout.row_list_view_contacts, contactList);
                            view.setAdapter(adapter);
                            view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                                    //Creating the instance of PopupMenu
                                    PopupMenu popup = new PopupMenu(ContactsActivity.this, view);
                                    //Inflating the Popup using xml file
                                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                                    //registering popup with OnMenuItemClickListener
                                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                        public boolean onMenuItemClick(MenuItem item) {
                                            if(item.getTitle().equals("Elimina")){
                                                final String id = contactList.get(position).getId();
                                                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_DELETE_CONTACT,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    JSONObject obj = new JSONObject(response);
                                                                    if(obj.getString("message").equals("Contact removed successfully")){
                                                                        Toast.makeText(context, "Eliminato con successo", Toast.LENGTH_SHORT).show();
                                                                        adapter.reset();
                                                                        getContacts();
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


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ContactsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, GroupHomeActivity.class));
    }

}

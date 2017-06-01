package it.unitn.disi.homemanager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ExpenseListFragment extends Fragment {

    private ExpenseAdapter adapter;
    private Context context;
    private String group_id;
    private String facebook_id;
    private Button insertButton;
    private View myFragmentView;

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_expense_list, container, false);
        context = getActivity();
        group_id = SharedPrefManager.getInstance(context).getGroupId();
        facebook_id = SharedPrefManager.getInstance(context).getFacebookID();
        insertButton = (Button) myFragmentView.findViewById(R.id.buttonInsert);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,InsertExpenseActivity.class));
            }
        });

        getExpenses();
        return myFragmentView;
        }

    public void getExpenses(){
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_GROUP_EXPENSES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            ListView view = (ListView) myFragmentView.findViewById(R.id.listViewExpenses);
                            ArrayList<Expense> expenses = new ArrayList<>();

                            JSONArray jsonExpenses = obj.getJSONArray("expenses");
                            for (int i = 0; i < jsonExpenses.length(); i++) {
                                Expense expense = new Expense();
                                JSONObject d = jsonExpenses.getJSONObject(i);
                                String name = d.getString("name");
                                String element = d.getString("element");
                                String cost = d.getString("cost");
                                String date = getDateWellFormed(d.getString("date"));

                                expense.setExpenseDescription(element);
                                expense.setDate(date);
                                expense.setName(name);
                                expense.setMoney(cost);

                                expenses.add(expense);
                            }

                            System.out.println(obj);

                            adapter = new ExpenseAdapter(context, R.layout.row_expenses_list, expenses);
                            view.setAdapter(adapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("group_id", group_id);
                return params;
            }
        };
        MyVolley.getInstance(context).addToRequestQueue(stringRequest);

    }

    private String getDateWellFormed (String completeData){
        Date date = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("passato da getwell");

        try {
            date = date_format.parse(completeData);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String data = String.valueOf(date);

        System.out.println("data" + data);
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

}


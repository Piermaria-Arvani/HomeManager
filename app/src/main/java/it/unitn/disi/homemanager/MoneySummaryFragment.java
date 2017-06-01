package it.unitn.disi.homemanager;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
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


public class MoneySummaryFragment extends Fragment {

    private MoneySummaryAdapter adapter;
    private Context context;
    private String group_id;
    ArrayList<MoneySummary> moneySummaries;
    private Button transferButton ;
    private View myFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        myFragmentView = inflater.inflate(R.layout.fragment_money_summary, container, false);
        context = getActivity();
        group_id = SharedPrefManager.getInstance(context).getGroupId();

        transferButton = (Button) myFragmentView.findViewById(R.id.buttonTransfer);
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,InsertNewTransferActivity.class));
            }
        });

        getMoneySummary();
        return myFragmentView;
    }

    public void getMoneySummary(){
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, EndPoints.URL_GROUP_DEBITS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            ListView view = (ListView) myFragmentView.findViewById(R.id.listViewSummary);
                            moneySummaries = new ArrayList<>();

                            JSONArray jsonMoneySummaries = obj.getJSONArray("moneySummaries");
                            for (int i = 0; i < jsonMoneySummaries.length(); i++) {
                                MoneySummary moneySummary = new MoneySummary();
                                JSONObject d = jsonMoneySummaries.getJSONObject(i);
                                String fb_id = d.getString("facebook_id");
                                String name = d.getString("name");
                                String debit = d.getString("debit_credit");
                                if (debit.length() >5){
                                    debit.substring(0,5);
                                }

                                moneySummary.setFb_id(fb_id);
                                moneySummary.setName(name);
                                moneySummary.setMoney(debit);

                                moneySummaries.add(moneySummary);
                            }

                            System.out.println(obj);

                            adapter = new MoneySummaryAdapter(context, R.layout.row_fragment_money_summary, moneySummaries);
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


}

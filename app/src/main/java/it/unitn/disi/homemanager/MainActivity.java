package it.unitn.disi.homemanager;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //defining views
    private Button buttonSendPush;
    private Button buttonRegister;
    private EditText editTextEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting views from xml
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonSendPush = (Button) findViewById(R.id.buttonSendNotification);

        //adding listener to view
        buttonRegister.setOnClickListener(this);
        buttonSendPush.setOnClickListener(this);
        System.out.println("1");
    }



    @Override
    public void onClick(View view) {
        if (view == buttonRegister) {

        }

        //starting send notification activity
        if(view == buttonSendPush){
            startActivity(new Intent(this, ActivitySendPushNotification.class));
        }


    }


}
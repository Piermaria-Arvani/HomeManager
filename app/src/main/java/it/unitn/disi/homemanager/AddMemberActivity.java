package it.unitn.disi.homemanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class AddMemberActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    Context context;
    private Button buttonGenarate;
    private Button buttonAnnulla;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        context = getApplicationContext();
        imageView = (ImageView) findViewById(R.id.qrcode);
        Bitmap bitmap = getIntent().getParcelableExtra("pic");
        imageView.setImageBitmap(bitmap);

        buttonGenarate = (Button) findViewById(R.id.genera);
        buttonAnnulla = (Button) findViewById(R.id.annulla);

        buttonGenarate.setOnClickListener(this);
        buttonAnnulla.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonAnnulla){
            startActivity(new Intent(this, GroupHomeActivity.class));
        }

        if (view == buttonGenarate){
            imageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, GroupHomeActivity.class));
    }
}

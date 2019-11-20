package com.techkets.balajiconfectioners.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.techkets.balajiconfectioners.R;

public class SplashScreen extends AppCompatActivity {
    private Button go;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        go=findViewById(R.id.lets_begin);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SplashScreen.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }
}

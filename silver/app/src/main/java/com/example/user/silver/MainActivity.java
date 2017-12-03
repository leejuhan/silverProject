package com.example.user.silver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;



public class MainActivity extends AppCompatActivity {

    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout=(LinearLayout)findViewById(R.id.main);
        layout.setBackgroundResource(R.drawable.start);
        Handler handler = new Handler();
        handler.postDelayed(new splashHandler(), 3000);



    }


    private class splashHandler implements Runnable{
        public void run () {
            startActivity(new Intent(getApplication(), login.class));
            MainActivity.this.finish();
        }

    }
}

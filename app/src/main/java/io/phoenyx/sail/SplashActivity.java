package io.phoenyx.sail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO Check connection

        Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(startIntent);
        finish();
    }
}

package com.blockchain.store.playstore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginPromptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_prompt);

        LoadNewUserWelcomeActivity(null);
    }

    @Override
    public void onBackPressed() {}

    public void LoadNewUserWelcomeActivity(View view) {
        Intent myIntent = new Intent(getApplicationContext(), NewUserWelcomeActivity.class);
        startActivityForResult(myIntent, 0);
    }

}

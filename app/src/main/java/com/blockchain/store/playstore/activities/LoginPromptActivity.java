package com.blockchain.store.playstore.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.blockchain.store.playstore.R;

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

package com.blockchain.store.playstore;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import io.ethmobile.ethdroid.EthDroid;

public class IntroLogoActivity extends AppCompatActivity {

    TextView logoTextView;
    VideoView logoVideoView;
    String datadir;
    CryptoUtils crypto;

    final int SplashDisplayLength = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_logo);

        initViewVariables();
        setLogoTextFont();
        setupAndPlayVideo();
        setDatadir();
        startEtherNode();
        loadLoginPromptActivity();
    }

    protected void initViewVariables() {
        logoTextView = (TextView) findViewById(R.id.LogoTextView);
        logoVideoView = (VideoView) findViewById(R.id.LogoVideoView);
    }

    protected void setLogoTextFont() {
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/roboto.ttf");
        logoTextView.setTypeface(tf);
    }

    protected void setupAndPlayVideo() {
        String path = "android.resource://" + getPackageName() + "/" + R.raw.image;
        logoVideoView.setVideoURI(Uri.parse(path));

        logoVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        logoVideoView.start();

    }

    protected void loadLoginPromptActivity() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent myIntent=new Intent(getApplicationContext(),LoginPromptActivity.class );
                startActivityForResult(myIntent,0);
            }
        }, SplashDisplayLength);
    }

    protected void setDatadir() {
        datadir = getFilesDir().getAbsolutePath();
    }

    protected void startEtherNode() {
        crypto = new CryptoUtils();
        crypto.startEtherNodeTestnet(datadir);
    }
}

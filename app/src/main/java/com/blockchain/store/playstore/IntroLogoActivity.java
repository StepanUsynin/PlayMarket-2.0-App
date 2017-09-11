package com.blockchain.store.playstore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.util.ArrayList;

public class IntroLogoActivity extends AppCompatActivity {

    TextView logoTextView;
    VideoView logoVideoView;
    String datadir;

    final int SplashDisplayLength = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_logo);

        initViewVariables();
        setLogoTextFont();
        setupAndPlayVideo();
        PermissionUtils.verifyStoragePermissions(this);
        setDatadir();
        startEtherNode();
        getNearestNodes();
        BuildUtils.printPhoneInfo();
        loadLoginPromptActivity();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
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
        CryptoUtils.buildEtherNodeTestnet(datadir);
    }

    protected void getNearestNodes() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList coords = NodeUtils.getCoordinates();
                    Log.d("Location", coords.get(0).toString() + "," + coords.get(1).toString());

                    String[] nodes = NodeUtils.getNodesList(NodeUtils.NODES_DNS_SERVER);
                    for (String node : nodes) {
                        Log.d("Node", node);
                    }

                    String nearestNodeIP = NodeUtils.getNearestNode(nodes, (double) coords.get(0), (double) coords.get(1));
                    Log.d("Node", nearestNodeIP);

                    initApiUtils(nearestNodeIP);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    protected void initApiUtils(String node) {
        new APIUtils(node);
    }
}


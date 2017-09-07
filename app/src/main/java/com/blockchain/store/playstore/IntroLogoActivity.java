package com.blockchain.store.playstore;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.VideoView;

import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Transaction;

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
        setDatadir();
        startEtherNode();
        getNearestNodes();
        loadLoginPromptActivity();

//        String gasPrice = APIUtils.api.getGasPrice();
//        int nonce = APIUtils.api.getNonce(CryptoUtils.ethdroid.getMainAccount().getAddress().getHex());
//
//        BigInt value = new BigInt(0);
//        value.setInt64(price.inWei());
//
//        Transaction tx = new Transaction(
//                nonce, new Address(CryptoUtils.TEST_ADDRESS),
//                value, new BigInt(200000), new BigInt(Long.valueOf(gasPrice)), CryptoUtils.getDataForBuyApp(String.valueOf(6), String.valueOf(1)).getBytes());
//        try {
//            Transaction transaction = CryptoUtils.ethdroid.getKeyManager().getKeystore().signTxPassphrase(CryptoUtils.ethdroid.getMainAccount(), "Test", tx, new BigInt(3));
//            Log.d("Ether", CryptoUtils.getRawTransaction(transaction));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


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


package com.blockchain.store.playstore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blockchain.store.playstore.dummy.DummyContent;

import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Transaction;
import org.json.JSONObject;

/**
 * An activity representing a single App detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link AppListActivity}.
 */
public class AppDetailActivity extends AppCompatActivity {

    ImageView iconView;
    TextView titleViewBody;
    TextView priceTextView;
    float price = 0.0f;
    String priceWei;
    int idApp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        priceTextView = (TextView) findViewById(R.id.AppPrice);
        titleViewBody = (TextView) findViewById(R.id.AppNameBody);
        iconView = (ImageView) findViewById(R.id.iconView);

        int itemId = getIntent().getExtras().getInt("item_id");
        DummyContent.DummyItem item = DummyContent.ITEMS.get(itemId);

        idApp = Integer.valueOf(item.id);
        price = item.price;

        titleViewBody.setText(item.content);
        priceTextView.setText("Цена: " + String.valueOf(price) + " ETH");

    }

    public void buyApp(View view) {

        BigInt value = new BigInt(0);
        value.setInt64((long) 1100000000000000.0);

        Transaction tx = new Transaction(
                3, new Address(CryptoUtils.CONTRACT_ADDRESS),
                value, new BigInt(200000), new BigInt((long) 30000000000.0), null);
        try {
            Transaction transaction = CryptoUtils.ethdroid.getKeyManager().getKeystore().signTxPassphrase(CryptoUtils.ethdroid.getMainAccount(), "Test", tx, new BigInt(3));
            Log.d("Ether", CryptoUtils.getRawTransaction(transaction));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
//                    String response = new CryptoUtils().buyApp(1, CryptoUtils.getAddress(getApplicationContext()), priceWei);
//                    Log.d("Ether", response);
//
//                    JSONObject parsedResponse = new JSONObject(response);
//                    if (parsedResponse.has("txn")) {
//                        new Handler(Looper.getMainLooper()).post(new Runnable () {
//                            @Override
//                            public void run () {
//                                displayProccessingAlert();
//                            }
//                        });
//
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }


    public void displayDownloadingAlert() {
        Toast.makeText(getApplicationContext(), "App Downloading!",
                Toast.LENGTH_LONG).show();
    }

    public void displayInvestmentCompletedAlert() {
        Toast.makeText(getApplicationContext(), "Tokens Received!",
                Toast.LENGTH_LONG).show();
    }


    public void displayInvestedAlert() {
        Toast.makeText(getApplicationContext(), "Investment Processing!",
                Toast.LENGTH_LONG).show();
    }


    public void displayProccessingAlert() {
        Toast.makeText(getApplicationContext(), "Purchase Processing!",
                Toast.LENGTH_LONG).show();
    }
}

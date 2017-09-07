package com.blockchain.store.playstore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blockchain.store.playstore.dummy.DummyContent;

import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Transaction;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * An activity representing a single App detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link AppListActivity}.
 */
public class AppDetailActivity extends AppCompatActivity {

    ImageView iconView;
    TextView appTitleHeader;
    TextView titleViewBody;
    TextView priceTextView;
    TextView developerTextView;
    Button buyButton;
    EthereumPrice price;
    boolean free;
    int idApp = 0;
    int idCat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        appTitleHeader = (TextView) findViewById(R.id.AppNameTitle);
        developerTextView = (TextView) findViewById(R.id.developerName);
        priceTextView = (TextView) findViewById(R.id.AppPrice);
        buyButton = (Button) findViewById(R.id.buyBtn);
        titleViewBody = (TextView) findViewById(R.id.AppNameBody);
        iconView = (ImageView) findViewById(R.id.iconView);

        int itemId = getIntent().getExtras().getInt("item_id");
        DummyContent.DummyItem item = DummyContent.ITEMS.get(itemId);

        idApp = Integer.valueOf(item.id);
        price = new EthereumPrice(String.valueOf((long)item.price * 1000000000));
        free = item.free;

        appTitleHeader.setText(item.content);
        titleViewBody.setText(item.content);
        developerTextView.setText(item.developer);
        if (free) {
            priceTextView.setText("Free");
            buyButton.setText("Download");
        } else {
            String priceUnit = price.getUnits();
            if  (priceUnit.equals("ETH")) {
                priceTextView.setText("Price: " + String.valueOf(price.inEther()) + "ETH");
                buyButton.setText("Buy: " + String.valueOf(price.inEther()));
            } else if (priceUnit.equals("Gwei")) {
                priceTextView.setText("Price: " + String.valueOf(price.inGwei()) + "Gwei");
                buyButton.setText("Buy: " + String.valueOf(price.inGwei()));
            } else {
                priceTextView.setText("Price: " + String.valueOf(price.inWei()) + "Wei");
                buyButton.setText("Buy: " + String.valueOf(price.inWei()));
            }

        }
    }

    public void buyApp(View view) {
        if (free) {
            displayDownloadingAlert();
            return;
        }

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    String gasPrice = APIUtils.api.getGasPrice();
                    int nonce = APIUtils.api.getNonce(CryptoUtils.ethdroid.getMainAccount().getAddress().getHex());

                    BigInt value = new BigInt(0);
//                    value.setInt64(price.inWei());
                    value.setInt64(Long.decode("40000000000000000"));

                    Transaction tx = new Transaction(
                            nonce, new Address(CryptoUtils.CONTRACT_ADDRESS),
                            value, new BigInt(200000), new BigInt(Long.valueOf(gasPrice)), CryptoUtils.getDataForBuyApp(String.valueOf(6), String.valueOf(1)));

                    try {
                        Transaction transaction = CryptoUtils.ethdroid.getKeyManager().getKeystore().signTxPassphrase(CryptoUtils.ethdroid.getMainAccount(), "Test", tx, new BigInt(3));
                        Log.d("Ether", CryptoUtils.getRawTransaction(transaction));
                        APIUtils.api.sendTX(CryptoUtils.getRawTransaction(transaction));

                        new Handler(Looper.getMainLooper()).post(new Runnable () {
                            @Override
                            public void run() {
                                displayDownloadingAlert();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


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

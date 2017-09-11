package com.blockchain.store.playstore;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.INotificationSideChannel;
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
import java.text.DecimalFormat;

import io.ethmobile.ethdroid.KeyManager;

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
    String idApp2 = "";
    int idApp = 0;
    int idCat = 0;

    private KeyManager keyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        setupKeyManager();

        appTitleHeader = (TextView) findViewById(R.id.AppNameTitle);
        developerTextView = (TextView) findViewById(R.id.developerName);
        priceTextView = (TextView) findViewById(R.id.AppPrice);
        buyButton = (Button) findViewById(R.id.buyBtn);
        titleViewBody = (TextView) findViewById(R.id.AppNameBody);
        iconView = (ImageView) findViewById(R.id.iconView);

        DummyContent.DummyItem item = (DummyContent.DummyItem) getIntent().getExtras().get("item");

        idApp = Integer.valueOf(item.id);
        idApp2 = item.appId;
        idCat = Integer.valueOf(item.category);
        price = new EthereumPrice(String.valueOf(item.price));
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
                priceTextView.setText("Price: " + price.getDisplayPrice());
                buyButton.setText("Buy: " + price.getDisplayPrice());
            } else if (priceUnit.equals("Gwei")) {
                priceTextView.setText("Price: " + price.getDisplayPrice());
                buyButton.setText("Buy: " + price.getDisplayPrice());
            } else {
                priceTextView.setText("Price: " + price.getDisplayPrice());
                buyButton.setText("Buy: " + price.getDisplayPrice());
            }

        }
    }

    protected void setupKeyManager() {
        keyManager = CryptoUtils.setupKeyManager(getFilesDir().getAbsolutePath());
    }

    public void buyApp(View view) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    boolean result = installApk();

                    if (result == true) {
                        new Handler(Looper.getMainLooper()).post(new Runnable () {
                            @Override
                            public void run()
                            {
                                displayDownloadingAlert();
                            }
                        });

                        return;
                    }

                    String gasPrice = APIUtils.api.getGasPrice();
                    int nonce = APIUtils.api.getNonce(keyManager.getAccounts().get(0).getAddress().getHex());

                    BigInt value = new BigInt(0);
                    value.setInt64(price.inWei().longValue());

                    Transaction tx = new Transaction(
                            nonce, new Address(CryptoUtils.CONTRACT_ADDRESS),
                            value, new BigInt(200000), new BigInt(Long.valueOf(gasPrice)), CryptoUtils.getDataForBuyApp(idApp2, String.valueOf(idCat)));

                    try {
                        Transaction transaction = keyManager.getKeystore().signTxPassphrase(keyManager.getAccounts().get(0), "Test", tx, new BigInt(3));
                        Log.d("Ether", CryptoUtils.getRawTransaction(transaction));
                        installApkAfterPurchase(CryptoUtils.getRawTransaction(transaction));

                        if (result == true) {
                            new Handler(Looper.getMainLooper()).post(new Runnable () {
                                @Override
                                public void run()
                                {
                                    displayDownloadingAlert();
                                }
                            });
                        } else {

                        }

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

    public boolean installApk() {
        PermissionUtils.verifyStoragePermissions(this);
        ApkInstaller apkInstaller = new ApkInstaller();
        apkInstaller.setContext(getApplicationContext());
        try {
            apkInstaller.execute(APIUtils.getApkLink(keyManager.getAccounts().get(0).getAddress().getHex(), idApp2, String.valueOf(idCat)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (apkInstaller.isDownloading) {}

        return apkInstaller.successful;
    }

    public boolean installApkAfterPurchase(String tx) {
        PermissionUtils.verifyStoragePermissions(this);
        ApkInstaller apkInstaller = new ApkInstaller();
        apkInstaller.setContext(getApplicationContext());
        apkInstaller.execute(APIUtils.getSendTxLink("0x" + tx, idApp2, String.valueOf(idCat)));

        while (apkInstaller.isDownloading) {}

        return apkInstaller.successful;
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

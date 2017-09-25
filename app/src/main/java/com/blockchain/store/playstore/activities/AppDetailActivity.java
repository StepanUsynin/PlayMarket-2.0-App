package com.blockchain.store.playstore.activities;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blockchain.store.playstore.data.content.AppContent;
import com.blockchain.store.playstore.R;
import com.blockchain.store.playstore.data.types.EthereumPrice;
import com.blockchain.store.playstore.utilities.data.ClipboardUtils;
import com.blockchain.store.playstore.utilities.installer.ApkInstaller;
import com.blockchain.store.playstore.utilities.net.APIUtils;
import com.blockchain.store.playstore.crypto.CryptoUtils;
import com.blockchain.store.playstore.utilities.data.ImageUtils;
import com.blockchain.store.playstore.utilities.device.PermissionUtils;

import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Transaction;

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
    String hashIPFS;

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

        AppContent.AppItem item = (AppContent.AppItem) getIntent().getExtras().get("item");

        idApp = Integer.valueOf(item.id);
        idApp2 = item.appId;
        idCat = Integer.valueOf(item.category);
        price = new EthereumPrice(String.valueOf(item.price));
        free = item.free;
        hashIPFS = item.hashIPFS;

        appTitleHeader.setText(item.name);
        titleViewBody.setText(item.name);
        developerTextView.setText(item.developer);

        if (item.icon != null) {
            iconView.setImageBitmap(ImageUtils.getBitmapFromBase64(item.icon));
        }

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

        if (APIUtils.api.balance.isZero() && !free) {
            displayNotEnoughMoneyAlert();
            showAddFundsDialog();
            return;
        }

        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.purchase_confirm_dialog);

        TextView priceText = (TextView) d.findViewById(R.id.priceText);
        if (free) {
            priceText.setText("Free");
        } else {
            priceText.setText(buyButton.getText());
        }

        TextView appTitleText = (TextView) d.findViewById(R.id.appTitleText);
        appTitleText.setText(appTitleHeader.getText());

        TextView balanceText = (TextView) d.findViewById(R.id.balanceText);
        balanceText.setText(APIUtils.api.balance.getDisplayPrice());

        ImageView appIconView = (ImageView) d.findViewById(R.id.appIcon);
        appIconView.setImageDrawable(iconView.getDrawable());

        d.show();

        TextView addFundsBtn = (TextView) d.findViewById(R.id.addMoneyButton);
        addFundsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAddFundsDialog();
            }
        });


        Button close_btn = (Button) d.findViewById(R.id.close_button);
        close_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                purchase();
                d.dismiss();
            }
        });
    }

    public void showAddFundsDialog() {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.show_address_dialog);

        final TextView addressTextView = (TextView) d.findViewById(R.id.addressTextView);
        try {
            addressTextView.setText(keyManager.getAccounts().get(0).getAddress().getHex());
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView balanceTextView = (TextView) d.findViewById(R.id.balanceText);
        balanceTextView.setText(APIUtils.api.balance.getDisplayPrice());

        Button close_btn = (Button) d.findViewById(R.id.close_button);
        close_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                d.dismiss();
            }
        });

        Button copyAddressButton = (Button) d.findViewById(R.id.copyAddressButton);
        copyAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardUtils.copyToClipboard(getApplicationContext(), addressTextView.getText().toString());
                showCopiedAlert();
            }
        });

        d.show();
    }

    private void showCopiedAlert() {
        Toast.makeText(getApplicationContext(), "Address Copied!",
                Toast.LENGTH_LONG).show();
    }

    public void purchase() {
        displayProccessingAlert();

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
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

                            installApk(CryptoUtils.getRawTransaction(transaction));

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
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

        APIUtils.api.updateBalance(keyManager);

    }

    public boolean installApk(String tx) {
        PermissionUtils.verifyStoragePermissions(this);

        ApkInstaller apkInstaller = new ApkInstaller();
        apkInstaller.setContext(getApplicationContext());
        try {
            apkInstaller.execute(APIUtils.getApkLink(keyManager.getAccounts().get(0).getAddress().getHex(), idApp2, String.valueOf(idCat), hashIPFS),
                    APIUtils.getSendTxLink("0x" + tx, idApp2, String.valueOf(idCat), hashIPFS));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void displayNotEnoughMoneyAlert() {
        Toast.makeText(getApplicationContext(), "Insufficient Funds!",
                Toast.LENGTH_LONG).show();
    }

    public void investApp(View view) {
    }

    public void goBackToList(View view) {
        finish();
    }
}

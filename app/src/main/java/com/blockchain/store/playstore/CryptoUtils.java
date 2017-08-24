package com.blockchain.store.playstore;

import org.ethereum.geth.Account;

import java.util.List;

import io.ethmobile.ethdroid.EthDroid;
import io.ethmobile.ethdroid.KeyManager;

/**
 * Created by samsheff on 24/08/2017.
 */

public class CryptoUtils {

    public KeyManager keyManager;
    private List<Account> accounts;

    public void initKeyManager(String datadir) {
        keyManager = KeyManager.newKeyManager(datadir);
    }

    public String getLastAddress() {
        try {
            accounts = keyManager.getAccounts();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (accounts.isEmpty()){
            return null;
        }

        return(accounts.get(accounts.size()-1).getAddress().getHex().toString());
    }

    public void startEtherNode(String datadir) {
        try {
            new EthDroid.Builder(datadir)
                    .onMainnet()
                    .build()
                    .start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startEtherNodeTestnet(String datadir) {
        try {
            new EthDroid.Builder(datadir)
                    .onTestnet()
                    .build()
                    .start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

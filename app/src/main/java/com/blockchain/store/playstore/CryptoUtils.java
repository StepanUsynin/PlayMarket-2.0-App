package com.blockchain.store.playstore;

import org.ethereum.geth.Account;
import org.ethereum.geth.Transaction;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.ethmobile.ethdroid.EthDroid;
import io.ethmobile.ethdroid.KeyManager;

/**
 * Created by samsheff on 24/08/2017.
 */

public class CryptoUtils {

    public KeyManager keyManager;
    public static EthDroid ethdroid;

    public static final String CONTRACT_ADDRESS = "0x8be909dA263C51979e7B23219F0cCfB6f6C25087";
    public static final String TEST_ADDRESS = "0x5e5c1c8e03472666e0b9e218153869dcbc9c1e65";

    public static void buildEtherNodeTestnet(String datadir) {
        try {
            ethdroid = new EthDroid.Builder(datadir)
                    .onTestnet()
                    .withDatadirPath(datadir)
                    .withKeyManager(KeyManager.newKeyManager(datadir))
                    .withDefaultContext()
                    .build();
            ethdroid.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRawTransaction(Transaction transaction) {
        String transactionInfo = transaction.toString();
        Pattern pattern = Pattern.compile("Hex:.*");
        Matcher matcher = pattern.matcher(transactionInfo);
        if (matcher.find())
        {
            return matcher.group(0).replaceAll("Hex:\\s*", "");
        } else {
            return "";
        }
    }
}

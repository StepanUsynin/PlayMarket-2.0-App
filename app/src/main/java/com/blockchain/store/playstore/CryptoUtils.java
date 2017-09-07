package com.blockchain.store.playstore;

import android.util.Log;

import org.apache.commons.codec.binary.Hex;
import org.ethereum.geth.Account;
import org.ethereum.geth.Transaction;
import org.web3j.abi.datatypes.Bytes;
import org.web3j.crypto.Hash;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.ethmobile.ethdroid.EthDroid;
import io.ethmobile.ethdroid.KeyManager;

import static org.web3j.crypto.Hash.sha3;

/**
 * Created by samsheff on 24/08/2017.
 */

public class CryptoUtils {

    public KeyManager keyManager;
    public static EthDroid ethdroid;

    public static final String CONTRACT_ADDRESS = "0x77bC6e6B70029C9478265B15C8cF1e4E2fB7B133";
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

    public static byte[] getDataForBuyApp(String appId, String idCat) {
        byte[] hash = sha3("buyApp(uint256,uint256)".getBytes());
        String hashString = bytesToHexString(hash);

        String functionHash = hashString.substring(0, 8);

        Log.d("Ether", functionHash);
        Log.d("Ether", hashString);

        String appIdEnc = String.format("%64s", appId).replace(' ', '0');
        String catIdEnc = String.format("%64s", idCat).replace(' ', '0');
        Log.d("Ether", appIdEnc);
        Log.d("Ether", catIdEnc);

        String data = functionHash + appIdEnc + catIdEnc;
        Log.d("Ether", data);

        return hexStringToByteArray(data);
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b&0xff));
        }

        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }
}

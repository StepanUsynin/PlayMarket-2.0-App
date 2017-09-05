package com.blockchain.store.playstore;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by samsheff on 05/09/2017.
 */

public class APIUtils {

    public static APIUtils api;

    public static final String GET_BALANCE_URL = "/v1/getBalance";
    public static final String GET_GAS_PRICE_URL = "/vi/getGasPrice";
    public static final String SEND_TX_URL = "/vi/sendTX";
    public static final String START_URL = "/vi/start";

    public String nodeUrl = "http://";

    public APIUtils(String node) {
        this.nodeUrl = nodeUrl + node;
        api = this;
    }

    public String getBalance(String address) throws IOException {
        HttpClient client = new DefaultHttpClient();

        HttpPost request = new HttpPost("https://p.playmarket.io" + GET_BALANCE_URL);

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("address", address));

        //Encoding POST data
        try {
            request.setEntity(new UrlEncodedFormEntity(nameValuePair));

        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        HttpResponse response;
        response = client.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        String balance = "";
        try {
            balance = new JSONObject(responseBody).getString("balance");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("NET",  "Get Balance: " + balance);

        return balance;
    }

    public long getGasPrice(String address) throws IOException {
        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(nodeUrl + GET_GAS_PRICE_URL);

        HttpResponse response;
        response = client.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        Log.d("NET",  "Get Gas Price: " + responseBody);

        return 0;
    }

    public long sendTX(String rawTransaction) throws IOException {
        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(nodeUrl + SEND_TX_URL);

        HttpResponse response;
        response = client.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        Log.d("NET",  "Send TX: " + responseBody);

        return 0;
    }

    public long start() throws IOException {
        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(nodeUrl + START_URL);

        HttpResponse response;
        response = client.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        Log.d("NET",  "API Start: " + responseBody);

        return 0;
    }

}

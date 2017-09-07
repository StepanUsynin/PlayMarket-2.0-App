package com.blockchain.store.playstore;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
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
    public static final String GET_NONCE_URL = "/v1/getTransactionCount";
    public static final String GET_GAS_PRICE_URL = "/v1/getGasPrice";
    public static final String SEND_TX_URL = "/v1/sendRawTransaction";
    public static final String GET_APP_URL = "/v1/getApp";

    public String nodeUrl = "https://n";

    public APIUtils(String node) {
        this.nodeUrl = nodeUrl + node + ".playmarket.io";

        api = this;
    }

    public String getBalance(String address) throws IOException {
        HttpClient client = createHttpClient();
        HttpPost request = new HttpPost(nodeUrl + GET_BALANCE_URL);

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
        Log.d("NET", responseBody);

        String balance = "";
        try {
            balance = new JSONObject(responseBody).getString("balance");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("NET",  "Get Balance: " + balance);

        return balance;
    }

    public String getGasPrice() throws IOException {
        HttpClient client = createHttpClient();
        HttpPost request = new HttpPost(nodeUrl + GET_GAS_PRICE_URL);

        HttpResponse response;
        response = client.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        Log.d("NET",  "Get Gas Price: " + responseBody);

        String gasPrice = "0";
        try {
            gasPrice = new JSONObject(responseBody).getString("gasPrice");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return gasPrice;
    }

    public int getNonce(String address) throws IOException {
        HttpClient client = createHttpClient();
        HttpPost request = new HttpPost(nodeUrl + GET_NONCE_URL);

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
        Log.d("NET", responseBody);

        int nonce = 0;
        try {
            nonce = new JSONObject(responseBody).getInt("getTransactionCount");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("NET",  "Get Nonce: " + nonce);

        return nonce;
    }

    public boolean sendTX(String rawTransaction) throws IOException {
        HttpClient client = createHttpClient();
        HttpPost request = new HttpPost(nodeUrl + SEND_TX_URL);

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("serializedTx", "0x" + rawTransaction));

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
        Log.d("NET", responseBody);

        String status = "";
        try {
            status = new JSONObject(responseBody).getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("NET",  "Send TX: " + status);

        return true;
    }

    public JSONArray start() throws IOException {
        HttpClient client = createHttpClient();
        HttpPost request = new HttpPost(nodeUrl + GET_APP_URL);

        HttpResponse response;
        response = client.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        Log.d("NET",  "Get App: " + responseBody);

        JSONArray apps = new JSONArray();
        try {
            apps = new JSONObject(responseBody).getJSONArray("getApp");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return apps;
    }

    public JSONArray getCategory(String category) throws IOException {
        HttpClient client = createHttpClient();
        HttpPost request = new HttpPost(nodeUrl + GET_APP_URL);

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("idCTG", category));

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
        Log.d("NET",  "Get App: " + responseBody);

        JSONArray apps = new JSONArray();
        try {
            apps = new JSONObject(responseBody).getJSONArray("getApp");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return apps;
    }

    public JSONArray pageCategory(String category, int startId, int count) throws IOException {
        HttpClient client = createHttpClient();
        HttpPost request = new HttpPost(nodeUrl + GET_APP_URL);

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
        nameValuePair.add(new BasicNameValuePair("idCTG", category));
        nameValuePair.add(new BasicNameValuePair("startIdApp", String.valueOf(startId)));
        nameValuePair.add(new BasicNameValuePair("startIdCount", String.valueOf(count)));

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
        Log.d("NET",  "Get App: " + responseBody);

        JSONArray apps = new JSONArray();
        try {
            apps = new JSONObject(responseBody).getJSONArray("getApp");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return apps;
    }



    private HttpClient createHttpClient() {
        return new DefaultHttpClient();
    }

}

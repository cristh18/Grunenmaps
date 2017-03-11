package com.leinaro.grunenthal;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Adela on 6/02/2016.
 */
public class GetTerms extends AsyncTask<String, String, JSONObject> {

    public interface AsyncResponse {
        void getTermsComplete(JSONObject output);
    }

    public AsyncResponse asyncResponse = null;

    public GetTerms(AsyncResponse delegate){
        this.asyncResponse = delegate;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject response = null;

        try {
            response = new JSONObject(GetSomething());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

    String result = "";

    final String GetSomething()
    {
        String url = "http://grt.disenostudio.com.co/api/conditions";
        BufferedReader inStream = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpRequest = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpRequest);
            inStream = new BufferedReader(
                    new InputStreamReader(
                            response.getEntity().getContent()));

            StringBuffer buffer = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = inStream.readLine()) != null) {
                buffer.append(line + NL);
            }
            inStream.close();

            result = buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(JSONObject result) {

        asyncResponse.getTermsComplete(result);
    }
}

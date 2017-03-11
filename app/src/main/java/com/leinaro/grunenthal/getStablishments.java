package com.leinaro.grunenthal;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.leinaro.grunenthal.GetStablishment.retrofit;

/**
 * Created by Adela on 6/02/2016.
 */
public class getStablishments {

    public interface AsyncResponse {
        void getStablishmentsComplete(List<Pharmacies> output);
    }

    public AsyncResponse asyncResponse = null;

    public getStablishments(AsyncResponse delegate){
        this.asyncResponse = delegate;
    }

//    @Override
//    protected JSONObject doInBackground(String... params) {
//        JSONObject response = null;
//        Log.d("iarl", "iarl iarl start ");
//
////        try {
////            response = new JSONObject(GetSomething());
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
////
//        Log.d("iarl", "iarl iarl "+response.toString());
//        return response;
//    }

//    String result = "";

    final void GetSomething() {

        GetStablishment gitHubService = retrofit.create(GetStablishment.class);
        Call<List<Pharmacies>> call = gitHubService.getALlPharmacies();
//        List<Pharmacies> result = call.execute().body();
        call.enqueue(new Callback<List<Pharmacies>>() {
            @Override
            public void onResponse(Call<List<Pharmacies>> call, Response<List<Pharmacies>> response) {
                if (response.isSuccessful()){
                    asyncResponse.getStablishmentsComplete(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Pharmacies>> call, Throwable t) {

            }
        });


//        String url = "http://grt.disenostudio.com.co/api/all";
//        BufferedReader inStream = null;
//        try {
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpGet httpRequest = new HttpGet(url);
//            HttpResponse response = httpClient.execute(httpRequest);
//            inStream = new BufferedReader(
//                    new InputStreamReader(
//                            response.getEntity().getContent()));
//
//            StringBuffer buffer = new StringBuffer("");
//            String line = "";
//            String NL = System.getProperty("line.separator");
//            while ((line = inStream.readLine()) != null) {
//                buffer.append(line + NL);
//            }
//            inStream.close();
//
//            result = buffer.toString();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } finally {
//            if (inStream != null) {
//                try {
//                    inStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return result;
    }

//    @Override
//    protected void onPostExecute(JSONObject result) {
//        asyncResponse.getStablishmentsComplete(result);
//    }
}

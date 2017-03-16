package com.leinaro.grunenthal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.leinaro.grunenthal.GetStablishment.retrofit;

/**
 * Created by Adela on 6/02/2016.
 */
public class getStablishments {

    public interface AsyncResponse {
        void getStablishmentsComplete(ResponseGetAllPharmacies output);
    }

    public AsyncResponse asyncResponse = null;

    public getStablishments(AsyncResponse delegate) {
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
        Call<ResponseGetAllPharmacies> call = gitHubService.getALlPharmacies();
//        List<Pharmacies> result = call.execute().body();
        call.enqueue(new Callback<ResponseGetAllPharmacies>() {
            @Override
            public void onResponse(Call<ResponseGetAllPharmacies> call, Response<ResponseGetAllPharmacies> response) {
                if (response.isSuccessful()) {
                    asyncResponse.getStablishmentsComplete(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseGetAllPharmacies> call, Throwable t) {

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

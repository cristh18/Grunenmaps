package com.leinaro.grunenthal;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class Server {

    private final ServerRequests request;


    public interface ServerRequests {
        @GET("all")
        Call<ResponseGetAllPharmacies> getALlPharmacies();
    }

//    public static GenericResponse errorResponses(ResponseBody responseBody) {
//        return errorResponses(responseBody);
//    }


//    public Call<PhoneResponse> getTerms(DevicePhone phone) {
//        return mAuthenticate.validatePhone(phone);
//    }

    public Call<ResponseGetAllPharmacies> getStablishment() {
        return request.getALlPharmacies();
    }

    /*****************************************************************
     * CONSTRUCTOR
     *****************************************************************/

    public Server() {
        request = getAuthAdapter().create(ServerRequests.class);
    }

    public Retrofit getAuthAdapter() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://grt.disenostudio.com.co/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;

    }

//    public GenericResponse errorResponse(ResponseBody errorBody) {
//        try {
//            return (GenericResponse) getAuthAdapter().responseBodyConverter(GenericResponse.class, new Annotation[0]).convert(errorBody);
//        } catch (NullPointerException e) {
//            return GenericResponse.create(StatusResponse.create(R.string.error_reading_response));
//        } catch (IOException e) {
//            throw new IllegalArgumentException("error body can't be serialized");
//        }
//    }

}


package com.leinaro.grunenthal;


import java.util.List;

public class ResponseGetAllPharmacies {
    public boolean result;
    public List<Response> data;
    public String msg;

    public class Response {
        public List<Pharmacies> pharmacies1;
        public List<Pharmacies> pharmacies2;
    }
}

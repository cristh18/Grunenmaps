package com.leinaro.grunenthal;


import java.util.List;

class ResponseGetAllPharmacies {
    public boolean result;
    public List<Response> data;

    public class Response {
        public List<Pharmacies> pharmacies1;
        public List<Pharmacies> pharmacies2;
    }
}

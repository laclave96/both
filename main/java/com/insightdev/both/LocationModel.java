package com.insightdev.both;

public class LocationModel {

   private String city, country, countryCode;
   private boolean successful;

    public LocationModel(String city, String country, String countryCode, boolean successful) {
        this.city = city;
        this.country = country;
        this.countryCode = countryCode;
        this.successful = successful;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public boolean isSuccessful() {
        return successful;
    }
}

package com.isariev.paymentservice.utils;

import com.maxmind.geoip2.model.CityResponse;

public interface CustomerGeoData {
    CityResponse getCityResponse();

    String getClientMACAddress();
}

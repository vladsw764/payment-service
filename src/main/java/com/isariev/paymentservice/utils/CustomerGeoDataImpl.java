package com.isariev.paymentservice.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;

@Component
public class CustomerGeoDataImpl implements CustomerGeoData {

    private final File database = new File("src/main/resources/GeoLite2-City.mmdb");

    @Override
    public CityResponse getCityResponse() {
        CityResponse cityResponse;
        try (DatabaseReader reader = new DatabaseReader.Builder(database).build();) {
            InetAddress ipAddress = InetAddress.getByName(getIp());
            cityResponse = reader.city(ipAddress);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cityResponse;
    }

    @Override
    public String getClientMACAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] macBytes = networkInterface.getHardwareAddress();
                if (macBytes != null && macBytes.length == 6) {
                    // Convert the MAC address bytes to a formatted string
                    StringBuilder macAddress = new StringBuilder();
                    for (byte b : macBytes) {
                        macAddress.append(String.format("%02X:", b));
                    }
                    if (macAddress.length() > 0) {
                        macAddress.deleteCharAt(macAddress.length() - 1); // Remove the trailing ":"
                        return macAddress.toString().toLowerCase();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getIp() throws Exception {
        URL ipUrl = new URI("https://checkip.amazonaws.com").toURL();

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    ipUrl.openStream()));
            return in.readLine();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

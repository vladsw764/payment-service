package com.isariev.paymentservice.dto.mapper;

import com.isariev.paymentservice.dto.request.CustomerRequestDto;
import com.isariev.paymentservice.dto.response.CustomerResponseDto;
import com.isariev.paymentservice.model.Customer;
import com.isariev.paymentservice.utils.CustomerGeoData;
import com.isariev.paymentservice.utils.CustomerGeoDataImpl;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    private final CustomerGeoData customerGeoData;
    private final CityResponse cityResponse;

    public CustomerMapper(CustomerGeoDataImpl customerGeoData) {
        this.customerGeoData = customerGeoData;
        this.cityResponse = customerGeoData.getCityResponse();
    }

    public Customer toEntity(CustomerRequestDto requestDto) {
        return Customer.builder()
                .uid(null)
                .firstName(requestDto.first_name())
                .lastName(requestDto.last_name())
                .email(requestDto.email())
                .country(requestDto.country())
                .city(cityResponse.getCity().getName())
                .zip(cityResponse.getPostal().getCode())
                .address("National park, 9")
                .phoneNumber(requestDto.phone_number())
                .deviceId(customerGeoData.getClientMACAddress())
                .accountCreationCountry(cityResponse.getCountry().getIsoCode())
                .build();
    }

    public CustomerResponseDto mapToResponseDto(Customer customer) {
        return new CustomerResponseDto(
                customer.getFirstName(),
                customer.getLastName(),
                customer.getCountry(),
                customer.getCity(),
                customer.getZip(),
                customer.getAddress(),
                customer.getPhoneNumber(),
                customer.getDeviceId(),
                customer.getUid().toString(),
                customer.getAccountCreationDate() == null ? null : customer.getAccountCreationDate().toString(),
                customer.getAccountCreationCountry(),
                customer.getEmail()
        );
    }
}

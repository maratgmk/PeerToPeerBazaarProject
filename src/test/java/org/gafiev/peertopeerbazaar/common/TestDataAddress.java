package org.gafiev.peertopeerbazaar.common;

import com.github.javafaker.Faker;
import org.gafiev.peertopeerbazaar.dto.api.request.AddressCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.AddressFilterRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Set;

public class TestDataAddress {
    public static final Faker faker = new Faker();
    public static final long EXIST_ADDRESS_ID = 15L;

    public static Address getAddressSample(){
        String latString = faker.address().latitude().replace(",",".");
        double latValue = Double.parseDouble(latString);
        double latitude = Double.parseDouble(String.format(Locale.US,"%.6f",latValue));
        if(latitude < 0) latitude = - latitude;

        String longString = faker.address().longitude().replace(",",".");
        double longValue = Double.parseDouble(longString);
        double longitude = Double.parseDouble(String.format(Locale.US,"%.6f",longValue));
        if(longitude < 0) longitude = - longitude;

        double attitude = Double.parseDouble(String.format(Locale.US,"%.2f",faker.number().randomDouble(2,1,1000)));
        double accuracy = Double.parseDouble(String.format(Locale.US,"%.2f",faker.number().randomDouble(2,0,1)));

        return Address.builder()
                .town(faker.address().cityName())
                .street(faker.address().streetName())
                .numberBuilding(faker.number().numberBetween(1,500))
                .zipCode(faker.number().numberBetween(100000, 999999))
                .latitude(latitude)
                .longitude(longitude)
                .attitude(attitude)
                .accuracy(accuracy)
                .createdAt(Instant.now().minus(faker.number().numberBetween(1,365), ChronoUnit.DAYS))
                .build();
    }

    public static AddressFilterRequest getAAddressFilterRequest(){
        return AddressFilterRequest.builder()
                .ids(Set.of(101L,102L,103L,104L,105L))
                .town("Вятские поляны")
                .street("Кукина")
                .numbers(Set.of(61,62,63,91,92,93))
                .attitudeHigh(250.55)
                .attitudeLow(12.75)
                .latitudeNorth(58.224843)
                .latitudeSouth(55.224843)
                .longitudeLeft(50.066043)
                .longitudeRight(52.066043)
                .build();
    }

    public static AddressCreateRequest addressCreateRequest(){
        String latString = faker.address().latitude().replace(",",".");
        double latValue = Double.parseDouble(latString);
        double latitude = Double.parseDouble(String.format(Locale.US,"%.6f",latValue));
        if(latitude < 0) latitude = - latitude;

        String longString = faker.address().longitude().replace(",",".");
        double longValue = Double.parseDouble(longString);
        double longitude = Double.parseDouble(String.format(Locale.US,"%.6f",longValue));
        if(longitude < 0) longitude = - longitude;

        double attitude = Double.parseDouble(String.format(Locale.US,"%.2f",faker.number().randomDouble(2,1,1000)));
        double accuracy = Double.parseDouble(String.format(Locale.US,"%.2f",faker.number().randomDouble(2,0,1)));
        return AddressCreateRequest.builder()
                .town(faker.address().cityName())
                .street(faker.address().streetName())
                .numberBuilding(faker.number().numberBetween(0,100))
                .zipCode(faker.number().numberBetween(100000,999999))
                .latitude(latitude)
                .longitude(longitude)
                .attitude(attitude)
                .accuracy(accuracy)
                .createdAt(Instant.now().minus(faker.number().numberBetween(1,365), ChronoUnit.DAYS))
                .build();
    }
}

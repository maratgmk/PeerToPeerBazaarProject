package org.gafiev.peertopeerbazaar.common;

import com.github.javafaker.Faker;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.AddressDroneRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.request.BuyerOrderDroneRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.request.DeliveryDroneRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class TestDataDrone {

    public final Faker faker = new Faker();
    public static final Long EXISTING_DELIVERY_ID1 = 15L;
    public static final Long EXISTING_DELIVERY_ID2 = 16L;
    public static final Long EXISTING_DELIVERY_ID3 = 17L;;
    public static final Long EXISTING_DRONE_ID1 = 101L;
    public static final Long EXISTING_DRONE_ID2 = 102L;
    public static final Long EXISTING_DRONE_ID3 = 103L;
    public static final Long EXISTING_DRONE_SERVICE_ID1 = 1001L;
    public static final Long EXISTING_DRONE_SERVICE_ID2 = 1002L;
    public static final Long EXISTING_DRONE_SERVICE_ID3 = 1003L;
    public static final ExistingDroneTestData EXISTING_DRONE1 = new ExistingDroneTestData(EXISTING_DRONE_ID1, EXISTING_DRONE_SERVICE_ID1, Set.of(EXISTING_DELIVERY_ID1,EXISTING_DELIVERY_ID2));
    public static final ExistingDroneTestData EXISTING_DRONE2 = new ExistingDroneTestData(EXISTING_DRONE_ID2, EXISTING_DRONE_SERVICE_ID2, Set.of(EXISTING_DELIVERY_ID3));
    public static final ExistingDroneTestData EXISTING_DRONE3 = new ExistingDroneTestData(EXISTING_DRONE_ID3, EXISTING_DRONE_SERVICE_ID3, Set.of());


    public static final DroneFilterRequest FILTER_BY_IDS = DroneFilterRequest.builder()
            .droneIds(Set.of(EXISTING_DRONE1.id, EXISTING_DRONE2.id))
            .build();

    public static final DroneFilterRequest FILTER_BY_SERVICE_IDS = DroneFilterRequest.builder()
            .droneServiceIds(Set.of(EXISTING_DRONE1.serviceId, EXISTING_DRONE2.serviceId))
            .build();

    public static final DroneFilterRequest FILTER_BY_DELIVERY_IDS = DroneFilterRequest.builder()
            .deliveryIds(Set.of(EXISTING_DELIVERY_ID1, EXISTING_DELIVERY_ID2,EXISTING_DELIVERY_ID3))
            .build();

    public static Drone getDroneSample() {
        return Drone.builder()
                .droneServiceId(ThreadLocalRandom.current().nextLong(10000,99999))
                .droneStatus(DroneStatus.values()[ThreadLocalRandom.current().nextInt(DroneStatus.values().length)])
                .createdAt(Instant.now().minusSeconds(3600))
                .deliverySet(new HashSet<>())
                .build();
    }




    public static DeliveryDroneRequest getDeliveryDroneRequest() {
        return DeliveryDroneRequest.builder()
                .timeSlot(TestDataDrone.getTimeSlotSample())
                .buyerOrder(TestDataDrone.getBuyerOrderDroneRequest())
                .fromAddress(TestDataDrone.getAddressDroneRequest())
                .toAddress(TestDataDrone.getAddressDroneRequest())
                .build();
    }

    public static DroneCreateRequest getDroneCreateRequest(Set<Long> idsToRemove, Set<Long> idsToAdd) {
        return new DroneCreateRequest(idsToRemove, idsToAdd);
    }

    public static BuyerOrderDroneRequest getBuyerOrderDroneRequest() {
        return  BuyerOrderDroneRequest.builder()
                .weightKg(new BigDecimal("19.48"))
                .volumeLtr(new BigDecimal("19.95"))
                .build();
    }

    public static AddressDroneRequest getAddressDroneRequest() {
        Address address = TestDataAddress.getAddressSample();
        return AddressDroneRequest.builder()
                .id(ThreadLocalRandom.current().nextLong())
                .town(address.getTown())
                .zipCode(address.getZipCode())
                .street(address.getStreet())
                .numberBuilding(address.getNumberBuilding())
                .longitude(address.getLongitude())
                .latitude(address.getLatitude())
                .attitude(address.getAttitude())
                .accuracy(address.getAccuracy())
                .build();
    }

    public static TimeSlotResponse getTimeSlotSample() {
        return new TimeSlotResponse(LocalDateTime.of(2025, 11, 20, 0, 0, 0)
                .withHour(ThreadLocalRandom.current().nextInt(11))
                .plusMinutes(ThreadLocalRandom.current().nextInt(59))
                , LocalDateTime.of(2025, 11, 20, 12, 45, 0));
    }

    public static Set<TimeSlotResponse> getListTimeSlots() {
        return Set.of(getTimeSlotSample(), getTimeSlotSample());

    }

    public record ExistingDroneTestData(Long id, Long serviceId, Set<Long> deliveryIds) {
    }
}

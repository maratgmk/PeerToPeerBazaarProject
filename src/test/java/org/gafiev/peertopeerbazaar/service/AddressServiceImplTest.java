package org.gafiev.peertopeerbazaar.service;

import org.gafiev.peertopeerbazaar.entity.delivery.Address;
import org.gafiev.peertopeerbazaar.repository.AddressRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.longThat;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {
//    private long counter = 0L;
//
//    @InjectMocks
//    private AddressServiceImpl addressService;
//
//    @Mock
//    private AddressRepository addressRepository;
//
//    @BeforeEach
//    void setUp() {
//        Mockito.reset(addressRepository);
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
////
////    @Test
////    void getAddressById_addressFound_addressNotNull() {
////        Address expected = getAddress(); // eq используется для объектов(заворачивается)
////        Address testAddress = getAddress();
////        assertNotEquals(expected,testAddress);
////        Mockito.when(addressRepository.findById(eq(expected.getId()))).thenReturn(Optional.of(expected));
////        Mockito.when(addressRepository.findById(eq(testAddress.getId()))).thenReturn(Optional.of(testAddress));
////        Optional<Address> actual = addressService.getAddressById(expected.getId());
////        assertEquals(expected,actual);
////    }
//    @Test
//    void getAddressById_addressFound_throwIllegalArgumentException() {
//        Address testAddress = getAddress();
//        long wrongId = -1L;
//
//        Mockito.when(addressRepository.findById(eq(testAddress.getId()))).thenReturn(Optional.of(testAddress));
//        Exception exception = assertThrows(IllegalArgumentException.class,() -> addressService.getAddressById(wrongId));
//        assertEquals("Address not found with id " + wrongId,exception.getMessage());
//    }
//
//    private   Address getAddress() {
//        Address expected = new Address();
//        expected.setId(counter++);
//        expected.setTown("Tulsa");
//        expected.setStreet("First avenue");
//        expected.setNumberBuilding(123);
//        expected.setZipCode(ThreadLocalRandom.current().nextInt(1,10000));
//        expected.setLatitude(55.753744);
//        expected.setLongitude(37.619722);
//        expected.setAccuracy(1.5F);
//        return expected;
//    }
//
//    @Test
//    void createAddress() {
//        Address address = getAddress();
//
//        addressService.createAddress(address);
//
//    }
//
//    @Test
//    void getAllAddresses() {
//    }
//
//    @Test
//    void getAddressesByTown() {
//    }
//
//    @Test
//    void getAddressesByZipCode() {
//    }
//
//    @Test
//    void getAddressesByCoordinates() {
//    }
//
//    @Test
//    void countAddressesInTown() {
//    }
//
//    @Test
//    void getAddressesByStreet() {
//    }
//
//    @Test
//    void updateAddress() {
//    }
//
//    @Test
//    void deleteAddress() {
//    }
}
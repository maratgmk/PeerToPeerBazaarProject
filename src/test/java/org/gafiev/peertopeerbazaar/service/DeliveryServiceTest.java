package org.gafiev.peertopeerbazaar.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {
//    private long counter = 0L;
//    private long counterAddress = 0L;
//    private long counterOrder = 0L;
//    private long counterQuadcopter = 0L;
//    private Address address;
//    private SellerOffer order;
//    private Drone drone;
//
//    private   Address getAddress() {
//        Address address = new Address();
//        address.setId(counterAddress++);
//        address.setTown("Tulsa");
//        address.setStreet("First avenue");
//        address.setNumberBuilding(123);
//        address.setZipCode(ThreadLocalRandom.current().nextInt(1,10000));
//        address.setLatitude(55.753744);
//        address.setLongitude(37.619722);
//        address.setAccuracy(1.5F);
//        return address;
//    }
//
//    private SellerOffer getSellerOffer(){
//           SellerOffer order = new SellerOffer();
//           order.setId(counterOrder++);
//           order.setAddress(getAddress());
//         //  order.setBuyer(buyer);
//        //   order.setSeller(seller);
//        return order;
//    }
//
//    private Drone getQuadcopter(){
//        Drone drone = new Drone();
//        drone.setId(counterQuadcopter++);
//      //  drone.setDeliveries(createDelivery());
//
//        return drone;
//    }
//
//    @InjectMocks
//    private DeliveryService deliveryService;
//
//    @Mock
//    private DeliveryRepository deliveryRepository;
//
//
//    @BeforeEach
//    void setUp() {
//        Mockito.reset(deliveryRepository);
//    }
//
//    @AfterEach
//    void tearDown() {
//    }
//
//    @Test
//    void createDelivery() {
//        Delivery delivery = getDelivery();
//        deliveryService.createDelivery(delivery);
//    }
//
//    private @NotNull Delivery getDelivery() {
//        Delivery delivery = new Delivery();
//        delivery.setId(counter++);
//        delivery.setAddress(address);
//        delivery.setDeliveryStatus(DeliveryStatus.ON_THE_WAY);
//        delivery.setDateTime(LocalDateTime.of(2024,10,12,12,48,12));
//        delivery.setSellerOffer(order);
//        //  delivery.setDrones(drone);
//        return delivery;
//    }
//
//    @Test
//    void getAllDeliveries() {
//
//    }
//
////    @Test
////    void getDeliveryById_deliveryFound_deliveryNotNull() {
////        Delivery expected = getDelivery();
////        Delivery testDelivery = getDelivery();
////        assertNotEquals(expected,testDelivery);
////        Mockito.when(deliveryRepository.findById(eq(expected.getId()))).thenReturn(Optional.of(expected));
////        Mockito.when(deliveryRepository.findById(eq(testDelivery.getId()))).thenReturn(Optional.of(testDelivery));
////        Optional<Delivery> actual = deliveryService.getDeliveryById(expected.getId());
////        assertEquals(expected,actual);
////    }
//
//    @Test
//    void getDeliveryById_deliveryFound_throwIllegalArgumentException() {
//        Delivery testDelivery = getDelivery();
//        long wrongId = -1L;
//        Mockito.when(deliveryRepository.findById(eq(testDelivery.getId()))).thenReturn(Optional.of(testDelivery));
//        Exception exception = assertThrows(IllegalArgumentException.class,() -> deliveryService.getDeliveryById(wrongId));
//        assertEquals("Delivery not found with id " + wrongId,exception.getMessage());
//    }
//
//    @Test
//    void deleteDelivery() {
//    }

}
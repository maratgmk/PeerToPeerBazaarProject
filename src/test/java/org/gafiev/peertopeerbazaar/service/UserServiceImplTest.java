package org.gafiev.peertopeerbazaar.service;

import org.gafiev.peertopeerbazaar.entity.user.Role;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
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

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
//    private long counter = 0L;
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    @Mock
//    private UserRepository userRepository;
//
////    @Test
////    void getUserByID_userFound_userNotNull() {
////        User expected = getUser();
////        User testUser = getUser();
////        assertNotEquals(expected,testUser);
////        Mockito.when(userRepository.findBy(eq(expected.getId()))).thenReturn(Optional.of(expected));
////        Mockito.when(userRepository.findBy(eq(testUser.getId()))).thenReturn(Optional.of(testUser));
////        Optional<User> actual = userService.getUserById(expected.getId());
////        assertEquals(expected,actual);
////    }
//
//    @Test
//    void getUserByID_userFound_throwIllegalArgumentException() {
//        User testUser = getUser();
//        long wrongId = -1L;
//        Mockito.when(userRepository.findBy(eq(testUser.getId()))).thenReturn(Optional.of(testUser));
//        Exception exception = assertThrows(IllegalArgumentException.class,() -> userService.getUserById(testUser.getId()));
//        assertEquals("User not found by id " + wrongId,exception.getMessage());
//    }
//
//
//
//    private User getUser() {
//        User user = new User();
//        user.setId(counter++);
//        user.setFirstName("Bob");
//        user.setLastName("Bean");
//        user.setEmail("bean@jj.com");
//        user.setPassword("jh24!aS"+ ThreadLocalRandom.current().nextInt(0,11));
//        user.setPhone("+958262568"+ThreadLocalRandom.current().nextInt(0,10));
//        user.setRole(Role.USER);
//        user.setRatingBuyer(ThreadLocalRandom.current().nextInt(0,101));
//        user.setRatingSeller(ThreadLocalRandom.current().nextInt(0,101));
////        user.setProductSet(????????);
////        user.setBuyerSellerOffers(????????);
////        user.setSellerSellerOffers(????????);
//        return user;
//    }
//
//    @Test
//    void createUser() {
//    }
//
//    @Test
//    void getAllUsers() {
//    }
//
//    @Test
//    void deleteUser() {
//    }
}
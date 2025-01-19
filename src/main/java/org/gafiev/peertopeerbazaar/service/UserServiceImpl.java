package org.gafiev.peertopeerbazaar.service;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.request.UserCreateRequest;
import org.gafiev.peertopeerbazaar.dto.request.UserFilterRequest;
import org.gafiev.peertopeerbazaar.dto.response.UserResponse;
import org.gafiev.peertopeerbazaar.entity.user.Role;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.mapper.UserMapper;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(id))));
        return userMapper.toUserResponse(user);
    }


    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("email", String.valueOf(email))));
        return userMapper.toUserResponse(user);
    }

    @Override
    public Set<UserResponse> getAllUsers(UserFilterRequest filterRequest) {
        return Set.of();
    }


    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest candidate) {
        User user =  new User();
        user.setFirstName(candidate.firstName());
        user.setLastName(candidate.lastName());
        user.setEmail(candidate.email());
        user.setPhone(candidate.phone());
        user.setPassword(candidate.password());

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserCreateRequest updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(id))));

        user.setFirstName(updatedUser.firstName());
        user.setLastName(updatedUser.lastName());
        user.setEmail(updatedUser.email());
        user.setPhone(updatedUser.phone());
        user.setPassword(updatedUser.password());

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }


    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }


    @Override
    public UserResponse getUserByIdWithBuyerOrdersAndSellerOffers(Long id) {
        User user = userRepository.findByIdWithBuyerOrdersAndSellerOffers(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class,Map.of("id", String.valueOf(id))));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse findByIdWithPaymentAccounts(Long id) {
        return null;
    }

    @Override
    public UserResponse findByIdWithBasket(Long id) {
        return null;
    }

    @Override
    public UserResponse findByIdWithProducts(Long id) {
        return null;
    }

    @Override
    public UserResponse findByIdFull(Long id) {
        return null;
    }


}
// TODO доделать этот сервис в соответствии с интерфейсом
//TODO сделать интерфейсы для каждого сервиса
// TODO в контролеры внедрять интерфейсы


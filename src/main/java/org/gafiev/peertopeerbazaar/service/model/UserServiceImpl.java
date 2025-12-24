package org.gafiev.peertopeerbazaar.service.model;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.UserFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.UserUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.UserResponse;
import org.gafiev.peertopeerbazaar.entity.user.User;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.mapper.UserMapper;
import org.gafiev.peertopeerbazaar.repository.UserRepository;
import org.gafiev.peertopeerbazaar.repository.specification.UserSpecification;
import org.gafiev.peertopeerbazaar.service.model.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
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
        List<User> userList = userRepository.findAll(UserSpecification.filterByParams(filterRequest));
        return userMapper.toUserResponseSet(new HashSet<>(userList));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(id))));

        user.setFirstName(updatedUser.firstName());
        user.setLastName(updatedUser.lastName());
        user.setPhone(updatedUser.phone());
        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(id))));
        userRepository.delete(user);
    }

    @Override
    public UserResponse getUserByIdWithBuyerOrdersAndSellerOffers(Long id) {
        User user = userRepository.findByIdWithBuyerOrdersAndSellerOffers(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(id))));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserByIdWithBasket(Long id) {
        User user = userRepository.findByIdWithBasket(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(id))));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse findByIdFull(Long id) {
        User user = userRepository.findByIdFull(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(id))));
        return userMapper.toUserResponse(user);
    }
}





package org.gafiev.peertopeerbazaar.service.model;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.UserCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.UserFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.UserResponse;
import org.gafiev.peertopeerbazaar.entity.user.Role;
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

    /**
     * нахождение пользователя по Id
     *
     * @param id идентификатор пользователя
     * @return DTO user
     */
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(id))));
        return userMapper.toUserResponse(user);
    }

    /**
     * нахождение пользователя по email
     *
     * @param email почта пользователя
     * @return DTO user
     */
    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("email", String.valueOf(email))));
        return userMapper.toUserResponse(user);
    }

    /**
     * поиск клиентов согласно фильтра, настроенный администратором
     *
     * @param filterRequest фильтр определяющий параметры и условия поиска
     * @return множество DTO user
     */
    @Override
    public Set<UserResponse> getAllUsers(UserFilterRequest filterRequest) {
        List<User> userList = userRepository.findAll(UserSpecification.filterByParams(filterRequest));
        return userMapper.toUserResponseSet(new HashSet<>(userList));
    }

    /**
     * создание нового клиента
     *
     * @param candidate информация для создания нового пользователя
     * @return DTO нового пользователя
     */
    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest candidate) {
        User user = new User();
        user.setFirstName(candidate.firstName());
        user.setLastName(candidate.lastName());
        user.setEmail(candidate.email());
        user.setPhone(candidate.phone());
        user.setPassword(candidate.password());
        user.addRole(Role.UNCONFIRMED);

        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    /**
     * изменения существующего пользователя
     *
     * @param id          идентификатор существующего пользователя
     * @param updatedUser информация, которую надо поменять в существующем пользователе
     * @return DTO изменённого пользователя
     */
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

        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    /**
     * метод установки роли пользователю, или продавца или покупателя, или обе роли.
     *
     * @param id идентификатор пользователя
     * @return DTO пользователя с назначенными ролями
     */
    @Override
    public UserResponse confirmUser(Long id) {
        User user = userRepository.findByIdFull(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(id))));

        user.addRole(Role.USER);
        user.addRole(Role.BUYER);
        user.removeRole(Role.UNCONFIRMED);


        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }


    /**
     * удаление пользователя из БД
     *
     * @param id идентификатор пользователя
     */
    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class,Map.of("id", String.valueOf(id))));
        userRepository.delete(user);
    }

    /**
     * получение пользователя из БД вместе со множеством заказов покупателя и со множеством предложений продавца
     *
     * @param id идентификатор пользователя
     * @return DTO пользователя с подтягиванием ленивых частей
     */
    @Override
    public UserResponse getUserByIdWithBuyerOrdersAndSellerOffers(Long id) {
        User user = userRepository.findByIdWithBuyerOrdersAndSellerOffers(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(id))));
        return userMapper.toUserResponse(user);
    }

    /**
     * получение пользователя из БД вместе с его корзиной
     *
     * @param id идентификатор пользователя
     * @return DTO пользователя с подтягиванием ленивых частей
     */
    @Override
    public UserResponse getUserByIdWithBasket(Long id) {
        User user = userRepository.findByIdWithBasket(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(id))));
        return userMapper.toUserResponse(user);
    }


    /**
     * получение пользователя из БД вместе с его множествами productSet, sellerOrderSet, buyerOrderSet, paymentAccountSet
     *
     * @param id идентификатор пользователя
     * @return DTO пользователя с подтягиванием ленивых частей
     */
    @Override
    public UserResponse findByIdFull(Long id) {
        User user = userRepository.findByIdFull(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, Map.of("id", String.valueOf(id))));
        return userMapper.toUserResponse(user);
    }
}





package org.gafiev.peertopeerbazaar.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.UserCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.UserFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.UserResponse;
import org.gafiev.peertopeerbazaar.service.model.interfaces.UserService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(
        path = "user",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest candidate) {
        return userService.createUser(candidate);
    }

    @GetMapping(path = "/confirm/{id}",consumes = MediaType.ALL_VALUE)
    public UserResponse confirmUser(@Positive @NotNull @PathVariable Long id){
        return userService.confirmUser(id);
    }

    @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public UserResponse getUser(@NotNull @Positive @PathVariable Long id, @RequestParam(value = "full", required = false, defaultValue = "false") Boolean isFull) {
        return isFull ? userService.findByIdFull(id) : userService.getUserById(id);
    }

    @PostMapping("/all")
    public Set<UserResponse> getAllUsers(@NotNull @Valid @RequestBody UserFilterRequest filterRequest) {
        return userService.getAllUsers(filterRequest);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@Positive @PathVariable Long id, @NotNull @Valid @RequestBody UserCreateRequest updateUser) {
        return userService.updateUser(id, updateUser);
    }

    @PutMapping(path = "/{id}/role", consumes = MediaType.ALL_VALUE)
    public UserResponse updateUserRole(@Positive @PathVariable Long id){
        return userService.confirmUser(id);
    }

    @DeleteMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public void deleteUserById(@Positive @PathVariable Long id) {
        userService.deleteUserById(id);
    }

}

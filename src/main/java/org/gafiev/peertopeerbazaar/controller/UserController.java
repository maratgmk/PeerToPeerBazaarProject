package org.gafiev.peertopeerbazaar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.UserFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.UserUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.UserResponse;
import org.gafiev.peertopeerbazaar.service.model.interfaces.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Tag(name = "Users", description = "Provides endpoints for managing users.")
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

    @Operation(summary = "Retrieves a user by his ID",
            description = "Retrieves full information of a user, including his products,offers and orders.")
    @PreAuthorize("hasRole('ADMIN') or (@authz.isSelf(#id, authentication))")
    @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public UserResponse getUser(@NotNull @Positive @PathVariable Long id,
                                @RequestParam(value = "full", required = false, defaultValue = "false") Boolean isFull) {
        return isFull ? userService.findByIdFull(id) : userService.getUserById(id);
    }

    @Operation(summary = "Retrieves a set of users using filter criteria.",
            description = "Allows users with the ADMIN role to retrieve users using specific filter criteria.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/all")
    public Set<UserResponse> getAllUsers(
            @Parameter(description = "Data for filtering to retrieve desired users.", required = true)
            @NotNull @Valid @RequestBody UserFilterRequest filterRequest) {
        return userService.getAllUsers(filterRequest);
    }

    @Operation(summary = "Updates a user.", description = "Allows the user to update his profile with new information.")
    @PreAuthorize("hasRole('ADMIN') or (@authz.isSelf(#id, authentication))")
    @PutMapping("/{id}")
    public UserResponse updateUser(@Positive @PathVariable Long id,
                                   @Parameter(description = "Data for updating the user's profile.", required = true)
                                   @NotNull @Valid @RequestBody UserUpdateRequest updateUser) {
        return userService.updateUser(id, updateUser);
    }

    @Operation(summary = "Deletes a user.", description = "Allows users with the ADMIN role to delete the user by his ID.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public void deleteUserById(@Positive @PathVariable Long id) {
        userService.deleteUserById(id);
    }
}

package org.gafiev.peertopeerbazaar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.ProductResponse;
import org.gafiev.peertopeerbazaar.service.model.interfaces.ProductService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Tag(name = "Products", description = "Operations for managing product processes and history.")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(
        path = "product",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Creates a product.", description = "Allows a user to create a product using data from the ProductCreateRequest DTO.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#createRequest.userId(),authentication)")
    @PostMapping
    public ProductResponse createProduct(
            @Parameter(description = "Data for product creation.", required = true)
            @Valid @RequestBody ProductCreateRequest createRequest) {
        return productService.createProduct(createRequest);
    }

    @Operation(summary = "Retrieves a product by ID.", description = "Helps a user to get a product by its ID.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId(),authentication)")
    @GetMapping(path = "/{id}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public ProductResponse getProductById(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long userId) {
        return productService.getProductById(id);
    }

    @Operation(summary = "Retrieves a product by author's ID.", description = "Helps a user to get a product by the author's ID (User entity).")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping(path = "/author/{authorId}")
    public Set<ProductResponse> getProductByAuthorId(@NotNull @Positive @PathVariable Long authorId) {
        return productService.getProductByAuthorId(authorId);
    }

    @Operation(summary = "Retrieves a set of products.", description = "Allows users with ADMIN role to retrieve products based on specific filter criteria. ")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/all")
    public Set<ProductResponse> getAllProducts(
            @Parameter(description = "Data for the specific filter criteria", required = true)
            @Valid @RequestBody ProductFilterRequest filterRequest) {
        return productService.getAllProducts(filterRequest);
    }

    @Operation(summary = "Updates an existing product.", description = "Allows a user to update his product using the new data parameters.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#updateRequest.userId(), authentication)")
    @PutMapping("{id}")
    public ProductResponse updateProduct(@NotNull @Positive @PathVariable Long id,
                                         @Parameter(description = "Data for updating an existing product.", required = true)
                                         @Valid @RequestBody ProductUpdateRequest updateRequest) {
        return productService.updateProduct(id, updateRequest);
    }

    @Operation(summary = "Deletes a product.", description = "Allows a user with ADMIN or SELLER role to delete the product by its ID.")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @DeleteMapping(path = "{id}/author/{userId}", consumes = MediaType.ALL_VALUE)
    public void deleteProduct(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long userId) {
        productService.deleteProduct(id);
    }
}

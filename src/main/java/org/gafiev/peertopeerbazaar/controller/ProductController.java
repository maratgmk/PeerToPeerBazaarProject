package org.gafiev.peertopeerbazaar.controller;

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

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(
        path = "product",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#createRequest.userId(),authentication)")
    @PostMapping
    public ProductResponse createProduct(@Valid @RequestBody ProductCreateRequest createRequest) {
        return productService.createProduct(createRequest);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId(),authentication)")
    @GetMapping(path = "/{id}/user/{userId}", consumes = MediaType.ALL_VALUE)
    public ProductResponse getProductById(@NotNull @Positive @PathVariable Long id,@NotNull @Positive @PathVariable Long userId) {
        return productService.getProductById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping(path = "/author/{authorId}")
    public Set<ProductResponse> getProductByAuthorId(@NotNull @Positive @PathVariable Long authorId) {
        return productService.getProductByAuthorId(authorId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/all")
    public Set<ProductResponse> getAllProducts(@Valid @RequestBody ProductFilterRequest filterRequest) {
        return productService.getAllProducts(filterRequest);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#updateRequest.userId(), authentication)")
    @PutMapping("{id}")
    public ProductResponse updateProduct(@NotNull @Positive @PathVariable Long id, @Valid @RequestBody ProductUpdateRequest updateRequest) {
        return productService.updateProduct(id, updateRequest);
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#userId, authentication)")
    @DeleteMapping(path = "{id}/author/{userId}", consumes = MediaType.ALL_VALUE)
    public void deleteProduct(@NotNull @Positive @PathVariable Long id, @NotNull @Positive @PathVariable Long userId) {
        productService.deleteProduct(id);
    }
}

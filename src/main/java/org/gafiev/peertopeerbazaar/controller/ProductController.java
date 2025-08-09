package org.gafiev.peertopeerbazaar.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.ProductResponse;
import org.gafiev.peertopeerbazaar.service.model.interfaces.ProductService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ProductResponse createProduct(@Valid @RequestBody ProductCreateRequest candidate) {
        return productService.createProduct(candidate);
    }

    @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
    public ProductResponse getProductById(@NotNull @Positive @PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping
    public Set<ProductResponse> getProductByAuthorId(@NotNull @Positive @RequestParam Long user_id) {
        return productService.getProductByAuthorId(user_id);
    }

    @PostMapping("/all")
    public Set<ProductResponse> getAllProducts(@Valid @RequestBody ProductFilterRequest filterRequest) {
        return productService.getAllProducts(filterRequest);
    }

    @PutMapping("{id}")
    public ProductResponse updateProduct(@NotNull @Positive @PathVariable Long id, @Valid @RequestBody ProductCreateRequest productNew) {
        return productService.updateProduct(id, productNew);
    }

    @DeleteMapping(path = "{id}", consumes = MediaType.ALL_VALUE)
    public void deleteProduct(@NotNull @Positive @PathVariable Long id) {
        productService.deleteProduct(id);
    }
}

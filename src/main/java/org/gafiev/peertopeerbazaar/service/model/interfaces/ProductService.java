package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.ProductCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.ProductResponse;

import java.util.Set;

/**
 * Provides methods for Product data operations.
 */
public interface ProductService {

    /**
     * Retrieves Product response from database by ID.
     *
     * @param id Unique product identifier (ID).
     * @return ProductResponse DTO.
     */
    ProductResponse getProductById(Long id);

    /**
     * Retrieves Set of Product response DTOs by filter criteria.
     *
     * @param filterRequest ProductFilterRequest DTO contains data for the filter criteria.
     * @return Set of ProductResponse DTOs.
     */
    Set<ProductResponse> getAllProducts(ProductFilterRequest filterRequest);

    /**
     * Creates new Product using data from the ProductCreateRequest DTO.
     *
     * @param product ProductCreateRequest DTO holds data for the new product creating.
     * @return ProductResponse DTO.
     */
    ProductResponse createProduct(ProductCreateRequest product);

    /**
     * Updates the existing Product using a ProductUpdateRequest DTO.
     *
     * @param id            Unique product identifier (ID).
     * @param updateRequest ProductUpdateRequest DTO holds data for the existing product updating.
     * @return ProductResponse DTO.
     */
    ProductResponse updateProduct(Long id, ProductUpdateRequest updateRequest);

    /**
     * Deletes the product by ID from database.
     *
     * @param id Unique product identifier (ID).
     */
    void deleteProduct(Long id);

    /**
     * Retrieves a set of Product response DTOs by the author's ID.
     *
     * @param user_id Unique the author (User) identifier (ID).
     * @return Set of ProductResponse DTOs.
     */
    Set<ProductResponse> getProductByAuthorId(Long user_id);
}

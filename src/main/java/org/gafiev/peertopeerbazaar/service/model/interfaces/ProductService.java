package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.ProductCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.ProductFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.ProductResponse;

import java.util.Set;

/**
 * интерфейс описывает методы получения DTO product или множества DTO productSet из БД,
 * стандартные методы CRUD или кастомные
 */
public interface ProductService {

    ProductResponse getProductById(Long id);

    Set<ProductResponse> getAllProducts(ProductFilterRequest filterRequest);

    ProductResponse createProduct(ProductCreateRequest product);

    ProductResponse updateProduct(Long id, ProductCreateRequest productDetails);

    void deleteProduct(Long id);

    Set<ProductResponse> getProductByAuthorId(Long user_id);
}

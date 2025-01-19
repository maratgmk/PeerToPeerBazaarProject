package org.gafiev.peertopeerbazaar.service.interfaces;

import org.gafiev.peertopeerbazaar.dto.request.ProductCreateRequest;
import org.gafiev.peertopeerbazaar.dto.request.ProductFilterRequest;
import org.gafiev.peertopeerbazaar.dto.response.ProductResponse;

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

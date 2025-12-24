package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.ProductResponse;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class for converting Product entities to various DTOs (Data Transfer Objects).
 */
@Component
@AllArgsConstructor
public class ProductMapper {
    private SellerOfferMapper sellerOfferMapper;

    /**
     * Converts Product entity to ProductResponse DTO.
     *
     * @param product Product entity.
     * @return ProductResponse DTO.
     */
    public ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .portionUnit(product.getPortionUnit())
                .weight(product.getWeightKg())
                .volume(product.getVolumeLtr())
                .price(product.getPrice())
                .imageURI(product.getImageURI())
                .qrCode(product.getQrCode())
                .sellerOfferResponseSet(sellerOfferMapper.toSellerOfferResponseSet(product.getSellerOfferSet()))
                .userId(product.getAuthor() == null ? null : product.getAuthor().getId())
                .createdAt(product.getCreatedAt())
                .build();
    }

    /**
     * Converts Set of Product entities to Set of ProductResponse DTOs.
     *
     * @param productSet Set of Product entities.
     * @return Set of ProductResponse DTOs.
     */
    public Set<ProductResponse> toProductResponseSet(Set<Product> productSet) {
        return productSet == null ? null : productSet.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toSet());
    }
}

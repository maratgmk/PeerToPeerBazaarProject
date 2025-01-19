package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.response.ProductResponse;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ProductMapper {
    private SellerOfferMapper sellerOfferMapper;

    public ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .portionUnit(product.getPortionUnit())
                .weight(product.getWeight())
                .volume(product.getVolume())
                .price(product.getPrice())
                .imageURI(product.getImageURI())
                .qrCode(product.getQrCode())
                .sellerOfferResponseSet(sellerOfferMapper.toSellerOfferResponseSet(product.getSellerOfferSet()))
                .userId(product.getAuthor() == null ? null : product.getAuthor().getId())
                .build();
    }

    public Set<ProductResponse> toProductResponseSet(Set<Product> productSet) {
        return productSet == null ? null : productSet.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toSet());
    }
}
